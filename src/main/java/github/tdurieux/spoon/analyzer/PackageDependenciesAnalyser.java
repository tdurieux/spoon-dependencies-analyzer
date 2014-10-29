package github.tdurieux.spoon.analyzer;

import github.tdurieux.spoon.graph.DependencyGraph;
import github.tdurieux.spoon.graph.node.DependencyDeclaration;
import github.tdurieux.spoon.graph.node.PackageNode;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtSimpleType;
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
		// get the class where the element is used
		CtSimpleType<?> parent = element.getParent(CtSimpleType.class);

		// manage anonymous class
		while (parent.getSimpleName().isEmpty()) {
			parent = parent.getParent(CtSimpleType.class);
		}

		String parentClass = parent.getQualifiedName();
		CtSimpleType<?> tmp = parent;
		// manage internal class
		while (!(tmp.getParent() instanceof CtPackage)) {
			tmp = tmp.getParent(CtSimpleType.class);
		}
		CtPackage parentPackage = tmp.getPackage();

		SourcePosition elementPosition = element.getPosition();

		PackageNode parentNode = new PackageNode(
				parentPackage.getQualifiedName(), elementPosition.getLine(),
				parentClass, isExternal(parent));

		List<CtTypeReference<?>> listDependencies = getDependencies(element);

		for (CtTypeReference<?> ctTypeReference : new ArrayList<CtTypeReference<?>>(
				listDependencies)) {
			listDependencies.addAll(getDependencies(ctTypeReference));
		}

		for (CtTypeReference<?> ctTypeReference : listDependencies) {
			/*try {
				List<DependencyDeclaration> dependenciesElement = getDependenciesOfType(ctTypeReference
						.getActualClass());
				getDependencyGraph().addDependencyNode(parentNode,
						dependenciesElement);
			} catch (NoClassDefFoundError e) {
				// the declaration of the super class is not found
				// ignore it
			}*/

			CtPackageReference typePackage = ctTypeReference.getPackage();

			if (typePackage == null) {
				continue;
			}

			PackageNode node = new PackageNode(typePackage.getSimpleName(),
					elementPosition.getLine(), parentClass,
					isExternal(ctTypeReference));
			getDependencyGraph().addDependencyNode(parentNode, node);
		}

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