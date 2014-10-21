package github.tdurieux.spoon.analyser;

import java.util.HashMap;
import java.util.Map;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtTypedElement;

/**
 * Reports warnings when empty catch blocks are found.
 */
public class PackageDependenciesAnalyser extends
		AbstractProcessor<CtStatement> {

	public Map<String, String> packages = new HashMap<String, String>();

	@Override
	public boolean isToBeProcessed(CtStatement candidate) {
		if(candidate.toString().contains("jun")) {
			System.out.println(candidate);
		}
		return super.isToBeProcessed(candidate);
		//return candidate.getType() != null
		//		&& candidate.getType().getPackage() != null;
	}

	public void process(CtStatement element) {
		
	}

	@Override
	public void processingDone() {
		for (String pack : packages.keySet()) {
			System.out.println(pack);
		}
	}
}