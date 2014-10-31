package github.tdurieux.dependencyAnalyzer.graph.node.classDep;

import spoon.SpoonException;
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
		
		if (element.getDeclaration() != null) {
			isInternal = !element.getDeclaration().isTopLevel();
			isAbstract = element.getDeclaration().getModifiers()
					.contains(ModifierKind.ABSTRACT);
		}

		Type type = Type.CLASS;

		try {
			if (element.isPrimitive()) {
				type = Type.PRIMITIVE;
			} else if (element.getActualClass() != null && element.isInterface()) {
				type = Type.INTERFACE;
			}
			Class<?> elementClass = element.getActualClass();
			if(elementClass != null) {
				if(elementClass.isEnum()) {
					type = Type.ENUM;
				} else if(elementClass.isAnnotation()) {
					type = Type.ANNOTATION;
				}
				isInternal = elementClass.isMemberClass();
				isPrimitive = elementClass.isPrimitive();
			}
		} catch (SpoonException | NoClassDefFoundError e) {
			// class cannot be loaded
			type = Type.CLASS;
		}

		DependencyNode dependency = new DependencyNodeImpl(
				element.getQualifiedName(), element.getSimpleName(), type,
				isExternal, isInternal, isAbstract, isAnonymous, isPrimitive);

		return dependency;
	}

}
