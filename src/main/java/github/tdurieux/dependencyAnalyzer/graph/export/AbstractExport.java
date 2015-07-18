package github.tdurieux.dependencyAnalyzer.graph.export;

import github.tdurieux.dependencyAnalyzer.AnalyzerConfig;
import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * abstract class which contains common methods for the export.
 * 
 * @author Thomas Durieux
 * 
 */
public abstract class AbstractExport implements DependencyGraphExport {

	protected final DependencyGraph graph;
	protected final AnalyzerConfig config;
	protected final List<Pattern> ignoreRegex;

	public AbstractExport(DependencyGraph graph, AnalyzerConfig config) {
		this.graph = graph;
		this.config = config;

		this.ignoreRegex = new ArrayList<>();
		for (String i : this.config.getIgnoreRegex()) {
			ignoreRegex.add(Pattern.compile(i));
		}
	}

	/**
	 * returns true if the element must not be exported
	 * 
	 * @param child
	 * @return true if the element must not be exported
	 */
	protected boolean isToIgnore(DependencyNode child) {
		if (child == null) {
			return true;
		}
		if (child.isExternal() && this.config.isIgnoreExternalDependencies()) {
			return true;
		}
		for (Pattern pattern : ignoreRegex) {
			Matcher matcher = pattern.matcher(child.getSimpleName());
			if (matcher.find()) {
				return true;
			}
		}
		return false;
	}

}
