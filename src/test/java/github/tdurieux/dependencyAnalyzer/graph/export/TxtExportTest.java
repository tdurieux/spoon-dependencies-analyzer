package github.tdurieux.dependencyAnalyzer.graph.export;

import github.tdurieux.dependencyAnalyzer.AnalyzerConfig;
import github.tdurieux.dependencyAnalyzer.AnalyzerConfig.Level;
import github.tdurieux.dependencyAnalyzer.DependencyAnalyzer;
import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TxtExportTest {

	private DependencyAnalyzer dependencyAnalizer;
	private DependencyGraph graph;
	private AnalyzerConfig config;

	public TxtExportTest() throws Exception {
		this.config = new AnalyzerConfig();
		config.setLevel(Level.CLASS);
		config.setOutputFormat(AnalyzerConfig.OutputFormat.TXT);

		this.dependencyAnalizer = new DependencyAnalyzer(
				"src/testProject/java", config);

		this.graph = dependencyAnalizer.run();
	}

	@Test
	public void testNullGraph() {
		DependencyGraphExport export = new TxtExport(null, config);
		String result = export.generate();
		assertEquals("The result must be empty", "", result);
	}

	@Test
	public void testEmtpyGraph() {
		DependencyGraphExport export = new TxtExport(new DependencyGraph(),
				config);
		String result = export.generate();
		assertEquals("The result must be empty", "", result);
	}

	@Test
	public void testGraphExport() {
		DependencyGraphExport export = new TxtExport(graph, config);
		String result = export.generate();
		String expected = "github.tdurieux.testProject.entity\n"+
"    Administrator\n"+
"        --> github.tdurieux.testProject.entity.User\n"+
"        --> github.tdurieux.testProject.entity.User$Type\n"+
"        --> java.io.Serializable *\n"+
"    User\n"+
"        <-- github.tdurieux.testProject.entity.Administrator\n"+
"        <-- github.tdurieux.testProject.entity.User$1\n"+
"        --> github.tdurieux.testProject.entity.User$Type\n"+
"        --> java.io.Serializable *\n"+
"        --> java.lang.Boolean *\n"+
"        --> java.lang.Class *\n"+
"        --> java.lang.Exception *\n"+
"        --> java.lang.Integer *\n"+
"        --> java.lang.NullPointerException *\n"+
"        --> java.lang.Object *\n"+
"        --> java.lang.Override *\n"+
"        --> java.lang.Runnable *\n"+
"        --> java.lang.RuntimeException *\n"+
"        --> java.lang.String *\n"+
"        --> java.lang.Thread *\n"+
"        $1\n"+
"                --> github.tdurieux.testProject.entity.User\n"+
"                --> java.io.Serializable *\n"+
"                --> java.lang.Class *\n"+
"                --> java.lang.Object *\n"+
"                --> java.lang.Override *\n"+
"                --> java.lang.Runnable *\n"+
"                --> java.lang.String *\n"+
"        Type\n"+
"                <-- github.tdurieux.testProject.entity.Administrator\n"+
"                <-- github.tdurieux.testProject.entity.User\n"+
"java.io *\n"+
"    Serializable *\n"+
"        <-- github.tdurieux.testProject.entity.Administrator\n"+
"        <-- github.tdurieux.testProject.entity.User\n"+
"        <-- github.tdurieux.testProject.entity.User$1\n"+
"java.lang *\n"+
"    Boolean *\n"+
"        <-- github.tdurieux.testProject.entity.User\n"+
"    Class *\n"+
"        <-- github.tdurieux.testProject.entity.User\n"+
"        <-- github.tdurieux.testProject.entity.User$1\n"+
"    Exception *\n"+
"        <-- github.tdurieux.testProject.entity.User\n"+
"    Integer *\n"+
"        <-- github.tdurieux.testProject.entity.User\n"+
"    NullPointerException *\n"+
"        <-- github.tdurieux.testProject.entity.User\n"+
"    Object *\n"+
"        <-- github.tdurieux.testProject.entity.User\n"+
"        <-- github.tdurieux.testProject.entity.User$1\n"+
"    Override *\n"+
"        <-- github.tdurieux.testProject.entity.User\n"+
"        <-- github.tdurieux.testProject.entity.User$1\n"+
"    Runnable *\n"+
"        <-- github.tdurieux.testProject.entity.User\n"+
"        <-- github.tdurieux.testProject.entity.User$1\n"+
"    RuntimeException *\n"+
"        <-- github.tdurieux.testProject.entity.User\n"+
"    String *\n"+
"        <-- github.tdurieux.testProject.entity.User\n"+
"        <-- github.tdurieux.testProject.entity.User$1\n"+
"    Thread *\n"+
"        <-- github.tdurieux.testProject.entity.User\n";
		assertEquals(expected, result);
	}
	
	@Test
	public void testIgnoreElement() {
		config.addIgnoreRegex("entity");
		DependencyGraphExport export = new TxtExport(new DependencyGraph(),
				config);
		String result = export.generate();
		assertFalse("The result must not contains enity", result.contains("entity"));
	}
	
	@Test
	public void testIgnoreExternalElement() {
		config.setIgnoreExternalDependencies(true);
		DependencyGraphExport export = new TxtExport(new DependencyGraph(),
				config);
		String result = export.generate();
		assertFalse("The result must not contains enity", result.contains("*"));
	}
}
