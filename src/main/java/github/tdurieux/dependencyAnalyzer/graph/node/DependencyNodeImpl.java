package github.tdurieux.dependencyAnalyzer.graph.node;

/**
 * an implementation of the dependency node. Two dependency nodes are considered
 * as equals if the qualifiedName are identical.
 *
 * @author Thomas Durieux
 */
public class DependencyNodeImpl implements DependencyNode {

    private final String simpleName;

    private final String qualifiedName;

    private final Type type;

    private final boolean isExternal;

    private final boolean isInternal;

    private final boolean isAbstract;

    private final boolean isAnonymous;

    private final boolean isPrimitive;

    public DependencyNodeImpl(String qualifiedName, String simpleName,
                              Type type, boolean isExternal, boolean isInternal,
                              boolean isAbstract, boolean isAnonymous, boolean isPrimitive) {

        this.qualifiedName = qualifiedName;
        this.type = type;
        this.simpleName = simpleName;
        this.isExternal = isExternal;
        this.isInternal = isInternal;
        this.isAbstract = isAbstract;
        this.isAnonymous = isAnonymous;
        this.isPrimitive = isPrimitive;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * github.tdurieux.dependencies.graph.node.DependencyNode#getSimpleName()
     */
    public String getSimpleName() {
        return this.simpleName;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * github.tdurieux.dependencies.graph.node.DependencyNode#getQualifiedName()
     */
    public String getQualifiedName() {
        return qualifiedName;
    }

    /*
     * (non-Javadoc)
     *
     * @see github.tdurieux.dependencies.graph.node.DependencyNode#getType()
     */
    public Type getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     *
     * @see github.tdurieux.dependencies.graph.node.DependencyNode#isInternal()
     */
    public boolean isInternal() {
        return isInternal;
    }

    /*
     * (non-Javadoc)
     *
     * @see github.tdurieux.dependencies.graph.node.DependencyNode#isAbstract()
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /*
     * (non-Javadoc)
     *
     * @see github.tdurieux.dependencies.graph.node.DependencyNode#isAnonymous()
     */
    public boolean isAnonymous() {
        return isAnonymous;
    }

    /*
     * (non-Javadoc)
     *
     * @see github.tdurieux.dependencies.graph.node.DependencyNode#isExternal()
     */
    public boolean isExternal() {
        return this.isExternal;
    }

    /*
     * (non-Javadoc)
     *
     * @see github.tdurieux.dependencies.graph.node.DependencyNode#isPrimitive()
     */
    public boolean isPrimitive() {
        return isPrimitive;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DependencyNode)) {
            return false;
        }
        return this.getQualifiedName().equals(
                                                     ((DependencyNode) obj).getQualifiedName());
    }

    @Override
    public int hashCode() {
        return this.qualifiedName.hashCode();
    }

    @Override
    public String toString() {
        return this.qualifiedName;
    }

    @Override
    public int compareTo(DependencyNode o) {
        if (o == null) return -1;
        return this.qualifiedName.compareTo(o.getQualifiedName());
    }

}
