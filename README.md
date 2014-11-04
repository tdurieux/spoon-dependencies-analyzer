# Extraction du graphe de dépendances avec Spoon [![Build Status](https://travis-ci.org/tdurieux/spoon-dependencies-analizer.svg)](https://travis-ci.org/tdurieux/spoon-dependencies-analizer)

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
- [Références](#références)

<!-- /MarkdownTOC -->


## Introduction

Cette article traite de la génération de graphes de dépendances pour les projets Java à l'aide de la librairie Spoon.

Les graphes de dépendances permettent de visualiser les dépendances d'un projet. L'analyse des dépendances peut se faire à plusieurs niveaux en fonction du niveau de précision souhaitée:

- au niveau des paquets,
- au niveau des classes,
- et au niveau des méthodes (non traité).

L'analyse des dépendances a plusieurs intérêts: 
elle permet de rapidement comprendre l'architecture du projet,
d'identifier la complexité d'un projet (nombres de dépendances, nombres de classes, paquets, méthodes) ou 
encore d'analyser la qualité de l'architecture (bonne découpe du projet en packages, interaction entre les paquets, présence de dépendances cycliques). Elle permet également d'éviter le "morning-after syndrome" décrit par Uncle Bob Martin. [^1]

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

Tous ces éléments (à l'exception des annotations) sont encapsulés par Spoon sous le type: CtTypedElement. Un processeur traitant les CtTypedElement a pu être réalisé. Ce processeur a comme rôle de générer l'arbre de dépendances.

A partir de ce graphe de dépendances, il est possible de générer deux représentations. Le premier format de sortie est celui utilisé par Dependency Finder. Le second format est le format utilisé par la librairie Graphviz qui permet de générer des graphes sous forme d'image (png, jpg, pdf, svg, ...).

## Analyse des résultats 

La génération de graphe de dépendances au niveau des paquets est assez probante et les résultats restent compréhensibles. 

Les graphes de dépendances au niveau des classes est difficilement exploitable en état. En effet, la quantité d'informations générées rend la compréhension des graphes particulièrement difficile sur des projets ayant une taille importante. De plus, la génération d'images à partir de Graphviz peut ne jamais aboutir sur des gros projets (par exemple la génération de l'image du graphe de dépendances du projet Spoon par Graphviz n'a jamais abouti). 

Plusieurs solutions optionnelles ont été implémentées: 
- identifier et masquer les classes ne provenant pas du projet analysé (librairies externes, les classes Java, ...),
- ajout de la possibilité de filtrer les dépendances sur base d'expressions régulières. 
Ces solutions permettent d'améliorer la lisibilité des résultats mais elles induisent une perte d'informations. 

Une solution qui reste à investiguer est de produire une interface qui permet de filtrer dynamiquement la vue du graphe. Plusieurs types d'interface sont envisageables:
- améliorer l'interface utilisateur actuelle en y ajoutant le support des filtres,
- créer une page HTML/JavaScript qui permettrait de filtrer dynamiquement les éléments du graphe. Plusieurs librairies JavaScript permettent de faciliter cette réalisation comme par exemple la librairie sigma.js. Cette solution offre l'avantage de pouvoir facilement partager le graphe de dépendances.

## Validation des résultats

La validation a été effectuée sur différents projets de taille variable. Les différents projets testés sont:

- ce projet
- le projet servant aux tests unitaires de ce projet,
- Spoon,
- un projet faisant appel à de nombreuses classes anonymes.

Les graphes de dépendances au niveau des paquets sont très similaires. 
Il y a néanmoins quelques différences:

- ce projet considère les annotations comme des dépendances au contraire de Dependency Finder. 
- certains éléments sont générés à la compilation et ne sont donc pas visibles par ce projet, par exemple les fichiers package-info.java qui sont transformés en classe à la compilation. 
- l'ordre des dépendances est également légèrement différent: nous avons fait le choix de séparer les dépendances entrantes et sortantes. Dependency Finder ne fait qu'un tri alphabétique.

Les quelques différences détectées au niveau des paquets sont également présentes au niveau des classes.

- Dependency Finder considère tous les héritages. Cet outil se limite aux héritages des classes et interfaces déclarées dans le projet,
- Dependency Finder renomme les classes anonymes.

TODO: classes internes ne sont pas considérées comme des dépendances dans les classes parentes.

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

### Exemple

```Bash
mvn package

java -jar target/DependencyAnalyzer-0.0.1-SNAPSHOT.jar \
    <projectPath> \
    --ignore-external \
    --ignore '.*(Impl)$','^([^\.]+\.)*(?!Ct)[^\.]+$','.*Abstract.*'\
    --format dot \
    --level class;
```

## Références

- Dependency Finder: [http://depfind.sourceforge.net/](http://depfind.sourceforge.net/)
- Spoon: [http://spoon.gforge.inria.fr/](http://spoon.gforge.inria.fr/)
- Graphviz: [http://www.graphviz.org/](http://www.graphviz.org/)
- Sigma.js: [https://github.com/jacomyal/sigma.js](https://github.com/jacomyal/sigma.js)

[^1]: More Reasons For Testing: Prevent The Morning-After Syndrome, March 11th, 2010, [http://adam.kahtava.com/journal/2010/03/11/more-reasons-for-testing-prevent-the-morning-after-syndrome/](http://adam.kahtava.com/journal/2010/03/11/more-reasons-for-testing-prevent-the-morning-after-syndrome/)
