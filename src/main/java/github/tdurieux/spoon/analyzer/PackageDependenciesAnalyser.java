package github.tdurieux.spoon.analyzer;

import github.tdurieux.spoon.graph.DependencyGraph;
import github.tdurieux.spoon.graph.node.DependencyDeclaration;
import github.tdurieux.spoon.graph.node.PackageNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.SpoonException;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * analyzes package dependencies
 */
public class PackageDependenciesAnalyser extends AbstractDependenciesAnalyser {

	public PackageDependenciesAnalyser(DependencyGraph graph) {
		super(graph);
	}

	public PackageDependenciesAnalyser() {
		super();
	}

	public void process(CtTypedElement<?> element) {
		// the element has no type
		if (element.getType() == null) {
			return;
		}

		List<CtTypeReference<?>> listDependencies = new ArrayList<CtTypeReference<?>>();

		// get the class where the element is used
		CtSimpleType<?> parent = element.getParent(CtSimpleType.class);

		// manage anonymous class
		while (parent.getSimpleName().isEmpty()) {
			listDependencies.addAll(((CtSimpleType<?>) parent)
					.getReferencedTypes());
			parent = parent.getParent(CtSimpleType.class);
		}

		String parentClass = parent.getQualifiedName();
		CtSimpleType<?> tmp = parent;
		// manage internal class
		while (!(tmp.getParent() instanceof CtPackage)) {
			tmp = tmp.getParent(CtSimpleType.class);
		}
		CtPackage parentPackage = tmp.getPackage();

		String elementClass = element.getType().toString();
		SourcePosition elementPosition = element.getPosition();

		PackageNode parentNode = new PackageNode(
				parentPackage.getQualifiedName(), elementPosition.getLine(),
				parentClass, isExternal(parent));

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

		for (CtTypeReference<?> ctTypeReference : listDependencies) {
			List<DependencyDeclaration> dependenciesElement = getDependenciesOfType(ctTypeReference);
			getDependencyGraph().addDependencyNode(parentNode,
					dependenciesElement);

			CtPackageReference typePackage = ctTypeReference.getPackage();

			if (typePackage == null) {
				continue;
			}

			PackageNode node = new PackageNode(typePackage.getSimpleName(),
					elementPosition.getLine(), elementClass,
					isExternal(ctTypeReference));
			getDependencyGraph().addDependencyNode(parentNode, node);
		}

	}

	private List<DependencyDeclaration> getDependenciesOfType(
			CtTypeReference<?> ctTypeReference) {

		List<DependencyDeclaration> packages = new ArrayList<DependencyDeclaration>();

		CtSimpleType<?> ctSimpleType = ctTypeReference.getDeclaration();

		if (ctSimpleType instanceof CtClass<?>) {
			CtClass<?> tmp = (CtClass<?>) ctSimpleType;
			// super class
			CtTypeReference<?> superclass = tmp.getSuperclass();
			if (superclass != null) {
				if (superclass.getPackage() != null) {
					packages.add(new PackageNode(superclass.getPackage()
							.getSimpleName(), 0, superclass.getQualifiedName(),
							isExternal(superclass)));
				}
				// packages.addAll(getDependenciesOfType(superclass));
			}
		}
		if (ctSimpleType instanceof CtType<?>) {
			CtType<?> tmp = (CtType<?>) ctSimpleType;
			// interfaces
			Set<CtTypeReference<?>> interfaces = tmp.getSuperInterfaces();
			for (CtTypeReference<?> interfaceType : interfaces) {
				CtPackageReference typePackage = interfaceType.getPackage();
				if (typePackage != null) {
					packages.add(new PackageNode(typePackage.getSimpleName(),
							0, interfaceType.getQualifiedName(),
							isExternal(interfaceType)));
				}
				// packages.addAll(getDependenciesOfType(interfaceType));
			}
		}

		return packages;
	}

	private List<DependencyDeclaration> getDependenciesOfType(
			Class<?> elementClass) {
		List<DependencyDeclaration> packages = new ArrayList<DependencyDeclaration>();
		if (elementClass == null) {
			return packages;
		}
		// interface of t
		Class<?>[] elementInterface = elementClass.getInterfaces();

		if (elementClass.getSuperclass() != null
				&& !packages
						.contains(elementClass.getSuperclass().getPackage())) {
			packages.add(new PackageNode(elementClass.getSuperclass()
					.getPackage(), 0, elementClass.getSuperclass()
					.getCanonicalName(), true));
			// packages.addAll(getDependenciesOfType(elementClass.getSuperclass()));
		}

		for (Class<?> type : elementInterface) {
			Package ctPackage = type.getPackage();
			packages.add(new PackageNode(ctPackage, 0, type.getCanonicalName(),
					true));

			// packages.addAll(getDependenciesOfType(type));
		}

		return packages;
	}
}