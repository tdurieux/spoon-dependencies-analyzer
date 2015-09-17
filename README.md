[![Build Status](https://travis-ci.org/tdurieux/spoon-dependencies-analizer.svg)](https://travis-ci.org/tdurieux/spoon-dependencies-analizer) [![Coverage Status](https://coveralls.io/repos/tdurieux/spoon-dependencies-analizer/badge.svg?branch=master&service=github)](https://coveralls.io/github/tdurieux/spoon-dependencies-analizer?branch=master)

# Extraction du graphe de dépendances avec Spoon

```text
Jérémy Bossut
Thomas Durieux
5 novembre 2014
```
<!-- MarkdownTOC depth=3 -->

- [Introduction](#introduction)
- [Approche](#approche)
- [Analyse des résultats](#analyse-des-résultats)
- [Validation des résultats](#validation-des-résultats)
- [Pistes d'amélioration](#pistes-damélioration)
- [Usage](#usage)
  - [Dépendances](#dépendances)
  - [Interface utilisateur](#interface-utilisateur)
  - [Lignes de commande](#lignes-de-commande)
- [Tests](#tests)
- [Références](#références)

<!-- /MarkdownTOC -->


## Introduction

Cette article traite de la génération de graphes de dépendances pour les projets Java à l'aide de la librairie Spoon.

Les graphes de dépendances permettent de visualiser les dépendances d'un projet. L'analyse des dépendances peut se faire à plusieurs niveaux en fonction du niveau de précision souhaitée:

- au niveau des paquets,
- au niveau des classes,
- et au niveau des méthodes (non traité).

#### Dépendances au niveau des paquets
![dépendances au niveau des paquets](https://cloud.githubusercontent.com/assets/5577568/4902556/144b3eec-643a-11e4-99f2-eeee8df01569.png)

#### Dépendances au niveau des classses
![dépendances au niveau des classses](https://cloud.githubusercontent.com/assets/5577568/4902558/16a15a32-643a-11e4-9a7e-c2010373e2e1.png)


L'analyse des dépendances a plusieurs intérêts: 
elle permet de rapidement comprendre l'architecture du projet,
d'identifier la complexité d'un projet (nombres de dépendances, nombres de classes, paquets, méthodes) ou 
encore d'analyser la qualité de l'architecture (bonne découpe du projet en packages, interaction entre les paquets, présence de dépendances cycliques). Elle permet également d'éviter le ["morning-after syndrome"] [1] décrit par Uncle Bob Martin.

Spoon est utilisé comme librairie d'AST pour parcourir le code du projet. Spoon a la particularité d'analyser le code source des applications et offre une API de transformation et de parcours claire. Spoon utilise le principe de processeur. Les processeurs permettent de parcourir le code, certains de ces processeurs permettent de filtrer les types des éléments parcourus. Afin de valider ce projet, les résultats sont comparés avec ceux de Dependency Finder. Dependency Finder est un logiciel qui permet d'extraire des graphes de dépendances à partir de code compilé Java.

Dans les sections suivantes, l'approche, l'analyse et la validation des résultats, des pistes d'améliorations puis l'usage du projet seront présentés.

## Approche

Afin de déterminer toutes les dépendances d'un projet, il faut dans un premier temps identifier tous les éléments susceptibles d'ajouter des dépendances au projet.
Les éléments suivants ont été identifiés comme étant source potentielle de dépendances:

Niveau Classe			|   Niveau Méthode 
--------------------------------|--------------------------------------
Interfaces implémentées		|   Type de retour
Classe parente			|   Paramètres
Attributs			|   Paramètres des méthodes invoquées
Variables locales		|   Exceptions lancées
Assignations			|   Exceptions interceptées
Classes instanciées		|   Annotations
Classes des méthodes invoquées	|   
Enumerations		     	|   
Classes anonymes	     	|
Constantes	     		|

Tous ces éléments (à l'exception des annotations) sont encapsulés par Spoon sous le type: CtTypedElement. Un processeur traitant les CtTypedElement a été réalisé. Ce processeur a comme rôle de générer l'arbre de dépendances.

A partir de ce graphe de dépendances, il est possible de générer deux représentations. Le premier format de sortie est celui utilisé par Dependency Finder. Le second format est le format utilisé par la librairie Graphviz qui permet de générer des graphes sous forme d'image (png, jpg, pdf, svg, ...).

## Analyse des résultats 

Lors de l'analyse du projet Spoon, cet outil détecte 57 dépendances au niveau des paquets et 635 dépendances au niveau des classes tandis que Dependency Finder en détecte 4 de plus dans les deux cas. La différence s'explique par le fait que Dependency Finder analyse le code compilé alors que Dependency Analyzer analyse le code source. Le format de sortie par défaut étant celui de Dependency Finder, les résultats sont tout aussi lisibles.

Les graphes de dépendances au niveau des classes sont plus difficilement exploitables à cause de la quantité importante d'informations générées par un projet non trivial. Le temps d'exécution de Dependency Analyzer peut être relativement long (1'15'' pour analyser Spoon). De plus, la génération d'images à partir de Graphviz peut ne jamais aboutir sur des gros projets (par exemple la génération de l'image du graphe de dépendances du projet Spoon par Graphviz n'a jamais abouti). 

Afin d'améliorer la lisibilité des résultats, plusieurs solutions optionnelles ont été implémentées: 
- identifier et masquer les classes ne provenant pas du projet analysé (librairies externes, les classes Java, ...),
- ajout de la possibilité de filtrer les dépendances sur base d'expressions régulières.

Ces solutions induisent en contrepartie une perte d'informations. 

Une solution qui reste à investiguer est de produire une interface qui permet de filtrer dynamiquement la vue du graphe. Plusieurs types d'interface sont envisageables:
- améliorer l'interface utilisateur actuelle en y ajoutant le support des filtres,
- créer une page HTML/JavaScript qui permettrait de filtrer dynamiquement les éléments du graphe. Plusieurs librairies JavaScript permettent de faciliter cette réalisation comme par exemple la librairie sigma.js. Cette solution offre l'avantage de pouvoir facilement partager le graphe de dépendances.

## Validation des résultats

La validation a été effectuée sur différents projets de taille variable. Les différents projets testés sont ce projet, le projet servant aux tests unitaires de ce projet, Spoon, un projet faisant appel à de nombreuses classes anonymes. Les résultats de ces analyses se trouvent dans le dossier examples et sont de la forme *NomDuProjetTesté-OutilUtilisé.NiveauDeDependances\[.extension\]* où *OutilUtilisé* est DependencyFinder ou DependencyAnalyzer et *NiveauDeDependances* est p2p pour les packages et c2c pour les classes.

Les graphes de dépendances au niveau des paquets sont très similaires. 
Il y a néanmoins quelques différences:

- ce projet considère les annotations comme des dépendances au contraire de Dependency Finder,
- certains éléments sont générés à la compilation et ne sont donc pas visibles par ce projet: les fichiers package-info.java sont transformés en classes à la compilation et sont donc détectés par Dependency Finder, les énumerations sont également transformées à la compilation en classes héritant de la classe Enum de Java ce qui ajoute une dépendance non visible par l'analyse du code source.

Les quelques différences détectées au niveau des paquets sont également présentes au niveau des classes.

- Dependency Finder considère tous les héritages. Cet outil se limite aux héritages des classes et interfaces déclarées dans le projet,
- les classes anonymes sont numérotées à la compilation et apparaissent donc différemment dans Dependency Finder alors que cet outil les nomme \<Anonymous\>,
- les classes internes ne sont pas considérées comme des dépendances dans les classes parentes.

![image](https://cloud.githubusercontent.com/assets/5577568/4906881/a42b8d9c-645c-11e4-8a98-ab6cbbb02786.png)
Différences entre le graphe de dépendances de Dependency Finder (à gauche) et le graphe de dépendances de Dependency Analyzer (à droite).

## Pistes d'amélioration

Plusieurs pistes d'amélioration sont envisageables:

- gérer dans les exports la position d'utilisation des dépendances,
- gérer dans les exports le type de dépendances (classe, interfaces, énumération, annotation,...)
- détecter dans quel contexte la dépendance est utilisée (variable locale, paramètre, variable de classe, héritage, ...),
- créer un format de sortie qui supporte les filtres dynamiques,
- analyser les dépendances au niveau des méthodes.

## Usage

L'outil a été réalisé en Java et peut être lancé soit via une interface d'utilisateur rudimentaire, soit via un programme en lignes de commande.

### Dépendances

- Java: > v.7
- Spoon: > v.3
- Jsap: v.2.1

### Interface utilisateur

Lancer l'interface utilisateur à partir de maven:
```bash
mvn exec:java
```

![depanalyzer](https://cloud.githubusercontent.com/assets/5577568/4861912/a3c49d9c-6107-11e4-8745-cd7f4a0e33e7.gif)


### Lignes de commande

```text
Usage:
  DependencyGraph [--help] <project> [<classpath>] [(-l|--level) <level>]
  [(-f|--format) <format>] [(-o|--out) <output>]
  [(-j|--ignore-external)[:<ignore-external>]] [(-v|--verbose)[:<verbose>]]
  [(-i|--ignore) ignore1,ignore2,...,ignoreN ]

Get the dependency graph of a project

  [--help]
        Prints this help message.

  <project>
        The path to the project

  [<classpath>]
        The claspath of the project

  [(-l|--level) <level>]
        The level of dependency analyzis (package, class, method). (default:
        package)

  [(-f|--format) <format>]
        The ouput format of the script (txt, dot). (default: txt)

  [(-o|--out) <output>]
        The ouput file of the script (txt, dot).

  [(-j|--ignore-external)[:<ignore-external>]]
        Ignore external depencencies.

  [(-v|--verbose)[:<verbose>]]
        Requests verbose output.

  [(-i|--ignore) ignore1,ignore2,...,ignoreN ]
        Regex to ignore element, sepearated by a ','.
```

#### Exemple

```Bash
mvn package

java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar \
    <projectPath> \
    --ignore-external \
    --ignore '.*(Impl)$','^([^\.]+\.)*(?!Ct)[^\.]+$','.*Abstract.*'\
    --format dot \
    --level class;
```

## Tests

Les tests se basent sur un projet exemple qui contient les sources potentielles de dépendances précédemment indentifiées. Ils vérifient la présence de dépendances, le type de la dépendance (classe, interface, enum,...) et que les dépendances externes du projet soient effectivement détectées comme telles. 

La génération des représentations textuelles de graphe de dépendances a également été testée en vérifiant le texte généré en fonction du graphe et des différentes options disponibles (ignorer les dépendances externes, ignorer des éléments en fonction d'expressions régulières). 

Ces tests sont répartis en 4 classes contenant en tout 22 tests et couvrant 89% du code. 

## Références

- Dependency Finder: [http://depfind.sourceforge.net/](http://depfind.sourceforge.net/)
- Spoon: [http://spoon.gforge.inria.fr/](http://spoon.gforge.inria.fr/)
- Graphviz: [http://www.graphviz.org/](http://www.graphviz.org/)
- Sigma.js: [https://github.com/jacomyal/sigma.js](https://github.com/jacomyal/sigma.js)

[1]: http://adam.kahtava.com/journal/2010/03/11/more-reasons-for-testing-prevent-the-morning-after-syndrome/ "More Reasons For Testing: Prevent The Morning-After Syndrome, March 11th, 2010"
