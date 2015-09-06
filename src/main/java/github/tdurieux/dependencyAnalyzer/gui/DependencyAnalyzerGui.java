package github.tdurieux.dependencyAnalyzer.gui;

import github.tdurieux.dependencyAnalyzer.AnalyzerConfig;
import github.tdurieux.dependencyAnalyzer.AnalyzerConfig.Level;
import github.tdurieux.dependencyAnalyzer.DependencyAnalyzer;
import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;
import github.tdurieux.dependencyAnalyzer.graph.export.TxtExport;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * is a basic UI for the project
 *
 * @author Thomas Durieux
 */
public class DependencyAnalyzerGui extends JFrame {

    private static final long serialVersionUID = -7301931419288014369L;

    private DependencyAnalyzer dependencyAnalyzer;

    private final AnalyzerConfig config = new AnalyzerConfig();

    public DependencyAnalyzerGui() {
        // create the window
        setTitle("Dependency analyzer");
        setSize(800, 600); // default size is 0,0

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        final JTextArea jtGraph = new JTextArea();
        jtGraph.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(jtGraph,
                                                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JButton jbOpenProject = new JButton("Open project");
        jbOpenProject.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle("chooserTitle");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    config.setProjectPath(chooser.getSelectedFile()
                                                  .getAbsolutePath());
                    try {
                        dependencyAnalyzer = new DependencyAnalyzer(config
                                                                            .getProjectPath(), config);

                        DependencyGraph graph = dependencyAnalyzer.run();
                        TxtExport txtExport = new TxtExport(graph, config);
                        jtGraph.setText(txtExport.generate());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

            }
        });
        toolbar.add(jbOpenProject);

        JButton jbAnalyze = new JButton("Analyze");
        jbAnalyze.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dependencyAnalyzer != null) {
                    DependencyGraph graph = dependencyAnalyzer.run();
                    TxtExport txtExport = new TxtExport(graph, config);
                    jtGraph.setText(txtExport.generate());
                }
            }
        });
        toolbar.add(jbAnalyze);

        String[] levels = {"Package", "Class"};

        final JComboBox<String> jcLevels = new JComboBox<>(levels);
        jcLevels.setSelectedIndex(0);
        jcLevels.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String level = (String) jcLevels.getSelectedItem();
                if (level.equals("Package")) {
                    config.setLevel(Level.PACKAGE);
                } else if (level.equals("Class")) {
                    config.setLevel(Level.CLASS);
                }

                if (dependencyAnalyzer != null) {
                    DependencyGraph graph = dependencyAnalyzer.run();
                    TxtExport txtExport = new TxtExport(graph, config);
                    jtGraph.setText(txtExport.generate());
                }
            }
        });

        toolbar.add(jcLevels);

        final JCheckBox jchExternalDep = new JCheckBox(
                                                              "Ignore external dependencies");
        jchExternalDep.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                config.setIgnoreExternalDependencies(jchExternalDep.isSelected());
                if (dependencyAnalyzer != null) {
                    DependencyGraph graph = dependencyAnalyzer.run();
                    TxtExport txtExport = new TxtExport(graph, config);
                    jtGraph.setText(txtExport.generate());
                }
            }
        });
        toolbar.add(jchExternalDep);

        add(toolbar, BorderLayout.NORTH);

    }
}
