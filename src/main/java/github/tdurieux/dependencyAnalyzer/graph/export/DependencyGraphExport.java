package github.tdurieux.dependencyAnalyzer.graph.export;

/**
 * the general interface for dependencyGraph
 *
 * @author Thomas Durieux
 */
public interface DependencyGraphExport {

    /**
     * Generate a representation of the dependency graph
     *
     * @return the representation of the dependency graph
     */
    String generate();
}
