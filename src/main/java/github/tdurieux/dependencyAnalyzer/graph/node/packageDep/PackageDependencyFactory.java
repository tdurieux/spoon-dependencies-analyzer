package github.tdurieux.dependencyAnalyzer.graph.node.packageDep;

import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;
import github.tdurieux.dependencyAnalyzer.graph.node.AbstractNodeFactory;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNodeImpl;

/**
 * is a factory class used to create class dependency node at package level.
 * 
 * @author Thomas Durieux
 * 
 */
public class PackageDependencyFactory extends AbstractNodeFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see NodeFactory#createDependencyNode()
	 */
	@Override
	public DependencyNode createDependencyNode(CtTypeReference<?> element) {
		// primitives don't have package
		if (element.isPrimitive()) {
			return null;
		}

		CtPackageReference elementPackage = element.getPackage();

		if (elementPackage == null) {
			return null;
		}

		boolean isExternal = isExternal(element);
		boolean isInternal = false;
		boolean isAbstract = false;
		boolean isAnonymous = false;
		boolean isPrimitive = false;

		DependencyNode dependency = new DependencyNodeImpl(
				elementPackage.getSimpleName(), elementPackage.getSimpleName(),
				DependencyNode.Type.PACKAGE, isExternal, isInternal,
				isAbstract, isAnonymous, isPrimitive);

		return dependency;
	}

}
