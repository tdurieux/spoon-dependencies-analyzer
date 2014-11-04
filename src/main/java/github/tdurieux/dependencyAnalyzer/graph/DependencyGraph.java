package github.tdurieux.dependencyAnalyzer.graph;

import github.tdurieux.dependencyAnalyzer.graph.node.DependencyLocation;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * represents the dependency graph of a project
 * 
 * @author Thomas Durieux
 * 
 */
public class DependencyGraph {
	private Map<DependencyNode, List<DependencyNode>> usedNodes;
	private Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> usedByNodes;

	public DependencyGraph() {
		usedNodes = new HashMap<DependencyNode, List<DependencyNode>>();
		usedByNodes = new HashMap<DependencyNode, Map<DependencyNode, List<DependencyLocation>>>();
	}

	/**
	 * initialize lists in usedNodes and usedByNodes maps
	 * 
	 * @param node
	 * @param location
	 */
	private void initializeListsInMaps(DependencyNode node,
			DependencyLocation location) {
		if (!this.usedNodes.containsKey(location)) {
			this.usedNodes.put(location, new ArrayList<DependencyNode>());
		}
		if (!this.usedNodes.containsKey(node)) {
			this.usedNodes.put(node, new ArrayList<DependencyNode>());
		}
		if (!this.usedByNodes.containsKey(node)) {
			this.usedByNodes.put(node,
					new HashMap<DependencyNode, List<DependencyLocation>>());
		}
		if (!this.usedByNodes.containsKey(location)) {
			this.usedByNodes.put(location,
					new HashMap<DependencyNode, List<DependencyLocation>>());
		}
		if (!this.usedByNodes.get(node).containsKey(location)) {
			this.usedByNodes.get(node).put(location,
					new ArrayList<DependencyLocation>());
		}
	}

	/**
	 * return true if the type must be ignored
	 * 
	 * @param name
	 *            the type name
	 * @return true if the type must be ignored
	 */
	private boolean isToIgnore(String name) {
		String[] toIgnore = { "int", "double", "boolean", "void", "?", "float",
				"long", "char", "<nulltype>" };
		for (String ignore : toIgnore) {
			if (name.equals(ignore)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * add a dependency to the graph
	 * 
	 * @param node
	 *            the dependency
	 * @param location
	 *            where the dependency is used
	 */
	public void addDependencyNode(DependencyNode node,
			DependencyLocation location) {

		if (node == null || location == null) {
			return;
		}

		if (node.getQualifiedName().equals(location.getQualifiedName())) {
			return;
		}
		if (isToIgnore(node.getQualifiedName())
				|| isToIgnore(location.getQualifiedName())) {
			return;
		}

		initializeListsInMaps(node, location);

		if (!usedNodes.get(location).contains(node)) {
			this.usedNodes.get(location).add(node);
		}

		if (!usedByNodes.get(node).get(location).contains(location)) {
			this.usedByNodes.get(node).get(location).add(location);
		}
	}

	/**
	 * get a copy of the used by nodes
	 * 
	 * @return localization of used dependencies
	 */
	public Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> getUsedByNodes() {
		return new HashMap<DependencyNode, Map<DependencyNode, List<DependencyLocation>>>(
				this.usedByNodes);
	}

	/**
	 * get a copy of used nodes
	 * 
	 * @return used dependencies
	 */
	public Map<DependencyNode, List<DependencyNode>> getUsedNodes() {
		return new HashMap<DependencyNode, List<DependencyNode>>(usedNodes);
	}

	@Override
	public String toString() {
		String content = "";
		List<DependencyNode> list = new ArrayList<DependencyNode>(
				this.usedNodes.keySet());
		Collections.sort(list);
		for (DependencyNode pack : list) {
			if (pack == null) {
				continue;
			}
			content += pack.getQualifiedName()
					+ (pack.isExternal() ? " *" : "") + "\n";
			if (usedByNodes.containsKey(pack)) {
				List<DependencyNode> dep = new ArrayList<DependencyNode>(
						usedByNodes.get(pack).keySet());
				Collections.sort(dep);
				for (DependencyNode nodeDep : dep) {
					if (nodeDep == null)
						continue;
					content += "    <-- " + nodeDep.getQualifiedName()
							+ (nodeDep.isExternal() ? " *" : "") + "\n";
				}
			}

			List<DependencyNode> depList = usedNodes.get(pack);
			Collections.sort(depList);
			for (DependencyNode dependencyNode : depList) {
				if (dependencyNode == null)
					continue;
				content += "    --> " + dependencyNode.getQualifiedName()
						+ (dependencyNode.isExternal() ? " *" : "") + "\n";
			}
		}
		return content;
	}
}
