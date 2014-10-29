package github.tdurieux.spoon.analyzer;

import github.tdurieux.spoon.graph.DependencyGraph;
import github.tdurieux.spoon.graph.node.DependencyDeclaration;
import github.tdurieux.spoon.graph.node.ClassNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;

/**
 * analyzes class dependencies
 */
public class ClassDependenciesAnalyser extends AbstractDependenciesAnalyser {

	public ClassDependenciesAnalyser(DependencyGraph graph) {
		super(graph);
	}

	public ClassDependenciesAnalyser() {
		super();
	}
	
	public void process(CtTypedElement<?> element) {
		List<CtTypeReference<?>> listDependencies = new ArrayList<CtTypeReference<?>>();
		
		// get the class where the element is used
		CtSimpleType<?> parent = element.getParent(CtSimpleType.class);

		// manage anonymous class
		while (parent.getSimpleName().isEmpty()) {
			listDependencies.addAll(((CtSimpleType<?>) parent).getReferencedTypes());
			parent = parent.getParent(CtSimpleType.class);
		}

		String parentClass = parent.getQualifiedName();
		
		SourcePosition elementPosition = element.getPosition();

		ClassNode parentNode = new ClassNode(
				parentClass, 
				elementPosition.getLine(),
				isExternal(parent));

		// annotations
		List<CtAnnotation<?>> annotations = element.getAnnotations();
		for (CtAnnotation<?> ctAnnotation : annotations) {
			listDependencies.add(ctAnnotation.getAnnotationType());
			
			// annotations elements 
			Map<String, Object> elements = ctAnnotation.getElementValues();
			for (Object elementValue : elements.values()) {
				if(elementValue instanceof CtTypeReference<?>) {
					listDependencies.add((CtTypeReference<?>) elementValue);
				} else if(elementValue instanceof  CtTypedElement<?>) {
					listDependencies.add(((CtTypedElement<?>) elementValue).getType());
				} 
			}
		}
		
		// Literal
		if(element instanceof CtLiteral<?>) {
			CtLiteral<?> literal = (CtLiteral<?>) element;
			literal.getValue();
			
			if(literal.getValue() instanceof CtTypeReference<?>) {
				listDependencies.add((CtTypeReference<?>) literal.getValue());
			} else if(literal.getValue() instanceof  CtTypedElement<?>) {
				listDependencies.add(((CtTypedElement<?>) literal.getValue()).getType());
			}
		}

		// method invocation
		if (element instanceof CtInvocation<?>) {
			CtInvocation<?> invocation = (CtInvocation<?>) element;

			// the class of the method
			listDependencies.add(invocation.getExecutable().getDeclaringType());

			// parameters 
			List<CtTypeReference<?>> parameters = invocation.getExecutable()
					.getParameterTypes();
			listDependencies.addAll(parameters);
		}
		
		// method declaration
		if (element instanceof CtMethod<?>) {
			CtMethod<?> method = (CtMethod<?>) element;
			// exceptions
			Set<CtTypeReference<? extends Throwable>> thrownTypes = method.getThrownTypes();
			for (CtTypeReference<? extends Throwable> ctTypeReference : thrownTypes) {
				listDependencies.add(ctTypeReference);
			}
		}

		
		listDependencies.add(element.getType());

		for (CtTypeReference<?> ctTypeReference : listDependencies) {
			if(ctTypeReference == null) {
				continue;
			}
			List<DependencyDeclaration> dependenciesElement = getDependenciesOfType(ctTypeReference);
			getDependencyGraph().addDependencyNode(parentNode, dependenciesElement);
			
			ClassNode node = new ClassNode(
					ctTypeReference.getQualifiedName(), 
					elementPosition.getLine(),  
					parentClass,
					isExternal(ctTypeReference));
			getDependencyGraph().addDependencyNode(parentNode, node);
		}
		
	}

	private List<DependencyDeclaration> getDependenciesOfType(
			CtTypeReference<?> ctTypeReference) {
		
		List<DependencyDeclaration> classes = new ArrayList<DependencyDeclaration>();
		
		if(ctTypeReference == null) {
			return classes;
		}
		
		CtSimpleType<?> ctSimpleType = ctTypeReference.getDeclaration();
		
		if (ctSimpleType instanceof CtClass<?>) {
			CtClass<?> tmp = (CtClass<?>) ctSimpleType;
			// super class
			CtTypeReference<?> superclass = tmp.getSuperclass();
			if (superclass != null) {
				classes.add(new ClassNode(
						superclass.getQualifiedName(),
						0,
						isExternal(superclass)));
				// classes.addAll(getDependenciesOfType(superclass));
			}
		}
		if (ctSimpleType instanceof CtType<?>) {
			CtType<?> tmp = (CtType<?>) ctSimpleType;
			// interfaces 
			Set<CtTypeReference<?>> interfaces = tmp.getSuperInterfaces();
			for (CtTypeReference<?> interfaceType : interfaces) {
				classes.add(new ClassNode(
						interfaceType.getQualifiedName(),
						0,
						isExternal(interfaceType)));
				// classes.addAll(getDependenciesOfType(interfaceType));
			}
		}
		/*try {
			classes.addAll(getDependenciesOfType(ctTypeReference.getActualClass()));
		} catch(SpoonException e) {
			// the class the compiled file is not available for this type 
			// just ignore it
		}*/
		return classes;
	}

	private List<DependencyDeclaration> getDependenciesOfType(
			Class<?> elementClass) {
		List<DependencyDeclaration> classes = new ArrayList<DependencyDeclaration>();
		if(elementClass == null) {
			return classes;
		}
		// interface of t
		Class<?>[] elementInterface = elementClass.getInterfaces();

		try{
			Class<?> superclass = elementClass.getSuperclass();
			if (superclass != null) {
				classes.add(new ClassNode(superclass, 0, true));
				classes.addAll(getDependenciesOfType(superclass));
			}
		} catch(NoClassDefFoundError e) {
			// the declaration of the super class is not found 
			// ignore it
		}

		for (Class<?> type : elementInterface) {
			classes.add(new ClassNode(type, 0,true));
			classes.addAll(getDependenciesOfType(type));
		}

		return classes;
	}
}