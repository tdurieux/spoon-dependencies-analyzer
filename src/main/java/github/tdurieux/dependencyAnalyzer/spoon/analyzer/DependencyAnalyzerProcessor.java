package github.tdurieux.dependencyAnalyzer.spoon.analyzer;

import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyLocation;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode;
import github.tdurieux.dependencyAnalyzer.graph.node.LocationFactory;
import github.tdurieux.dependencyAnalyzer.graph.node.NodeFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Spoon processor used to analyze project dependencies.
 *
 * @author Thomas Durieux
 */
public class DependencyAnalyzerProcessor extends AbstractProcessor<CtTypedElement<?>> {

    private final DependencyGraph dependencyGraph;
    private final NodeFactory nodeFactory;
    private final LocationFactory locationFactory;

    public DependencyAnalyzerProcessor(DependencyGraph dependencyGraph,
                                       NodeFactory nodeFactory, LocationFactory locationFactory) {
        this.dependencyGraph = dependencyGraph;
        this.nodeFactory = nodeFactory;
        this.locationFactory = locationFactory;
    }

    public DependencyAnalyzerProcessor(NodeFactory nodeFactory,
                                       LocationFactory locationFactory) {
        this(new DependencyGraph(), nodeFactory, locationFactory);
    }

    @Override
    public void process(CtTypedElement<?> element) {
        DependencyLocation dependencyLocation = locationFactory
                                                        .createDependencyLocation(element);

        Set<CtTypeReference<?>> listDependencies = getDependencies(element);

        for (CtTypeReference<?> ctTypeReference : new HashSet<>(listDependencies)) {
            listDependencies.addAll(getDependencies(ctTypeReference));
        }

        for (CtTypeReference<?> ctTypeReference : listDependencies) {
            if (ctTypeReference == null) {
                continue;
            }
            DependencyNode node = nodeFactory
                                          .createDependencyNode(ctTypeReference);
            getDependencyGraph().addDependencyNode(node, dependencyLocation);
        }

    }

    /**
     * get all dependencies added by a CtTypeReference
     *
     * @param ctTypeReference
     * @return all dependencies added by ctTypeReference
     */
    protected Set<CtTypeReference<?>> getDependencies(
                                                             CtTypeReference<?> ctTypeReference) {

        Set<CtTypeReference<?>> listDependencies = new HashSet<>();

        if (ctTypeReference == null) {
            return listDependencies;
        }

        CtType<?> ctType = ctTypeReference.getDeclaration();
        if (ctType == null) {
            return listDependencies;
        }

        if (ctType instanceof CtClass<?>) {
            CtClass<?> tmp = (CtClass<?>) ctType;
            // super class
            CtTypeReference<?> superclass = tmp.getSuperclass();
            if (superclass != null) {
                listDependencies.add(superclass);
                listDependencies.addAll(getDependencies(superclass));
            }
        }
        // interfaces
        Set<CtTypeReference<?>> interfaces = ctType.getSuperInterfaces();
        for (CtTypeReference<?> interfaceType : interfaces) {
            listDependencies.add(interfaceType);
            listDependencies.addAll(getDependencies(interfaceType));
        }

        return listDependencies;
    }

    /**
     * get all dependencies added by a CtTypedElement
     *
     * @param element
     * @return all dependencies added by element
     */
    private Set<CtTypeReference<?>> getDependencies(CtTypedElement<?> element) {
        Set<CtTypeReference<?>> listDependencies = new HashSet<>();

        // annotations
        List<CtAnnotation<?>> annotations = element.getAnnotations();
        for (CtAnnotation<?> ctAnnotation : annotations) {
            listDependencies.add(ctAnnotation.getAnnotationType());

            // annotations elements
            Map<String, Object> elements = ctAnnotation.getElementValues();
            for (Object elementValue : elements.values()) {
                if (elementValue instanceof CtTypeReference<?>) {
                    listDependencies.add((CtTypeReference<?>) elementValue);
                } else if (elementValue instanceof CtTypedElement<?>) {
                    listDependencies.add(((CtTypedElement<?>) elementValue)
                                                 .getType());
                }
            }
        }

        // Literal
        if (element instanceof CtLiteral<?>) {
            CtLiteral<?> literal = (CtLiteral<?>) element;
            literal.getValue();

            if (literal.getValue() instanceof CtTypeReference<?>) {
                listDependencies.add((CtTypeReference<?>) literal.getValue());
            } else if (literal.getValue() instanceof CtTypedElement<?>) {
                listDependencies.add(((CtTypedElement<?>) literal.getValue())
                                             .getType());
            }
        }

        // method invocation
        if (element instanceof CtInvocation<?>) {
            CtInvocation<?> invocation = (CtInvocation<?>) element;

            // the class of the method
            listDependencies.add(invocation.getExecutable().getDeclaringType());

            // parameters
            List<CtTypeReference<?>> parameters = invocation.getExecutable()
                                                          .getActualTypeArguments();
            listDependencies.addAll(parameters);
        }

        // method declaration
        if (element instanceof CtMethod<?>) {
            CtMethod<?> method = (CtMethod<?>) element;
            // exceptions
            Set<CtTypeReference<? extends Throwable>> thrownTypes = method
                                                                            .getThrownTypes();
            for (CtTypeReference<? extends Throwable> ctTypeReference : thrownTypes) {
                listDependencies.add(ctTypeReference);
            }
        }

        listDependencies.add(element.getType());
        return listDependencies;
    }

    public DependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }
}