package github.tdurieux.dependencyAnalyzer.graph.node;

/**
 * is representation of a dependency in the project
 * 
 * @author Thomas Durieux
 * 
 */
public interface DependencyNode extends Comparable<DependencyNode> {

	/**
	 * The dependency type
	 * 
	 * @author Thomas Durieux
	 * 
	 */
	enum Type {
		CLASS, PACKAGE, INTERFACE, ENUM, METHOD, PRIMITIVE, ANNOTATION
	}

	/**
	 * Get the name of the node
	 * 
	 * @return the simple name
	 */
	String getSimpleName();

	/**
	 * Get the qualified name of the element
	 * 
	 * @return the qualified name
	 */
	String getQualifiedName();

	/**
	 * Get the type of the element
	 * 
	 * @return the element type
	 */
	Type getType();

	/**
	 * returns true if the dependency is an external dependency
	 * 
	 * @return true if the dependency is an external dependency
	 */
	boolean isExternal();

	/**
	 * returns true if the dependency is an internal element
	 * 
	 * @return true if the dependency is an internal element
	 */
	boolean isInternal();

	/**
	 * returns true if the dependency is an abstract class
	 * 
	 * @return true if the dependency is an abstract class
	 */
	boolean isAbstract();

	/**
	 * returns true if the dependency is an anonymous class
	 * 
	 * @return true if the dependency is an anonymous class
	 */
	boolean isAnonymous();

}
