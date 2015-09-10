package github.tdurieux.dependencyAnalyzer.graph.node.methodDep;

import github.tdurieux.dependencyAnalyzer.graph.node.DependencyLocation;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyLocationImpl;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode.Type;
import github.tdurieux.dependencyAnalyzer.graph.node.classDep.ClassDependencyLocationFactory;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.*;

import java.util.List;

/**
 * is a factory class used to create DependencyLocation at class level
 *
 * @author Thomas Durieux
 */
public class MethodDependencyLocationFactory extends
        ClassDependencyLocationFactory {

    /*
     * (non-Javadoc)
     *
     * @see LocationFactory#createDependencyLocation()
     */
    @Override
    public DependencyLocation createDependencyLocation(CtTypedElement<?> element) {
        // get the class where the element is used
        CtExecutable<?> ctExecutable = element.getParent(CtExecutable.class);
        if (element instanceof CtAnnotation) {
            CtElement annotatedElement = ((CtAnnotation) element).getAnnotatedElement();
            if (annotatedElement instanceof CtExecutable) {
                ctExecutable = (CtExecutable<?>) annotatedElement;
            }
        }
        if (ctExecutable == null) {
            return super.createDependencyLocation(element);
        }
        boolean isExternal = false;
        boolean isInternal = true;
        boolean isAbstract = false;
        if (ctExecutable instanceof CtMethod) {
            isAbstract = ((CtMethod) ctExecutable).getModifiers().contains(
                    ModifierKind.ABSTRACT);
        }
        boolean isAnonymous = false;
        boolean isPrimitive = false;

        Type type = Type.METHOD;
        if (ctExecutable instanceof CtConstructor) {
            type = Type.CONSTRUCTOR;
        }

        SourcePosition elementPosition = element.getPosition();
        //isExternal = elementPosition == null;
        DependencyLocation location;
        if (elementPosition != null) {
            location = new DependencyLocationImpl(
                    getSignature(ctExecutable), getMethodSignature(ctExecutable), type,
                    isExternal, isInternal, isAbstract, isAnonymous, isPrimitive,
                    elementPosition.getFile().getAbsolutePath(),
                    elementPosition.getLine());
        } else {
            location = new DependencyLocationImpl(
                    getSignature(ctExecutable), getMethodSignature(ctExecutable), type,
                    isExternal, isInternal, isAbstract, isAnonymous, isPrimitive,
                    null,
                    -1);
        }


        return location;
    }

    private String getSignature(CtExecutable ctExecutable) {
        String output = "";
        CtType parent = ctExecutable.getParent(CtType.class);
        output = parent.getQualifiedName();
        output += "::" + getMethodSignature(ctExecutable);
        return output;
    }

    private String getMethodSignature(CtExecutable ctExecutable) {
        String output = ctExecutable.getSimpleName();
        if (ctExecutable instanceof CtConstructor) {
            output = ((CtConstructor) ctExecutable).getDeclaringType().getSimpleName();
        }
        output += "(";
        List<CtParameter> parameters = ctExecutable.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            CtParameter ctParameter = parameters.get(i);
            output += ctParameter.getType().getQualifiedName();
            if (i < parameters.size() - 1) {
                output += ", ";
            }
        }
        return output + ")";
    }

}
