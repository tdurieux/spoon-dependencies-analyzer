package github.tdurieux.spoon.graph.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.martiansoftware.jsap.JSAPResult;

import github.tdurieux.spoon.graph.DependencyGraph;
import github.tdurieux.spoon.graph.node.DependencyNode;

public class TxtExport extends AbstractExport {

	public TxtExport(DependencyGraph graph, JSAPResult config) {
		super(graph, config);
	}

	@Override
	public String generate() {
		String content = "";
		Map<DependencyNode, List<DependencyNode>> usedNodes = this.graph
				.getUsedNodes();
		Map<DependencyNode, List<DependencyNode>> usedByNodes = this.graph
				.getUsedByNodes();

		List<DependencyNode> listUsedNodes = new ArrayList<DependencyNode>(
				usedNodes.keySet());
		Collections.sort(listUsedNodes);

		for (DependencyNode parent : listUsedNodes) {
			if (isToIgnore(parent)) {
				continue;
			}
			content += parent.getName() + (parent.isExternal() ? " *" : "")
					+ "\n";
			if (usedByNodes.containsKey(parent)) {
				Collections.sort(usedByNodes.get(parent));
				for (DependencyNode nodeDep : usedByNodes.get(parent)) {
					if (isToIgnore(nodeDep)) {
						continue;
					}
					content += "    <-- " + nodeDep.getName()
							+ (nodeDep.isExternal() ? " *" : "") + "\n";
				}
			}

			List<DependencyNode> depList = usedNodes.get(parent);
			Collections.sort(depList);
			for (DependencyNode dependencyNode : depList) {
				if (isToIgnore(dependencyNode)) {
					continue;
				}
				content += "    --> " + dependencyNode.getName()
						+ (dependencyNode.isExternal() ? " *" : "") + "\n";
			}
		}
		return content;
	}

}
