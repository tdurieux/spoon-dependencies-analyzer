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
			if (!parent.getType().equals(DependencyNode.Type.PACKAGE)) {
				pack = pack.replace("." + parent.getSimpleName(), "");
				if(parent.isInternal()) {
					pack = pack.replace("$" + parent.getSimpleName(), "");
					String[] splitted  = pack.split("\\.");
					pack = pack.replace("." + splitted[splitted.length - 1], "");
					tab += tab;
				}
			}
			if(!pack.equals(lastPackage)) {
				content += pack + (parent.isExternal() ? " *" : "") + "\n";
				lastPackage = pack;
			}
			if (!parent.getType().equals(DependencyNode.Type.PACKAGE)) {
				content += tab + parent.getSimpleName() + (parent.isExternal() ? " *" : "") + "\n";
				tab += tab;
			}
			if (usedByNodes.containsKey(parent)) {
				List<DependencyNode> list = new ArrayList<DependencyNode>(
						usedByNodes.get(parent).keySet());
				Collections.sort(list);
				for (DependencyNode nodeDep : list) {
					if (isToIgnore(nodeDep)) {
						continue;
					}
					content += tab + "<-- " + nodeDep.getQualifiedName()
							+ (nodeDep.isExternal() ? " *" : "") + "\n";
				}
			}

			List<DependencyNode> depList = usedNodes.get(parent);
			Collections.sort(depList);
			for (DependencyNode dependencyNode : depList) {
				if (isToIgnore(dependencyNode)) {
					continue;
				}
				content += tab + "--> " + dependencyNode.getQualifiedName()
						+ (dependencyNode.isExternal() ? " *" : "") + "\n";
			}
		}
		return content;
	}

}
