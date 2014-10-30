package github.tdurieux.dependencyAnalyzer.graph.node;

import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.reference.CtReference;

/**
 * is an abstract class which provides common methods to all node factories.
 * 
 * @author Thomas Durieux
 * 
 */
public abstract class AbstractNodeFactory implements NodeFactory {

	public AbstractNodeFactory() {
		super();
	}

	/**
	 * detects if the element is declared in an external source.
	 * 
	 * @param e
	 *            the element
	 * @return true if the element is declared in an external source.
	 */
	protected boolean isExternal(CtReference e) {
		return isExternal(e.getDeclaration());
	}

	/**
	 * detects if the element is declared in an external source.
	 * 
	 * @param e
	 *            the element
	 * @return true if the element is declared in an external source.
	 */
	protected boolean isExternal(CtElement e) {
		if (e instanceof CtPackage) {
			return false;
		}
		if (e == null) {
			return true;
		}
		SourcePosition position = e.getPosition();

		if (position != null) {
			position.getFile();
		} else {
			return true;
		}
		return false;
	}

}