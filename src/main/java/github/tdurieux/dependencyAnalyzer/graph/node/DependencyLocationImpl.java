package github.tdurieux.dependencyAnalyzer.graph.node;

/**
 * represents the location where a dependency is used
 * 
 * @author Thomas Durieux
 * 
 */
public class DependencyLocationImpl extends DependencyNodeImpl implements
		DependencyLocation {

	private final String file;

	private final int line;

	public DependencyLocationImpl(String qualifiedName, String simpleName,
			Type type, boolean isExternal, boolean isInternal,
			boolean isAbstract, boolean isAnonymous, boolean isPrimitive,
			String file, int line) {

		super(qualifiedName, simpleName, type, isExternal, isInternal,
				isAbstract, isAnonymous, isPrimitive);
		this.file = file;
		this.line = line;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * github.tdurieux.dependencies.graph.node.DependencyLocationI#getFile()
	 */
	@Override
	public String getFile() {
		return file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * github.tdurieux.dependencies.graph.node.DependencyLocationI#getLine()
	 */
	@Override
	public int getLine() {
		return line;
	}

	@Override
	public String toString() {
		return this.getQualifiedName() + " at " + this.file + ":" + this.line;
	}
}
