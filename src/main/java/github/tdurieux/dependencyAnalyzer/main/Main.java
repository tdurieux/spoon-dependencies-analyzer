package github.tdurieux.dependencyAnalyzer.main;

import com.martiansoftware.jsap.*;
import github.tdurieux.dependencyAnalyzer.AnalyzerConfig;
import github.tdurieux.dependencyAnalyzer.AnalyzerConfig.Level;
import github.tdurieux.dependencyAnalyzer.AnalyzerConfig.OutputFormat;
import github.tdurieux.dependencyAnalyzer.DependencyAnalyzer;
import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;
import github.tdurieux.dependencyAnalyzer.graph.export.DependencyGraphExport;
import github.tdurieux.dependencyAnalyzer.graph.export.DotExport;
import github.tdurieux.dependencyAnalyzer.graph.export.TxtExport;

import java.io.File;
import java.io.FileWriter;

/**
 * is the launcher of the project
 *
 * @author Thomas Durieux
 */
public class Main {
    public static void main(String[] args) throws Exception {
        AnalyzerConfig config = argsParser(args);

        DependencyAnalyzer dependenciesAnalyzer = new DependencyAnalyzer(
                config.getProjectPath(), config);

        DependencyGraph graph = dependenciesAnalyzer.run();
        DependencyGraphExport export = null;
        switch (config.getOutputFormat()) {
            case DOT:
                export = new DotExport(graph, config);
                break;

            case TXT:
                export = new TxtExport(graph, config);
                break;
        }

        if (config.getOutput() == null || config.getOutput().equals("stdout")) {
            System.out.println(export.generate());
        } else if (config.getOutput().equals("stderr")) {
            System.err.println(export.generate());
        } else {
            if (config.isVerbose()) {
                System.err.println("Create the file");
            }
            File file = new File(config.getOutput());
            file.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(file);
            writer.append(export.generate());
            writer.close();
        }
    }

    private static AnalyzerConfig argsParser(String[] args)
            throws JSAPException {
        Parameter[] parameters = {
                new UnflaggedOption("project", JSAP.STRING_PARSER,
                        JSAP.NO_DEFAULT, JSAP.REQUIRED,
                        JSAP.NOT_GREEDY, "The path to the project"),
                new UnflaggedOption("classpath", JSAP.STRING_PARSER,
                        JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED,
                        JSAP.NOT_GREEDY, "The classpath of the project"),
                new FlaggedOption("level", JSAP.STRING_PARSER,
                        "package", JSAP.NOT_REQUIRED, 'l', "level",
                        "The level of dependency analysis (package, class, method)."),
                new FlaggedOption("format", JSAP.STRING_PARSER, "txt",
                        JSAP.NOT_REQUIRED, 'f', "format",
                        "The output format of the script (txt, dot)."),
                new FlaggedOption("output", JSAP.STRING_PARSER,
                        JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'o', "out",
                        "The output file of the script (txt, dot)."),
                new QualifiedSwitch("ignore-external",
                        JSAP.STRING_PARSER, JSAP.NO_DEFAULT,
                        JSAP.NOT_REQUIRED, 'j', "ignore-external",
                        "Ignore external dependencies."),
                new QualifiedSwitch("verbose", JSAP.STRING_PARSER,
                        JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'v',
                        "verbose", "Requests verbose output."),
                new FlaggedOption("ignore", JSAP.STRING_PARSER,
                        JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, 'i',
                        "ignore",
                        "Regex to ignore element, separated by a ','.")
                        .setList(true).setListSeparator(',')};
        SimpleJSAP jsap = new SimpleJSAP(
                "DependencyGraph",
                "Get the dependency graph of a project",
                parameters);

        JSAPResult config = jsap.parse(args);

        if (!config.success() && !config.getBoolean("help")) {
            System.err.println("Usage: ");
            System.err.println("       " + jsap.getUsage());
            System.err.println();
            System.err.println(jsap.getHelp());
            System.exit(1);
        }
        if (config.getBoolean("help")) {
            System.exit(1);
        }

        AnalyzerConfig analyzerConfig = AnalyzerConfig.INSTANCE;
        analyzerConfig.setProjectPath(config.getString("project"));
        analyzerConfig.setClassPath(config.getString("classpath"));
        analyzerConfig.setIgnoreExternalDependencies(config
                .getBoolean("ignore-external"));
        if (config.getStringArray("ignore") != null) {
            for (String ignoreRegex : config.getStringArray("ignore")) {
                analyzerConfig.addIgnoreRegex(ignoreRegex);
            }
        }

        if (config.getString("level").equals("class")) {
            analyzerConfig.setLevel(Level.CLASS);
        } else if (config.getString("level").equals("package")) {
            analyzerConfig.setLevel(Level.PACKAGE);
        }

        analyzerConfig.setOutput(config.getString("output"));
        if (config.getString("format").equals("dot")) {
            analyzerConfig.setOutputFormat(OutputFormat.DOT);
        } else if (config.getString("format").equals("txt")) {
            analyzerConfig.setOutputFormat(OutputFormat.TXT);
        }
        analyzerConfig.setVerbose(config.getBoolean("verbose"));
        return analyzerConfig;

    }
}
