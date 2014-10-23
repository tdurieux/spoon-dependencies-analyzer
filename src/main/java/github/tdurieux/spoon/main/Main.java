package github.tdurieux.spoon.main;

import github.tdurieux.spoon.analyser.PackageDependenciesAnalyser;

import java.io.File;
import java.util.ArrayList;

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
		if (args.length < 2) {
			usage();
			return;
		}
		String project = args[0];
		String classpath = args[1];
		
		Launcher spoon = new Launcher();
		Factory factory = spoon.getFactory();
		
		spoon.addInputResource(new FileSystemFolder(new File(project)));
		SpoonCompiler compiler = spoon.createCompiler(factory);
		
		ArrayList<SpoonResource> resource = new ArrayList<SpoonResource>();
		resource.add(new FileSystemFolder(new File(project)));
		
		spoon.run(compiler, null, false, OutputType.NO_OUTPUT, new File("spooned"),
				new ArrayList<String>(), false, null, true,
				classpath, null, resource,new ArrayList<SpoonResource>());
		
		ProcessingManager p = new QueueProcessingManager(factory);
		PackageDependenciesAnalyser proc = new PackageDependenciesAnalyser();
		p.addProcessor(proc);
		p.process(factory.Class().getAll());    
		
	}

	private static void usage() {
		System.out.println("Usage: project-src project-classpath");

	}
}
