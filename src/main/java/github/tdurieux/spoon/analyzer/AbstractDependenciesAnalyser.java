package github.tdurieux.spoon.analyzer;

import github.tdurieux.spoon.graph.DependencyGraph;
import spoon.processing.AbstractProcessor;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtReference;

public abstract class AbstractDependenciesAnalyser extends
		AbstractProcessor<CtTypedElement<?>> {

	private DependencyGraph dependencyGraph;

	protected boolean versbose = false;

	public AbstractDependenciesAnalyser(DependencyGraph dependencyGraph) {
		this.dependencyGraph = dependencyGraph;
	}

	public AbstractDependenciesAnalyser() {
		this.dependencyGraph = new DependencyGraph();
	}

	public DependencyGraph getDependencyGraph() {
		return dependencyGraph;
	}

	protected boolean isExternal(CtReference e) {
		return isExternal(e.getDeclaration());
	}

	protected boolean isExternal(CtElement e) {
		if(e instanceof CtPackage) {
			return false;
		}
		if (e == null) {
			return true;
		}
		SourcePosition position = e.getPosition();

		if (position != null) {
			position.getFile();
		} else {
			return true;
		}
		return false;
	}
}