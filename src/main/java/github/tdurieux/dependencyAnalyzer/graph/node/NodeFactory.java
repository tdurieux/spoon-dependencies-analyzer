package github.tdurieux.dependencyAnalyzer.graph.node;

import spoon.reflect.reference.CtTypeReference;

/**
 * is an interface which describe node factories
 * 
 * @author Thomas Durieux
 * 
 */
public interface NodeFactory {

	/**
	 * creates of dependency node from a CtTypedElement
	 * 
	 * @param element
	 * @return a DependencyNode
	 */
	DependencyNode createDependencyNode(CtTypeReference<?> element);
}
