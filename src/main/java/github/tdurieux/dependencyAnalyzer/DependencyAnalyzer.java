package github.tdurieux.dependencyAnalyzer;

import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;
import github.tdurieux.dependencyAnalyzer.graph.node.classDep.ClassDependencyFactory;
import github.tdurieux.dependencyAnalyzer.graph.node.classDep.ClassDependencyLocationFactory;
import github.tdurieux.dependencyAnalyzer.graph.node.methodDep.MethodDependencyFactory;
import github.tdurieux.dependencyAnalyzer.graph.node.methodDep.MethodDependencyLocationFactory;
import github.tdurieux.dependencyAnalyzer.graph.node.packageDep.PackageDependencyFactory;
import github.tdurieux.dependencyAnalyzer.graph.node.packageDep.PackageDependencyLocationFactory;
import github.tdurieux.dependencyAnalyzer.spoon.analyzer.DependencyAnalyzerProcessor;
import org.apache.log4j.Level;
import spoon.Launcher;
import spoon.processing.ProcessingManager;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;

/**
 * is the class used to start the analysis
 *
 * @author Thomas Durieux
 */
public class DependencyAnalyzer {

    private final AnalyzerConfig config;
    private final Factory factory;

    public DependencyAnalyzer(Factory factory, AnalyzerConfig config) {
        super();
        this.config = config;

        this.factory = factory;

        //factory.getEnvironment().setAutoImports(true);
        factory.getEnvironment().setNoClasspath(true);

        // disable spoon logs
        disableLog();
    }

    public DependencyAnalyzer(String projectPath, AnalyzerConfig config) {
        super();
        this.config = config;

        // create spoon
        Launcher spoon = new Launcher();
        spoon.addInputResource(projectPath);
        factory = spoon.getFactory();

        //factory.getEnvironment().setAutoImports(true);
        factory.getEnvironment().setNoClasspath(true);

        spoon.buildModel();

        // disable spoon logs
        disableLog();
    }

    public DependencyGraph run() {
        return run(factory);
    }

    /**
     * Execute the analysis
     *
     * @return the dependency graph generated
     */
    public DependencyGraph run(Factory factory) {
        DependencyGraph graph = new DependencyGraph();

        // create the processor
        ProcessingManager p = new QueueProcessingManager(factory);

        DependencyAnalyzerProcessor processor = null;
        switch (config.getLevel()) {
            case CLASS:
                processor = new DependencyAnalyzerProcessor(graph, new ClassDependencyFactory(),
                        new ClassDependencyLocationFactory());
                break;

            case PACKAGE:
                processor = new DependencyAnalyzerProcessor(graph, new PackageDependencyFactory(),
                        new PackageDependencyLocationFactory());
                break;
            case METHOD:
                processor = new DependencyAnalyzerProcessor(graph,
                        new MethodDependencyFactory(),
                        new MethodDependencyLocationFactory());
                break;
        }

        p.addProcessor(processor);

        if (config.isVerbose()) {
            System.err.println("Analyze the project");
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
        Launcher.LOGGER.setLevel(Level.OFF);
    }

}
