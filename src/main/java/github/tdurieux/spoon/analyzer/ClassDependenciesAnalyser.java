package github.tdurieux.spoon.analyzer;

import github.tdurieux.spoon.graph.DependencyGraph;
import github.tdurieux.spoon.graph.node.DependencyDeclaration;
import github.tdurieux.spoon.graph.node.ClassNode;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;

/**
 * analyzes class dependencies
 */
public class ClassDependenciesAnalyser extends AbstractDependenciesAnalyser {

	public ClassDependenciesAnalyser(DependencyGraph graph) {
		super(graph);
	}

	public ClassDependenciesAnalyser() {
		super();
	}

	public void process(CtTypedElement<?> element) {
		// get the class where the element is used
		CtSimpleType<?> parent = element.getParent(CtSimpleType.class);

		// manage anonymous class
		while (parent.getSimpleName().isEmpty()) {
			parent = parent.getParent(CtSimpleType.class);
		}

		String parentClass = parent.getQualifiedName();

		SourcePosition elementPosition = element.getPosition();

		ClassNode parentNode = new ClassNode(parentClass,
				elementPosition.getLine(), isExternal(parent));

		List<CtTypeReference<?>> listDependencies = getDependencies(element);

		for (CtTypeReference<?> ctTypeReference : new ArrayList<CtTypeReference<?>>(
				listDependencies)) {
			listDependencies.addAll(getDependencies(ctTypeReference));
		}

		for (CtTypeReference<?> ctTypeReference : listDependencies) {
			if (ctTypeReference == null) {
				continue;
			}
			/*try {
				List<DependencyDeclaration> dependenciesElement = getDependenciesOfType(ctTypeReference
						.getActualClass());
				getDependencyGraph().addDependencyNode(parentNode,
						dependenciesElement);
			} catch (NoClassDefFoundError e) {
				// the declaration of the super class is not found
				// ignore it
			}*/
			ClassNode node = new ClassNode(ctTypeReference.getQualifiedName(),
					elementPosition.getLine(), parentClass,
					isExternal(ctTypeReference));
			getDependencyGraph().addDependencyNode(parentNode, node);
		}

	}

	private List<DependencyDeclaration> getDependenciesOfType(
			Class<?> elementClass) {
		List<DependencyDeclaration> classes = new ArrayList<DependencyDeclaration>();
		if (elementClass == null) {
			return classes;
		}
		// interface of t
		Class<?>[] elementInterface = elementClass.getInterfaces();

		try {
			Class<?> superclass = elementClass.getSuperclass();
			if (superclass != null) {
				classes.add(new ClassNode(superclass, 0, true));
				classes.addAll(getDependenciesOfType(superclass));
			}
		} catch (NoClassDefFoundError e) {
			// the declaration of the super class is not found
			// ignore it
		}

		for (Class<?> type : elementInterface) {
			classes.add(new ClassNode(type, 0, true));
			classes.addAll(getDependenciesOfType(type));
		}

		return classes;
	}
}