package github.tdurieux.dependencyAnalyzer;

import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;
import github.tdurieux.dependencyAnalyzer.graph.node.classDep.ClassDependencyFactory;
import github.tdurieux.dependencyAnalyzer.graph.node.classDep.ClassDependencyLocationFactory;
import github.tdurieux.dependencyAnalyzer.graph.node.packageDep.PackageDependencyFactory;
import github.tdurieux.dependencyAnalyzer.graph.node.packageDep.PackageDependencyLocationFactory;
import github.tdurieux.dependencyAnalyzer.spoon.analyzer.DependencyAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import spoon.Launcher;
import spoon.OutputType;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResource;
import spoon.processing.ProcessingManager;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;
import spoon.support.compiler.FileSystemFolder;

/**
 * is the class used to start the analysis
 * 
 * @author Thomas Durieux
 * 
 */
public class DependencyAnalizer {

	private String projectPath;
	private AnalyzerConfig config;
	private Factory factory;

	public DependencyAnalizer(String projectPath, AnalyzerConfig config)
			throws Exception {
		super();
		this.projectPath = projectPath;
		this.config = config;

		// create spoon
		Launcher spoon = new Launcher();
		this.factory = spoon.getFactory();

		FileSystemFolder projectFileSystem = new FileSystemFolder(new File(
				this.projectPath));
		spoon.addInputResource(projectFileSystem);
		SpoonCompiler compiler = spoon.createCompiler(factory);

		ArrayList<SpoonResource> resource = new ArrayList<SpoonResource>();
		resource.add(projectFileSystem);

		String classpath = config.getClassPath();

		//compiler.getFactory().getEnvironment().setAutoImports(true);
		compiler.getFactory().getEnvironment().setNoClasspath(true);

		// disable spoon logs
		disableLog();

		// spoon the project
		spoon.run(compiler, null, false, OutputType.NO_OUTPUT, new File(
				"spooned"), new ArrayList<String>(), false, null, true,
				classpath, null, resource, new ArrayList<SpoonResource>());
	}

	/**
	 * Execute the analysis
	 * 
	 * @return the dependency graph generated
	 */
	public DependencyGraph run() {
		DependencyGraph graph = new DependencyGraph();

		// create the processor
		ProcessingManager p = new QueueProcessingManager(factory);

		DependencyAnalyzer processor = null;
		switch (config.getLevel()) {
		case CLASS:
			processor = new DependencyAnalyzer(graph,
					new ClassDependencyFactory(),
					new ClassDependencyLocationFactory());
			break;

		case PACKAGE:
			processor = new DependencyAnalyzer(graph,
					new PackageDependencyFactory(),
					new PackageDependencyLocationFactory());
			break;
		}

		p.addProcessor(processor);

		if (config.isVerbose()) {
			System.err.println("Analize the project");
		}

		// analyze all classes
		p.process(factory.Class().getAll());

		// generate the output
		if (config.isVerbose()) {
			System.err.println("Create the output");
		}
		return graph;

	}

	/**
	 * Disable spoon logs
	 */
	private static void disableLog() {
		List<Logger> loggers = Collections.<Logger> list(LogManager
				.getCurrentLoggers());
		loggers.add(LogManager.getRootLogger());
		for (Logger logger : loggers) {
			logger.setLevel(Level.OFF);
		}
	}

}
