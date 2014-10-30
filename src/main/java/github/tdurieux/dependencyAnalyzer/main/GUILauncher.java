package github.tdurieux.dependencyAnalyzer.main;

import github.tdurieux.dependencyAnalyzer.gui.DependencyAnalyzerGui;

/**
 * The GUI launcher
 * 
 * @author thomas
 * 
 */
public class GUILauncher {

	public static void main(String[] args) {
		DependencyAnalyzerGui f = new DependencyAnalyzerGui();
		f.setVisible(true);
	}
}
