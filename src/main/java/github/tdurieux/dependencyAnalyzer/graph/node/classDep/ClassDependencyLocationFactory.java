package github.tdurieux.dependencyAnalyzer.graph.node.classDep;

import github.tdurieux.dependencyAnalyzer.graph.node.AbstractDependencyLocationFactory;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyLocation;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyLocationImpl;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode.Type;
import spoon.SpoonException;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;

/**
 * is a factory class used to create DependencyLocation at class level
 * 
 * @author Thomas Durieux
 * 
 */
public class ClassDependencyLocationFactory extends
		AbstractDependencyLocationFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see LocationFactory#createDependencyLocation()
	 */
	@Override
	public DependencyLocation createDependencyLocation(CtTypedElement<?> element) {
		// get the class where the element is used
		CtType<?> parent = element.getParent(CtType.class);

		boolean isExternal = false;
		boolean isInternal = !parent.isTopLevel();
		boolean isAbstract = parent.getModifiers().contains(
				ModifierKind.ABSTRACT);
		boolean isAnonymous = false;
		boolean isPrimitive = false;

		Type type = Type.CLASS;

		try {
			CtTypeReference<?> reference = parent.getReference();
			if (reference != null) {
				if (reference.isPrimitive()) {
					type = Type.PRIMITIVE;
				} else if (reference.isInterface()) {
					type = Type.INTERFACE;
				}
				isAnonymous = reference.isAnonymous();
				isPrimitive = reference.isPrimitive();
			}

			Class<?> elementClass = parent.getActualClass();
			if (elementClass != null) {
				if (elementClass.isEnum()) {
					type = Type.ENUM;
				} else if (elementClass.isAnnotation()) {
					type = Type.ANNOTATION;
				}
				isInternal = elementClass.isMemberClass();
				isPrimitive = elementClass.isPrimitive();
			}
		} catch (SpoonException | NoClassDefFoundError e) {
			// class cannot be loaded
			type = Type.CLASS;
		}

        if(isAnonymous) {
            isInternal = true;
        }
		
		SourcePosition elementPosition = element.getPosition();
        //isExternal = elementPosition == null;
        DependencyLocation location;
        if(elementPosition != null) {
            location = new DependencyLocationImpl(
                parent.getQualifiedName(), parent.getSimpleName(), type,
                isExternal, isInternal, isAbstract, isAnonymous, isPrimitive,
                elementPosition.getFile().getAbsolutePath(),
                elementPosition.getLine());
        } else {
            location = new DependencyLocationImpl(
                parent.getQualifiedName(), parent.getSimpleName(), type,
                isExternal, isInternal, isAbstract, isAnonymous, isPrimitive,
                null,
                -1);
        }


		return location;
	}

}
