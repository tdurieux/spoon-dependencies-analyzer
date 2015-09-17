package github.tdurieux.dependencyAnalyzer.gui;

import github.tdurieux.dependencyAnalyzer.AnalyzerConfig;
import github.tdurieux.dependencyAnalyzer.AnalyzerConfig.Level;
import github.tdurieux.dependencyAnalyzer.DependencyAnalyzer;
import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;
import github.tdurieux.dependencyAnalyzer.graph.export.TxtExport;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * is a basic UI for the project
 *
 * @author Thomas Durieux
 */
public class DependencyAnalyzerGui extends JFrame {

    private static final long serialVersionUID = -7301931419288014369L;

    private DependencyAnalyzer dependencyAnalyzer;

    private final AnalyzerConfig config = AnalyzerConfig.INSTANCE;

    public DependencyAnalyzerGui() {
        // create the window
        setTitle("Dependency analyzer");
        setSize(800, 600); // default size is 0,0

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        showOnScreen(1);

        final JTextArea jtGraph = new JTextArea();
        jtGraph.setEditable(false);
        DefaultCaret caret = (DefaultCaret) jtGraph.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        final JScrollPane scrollPane = new JScrollPane(jtGraph,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        final String[] lastDirectory = {"."};
        JButton jbOpenProject = new JButton("Open project");
        jbOpenProject.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser() {
                    protected JDialog createDialog(Component parent) throws HeadlessException {
                        JDialog dialog = super.createDialog(parent);
                        Point p = calculateCenter(dialog);
                        dialog.setLocation(p.x, p.y);
                        return dialog;
                    }
                };
                chooser.setLocation(calculateCenter(chooser));
                chooser.setCurrentDirectory(new File(lastDirectory[0]));
                chooser.setDialogTitle("Select the source directory");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    lastDirectory[0] = chooser.getSelectedFile().getAbsolutePath();
                    config.setProjectPath(chooser.getSelectedFile().getAbsolutePath());
                    try {
                        dependencyAnalyzer = new DependencyAnalyzer(config.getProjectPath(), config);

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

        String[] levels = {"Package", "Class", "Method"};

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
                } else if (level.equals("Method")) {
                    config.setLevel(Level.METHOD);
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

    public void showOnScreen(int screen) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        if (gd.length > 0) {
            if (screen >= gd.length) {
                screen = 0;
            }
            DisplayMode mode = gd[screen].getDisplayMode();
            int screenWidth = mode.getWidth();
            int screenHeight = mode.getHeight();
            int x = gd[screen].getDefaultConfiguration().getBounds().x;
            int y = gd[screen].getDefaultConfiguration().getBounds().y;
            x += (screenWidth - getWidth()) / 2;
            y += (screenHeight - getHeight()) / 2;
            setLocation(x, y);
        } else {
            throw new RuntimeException("No Screens Found");
        }
    }

    public static Point calculateCenter(Container popupFrame) {
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        Window windowFrame = kfm.getFocusedWindow();
        Point frameTopLeft = windowFrame.getLocation();
        Dimension frameSize = windowFrame.getSize();
        Dimension popSize = popupFrame.getSize();

        int x = (int) (frameTopLeft.getX() + (frameSize.width / 2) - (popSize.width / 2));
        int y = (int) (frameTopLeft.getY() + (frameSize.height / 2) - (popSize.height / 2));
        Point center = new Point(x, y);
        return center;
    }
}
