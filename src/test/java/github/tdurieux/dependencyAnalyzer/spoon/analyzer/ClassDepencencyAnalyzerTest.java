package github.tdurieux.dependencyAnalyzer.spoon.analyzer;

import github.tdurieux.dependencyAnalyzer.AnalyzerConfig;
import github.tdurieux.dependencyAnalyzer.AnalyzerConfig.Level;
import github.tdurieux.dependencyAnalyzer.DependencyAnalyzer;
import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyLocation;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNodeImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ClassDepencencyAnalyzerTest {

    private github.tdurieux.dependencyAnalyzer.DependencyAnalyzer dependencyAnalyzer;
    private DependencyGraph graph;

    public ClassDepencencyAnalyzerTest() throws Exception {
        AnalyzerConfig config = new AnalyzerConfig();
        config.setLevel(Level.CLASS);

        this.dependencyAnalyzer = new DependencyAnalyzer(
                                                                "src/testProject/java", config);

        this.graph = dependencyAnalyzer.run();
    }

    @Test
    public void detectsThrowExceptionTest() {
        Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> nodes = graph
                                                                                           .getUsedByNodes();

        ArrayList<DependencyNode> keys = new ArrayList<DependencyNode>(nodes.keySet());

        assertTrue(keys.contains(createFakeDependencyNode("java.lang.RuntimeException")));
        DependencyNode object = keys.get(keys.indexOf(createFakeDependencyNode("java.lang.RuntimeException")));
        assertEquals(DependencyNode.Type.CLASS, object.getType());
        assertTrue(object.isExternal());
    }

    @Test
    public void detectsThrowsExceptionTest() {
        Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> nodes = graph
                                                                                           .getUsedByNodes();

        ArrayList<DependencyNode> keys = new ArrayList<DependencyNode>(nodes.keySet());

        assertTrue(keys.contains(createFakeDependencyNode("java.lang.NullPointerException")));
        DependencyNode object = keys.get(keys.indexOf(createFakeDependencyNode("java.lang.NullPointerException")));
        assertEquals(DependencyNode.Type.CLASS, object.getType());
        assertTrue(object.isExternal());
    }

    @Test
    public void detectsInterfaceTest() {
        Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> nodes = graph
                                                                                           .getUsedByNodes();

        ArrayList<DependencyNode> keys = new ArrayList<>(nodes.keySet());

        assertTrue(keys.contains(createFakeDependencyNode("java.io.Serializable")));
        DependencyNode object = keys.get(keys.indexOf(createFakeDependencyNode("java.io.Serializable")));
        assertEquals(DependencyNode.Type.INTERFACE, object.getType());
        assertTrue(object.isExternal());
    }

    @Test
    public void detectsMethodParamtersOnCallTest() {
        Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> nodes = graph
                                                                                           .getUsedByNodes();

        ArrayList<DependencyNode> keys = new ArrayList<DependencyNode>(nodes.keySet());

        assertTrue(keys.contains(createFakeDependencyNode("java.lang.Runnable")));
        DependencyNode object = keys.get(keys.indexOf(createFakeDependencyNode("java.lang.Runnable")));
        assertEquals(DependencyNode.Type.INTERFACE, object.getType());
        assertTrue(object.isExternal());
    }

    @Test
    public void detectsMethodReturnsTest() {
        Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> nodes = graph
                                                                                           .getUsedByNodes();

        ArrayList<DependencyNode> keys = new ArrayList<DependencyNode>(nodes.keySet());

        assertTrue(keys.contains(createFakeDependencyNode("java.lang.Thread")));
        DependencyNode object = keys.get(keys.indexOf(createFakeDependencyNode("java.lang.Thread")));
        assertEquals(DependencyNode.Type.CLASS, object.getType());
        assertTrue(object.isExternal());
    }

    @Test
    public void detectsDeclaredMethodReturnsTest() {
        Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> nodes = graph
                                                                                           .getUsedByNodes();

        ArrayList<DependencyNode> keys = new ArrayList<DependencyNode>(nodes.keySet());

        assertTrue(keys.contains(createFakeDependencyNode("java.lang.Boolean")));
        DependencyNode object = keys.get(keys.indexOf(createFakeDependencyNode("java.lang.Boolean")));
        assertEquals(DependencyNode.Type.CLASS, object.getType());
        assertTrue(object.isExternal());
    }

    @Test
    public void detectsAnnotationTest() {
        Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> nodes = graph
                                                                                           .getUsedByNodes();

        ArrayList<DependencyNode> keys = new ArrayList<DependencyNode>(nodes.keySet());

        assertTrue(keys.contains(createFakeDependencyNode("java.lang.Override")));
        DependencyNode object = keys.get(keys.indexOf(createFakeDependencyNode("java.lang.Override")));
        assertEquals(DependencyNode.Type.ANNOTATION, object.getType());
        assertTrue(object.isExternal());
    }

    @Test
    public void detectsEnumTest() {
        Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> nodes = graph
                                                                                           .getUsedByNodes();

        ArrayList<DependencyNode> keys = new ArrayList<DependencyNode>(nodes.keySet());

        assertTrue(keys.contains(createFakeDependencyNode("github.tdurieux.testProject.entity.User$Type")));
        DependencyNode object = keys.get(keys.indexOf(createFakeDependencyNode("github.tdurieux.testProject.entity.User$Type")));
        assertEquals(DependencyNode.Type.ENUM, object.getType());
        assertFalse(object.isExternal());
    }

    @Test
    public void detectsConstantTest() {
        Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> nodes = graph
                                                                                           .getUsedByNodes();

        ArrayList<DependencyNode> keys = new ArrayList<DependencyNode>(nodes.keySet());

        assertTrue(keys.contains(createFakeDependencyNode("java.lang.Integer")));
        DependencyNode object = keys.get(keys.indexOf(createFakeDependencyNode("java.lang.Integer")));
        assertEquals(DependencyNode.Type.CLASS, object.getType());
        assertTrue(object.isExternal());
    }

    @Test
    public void detectsSupperClassTest() {
        Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> nodes = graph
                                                                                           .getUsedByNodes();

        ArrayList<DependencyNode> keys = new ArrayList<DependencyNode>(nodes.keySet());

        DependencyNode fakeUserDep = createFakeDependencyNode("github.tdurieux.testProject.entity.User");
        DependencyNode fakeAdministratorDep = createFakeDependencyNode("github.tdurieux.testProject.entity.Administrator");
        assertTrue(keys.contains(fakeUserDep));
        DependencyNode object = keys.get(keys.indexOf(fakeUserDep));
        assertEquals(DependencyNode.Type.CLASS, object.getType());
        assertFalse(object.isExternal());

        assertTrue(keys.contains(fakeAdministratorDep));
        object = keys.get(keys.indexOf(fakeAdministratorDep));
        assertEquals(DependencyNode.Type.CLASS, object.getType());
        assertFalse(object.isExternal());

        graph.getUsedByNodes().get(fakeUserDep).containsKey(fakeAdministratorDep);
    }

    @Test
    public void DependencyGraphAtLevelClassTest() {
        Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> nodes = graph
                                                                                           .getUsedByNodes();

        ArrayList<DependencyNode> keys = new ArrayList<DependencyNode>(nodes.keySet());

        assertTrue(keys.contains(createFakeDependencyNode("java.lang.String")));
        DependencyNode object = keys.get(keys.indexOf(createFakeDependencyNode("java.lang.String")));
        assertEquals(DependencyNode.Type.CLASS, object.getType());
        assertTrue(object.isExternal());

        assertTrue(keys.contains(createFakeDependencyNode("github.tdurieux.testProject.entity.User")));
        object = keys.get(keys.indexOf(createFakeDependencyNode("github.tdurieux.testProject.entity.User")));
        assertEquals(DependencyNode.Type.CLASS, object.getType());
        assertFalse(object.isExternal());
    }

    private DependencyNode createFakeDependencyNode(String qualifiedName) {
        boolean isExternal = false;
        boolean isInternal = false;
        boolean isAbstract = false;
        boolean isAnonymous = false;
        boolean isPrimitive = false;

        return new DependencyNodeImpl(qualifiedName, qualifiedName,
                                             DependencyNode.Type.CLASS, isExternal, isInternal,
                                             isAbstract, isAnonymous, isPrimitive);
    }
}
