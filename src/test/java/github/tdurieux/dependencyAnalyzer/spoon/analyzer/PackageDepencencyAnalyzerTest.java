package github.tdurieux.dependencyAnalyzer.spoon.analyzer;

import static org.junit.Assert.*;

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

public class PackageDepencencyAnalyzerTest {

	private DependencyAnalizer dependencyAnalizer;
	private DependencyGraph graph;

	public PackageDepencencyAnalyzerTest() throws Exception {
		AnalyzerConfig config = new AnalyzerConfig();
		config.setLevel(Level.PACKAGE);

		this.dependencyAnalizer = new DependencyAnalizer(
				"src/testProject/java", config);

		this.graph = dependencyAnalizer.run();
	}

	@Test
	public void DependencyGraphAtLevelPackageTest() {

		Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> nodes = graph
				.getUsedByNodes();

		DependencyNode fakeEntity = createFakeDependencyNode("github.tdurieux.testProject.entity");
		DependencyNode fakeIo = createFakeDependencyNode("java.io");
		DependencyNode fakeLang = createFakeDependencyNode("java.lang");

		assertTrue(nodes.keySet().contains(fakeIo));
		assertTrue(nodes.keySet().contains(fakeLang));
		assertTrue(nodes.keySet().contains(fakeEntity));
		
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
