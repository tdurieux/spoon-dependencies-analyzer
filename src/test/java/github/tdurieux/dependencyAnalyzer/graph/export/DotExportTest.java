package github.tdurieux.dependencyAnalyzer.graph.export;

import static org.junit.Assert.*;

import github.tdurieux.dependencyAnalyzer.AnalyzerConfig;
import github.tdurieux.dependencyAnalyzer.AnalyzerConfig.Level;
import github.tdurieux.dependencyAnalyzer.DependencyAnalizer;
import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;

import org.junit.Test;

public class DotExportTest {

	private DependencyAnalizer dependencyAnalizer;
	private DependencyGraph graph;
	private AnalyzerConfig config;

	public DotExportTest() throws Exception {
		this.config = new AnalyzerConfig();
		config.setLevel(Level.CLASS);
		config.setOutputFormat(AnalyzerConfig.OutputFormat.DOT);

		this.dependencyAnalizer = new DependencyAnalizer(
				"src/testProject/java", config);

		this.graph = dependencyAnalizer.run();
	}

	@Test
	public void testNullGraph() {
		DependencyGraphExport export = new DotExport(null, config);
		String result = export.generate();
		assertEquals("The result must be empty", "digraph G {\n\tnode [shape=box]; compound=true; ratio=fill;\n}", result);
	}

	@Test
	public void testEmtpyGraph() {
		DependencyGraphExport export = new DotExport(new DependencyGraph(),
				config);
		String result = export.generate();
		assertEquals("The result must be empty", "digraph G {\n\tnode [shape=box]; compound=true; ratio=fill;\n}", result);
	}

	@Test
	public void testGraphExport() {
		DependencyGraphExport export = new DotExport(graph, config);
		String result = export.generate();
		String expected = "digraph G {\n"+
"	node [shape=box]; compound=true; ratio=fill;\n"+
"	\"java.lang.Runnable\" [color=grey];\n"+
"	\"java.lang.Object\" [color=grey];\n"+
"	\"java.lang.Boolean\" [color=grey];\n"+
"	\"java.lang.Override\" [color=grey];\n"+
"	\"java.lang.Thread\" [color=grey];\n"+
"	\"java.lang.Integer\" [color=grey];\n"+
"	\"java.lang.String\" [color=grey];\n"+
"	\"github.tdurieux.testProject.entity.User\" -> \"java.lang.String\";\n"+
"	\"github.tdurieux.testProject.entity.User\" -> \"github.tdurieux.testProject.entity.User$Type\";\n"+
"	\"github.tdurieux.testProject.entity.User\" -> \"java.lang.Object\";\n"+
"	\"github.tdurieux.testProject.entity.User\" -> \"java.io.Serializable\";\n"+
"	\"github.tdurieux.testProject.entity.User\" -> \"java.lang.Integer\";\n"+
"	\"github.tdurieux.testProject.entity.User\" -> \"java.lang.Boolean\";\n"+
"	\"github.tdurieux.testProject.entity.User\" -> \"java.lang.Class\";\n"+
"	\"github.tdurieux.testProject.entity.User\" -> \"java.lang.Override\";\n"+
"	\"github.tdurieux.testProject.entity.User\" -> \"java.lang.Runnable\";\n"+
"	\"github.tdurieux.testProject.entity.User\" -> \"java.lang.Thread\";\n"+
"	\"github.tdurieux.testProject.entity.User\" -> \"java.lang.NullPointerException\";\n"+
"	\"github.tdurieux.testProject.entity.User\" -> \"java.lang.RuntimeException\";\n"+
"	\"github.tdurieux.testProject.entity.User\" -> \"java.lang.Exception\";\n"+
"	\"github.tdurieux.testProject.entity.Administrator\" -> \"github.tdurieux.testProject.entity.User$Type\";\n"+
"	\"github.tdurieux.testProject.entity.Administrator\" -> \"github.tdurieux.testProject.entity.User\";\n"+
"	\"github.tdurieux.testProject.entity.Administrator\" -> \"java.io.Serializable\";\n"+
"	\"java.lang.RuntimeException\" [color=grey];\n"+
"	\"java.lang.Exception\" [color=grey];\n"+
"	\"java.lang.Class\" [color=grey];\n"+
"	\"java.lang.NullPointerException\" [color=grey];\n"+
"	\"java.io.Serializable\" [color=grey];\n"+
"	\"github.tdurieux.testProject.entity.User$\" -> \"java.lang.Object\";\n"+
"	\"github.tdurieux.testProject.entity.User$\" -> \"java.lang.Runnable\";\n"+
"	\"github.tdurieux.testProject.entity.User$\" -> \"java.lang.Class\";\n"+
"	\"github.tdurieux.testProject.entity.User$\" -> \"github.tdurieux.testProject.entity.User\";\n"+
"	\"github.tdurieux.testProject.entity.User$\" -> \"java.lang.String\";\n"+
"	\"github.tdurieux.testProject.entity.User$\" -> \"java.io.Serializable\";\n"+
"	\"github.tdurieux.testProject.entity.User$\" -> \"java.lang.Override\";\n"+
"	subgraph cluster0 { \n"+
"		rankdir=LR;label=\"github.tdurieux.testProject.entity\";\n"+
"		\"github.tdurieux.testProject.entity.User$Type\" [label=\"User$Type\"]\n"+
"		\"github.tdurieux.testProject.entity.User\" [label=\"User\"]\n"+
"		\"github.tdurieux.testProject.entity.Administrator\" [label=\"Administrator\"]\n"+
"		\"github.tdurieux.testProject.entity.User$\" [label=\"User$\"]\n"+
"	};\n"+
"	subgraph cluster1 { \n"+ 
"		rankdir=LR;label=\"java.lang\";\n"+
"		\"java.lang.Runnable\" [label=\"Runnable\"]\n"+
"		\"java.lang.Object\" [label=\"Object\"]\n"+
"		\"java.lang.Boolean\" [label=\"Boolean\"]\n"+
"		\"java.lang.Override\" [label=\"Override\"]\n"+
"		\"java.lang.Thread\" [label=\"Thread\"]\n"+
"		\"java.lang.Integer\" [label=\"Integer\"]\n"+
"		\"java.lang.String\" [label=\"String\"]\n"+
"		\"java.lang.RuntimeException\" [label=\"RuntimeException\"]\n"+
"		\"java.lang.Exception\" [label=\"Exception\"]\n"+
"		\"java.lang.Class\" [label=\"Class\"]\n"+
"		\"java.lang.NullPointerException\" [label=\"NullPointerException\"]\n"+
"	};\n"+
"	subgraph cluster2 { \n"+ 
"		rankdir=LR;label=\"java.io\";\n"+
"		\"java.io.Serializable\" [label=\"Serializable\"]\n"+
"	};\n"+
"}";
		assertEquals(expected, result);
	}
	
	@Test
	public void testIgnoreElement() {
		config.addIgnoreRegex("entity");
		DependencyGraphExport export = new DotExport(new DependencyGraph(),
				config);
		String result = export.generate();
		assertFalse("The result must not contains enity", result.contains("entity"));
	}
	
	@Test
	public void testIgnoreExternalElement() {
		config.setIgnoreExternalDependences(true);
		DependencyGraphExport export = new DotExport(new DependencyGraph(),
				config);
		String result = export.generate();
		assertFalse("The result must not contains enity", result.contains("*"));
	}
}
