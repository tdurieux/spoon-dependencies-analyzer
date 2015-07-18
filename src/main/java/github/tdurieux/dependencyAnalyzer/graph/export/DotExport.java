package github.tdurieux.dependencyAnalyzer.graph.export;

import github.tdurieux.dependencyAnalyzer.AnalyzerConfig;
import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode;

import java.util.*;

/**
 * exports the dependency graph to Graphviz format
 * 
 * @author Thomas Durieux
 * 
 */
public class DotExport extends AbstractExport {

	public DotExport(DependencyGraph graph, AnalyzerConfig config) {
		super(graph, config);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see DependencyGraphExport#generate()
	 */
	@Override
	public String generate() {
		Map<String, List<String>> map = new HashMap<>();
		List<String> starts = new ArrayList<>();

		String content = "digraph G {\n\tnode [shape=box]; compound=true; ratio=fill;\n";
		if(this.graph == null || this.config == null) {
			return content + "}";
		}
		Map<DependencyNode, List<DependencyNode>> nodes = graph.getUsedNodes();
        List<DependencyNode> listUsedNodes = new ArrayList<>( nodes.keySet());
        Collections.sort(listUsedNodes);

		for (DependencyNode parent : listUsedNodes) {
			if (isToIgnore(parent)) {
				continue;
			}

			String[] test = parent.getQualifiedName().split("\\.");
			String concat = test[0];
			if (!map.containsKey(concat)) {
				map.put(concat, new ArrayList<String>());
				starts.add(concat);
			}

			for (int i = 1; i < test.length; i++) {
				if (i == test.length - 1
						&& Character.isUpperCase(test[i].charAt(0))) {
					map.get(concat).add(concat + "." + test[i]);
					continue;
				}
				if (!map.containsKey(concat + "." + test[i])) {
					map.put(concat + "." + test[i], new ArrayList<String>());
				}
				if (!map.get(concat).contains(concat + "." + test[i])) {
					map.get(concat).add(concat + "." + test[i]);
				}
				concat += "." + test[i];
			}
			if (parent.isExternal()) {
				content += "\t\"" + parent.getQualifiedName()
						+ "\" [color=grey];\n";
			}
            final List<DependencyNode> dependencies = nodes.get(parent);
            Collections.sort(dependencies);
			for (DependencyNode child : dependencies) {
				if (isToIgnore(child)) {
					continue;
				}
				content += "\t\"" + parent.getQualifiedName() + "\" -> \""
						+ child.getQualifiedName() + "\";\n";
			}
		}
		List<String> keys = new ArrayList<String>(map.keySet());
		for (String string : keys) {
			if (map.get(string).size() == 0) {
				map.remove(string);
			}
		}
		for (String string : starts) {
			content += createSubGraphs(map, string);
		}
		return content + "}";
	}

	private int i = 0;

	private String createSubGraphs(Map<String, List<String>> map, String current) {
		String content = "";
		List<String> child = map.get(current);
		if (child == null) {
			return content;
		}
		boolean subgraph = false;
		for (String string : child) {
			if (!map.containsKey(string)) {
				subgraph = true;
				break;
			}
		}
		if (subgraph) {
			content += "\tsubgraph cluster" + i++
					+ " { \n\t\trankdir=LR;label=\"" + current + "\";\n";
		} else if (!map.containsKey(current)) {
			content += "\t\t\"" + current + "\";\n";
			return content;
		}

		for (String ch : child) {
			if (map.containsKey(ch)) {
				content += createSubGraphs(map, ch);
			} else {
				content += "\t\t\"" + ch + "\" [label=\""
						+ ch.replace(current + ".", "") + "\"]\n";
			}
		}
		if (subgraph) {
			content += "\t};\n";
		}
		return content;
	}

}
