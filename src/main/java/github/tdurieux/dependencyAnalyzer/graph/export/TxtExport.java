package github.tdurieux.dependencyAnalyzer.graph.export;

import github.tdurieux.dependencyAnalyzer.AnalyzerConfig;
import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyLocation;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * creates a textual representation of the graph.
 *
 * @author Thomas Durieux
 */
public class TxtExport extends AbstractExport {

    private static String TAB = "    ";

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
        if (this.graph == null || this.config == null) {
            return content;
        }
        Map<DependencyNode, List<DependencyNode>> usedNodes = this.graph.getUsedNodes();
        Map<DependencyNode, Map<DependencyNode, List<DependencyLocation>>> usedByNodes = this.graph.getUsedByNodes();

        List<DependencyNode> listUsedNodes = new ArrayList<>(usedNodes.keySet());
        Collections.sort(listUsedNodes);

        String lastPackage = "";
        for (DependencyNode parent : listUsedNodes) {
            String tab = TAB;
            if (isToIgnore(parent)) {
                continue;
            }
            String pack = parent.getQualifiedName();
            if (pack.equals(parent.getSimpleName()) && !parent.getType().equals(DependencyNode.Type.PACKAGE)) {
                pack = "<UnknownPackage>";
            }
            if (parent.getType().equals(DependencyNode.Type.METHOD) ||
                    parent.getType().equals(DependencyNode.Type.CONSTRUCTOR)) {
                if (!parent.isAnonymous()) {
                    pack = pack.replace("::" + parent.getSimpleName(), "");
                }
            }
            if (!parent.getType().equals(DependencyNode.Type.PACKAGE)) {
                if (!parent.isAnonymous()) {
                    pack = pack.replace("." + parent.getSimpleName(), "");
                }
                if (parent.isInternal()) {
                    if (pack.contains("$")) {
                        pack = pack.replace("$" + parent.getSimpleName(), "");
                        if (parent.getType().equals(DependencyNode.Type.METHOD) ||
                                parent.getType().equals(DependencyNode.Type.CONSTRUCTOR)) {
                            tab += TAB;
                        }
                    }
                    String[] splited = pack.split("\\.");
                    pack = pack.replace("." + splited[splited.length - 1], "");
                    tab += TAB;
                }
            }

            if (!pack.equals(lastPackage)) {
                content += pack + (parent.isExternal() ? " *" : "") + "\n";
                lastPackage = pack;
            }

            if (!parent.getType().equals(DependencyNode.Type.PACKAGE)) {
                String name = parent.getSimpleName();
                if (parent.isAnonymous()) {
                    name = "$" + name;
                }
                content += tab + name + (parent.isExternal() ? " *" : "") + "\n";
                tab += TAB;
            }

            List<DependencyNode> depList = usedNodes.get(parent);
            List<DependencyNode> depUsedList = new ArrayList<>();
            if (usedByNodes.containsKey(parent)) {
                depUsedList.addAll(usedByNodes.get(parent).keySet());
            }
            List<DependencyNode> dependencies = new ArrayList<>();
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
            String lastDependencyNodeName = "<not_valid>";
            for (DependencyNode dependencyNode : dependencies) {
                if (isToIgnore(dependencyNode)) {
                    continue;
                }
                String arrow = "<->";
                if (depList.contains(dependencyNode)
                        && depUsedList.contains(dependencyNode)) {
                    arrow = "<->";
                } else if (depList.contains(dependencyNode)) {
                    arrow = "-->";
                } else if (depUsedList.contains(dependencyNode)) {
                    arrow = "<--";
                }
                if (dependencyNode.getQualifiedName().startsWith(lastDependencyNodeName)) {
                    String qualifiedName = dependencyNode.getQualifiedName().replace(lastDependencyNodeName, "");
                    if (qualifiedName.startsWith("::")) {
                        qualifiedName = qualifiedName.substring(2);
                    } else if (qualifiedName.startsWith(".")) {
                        qualifiedName = qualifiedName.substring(1);
                    }
                    content += tab + arrow + TAB + " "
                            + qualifiedName;
                } else {
                    content += tab + arrow + " "
                            + dependencyNode.getQualifiedName();
                    lastDependencyNodeName = dependencyNode.getQualifiedName();
                }
                content += (dependencyNode.isExternal() ? " *" : "") + "\n";


            }
        }
        return content;
    }

}
