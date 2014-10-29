package github.tdurieux.spoon.graph.node;

public abstract class DependencyDeclaration {

	private boolean isExternal;

	private String name;

	private int line;

	private String qualifiedClass;

	public DependencyDeclaration(String name, int line, String qualifiedClass,
			boolean isExternal) {
		this.isExternal = isExternal;
		this.name = name;
		this.line = line;
		this.qualifiedClass = qualifiedClass;
	}

	public String getName() {
		return name;
	}

	public int getLine() {
		return line;
	}

	public String getQualifiedClass() {
		return qualifiedClass;
	}

	public boolean isExternal() {
		return isExternal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + line;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((qualifiedClass == null) ? 0 : qualifiedClass.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DependencyDeclaration other = (DependencyDeclaration) obj;
		if (line != other.line)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (qualifiedClass == null) {
			if (other.qualifiedClass != null)
				return false;
		} else if (!qualifiedClass.equals(other.qualifiedClass))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.name + " at " + this.qualifiedClass + ":" + this.line + " "
				+ (isExternal ? "*" : "");
	}
}
