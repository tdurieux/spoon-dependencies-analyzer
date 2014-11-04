package github.tdurieux.dependencyAnalyzer.graph.node.classDep;

import spoon.SpoonException;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import github.tdurieux.dependencyAnalyzer.graph.node.AbstractNodeFactory;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNodeImpl;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode.Type;

/**
 * is a factory class used to create class dependency node at class level.
 * 
 * @author Thomas Durieux
 * 
 */
public class ClassDependencyFactory extends AbstractNodeFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see NodeFactory#createDependencyNode()
	 */
	@Override
	public DependencyNode createDependencyNode(CtTypeReference<?> element) {

		if (element == null) {
			return null;
		}
		
		boolean isExternal = isExternal(element);
		boolean isInternal = false;
		boolean isAbstract = false;
		boolean isAnonymous = element.isAnonymous();
		boolean isPrimitive = element.isPrimitive();
		
		
		Type type = Type.CLASS;

		if (element.isPrimitive()) {
			type = Type.PRIMITIVE;
		}
		if (element.isInterface()) {
			type = Type.INTERFACE;
		}

		if (element.getDeclaration() != null) {
			CtSimpleType<?> declarationType = element.getDeclaration();
			isInternal = !declarationType.isTopLevel();
			isAbstract = declarationType.getModifiers().contains(
					ModifierKind.ABSTRACT);

			if (declarationType instanceof CtEnum) {
				type = Type.ENUM;
			} else if (declarationType instanceof CtAnnotationType<?>) {
				type = Type.ANNOTATION;
			}
		} else {

			try {
				Class<?> elementClass = element.getActualClass();
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
			}
		}
		
		DependencyNode dependency = new DependencyNodeImpl(
				element.getQualifiedName(), element.getSimpleName(), type,
				isExternal, isInternal, isAbstract, isAnonymous, isPrimitive);

		return dependency;
	}

}
