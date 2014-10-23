package github.tdurieux.spoon.analyser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtTypedElement;

/**
 * Reports warnings when empty catch blocks are found.
 */
public class PackageDependenciesAnalyser extends
		AbstractProcessor<CtTypedElement<?>> {

	public Map<Package, List<Package>> usedPackages = new HashMap<Package, List<Package>>();
	public Map<Package, List<Package>> packagesUsedBy = new HashMap<Package, List<Package>>();

	
	
	public void process(CtInvocation<?> element) {
		element.getExecutable().getType();
	}

	public void process(CtMethod<?> element) {
		if (element.getType() != null && element.getType().getPackage() != null) {
			Package parentPackage = getPackageOfParent(element);
			CtPackage elementPackage = element.getType().getPackage()
					.getDeclaration();
		}

	}

	public void process(CtTypedElement element) {
		if (element.getType() != null && element.getType().getActualClass() != null) {
			Package parentPackage = getPackageOfParent(element);
			Package elementPackage = element.getType().getActualClass().getPackage();
			List<Package> dependenciesElement = getDependenciesOfType(element
					.getType().getActualClass());
			if (!usedPackages.containsKey(elementPackage)) {
				usedPackages.put(elementPackage, new ArrayList<Package>());
			}
			List<Package> usedPackagesElement = usedPackages.get(elementPackage);
			for (Package ctPackage : dependenciesElement) {
				if (!usedPackagesElement.contains(ctPackage)) {
					usedPackagesElement.add(ctPackage);
				}
			}
			if (parentPackage != null) {
				if (!usedPackages.containsKey(parentPackage)) {
					usedPackages.put(parentPackage, new ArrayList<Package>());
				}
				if (!usedPackages.get(parentPackage).contains(elementPackage))
					usedPackages.get(parentPackage).add(elementPackage);
				if (!packagesUsedBy.containsKey(elementPackage)) {
					packagesUsedBy.put(elementPackage,
							new ArrayList<Package>());
				}
				if (!packagesUsedBy.get(elementPackage).contains(parentPackage))
					packagesUsedBy.get(elementPackage).add(parentPackage);
			}
		}
	}
	
//	public void process(CtElement statement) {
//		if (statement instanceof CtInvocation<?>) {
//			process((CtInvocation) statement);
//		} else if (statement instanceof CtMethod<?>) {
//			process((CtMethod) statement);
//		} else if (statement instanceof CtTypedElement) {
//			process((CtTypedElement) statement);
//		}
//	}

	private List<Package> getDependenciesOfType(Class class1) {
		List<Package> packages = new ArrayList<Package>();
		if (class1 == null) {
			return packages;
		}
		Class[] elementInterface = class1.getInterfaces();
		if (class1.getSuperclass() != null  && !packages.contains(class1.getSuperclass().getPackage())) {
			packages.add(class1.getSuperclass().getPackage());
			packages.addAll(getDependenciesOfType(class1.getSuperclass()));
		}
		for (Class type : elementInterface) {
			Package ctPackage = type.getPackage();
			if (!packages.contains(ctPackage)) {
				packages.add(ctPackage);
			}
			packages.addAll(getDependenciesOfType(type));
		}
		return packages;
	}

	private Package getPackageOfParent(CtElement element) {
		CtClass<?> ctClass = element.getParent(CtClass.class);
		if (ctClass == null)
			return null;
		return ctClass.getActualClass().getPackage();
	}

	@Override
	public void processingDone() {
		for (Package pack : usedPackages.keySet()) {
			if(pack == null) {
				continue;
			}
			System.out.println(pack.getName());
			for (Package packDep : usedPackages.get(pack)) {
				if(packDep == null) continue;
				System.out.println("\t --> " + packDep.getName());
			}
			if (packagesUsedBy.containsKey(pack)) {
				for (Package packDep : packagesUsedBy.get(pack)) {
					if(packDep == null) continue;
					System.out.println("\t <-- " + packDep.getName());
				}
			}
		}
	}
}