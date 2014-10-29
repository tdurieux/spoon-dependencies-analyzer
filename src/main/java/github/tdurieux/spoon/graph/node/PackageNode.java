package github.tdurieux.spoon.graph.node;

/**
 * 
 * @author Thomas Durieux
 *
 */
public class PackageNode extends DependencyDeclaration {

	public PackageNode(String name, int line, String c, boolean isExternal) {
		super(name, line, c, isExternal);
	}
	
	public PackageNode(Package pack, int line, String c, boolean isExternal) {
		super(pack.getName(), line, c, isExternal);
	}
}
