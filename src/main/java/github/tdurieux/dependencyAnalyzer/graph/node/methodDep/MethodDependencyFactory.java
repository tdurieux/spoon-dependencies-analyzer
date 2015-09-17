package github.tdurieux.dependencyAnalyzer.graph.node.methodDep;

import github.tdurieux.dependencyAnalyzer.graph.node.AbstractNodeFactory;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode.Type;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNodeImpl;
import spoon.SpoonException;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

/**
 * is a factory class used to create class dependency node at method level.
 *
 * @author Thomas Durieux
 */
public class MethodDependencyFactory extends AbstractNodeFactory {

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

    @Override
    public DependencyNode createDependencyNode(CtExecutableReference<?> element) {
        if (element == null) {
            return null;
        }

        boolean isExternal = isExternal(element);
        boolean isInternal = true;
        boolean isAbstract = false;
        if (element instanceof CtMethod) {
            isAbstract = ((CtMethod) element).getModifiers().contains(
                    ModifierKind.ABSTRACT);
        }
        boolean isAnonymous = false;
        boolean isPrimitive = false;


        Type type = Type.METHOD;
        if(element.isConstructor()) {
            type = Type.CONSTRUCTOR;
        }
        return new DependencyNodeImpl(
                getSignature(element), getMethodSignature(element), type,
                isExternal, isInternal, isAbstract, isAnonymous, isPrimitive);
    }

    private String getSignature(CtExecutableReference ctExecutable) {
        try {
            String output = ctExecutable.getDeclaringType().getQualifiedName();
            output += "::" + getMethodSignature(ctExecutable);
            return output;
        } catch (NullPointerException e) {
            return ctExecutable.getSimpleName() + "::" + getMethodSignature(ctExecutable);
        }
    }

    private String getMethodSignature(CtExecutableReference ctExecutable) {
        String output = ctExecutable.getSimpleName();
        if (output.equals("<init>")) {
            output = ctExecutable.getDeclaringType().getSimpleName();
        }
        output += "(";
        List<CtTypeReference> parameters = ctExecutable.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            CtTypeReference ctParameter = parameters.get(i);
            output += ctParameter.getQualifiedName();
            if (i < parameters.size() - 1) {
                output += ", ";
            }
        }
        return output + ")";
    }

}
