package github.tdurieux.dependencyAnalyzer.gui;

import github.tdurieux.dependencyAnalyzer.AnalyzerConfig;
import github.tdurieux.dependencyAnalyzer.DependencyAnalizer;
import github.tdurieux.dependencyAnalyzer.AnalyzerConfig.Level;
import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;
import github.tdurieux.dependencyAnalyzer.graph.export.TxtExport;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * is a basic UI for the project
 * 
 * @author Thomas Durieux
 * 
 */
public class DependencyAnalyzerGui extends JFrame {

	private static final long serialVersionUID = -7301931419288014369L;

	private DependencyAnalizer dependencyAnalizer;

	private AnalyzerConfig config = new AnalyzerConfig();

	public DependencyAnalyzerGui() {
		// create the window
		setTitle("Dependency analyzer");
		setSize(800, 600); // default size is 0,0

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
				chooser.setDialogTitle("choosertitle");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);

				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					config.setProjectPath(chooser.getSelectedFile()
							.getAbsolutePath());
					try {
						dependencyAnalizer = new DependencyAnalizer(config
								.getProjectPath(), config);

						DependencyGraph graph = dependencyAnalizer.run();
						TxtExport txtExport = new TxtExport(graph, config);
						jtGraph.setText(txtExport.generate());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

			}
		});
		toolbar.add(jbOpenProject);

		JButton jbAnalize = new JButton("Analyze");
		jbAnalize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (dependencyAnalizer != null) {
					DependencyGraph graph = dependencyAnalizer.run();
					TxtExport txtExport = new TxtExport(graph, config);
					jtGraph.setText(txtExport.generate());
				}
			}
		});
		toolbar.add(jbAnalize);

		String[] levels = { "Package", "Class" };

		final JComboBox<String> jcLevels = new JComboBox<String>(levels);
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

				if (dependencyAnalizer != null) {
					DependencyGraph graph = dependencyAnalizer.run();
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
				config.setIgnoreExternalDependences(jchExternalDep.isSelected());
				if (dependencyAnalizer != null) {
					DependencyGraph graph = dependencyAnalizer.run();
					TxtExport txtExport = new TxtExport(graph, config);
					jtGraph.setText(txtExport.generate());
				}
			}
		});
		toolbar.add(jchExternalDep);

		add(toolbar, BorderLayout.NORTH);

	}
}
