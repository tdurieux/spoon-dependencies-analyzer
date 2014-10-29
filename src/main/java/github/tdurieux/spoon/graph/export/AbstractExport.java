package github.tdurieux.spoon.graph.export;

import github.tdurieux.spoon.graph.DependencyGraph;
import github.tdurieux.spoon.graph.node.DependencyNode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.martiansoftware.jsap.JSAPResult;

public abstract class AbstractExport implements DependencyGraphExport {

	protected DependencyGraph graph;
	protected JSAPResult config;
	protected List<Pattern> ignoreRegex;

	public AbstractExport(DependencyGraph graph, JSAPResult config) {
		this.graph = graph;
		this.config = config;

		this.ignoreRegex = new ArrayList<>();
		if (this.config.getStringArray("ignore") != null) {
			for (String i : this.config.getStringArray("ignore")) {
				ignoreRegex.add(Pattern.compile(i));
			}
		}
	}

	protected boolean isToIgnore(DependencyNode child) {
		if(child == null) {
			return true;
		}
		if (child.isExternal()
				&& this.config.getBoolean("ignore-external")) {
			return true;
		}
		for (Pattern pattern : ignoreRegex) {
			Matcher matcher = pattern.matcher(child.getName());
			if (matcher.find()) {
				return true;
			}
		}
		return false;
	}

}
