package github.tdurieux.spoon.graph;

import github.tdurieux.spoon.graph.node.DependencyDeclaration;
import github.tdurieux.spoon.graph.node.DependencyNode;
import github.tdurieux.spoon.graph.node.DependencyNodeImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Thomas Durieux
 * 
 */
public class DependencyGraph {
	private Map<String, DependencyNode> nodes;
	private Map<DependencyNode, List<DependencyNode>> usedNodes;
	private Map<DependencyNode, List<DependencyNode>> usedByNodes;

	public DependencyGraph() {
		nodes = new HashMap<String, DependencyNode>();
		usedNodes = new HashMap<DependencyNode, List<DependencyNode>>();
		usedByNodes = new HashMap<DependencyNode, List<DependencyNode>>();
	}

	private void createList(DependencyNode node) {
		if (!this.usedNodes.containsKey(node)) {
			this.usedNodes.put(node, new ArrayList<DependencyNode>());
		}
		if (!this.usedByNodes.containsKey(node)) {
			this.usedByNodes.put(node, new ArrayList<DependencyNode>());
		}
	}

	private boolean isToIgnore(String name) {
		String[] toIgnore = {"int","double","boolean","void","?","float", "char", "<nulltype>"};
		for (String ignore : toIgnore) {
			if(name.equals(ignore)) {
				return true;
			}
		}
		return false;
	}
	public void addDependencyNode(DependencyDeclaration node,
			DependencyDeclaration dependency) {

		if (node.getName().equals(dependency.getName())) {
			return;
		}
		if(isToIgnore(node.getName()) || isToIgnore(dependency.getName())) {
			return;
		}
		
		DependencyNode parentNode = nodes.get(node.getName());
		if (parentNode == null) {
			parentNode = new DependencyNodeImpl(node.getName(),
					node.isExternal());
			nodes.put(node.getName(), parentNode);
		}
		parentNode.addDeclarationLicalization(node);

		DependencyNode dependencyNode = nodes.get(dependency.getName());
		if (dependencyNode == null) {
			dependencyNode = new DependencyNodeImpl(dependency.getName(),
					dependency.isExternal());
			nodes.put(dependency.getName(), dependencyNode);
		}
		dependencyNode.addDeclarationLicalization(dependency);

		createList(parentNode);
		createList(dependencyNode);

		if (!usedNodes.get(parentNode).contains(dependencyNode)) {
			usedNodes.get(parentNode).add(dependencyNode);
		}

		if (!usedByNodes.get(dependencyNode).contains(parentNode)) {
			usedByNodes.get(dependencyNode).add(parentNode);
		}
	}

	public void addDependencyNode(DependencyDeclaration node,
			Collection<DependencyDeclaration> declarations) {
		for (DependencyDeclaration dependencyDeclaration : declarations) {
			this.addDependencyNode(node, dependencyDeclaration);
		}
	}

	public Map<DependencyNode, List<DependencyNode>> getUsedByNodes() {
		return new HashMap<DependencyNode, List<DependencyNode>>(
				this.usedByNodes);
	}

	public Map<DependencyNode, List<DependencyNode>> getUsedNodes() {
		return new HashMap<DependencyNode, List<DependencyNode>>(this.usedNodes);
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
			content += pack.getName() + (pack.isExternal() ? " *" : "") + "\n";
			if (usedByNodes.containsKey(pack)) {
				Collections.sort(usedByNodes.get(pack));
				for (DependencyNode nodeDep : usedByNodes.get(pack)) {
					if (nodeDep == null)
						continue;
					content += "    <-- " + nodeDep.getName()
							+ (nodeDep.isExternal() ? " *" : "") + "\n";
				}
			}
			
			List<DependencyNode> depList = usedNodes.get(pack);
			Collections.sort(depList);
			for (DependencyNode dependencyNode : depList) {
				if (dependencyNode == null)
					continue;
				content += "    --> " + dependencyNode.getName()
						+ (dependencyNode.isExternal() ? " *" : "") + "\n";
			}
		}
		return content;
	}
}
