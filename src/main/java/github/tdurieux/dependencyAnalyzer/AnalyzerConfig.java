package github.tdurieux.dependencyAnalyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * The analyzer configuration
 * 
 * @author Thomas Durieux
 * 
 */
public class AnalyzerConfig {

	/**
	 * The analyze levels available
	 * 
	 * @author Thomas Durieux
	 * 
	 */
	public enum Level {
		PACKAGE, CLASS
	}

	/**
	 * The output format available
	 * 
	 * @author Thomas Durieux
	 * 
	 */
	public enum OutputFormat {
		DOT, TXT
	}

	private List<String> ignoreRegex = new ArrayList<>();

	private boolean ignoreExternalDependences = false;

	private String classPath;

	private Level level = Level.PACKAGE;

	private OutputFormat outputFormat = OutputFormat.TXT;

	private String output;

	private boolean verbose = false;

	private String projectPath;

	public boolean isIgnoreExternalDependences() {
		return ignoreExternalDependences;
	}

	public void setIgnoreExternalDependences(boolean ignoreExternalDependences) {
		this.ignoreExternalDependences = ignoreExternalDependences;
	}

	public void addIgnoreRegex(String ignoreRegex) {
		this.ignoreRegex.add(ignoreRegex);
	}

	public List<String> getIgnoreRegex() {
		return ignoreRegex;
	}

	public String getClassPath() {
		return this.classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public OutputFormat getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(OutputFormat outputFormat) {
		this.outputFormat = outputFormat;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public boolean isVerbose() {
		return this.verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;

	}

	public String getProjectPath() {
		return projectPath;
	}
}
