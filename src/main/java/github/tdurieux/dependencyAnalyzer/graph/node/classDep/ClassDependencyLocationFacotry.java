package github.tdurieux.dependencyAnalyzer.graph.node.classDep;

import spoon.SpoonException;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.ModifierKind;
import github.tdurieux.dependencyAnalyzer.graph.node.AbstractDependencyLocationFacotry;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyLocation;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyLocationImpl;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode.Type;

/**
 * is a factory class used to create DependencyLocation at class level
 * 
 * @author Thomas Durieux
 * 
 */
public class ClassDependencyLocationFacotry extends
		AbstractDependencyLocationFacotry {

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

		Type type = Type.CLASS;

		try {
			if (parent.getReference() != null && parent.getReference().isPrimitive()) {
				type = Type.PRIMITIVE;
			} else if (parent.getReference() != null && parent.getReference().isInterface()) {
				type = Type.INTERFACE;
			}
		} catch (SpoonException | NoClassDefFoundError e) {
			// class cannot be loaded
			type = Type.CLASS;
		}
		
		SourcePosition elementPosition = element.getPosition();

		boolean isExternal = false;
		boolean isInternal = parent.isTopLevel();
		boolean isAbstract = parent.getModifiers().contains(
				ModifierKind.ABSTRACT);
		boolean isAnonymous = parent.getReference().isAnonymous();
		boolean isPrimitive = parent.getReference().isPrimitive();

		DependencyLocation location = new DependencyLocationImpl(
				parent.getQualifiedName(), parent.getSimpleName(), type,
				isExternal, isInternal, isAbstract, isAnonymous, isPrimitive,
				elementPosition.getFile().getAbsolutePath(),
				elementPosition.getLine());

		return location;
	}

}
