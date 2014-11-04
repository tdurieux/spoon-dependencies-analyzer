package github.tdurieux.dependencyAnalyzer.graph.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import github.tdurieux.dependencyAnalyzer.AnalyzerConfig;
import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyLocation;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode;

/**
 * creates a textual representation of the graph.
 * 
 * @author Thomas Durieux
 * 
 */
public class TxtExport extends AbstractExport {

	public TxtExport(DependencyGraph graph, AnalyzerConfig config) {
		super(graph, config);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see DependencyGraphExport#generate()
	 */
	@Override
	public String generate() {
		String content = "";
		if(this.graph == null || this.config == null) {
			return content;
		}
		Map<DependencyNode, List<DependencyNode>> usedNodes = this.graph
				.getUsedNodes();
		Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> usedByNodes = this.graph
				.getUsedByNodes();

		List<DependencyNode> listUsedNodes = new ArrayList<DependencyNode>(
				usedNodes.keySet());
		Collections.sort(listUsedNodes);

		String lastPackage = "";
		for (DependencyNode parent : listUsedNodes) {
			String tab = "    ";
			if (isToIgnore(parent)) {
				continue;
			}
			String pack = parent.getQualifiedName();
			if(pack.equals(parent.getSimpleName()) && !parent.getType().equals(DependencyNode.Type.PACKAGE)) {
				pack = "<UnknowPackage>";
			}
			if (!parent.getType().equals(DependencyNode.Type.PACKAGE)) {
				if (!parent.isAnonymous()) {
					pack = pack.replace("." + parent.getSimpleName(), "");
				}
				if(parent.isInternal()) {
					pack = pack.replace("$" + parent.getSimpleName(), "");
					String[] splitted = pack.split("\\.");
					pack = pack.replace("." + splitted[splitted.length - 1], "");
					tab += tab;
				}
			}
			if (!pack.equals(lastPackage)) {
				content += pack + (parent.isExternal() ? " *" : "") + "\n";
				lastPackage = pack;
			}
			
			if (!parent.getType().equals(DependencyNode.Type.PACKAGE)) {
				String name = parent.getSimpleName();
				if (parent.isAnonymous()) {
					name = "<Anonymous>";
				}
				content += tab + name + (parent.isExternal() ? " *" : "") + "\n";
				tab += tab;
			}

			List<DependencyNode> depList = usedNodes.get(parent);
			List<DependencyNode> depUsedList = new ArrayList<DependencyNode>();
			if (usedByNodes.containsKey(parent)) {
				depUsedList.addAll(usedByNodes.get(parent).keySet());
			}
			List<DependencyNode> dependencies = new ArrayList<DependencyNode>();
			for (DependencyNode dependencyNode : depList) {
				if (!dependencies.contains(dependencyNode)) {
					dependencies.add(dependencyNode);
				}
			}
			for (DependencyNode dependencyNode : depUsedList) {
				if (!dependencies.contains(dependencyNode)) {
					dependencies.add(dependencyNode);
				}
			}

			Collections.sort(dependencies);
			for (DependencyNode dependencyNode : dependencies) {
				if (isToIgnore(dependencyNode)) {
					continue;
				}
				if (depList.contains(dependencyNode)
						&& depUsedList.contains(dependencyNode)) {
					content += tab + "<-> "
							+ dependencyNode.getQualifiedName()
							+ (dependencyNode.isExternal() ? " *" : "") + "\n";
				} else if (depList.contains(dependencyNode)) {
					content += tab + "--> " + dependencyNode.getQualifiedName()
							+ (dependencyNode.isExternal() ? " *" : "") + "\n";
				} else if (depUsedList.contains(dependencyNode)) {
					content += tab + "<-- " + dependencyNode.getQualifiedName()
							+ (dependencyNode.isExternal() ? " *" : "") + "\n";
				}

			}
		}
		return content;
	}

}
