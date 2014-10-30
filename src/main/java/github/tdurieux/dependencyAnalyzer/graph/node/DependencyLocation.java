package github.tdurieux.dependencyAnalyzer.graph.node;

/**
 * represents the location where a dependency is used
 * 
 * @author Thomas Durieux
 * 
 */
public interface DependencyLocation extends DependencyNode {

	/**
	 * the file where the dependency is used
	 * 
	 * @return the file
	 */
	public String getFile();

	/**
	 * the line number where the dependency is used
	 * 
	 * @return the line number
	 */
	public int getLine();

}