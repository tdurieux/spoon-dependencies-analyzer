package github.tdurieux.dependencyAnalyzer.graph.node.packageDep;

import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtTypedElement;
import github.tdurieux.dependencyAnalyzer.graph.node.AbstractDependencyLocationFactory;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyLocation;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyLocationImpl;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode;

/**
 * is a factory class used to create DependencyLocation at package level
 * 
 * @author Thomas Durieux
 * 
 */
public class PackageDependencyLocationFactory extends
		AbstractDependencyLocationFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see LocationFactory#createDependencyLocation()
	 */
	@Override
	public DependencyLocation createDependencyLocation(CtTypedElement<?> element) {
		// get the class where the element is used
		CtSimpleType<?> parent = element.getParent(CtSimpleType.class);

		// manage anonymous class
		while (parent.getSimpleName().isEmpty()) {
			parent = parent.getParent(CtSimpleType.class);
		}

		CtSimpleType<?> tmp = parent;
		// manage internal class
		while (!(tmp.getParent() instanceof CtPackage)) {
			tmp = tmp.getParent(CtSimpleType.class);
		}
		CtPackage elementPackage = tmp.getPackage();

		SourcePosition elementPosition = element.getPosition();

		boolean isExternal = false;
		boolean isInternal = false;
		boolean isAbstract = false;
		boolean isAnonymous = false;
		boolean isPrimitive = false;

		DependencyLocation location = new DependencyLocationImpl(
				elementPackage.getQualifiedName(),
				elementPackage.getSimpleName(), DependencyNode.Type.PACKAGE,
				isExternal, isInternal, isAbstract, isAnonymous, isPrimitive,
				elementPosition.getFile().getAbsolutePath(),
				elementPosition.getLine());

		return location;
	}

}
