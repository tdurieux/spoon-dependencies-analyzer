package github.tdurieux.spoon.graph.node;

import java.util.ArrayList;
import java.util.List;

public class DependencyNodeImpl implements DependencyNode {

	private String name;
	
	private boolean isExternal;

	private List<DependencyDeclaration> dependencieLocalizations;

	public DependencyNodeImpl(String name, boolean isExternal) {
		this.name = name;
		this.isExternal = isExternal;
		this.dependencieLocalizations = new ArrayList<DependencyDeclaration>();
	}

	public String getName() {
		return this.name;
	}

	public List<DependencyDeclaration> getDeclarationLocalization() {
		return new ArrayList<DependencyDeclaration>(this.dependencieLocalizations);
	}
	
	public void addDeclarationLicalization(DependencyDeclaration localization) {
		this.dependencieLocalizations.add(localization);
	}
	
	public void removeDeclarationLicalization(
			DependencyDeclaration localization) {
		this.dependencieLocalizations.remove(localization);
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof DependencyNode)) {
			return false;
		}
		return this.name.equals(((DependencyNode) obj).getName());
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public String toString() {
		return this.name;
	}

	public int compareTo(DependencyNode o) {
		return this.name.compareTo(o.getName());
	}

	public boolean isExternal() {
		return this.isExternal;
	}
}
