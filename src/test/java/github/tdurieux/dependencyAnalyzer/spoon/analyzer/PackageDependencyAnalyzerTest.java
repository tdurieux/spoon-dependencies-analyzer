package github.tdurieux.dependencyAnalyzer.spoon.analyzer;

import github.tdurieux.dependencyAnalyzer.AnalyzerConfig;
import github.tdurieux.dependencyAnalyzer.AnalyzerConfig.Level;
import github.tdurieux.dependencyAnalyzer.DependencyAnalyzer;
import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyLocation;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNodeImpl;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class PackageDependencyAnalyzerTest {

    private DependencyAnalyzer dependencyAnalyzer;
    private DependencyGraph graph;

    public PackageDependencyAnalyzerTest() throws Exception {
        AnalyzerConfig config = AnalyzerConfig.INSTANCE;
        config.setLevel(Level.PACKAGE);

        this.dependencyAnalyzer = new DependencyAnalyzer("src/testProject/java", config);

        this.graph = dependencyAnalyzer.run();
    }

    @Test
    public void DependencyGraphAtLevelPackageTest() {

        Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> nodes = graph
                .getUsedByNodes();

        DependencyNode fakeEntity = createFakeDependencyNode("github.tdurieux.testProject.entity");
        DependencyNode fakeIo = createFakeDependencyNode("java.io");
        DependencyNode fakeLang = createFakeDependencyNode("java.lang");

        assertTrue("Contains the dependency java.io", nodes.keySet().contains(fakeIo));
        assertTrue("Contains the dependency java.lang", nodes.keySet().contains(fakeLang));
        assertTrue("Contains the dependency testProject.entity", nodes.keySet().contains(fakeEntity));

        nodes.get(fakeIo).containsKey(fakeEntity);

        nodes.get(fakeLang).containsKey(fakeEntity);
    }

    private DependencyNode createFakeDependencyNode(String qualifiedName) {
        boolean isExternal = false;
        boolean isInternal = false;
        boolean isAbstract = false;
        boolean isAnonymous = false;
        boolean isPrimitive = false;

        return new DependencyNodeImpl(qualifiedName, qualifiedName,
                DependencyNode.Type.PACKAGE, isExternal, isInternal,
                isAbstract, isAnonymous, isPrimitive);
    }
}
