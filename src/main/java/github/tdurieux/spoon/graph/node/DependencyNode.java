package github.tdurieux.spoon.graph.node;

import java.util.List;

/**
 * 
 * @author thomas
 *
 */
public interface DependencyNode extends Comparable<DependencyNode> {

	/**
	 * Get the name of the node
	 * 
	 * @return the name
	 */
	String getName();

	/**
	 * Get all dependency localizations of this dependency
	 * 
	 * @return all dependency localizations
	 */
	List<DependencyDeclaration> getDeclarationLocalization();
	
	/**
	 * 
	 * @param localization
	 */
	void addDeclarationLicalization(DependencyDeclaration localization);
	
	/**
	 * 
	 * @param localization
	 */
	void removeDeclarationLicalization(DependencyDeclaration localization);

	boolean isExternal() ;
}
