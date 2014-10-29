package github.tdurieux.spoon.graph.node;

public class ClassNode extends DependencyDeclaration {

	public ClassNode(String name, int line, String c, boolean isExternal) {
		super(name, line, c, isExternal);
	}

	public ClassNode(String name, int line, boolean isExternal) {
		super(name, line, name, isExternal);
	}

	public ClassNode(Class<?> c, int line, boolean isExternal) {
		super(c.getCanonicalName(), line, c.getCanonicalName(), isExternal);
	}

}
