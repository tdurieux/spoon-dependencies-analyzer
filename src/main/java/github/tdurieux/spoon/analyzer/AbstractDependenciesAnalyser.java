package github.tdurieux.spoon.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import github.tdurieux.spoon.graph.DependencyGraph;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;

public abstract class AbstractDependenciesAnalyser extends
		AbstractProcessor<CtTypedElement<?>> {

	private DependencyGraph dependencyGraph;

	protected boolean versbose = false;

	public AbstractDependenciesAnalyser(DependencyGraph dependencyGraph) {
		this.dependencyGraph = dependencyGraph;
	}

	public AbstractDependenciesAnalyser() {
		this.dependencyGraph = new DependencyGraph();
	}

	public DependencyGraph getDependencyGraph() {
		return dependencyGraph;
	}

	protected boolean isExternal(CtReference e) {
		return isExternal(e.getDeclaration());
	}

	protected List<CtTypeReference<?>> getDependencies(
			CtTypeReference<?> ctTypeReference) {

		List<CtTypeReference<?>> listDependencies = new ArrayList<CtTypeReference<?>>();

		if (ctTypeReference == null) {
			return listDependencies;
		}

		CtSimpleType<?> ctSimpleType = ctTypeReference.getDeclaration();

		if (ctSimpleType instanceof CtClass<?>) {
			CtClass<?> tmp = (CtClass<?>) ctSimpleType;
			// super class
			CtTypeReference<?> superclass = tmp.getSuperclass();
			if (superclass != null) {
				listDependencies.add(superclass);
				// classes.addAll(getDependencies(superclass));
			}
		}

		if (ctSimpleType instanceof CtType<?>) {
			CtType<?> tmp = (CtType<?>) ctSimpleType;
			// interfaces
			Set<CtTypeReference<?>> interfaces = tmp.getSuperInterfaces();
			for (CtTypeReference<?> interfaceType : interfaces) {
				listDependencies.add(interfaceType);
				// listDependencies.addAll(getDependencies(interfaceType));
			}
		}
		return listDependencies;
	}

	protected List<CtTypeReference<?>> getDependencies(CtTypedElement<?> element) {
		List<CtTypeReference<?>> listDependencies = new ArrayList<CtTypeReference<?>>();

		// annotations
		List<CtAnnotation<?>> annotations = element.getAnnotations();
		for (CtAnnotation<?> ctAnnotation : annotations) {
			listDependencies.add(ctAnnotation.getAnnotationType());

			// annotations elements
			Map<String, Object> elements = ctAnnotation.getElementValues();
			for (Object elementValue : elements.values()) {
				if (elementValue instanceof CtTypeReference<?>) {
					listDependencies.add((CtTypeReference<?>) elementValue);
				} else if (elementValue instanceof CtTypedElement<?>) {
					listDependencies.add(((CtTypedElement<?>) elementValue)
							.getType());
				}
			}
		}

		// Literal
		if (element instanceof CtLiteral<?>) {
			CtLiteral<?> literal = (CtLiteral<?>) element;
			literal.getValue();

			if (literal.getValue() instanceof CtTypeReference<?>) {
				listDependencies.add((CtTypeReference<?>) literal.getValue());
			} else if (literal.getValue() instanceof CtTypedElement<?>) {
				listDependencies.add(((CtTypedElement<?>) literal.getValue())
						.getType());
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
			Set<CtTypeReference<? extends Throwable>> thrownTypes = method
					.getThrownTypes();
			for (CtTypeReference<? extends Throwable> ctTypeReference : thrownTypes) {
				listDependencies.add(ctTypeReference);
			}
		}

		listDependencies.add(element.getType());
		return listDependencies;
	}

	protected boolean isExternal(CtElement e) {
		if (e instanceof CtPackage) {
			return false;
		}
		if (e == null) {
			return true;
		}
		SourcePosition position = e.getPosition();

		if (position != null) {
			position.getFile();
		} else {
			return true;
		}
		return false;
	}
}