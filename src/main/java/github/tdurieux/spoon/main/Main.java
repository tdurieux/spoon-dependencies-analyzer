package github.tdurieux.spoon.main;

import github.tdurieux.spoon.analyzer.AbstractDependenciesAnalyser;
import github.tdurieux.spoon.analyzer.ClassDependenciesAnalyser;
import github.tdurieux.spoon.analyzer.PackageDependenciesAnalyser;
import github.tdurieux.spoon.graph.DependencyGraph;
import github.tdurieux.spoon.graph.export.DotExport;
import github.tdurieux.spoon.graph.export.DependencyGraphExport;
import github.tdurieux.spoon.graph.export.TxtExport;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.QualifiedSwitch;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.UnflaggedOption;

import spoon.Launcher;
import spoon.OutputType;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResource;
import spoon.processing.ProcessingManager;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;
import spoon.support.compiler.FileSystemFolder;

public class Main {
	public static void main(String[] args) throws Exception {
		JSAPResult config = argsParser(args);

		Launcher spoon = new Launcher();
		Factory factory = spoon.getFactory();

		FileSystemFolder projectFileSystem = new FileSystemFolder(new File(config.getString("project")));
		spoon.addInputResource(projectFileSystem);
		SpoonCompiler compiler = spoon.createCompiler(factory);

		ArrayList<SpoonResource> resource = new ArrayList<SpoonResource>();
		resource.add(projectFileSystem);

		String classpath = config.getString("classpath");

		compiler.getFactory().getEnvironment().setAutoImports(true);
		compiler.getFactory().getEnvironment().setNoClasspath(true);

		disableLog();
		
		spoon.run(compiler, null, false, OutputType.NO_OUTPUT, new File(
				"spooned"), new ArrayList<String>(), false, null, true,
				classpath, null, resource, new ArrayList<SpoonResource>());

		ProcessingManager p = new QueueProcessingManager(factory);

		DependencyGraph graph = new DependencyGraph();

		AbstractDependenciesAnalyser processor = null;

		if (config.getString("level").equals("package")) {
			processor = new PackageDependenciesAnalyser(graph);
		} else if (config.getString("level").equals("class")) {
			processor = new ClassDependenciesAnalyser(graph);
		} else if (config.getString("level").equals("method")) {
			// processor = new MethodDependenciesAnalyser();
		} else {
			throw new UnsupportedOperationException("Level "
					+ config.getString("level") + "not found");
		}

		p.addProcessor(processor);

		if (config.getBoolean("verbose")) {
			System.err.println("Analize the project");
		}
		
		p.process(factory.Class().getAll());

		if (config.getBoolean("verbose")) {
			System.err.println("Create the output");
		}
		DependencyGraphExport export = null;
		if (config.getString("format").equals("txt")) {
			export = new TxtExport(graph, config);
		} else if (config.getString("format").equals("dot")) {
			export = new DotExport(graph, config);
		}

		if (config.getString("output") == null ||
				config.getString("output").equals("stdout")) {
			System.out.println(export.generate());
		} else if (config.getString("output").equals("stderr")) {
			System.err.println(export.generate());
		} else {
			if (config.getBoolean("verbose")) {
				System.err.println("Create the file");
			}
			File file = new File(config.getString("output"));
			file.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(file);
			writer.append(export.generate());
			writer.close();
		}
	}

	private static void disableLog() {
		List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
		loggers.add(LogManager.getRootLogger());
		for ( Logger logger : loggers ) {
		    logger.setLevel(Level.OFF);
		}
	}

	private static JSAPResult argsParser(String[] args) throws JSAPException {
		SimpleJSAP jsap = new SimpleJSAP(
				"DependencyGraph",
				"Get the dependency graph of a project",
				new Parameter[] {
						new UnflaggedOption("project", JSAP.STRING_PARSER,
								JSAP.NO_DEFAULT, JSAP.REQUIRED,
								JSAP.NOT_GREEDY, "The path to the project"),
						new UnflaggedOption("classpath", JSAP.STRING_PARSER,
								JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED,
								JSAP.NOT_GREEDY, "The claspath of the project"),
						new FlaggedOption("level", JSAP.STRING_PARSER,
								"package", JSAP.NOT_REQUIRED, 'l', "level",
								"The level of dependency analyzis (package, class, method)."),
						new FlaggedOption("format", JSAP.STRING_PARSER, "txt",
								JSAP.NOT_REQUIRED, 'f', "format",
								"The ouput format of the script (txt, dot)."),
						new FlaggedOption("output", JSAP.STRING_PARSER,
								JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'o', "out",
								"The ouput file of the script (txt, dot)."),
						new QualifiedSwitch("ignore-external",
								JSAP.STRING_PARSER, JSAP.NO_DEFAULT,
								JSAP.NOT_REQUIRED, 'j', "ignore-external",
								"Ignore external depencencies."),
						new QualifiedSwitch("verbose", JSAP.STRING_PARSER,
								JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'v',
								"verbose", "Requests verbose output."),
						new FlaggedOption("ignore", JSAP.STRING_PARSER,
								JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'i',
								"ignore",
								"Regex to ignore element, sepearated by a ','.")
								.setList(true).setListSeparator(',') });

		JSAPResult config = jsap.parse(args);

		if (!config.success() && !config.getBoolean("help")) {
			System.err.println("Usage: ");
			System.err.println("       " + jsap.getUsage());
			System.err.println();
			System.err.println(jsap.getHelp());
			System.exit(1);
		}
		if(config.getBoolean("help")) {
			System.exit(1);
		}
		return config;
		
	}
}
