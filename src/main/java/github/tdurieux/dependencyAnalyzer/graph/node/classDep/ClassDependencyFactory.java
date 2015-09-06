package github.tdurieux.dependencyAnalyzer.graph.node.classDep;

import github.tdurieux.dependencyAnalyzer.graph.node.AbstractNodeFactory;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode.Type;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNodeImpl;
import spoon.SpoonException;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;

/**
 * is a factory class used to create class dependency node at class level.
 *
 * @author Thomas Durieux
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

        if (element.getDeclaration() != null) {
            CtType<?> declarationType = element.getDeclaration();
            isInternal = !declarationType.isTopLevel();
            isAbstract = declarationType.getModifiers().contains(
                                                                        ModifierKind.ABSTRACT);

            if (declarationType instanceof CtEnum) {
                type = Type.ENUM;
            } else if (declarationType instanceof CtAnnotationType<?>) {
                type = Type.ANNOTATION;
            } else if (declarationType instanceof CtInterface<?>) {
                type = Type.INTERFACE;
            }
        } else {

            try {
                Class<?> elementClass = element.getActualClass();
                if (elementClass != null) {
                    if (elementClass.isEnum()) {
                        type = Type.ENUM;
                    } else if (elementClass.isAnnotation()) {
                        type = Type.ANNOTATION;
                    } else if (elementClass.isInterface()) {
                        type = Type.INTERFACE;
                    }
                    isInternal = elementClass.isMemberClass();
                    isPrimitive = elementClass.isPrimitive();
                }
            } catch (SpoonException | NoClassDefFoundError e) {
                // class cannot be loaded
            }
        }

        return new DependencyNodeImpl(
                                             element.getQualifiedName(), element.getSimpleName(), type,
                                             isExternal, isInternal, isAbstract, isAnonymous, isPrimitive);
    }

}
