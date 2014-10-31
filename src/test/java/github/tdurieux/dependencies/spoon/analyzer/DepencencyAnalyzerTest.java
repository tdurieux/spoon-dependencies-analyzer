package github.tdurieux.dependencies.spoon.analyzer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import github.tdurieux.dependencyAnalyzer.AnalyzerConfig;
import github.tdurieux.dependencyAnalyzer.AnalyzerConfig.Level;
import github.tdurieux.dependencyAnalyzer.DependencyAnalizer;
import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyLocation;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNodeImpl;

import org.junit.Test;

public class DepencencyAnalyzerTest {
	@Test
	public void DependencyGraphAtLevelPackageTest() throws Exception {
		AnalyzerConfig config = new AnalyzerConfig();
		config.setLevel(Level.PACKAGE);

		DependencyAnalizer dependencyAnalizer = new DependencyAnalizer(
				"src/testProject/java", config);

		DependencyGraph graph = dependencyAnalizer.run();

		Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> nodes = graph
				.getUsedByNodes();

		assertTrue(nodes.keySet().contains(createFakeDependencyNode("java.io")));
		assertTrue(nodes.keySet().contains(createFakeDependencyNode("java.lang")));
		assertTrue(nodes.keySet().contains(createFakeDependencyNode("github.tdurieux.testProject.entity")));
	}
	
	
	@Test
	public void DependencyGraphAtLevelClassTest() throws Exception {
		AnalyzerConfig config = new AnalyzerConfig();
		config.setLevel(Level.CLASS);

		DependencyAnalizer dependencyAnalizer = new DependencyAnalizer(
				"src/testProject/java", config);

		DependencyGraph graph = dependencyAnalizer.run();
		
		Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> nodes = graph
				.getUsedByNodes();
		
		ArrayList<DependencyNode> keys = new ArrayList<DependencyNode>(nodes.keySet());

		assertTrue(keys.contains(createFakeDependencyNode("java.lang.RuntimeException")));
		DependencyNode object = keys.get(keys.indexOf(createFakeDependencyNode("java.lang.RuntimeException")));
		assertEquals(DependencyNode.Type.CLASS, object.getType());
		assertTrue(object.isExternal());
		
		assertTrue(keys.contains(createFakeDependencyNode("java.lang.Exception")));
		object = keys.get(keys.indexOf(createFakeDependencyNode("java.lang.Exception")));
		assertEquals(DependencyNode.Type.CLASS, object.getType());
		assertTrue(object.isExternal());
		
		assertTrue(keys.contains(createFakeDependencyNode("java.io.Serializable")));
		object = keys.get(keys.indexOf(createFakeDependencyNode("java.io.Serializable")));
		assertEquals(DependencyNode.Type.INTERFACE, object.getType());
		assertTrue(object.isExternal());
		
		assertTrue(keys.contains(createFakeDependencyNode("java.lang.Runnable")));
		object = keys.get(keys.indexOf(createFakeDependencyNode("java.lang.Runnable")));
		assertEquals(DependencyNode.Type.INTERFACE, object.getType());
		assertTrue(object.isExternal());
		
		assertTrue(keys.contains(createFakeDependencyNode("java.lang.Thread")));
		object = keys.get(keys.indexOf(createFakeDependencyNode("java.lang.Thread")));
		assertEquals(DependencyNode.Type.CLASS, object.getType());
		assertTrue(object.isExternal());
		
		assertTrue(keys.contains(createFakeDependencyNode("java.lang.String")));
		object = keys.get(keys.indexOf(createFakeDependencyNode("java.lang.String")));
		assertEquals(DependencyNode.Type.CLASS, object.getType());
		assertTrue(object.isExternal());
		
		assertTrue(keys.contains(createFakeDependencyNode("java.lang.Override")));
		object = keys.get(keys.indexOf(createFakeDependencyNode("java.lang.Override")));
		assertEquals(DependencyNode.Type.ANNOTATION, object.getType());
		assertTrue(object.isExternal());
		
		assertTrue(keys.contains(createFakeDependencyNode("github.tdurieux.testProject.entity.User")));
		object = keys.get(keys.indexOf(createFakeDependencyNode("github.tdurieux.testProject.entity.User")));
		assertEquals(DependencyNode.Type.CLASS, object.getType());
		assertFalse(object.isExternal());
		
		assertTrue(keys.contains(createFakeDependencyNode("github.tdurieux.testProject.entity.User$Type")));
		object = keys.get(keys.indexOf(createFakeDependencyNode("github.tdurieux.testProject.entity.User$Type")));
		assertEquals(DependencyNode.Type.ENUM, object.getType());
		assertFalse(object.isExternal());
		
		assertTrue(keys.contains(createFakeDependencyNode("java.lang.Integer")));
		object = keys.get(keys.indexOf(createFakeDependencyNode("java.lang.Integer")));
		assertEquals(DependencyNode.Type.CLASS, object.getType());
		assertTrue(object.isExternal());
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
