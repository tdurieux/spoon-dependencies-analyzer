package github.tdurieux.spoon.graph.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.martiansoftware.jsap.JSAPResult;

import github.tdurieux.spoon.graph.DependencyGraph;
import github.tdurieux.spoon.graph.node.DependencyNode;

public class DotExport extends AbstractExport {

	public DotExport(DependencyGraph graph, JSAPResult config) {
		super(graph, config);
	}

	@Override
	public String generate() {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> starts = new ArrayList<String>();

		String content = "digraph G {\n\tnode [shape=box]; compound=true; ratio=fill;\n";
		Map<DependencyNode, List<DependencyNode>> nodes = graph.getUsedNodes();
		for (DependencyNode parent : nodes.keySet()) {
			if (isToIgnore(parent)) {
				continue;
			}

			String[] test = parent.getName().split("\\.");
			String concat = test[0];
			if (!map.containsKey(concat)) {
				map.put(concat, new ArrayList<String>());
				starts.add(concat);
			}

			for (int i = 1; i < test.length; i++) {
				if (i == test.length - 1
						&& Character.isUpperCase(test[i].charAt(0))) {
					map.get(concat).add(concat + "." +  test[i]);
					continue;
				}
				if (!map.containsKey(concat + "." + test[i])) {
					map.put(concat + "." + test[i], new ArrayList<String>());
				}
				if(!map.get(concat).contains(concat + "." + test[i])){
					map.get(concat).add(concat + "." + test[i]);
				}
				concat += "." + test[i];
			}
			if (parent.isExternal()) {
				content += "\t\"" + parent.getName() + "\" [color=grey];\n";
			}
			for (DependencyNode child : nodes.get(parent)) {
				if (isToIgnore(child)) {
					continue;
				}
				content += "\t\"" + parent.getName() + "\" -> \""
						+ child.getName() + "\";\n";
			}
		}
		List<String> keys = new ArrayList<String>(map.keySet());
		for (String string : keys) {
			if (map.get(string).size() == 0) {
				map.remove(string);
			}
		}
		for (String string : starts) {
			content += testSubGraph(map, string);
		}
		return content + "}";
	}

	private int i = 0;

	private String testSubGraph(Map<String, List<String>> map, String current) {
		String content = "";
		List<String> childs = map.get(current);
		if(childs == null) {
			return content;
		}
		boolean subgraph = false;
		for (String string : childs) {
			if (!map.containsKey(string)) {
				subgraph = true;
				break;
			}
		}
		if (subgraph) {
			content += "\tsubgraph cluster" + i++ + " { \n\t\trankdir=LR;label=\""
					+ current + "\";\n";
		} else if (!map.containsKey(current)) {
			content += "\t\t\"" + current + "\";\n";
			return content;
		}

		for (String child : childs) {
			if (map.containsKey(child)) {
				content += testSubGraph(map, child);
			} else {
				content += "\t\t\"" + child + "\" [label=\""+child.replace(current+".", "") + "\"]\n";
			}
		}
		if (subgraph) {
			content += "\t};\n";
		}
		return content;
	}

}
