package github.tdurieux.dependencyAnalyzer.graph.node;

import spoon.reflect.declaration.CtTypedElement;

/**
 * is an interface which describes location factories
 *
 * @author Thomas Durieux
 */
public interface LocationFactory {

    /**
     * creates dependency location from a CtTypedElement
     *
     * @param element the element
     * @return a DependencyLocation
     */
    DependencyLocation createDependencyLocation(CtTypedElement<?> element);
}
