package github.tdurieux.dependencyAnalyzer.spoon.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import github.tdurieux.dependencyAnalyzer.graph.DependencyGraph;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyLocation;
import github.tdurieux.dependencyAnalyzer.graph.node.DependencyNode;
import github.tdurieux.dependencyAnalyzer.graph.node.LocationFactory;
import github.tdurieux.dependencyAnalyzer.graph.node.NodeFactory;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;

/**
 * Spoon processor used to analyze project dependencies.
 * 
 * @author Thomas Durieux
 * 
 */
public class DependencyAnalyzer extends AbstractProcessor<CtTypedElement<?>> {

	private DependencyGraph dependencyGraph;
	private NodeFactory nodeFacotry;
	private LocationFactory locationFactory;

	protected boolean versbose = false;

	public DependencyAnalyzer(DependencyGraph dependencyGraph,
			NodeFactory nodeFacotry, LocationFactory locationFactory) {
		this.dependencyGraph = dependencyGraph;
		this.nodeFacotry = nodeFacotry;
		this.locationFactory = locationFactory;
	}

	public DependencyAnalyzer(NodeFactory nodeFacotry,
			LocationFactory locationFactory) {
		this(new DependencyGraph(), nodeFacotry, locationFactory);
	}

	@Override
	public void process(CtTypedElement<?> element) {
		DependencyLocation dependencyLocation = locationFactory
				.createDependencyLocation(element);

		List<CtTypeReference<?>> listDependencies = getDependencies(element);

		for (CtTypeReference<?> ctTypeReference : new ArrayList<CtTypeReference<?>>(
				listDependencies)) {
			listDependencies.addAll(getDependencies(ctTypeReference));
		}

		for (CtTypeReference<?> ctTypeReference : listDependencies) {
			if (ctTypeReference == null) {
				continue;
			}
			DependencyNode node = nodeFacotry
					.createDependencyNode(ctTypeReference);

			getDependencyGraph().addDependencyNode(node, dependencyLocation);
		}

	}

	/**
	 * get all dependencies added by a CtTypeReference
	 * 
	 * @param ctTypeReference
	 * @return all dependencies added by ctTypeReference
	 */
	protected List<CtTypeReference<?>> getDependencies(
			CtTypeReference<?> ctTypeReference) {

		List<CtTypeReference<?>> listDependencies = new ArrayList<CtTypeReference<?>>();

		if (ctTypeReference == null) {
			return listDependencies;
		}

		CtSimpleType<?> ctSimpleType = ctTypeReference.getDeclaration();

		if (ctSimpleType instanceof CtClass<?>) {
			CtClass<?> tmp = (CtClass<?>) ctSimpleType;
			// super class
			CtTypeReference<?> superclass = tmp.getSuperclass();
			if (superclass != null) {
				listDependencies.add(superclass);
				listDependencies.addAll(getDependencies(superclass));
			}
		}

		if (ctSimpleType instanceof CtType<?>) {
			CtType<?> tmp = (CtType<?>) ctSimpleType;
			// interfaces
			Set<CtTypeReference<?>> interfaces = tmp.getSuperInterfaces();
			for (CtTypeReference<?> interfaceType : interfaces) {
				listDependencies.add(interfaceType);
				listDependencies.addAll(getDependencies(interfaceType));
			}
		}
		return listDependencies;
	}

	/**
	 * get all dependencies added by a CtTypedElement
	 * 
	 * @param element
	 * @return all dependencies added by element
	 */
	private List<CtTypeReference<?>> getDependencies(CtTypedElement<?> element) {
		List<CtTypeReference<?>> listDependencies = new ArrayList<CtTypeReference<?>>();

		// annotations
		List<CtAnnotation<?>> annotations = element.getAnnotations();
		for (CtAnnotation<?> ctAnnotation : annotations) {
			listDependencies.add(ctAnnotation.getAnnotationType());

			// annotations elements
			Map<String, Object> elements = ctAnnotation.getElementValues();
			for (Object elementValue : elements.values()) {
				if (elementValue instanceof CtTypeReference<?>) {
					listDependencies.add((CtTypeReference<?>) elementValue);
				} else if (elementValue instanceof CtTypedElement<?>) {
					listDependencies.add(((CtTypedElement<?>) elementValue)
							.getType());
				}
			}
		}

		// Literal
		if (element instanceof CtLiteral<?>) {
			CtLiteral<?> literal = (CtLiteral<?>) element;
			literal.getValue();

			if (literal.getValue() instanceof CtTypeReference<?>) {
				listDependencies.add((CtTypeReference<?>) literal.getValue());
			} else if (literal.getValue() instanceof CtTypedElement<?>) {
				listDependencies.add(((CtTypedElement<?>) literal.getValue())
						.getType());
			}
		}

		// method invocation
		if (element instanceof CtInvocation<?>) {
			CtInvocation<?> invocation = (CtInvocation<?>) element;

			// the class of the method
			listDependencies.add(invocation.getExecutable().getDeclaringType());

			// parameters
			List<CtTypeReference<?>> parameters = invocation.getExecutable()
					.getActualTypeArguments();
			listDependencies.addAll(parameters);
		}

		// method declaration
		if (element instanceof CtMethod<?>) {
			CtMethod<?> method = (CtMethod<?>) element;
			// exceptions
			Set<CtTypeReference<? extends Throwable>> thrownTypes = method
					.getThrownTypes();
			for (CtTypeReference<? extends Throwable> ctTypeReference : thrownTypes) {
				listDependencies.add(ctTypeReference);
			}
		}

		listDependencies.add(element.getType());
		return listDependencies;
	}

	public DependencyGraph getDependencyGraph() {
		return dependencyGraph;
	}
}