package github.tdurieux.dependencyAnalyzer.graph.export;

import github.tdurieux.dependencyAnalyzer.AnalyzerConfig;
import github.tdurieux.dependencyAnalyzer.AnalyzerConfig.Level;
import github.tdurieux.dependencyAnalyzer.DependencyAnalyzer;
import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DotExportTest {

	private DependencyAnalyzer dependencyAnalyzer;
	private DependencyGraph graph;
	private AnalyzerConfig config;

	public DotExportTest() throws Exception {
		this.config = new AnalyzerConfig();
		config.setLevel(Level.CLASS);
		config.setOutputFormat(AnalyzerConfig.OutputFormat.DOT);

		this.dependencyAnalyzer = new DependencyAnalyzer(
				"src/testProject/java", config);

		this.graph = dependencyAnalyzer.run();
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
		String expected = "digraph G {\n" +
                                  "\tnode [shape=box]; compound=true; ratio=fill;\n" +
                                  "\t\"github.tdurieux.testProject.entity.Administrator\" -> \"github.tdurieux.testProject.entity.User\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.Administrator\" -> \"github.tdurieux.testProject.entity.User$Type\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.Administrator\" -> \"java.io.Serializable\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User\" -> \"github.tdurieux.testProject.entity.User$Type\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User\" -> \"java.io.Serializable\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User\" -> \"java.lang.Boolean\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User\" -> \"java.lang.Class\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User\" -> \"java.lang.Exception\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User\" -> \"java.lang.Integer\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User\" -> \"java.lang.NullPointerException\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User\" -> \"java.lang.Object\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User\" -> \"java.lang.Override\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User\" -> \"java.lang.Runnable\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User\" -> \"java.lang.RuntimeException\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User\" -> \"java.lang.String\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User\" -> \"java.lang.Thread\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User$1\" -> \"github.tdurieux.testProject.entity.User\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User$1\" -> \"java.io.Serializable\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User$1\" -> \"java.lang.Class\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User$1\" -> \"java.lang.Object\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User$1\" -> \"java.lang.Override\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User$1\" -> \"java.lang.Runnable\";\n" +
                                  "\t\"github.tdurieux.testProject.entity.User$1\" -> \"java.lang.String\";\n" +
                                  "\t\"java.io.Serializable\" [color=grey];\n" +
                                  "\t\"java.lang.Boolean\" [color=grey];\n" +
                                  "\t\"java.lang.Class\" [color=grey];\n" +
                                  "\t\"java.lang.Exception\" [color=grey];\n" +
                                  "\t\"java.lang.Integer\" [color=grey];\n" +
                                  "\t\"java.lang.NullPointerException\" [color=grey];\n" +
                                  "\t\"java.lang.Object\" [color=grey];\n" +
                                  "\t\"java.lang.Override\" [color=grey];\n" +
                                  "\t\"java.lang.Runnable\" [color=grey];\n" +
                                  "\t\"java.lang.RuntimeException\" [color=grey];\n" +
                                  "\t\"java.lang.String\" [color=grey];\n" +
                                  "\t\"java.lang.Thread\" [color=grey];\n" +
                                  "\tsubgraph cluster0 { \n" +
                                  "\t\trankdir=LR;label=\"github.tdurieux.testProject.entity\";\n" +
                                  "\t\t\"github.tdurieux.testProject.entity.Administrator\" [label=\"Administrator\"]\n" +
                                  "\t\t\"github.tdurieux.testProject.entity.User\" [label=\"User\"]\n" +
                                  "\t\t\"github.tdurieux.testProject.entity.User$1\" [label=\"User$1\"]\n" +
                                  "\t\t\"github.tdurieux.testProject.entity.User$Type\" [label=\"User$Type\"]\n" +
                                  "\t};\n" +
                                  "\tsubgraph cluster1 { \n" +
                                  "\t\trankdir=LR;label=\"java.io\";\n" +
                                  "\t\t\"java.io.Serializable\" [label=\"Serializable\"]\n" +
                                  "\t};\n" +
                                  "\tsubgraph cluster2 { \n" +
                                  "\t\trankdir=LR;label=\"java.lang\";\n" +
                                  "\t\t\"java.lang.Boolean\" [label=\"Boolean\"]\n" +
                                  "\t\t\"java.lang.Class\" [label=\"Class\"]\n" +
                                  "\t\t\"java.lang.Exception\" [label=\"Exception\"]\n" +
                                  "\t\t\"java.lang.Integer\" [label=\"Integer\"]\n" +
                                  "\t\t\"java.lang.NullPointerException\" [label=\"NullPointerException\"]\n" +
                                  "\t\t\"java.lang.Object\" [label=\"Object\"]\n" +
                                  "\t\t\"java.lang.Override\" [label=\"Override\"]\n" +
                                  "\t\t\"java.lang.Runnable\" [label=\"Runnable\"]\n" +
                                  "\t\t\"java.lang.RuntimeException\" [label=\"RuntimeException\"]\n" +
                                  "\t\t\"java.lang.String\" [label=\"String\"]\n" +
                                  "\t\t\"java.lang.Thread\" [label=\"Thread\"]\n" +
                                  "\t};\n" +
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
		config.setIgnoreExternalDependencies(true);
		DependencyGraphExport export = new DotExport(new DependencyGraph(),
				config);
		String result = export.generate();
		assertFalse("The result must not contains enity", result.contains("*"));
	}
}
