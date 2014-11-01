# Extraction du graphe de dépendances avec Spoon [![Build Status](https://travis-ci.org/tdurieux/spoon-dependencies-analizer.svg)](https://travis-ci.org/tdurieux/spoon-dependencies-analizer)

```text
Jérémy Bossut
Thomas Durieux
5 novembre 2014
```
<!-- MarkdownTOC depth=3 -->

- [Dépendances](#dépendances)
- [Introduction](#introduction)
- [Approche](#approche)
- [Réalisation](#réalisation)
  - [Usage](#usage)
  - [Analyse des résultats](#analyse-des-résultats)
  - [Validation des résultats](#validation-des-résultats)
- [Pistes d'améliorations](#pistes-daméliorations)
- [Références](#références)

<!-- /MarkdownTOC -->

## Dépendances

- Java: > v.7
- Spoon: > v.3
- Jsap: v.2.1

## Introduction

Cette article traitera de la génération de graphe de dépendances pour les projets Java à l'aide de la librairie Spoon.

Les graphes de dépendances permettent de visualiser les dépendances d'un projet. L'analyse des dépendances peut se faire à plusieurs niveaux en fonction du niveau de précision souhaité:

- au niveau des paquets,
- au niveau des classes,
- et au niveau des méthodes (non traité).

L'analyse des dépendances a plusieurs intérêts: 
elle permet de rapidement comprendre l'architecture du projet,
d'identifié la complexité d'un projet (nombres de dépendances, nombres de classes, paquets, méthodes) ou 
encore d'analyser la qualité de l'architecture (bonne découpe du projet en package, interaction entre les paquets, présence dépendances cycliques).

Spoon sera utilisé comme librairie d'AST pour parcourir le code du projet. Spoon a la particularité d'analyser le code source des applications et offre une API de transformation et de parcourt claire. Spoon utilise le principe de processeur, les processeurs permettent de parcourir le code, certains de ces processeurs permettent de filtrer les types des éléments parcourus.

## Approche

Afin de déterminer toutes les dépendances d'un projet, il faut dans un premier temps identifier tous les éléments susceptibles d'ajouter des dépendances au projet.
Les éléments suivant ont été identifié comme étant du source potentielle de dépendances:

- les interfaces implémentées par une classe,
- la classe parente d'une classe,
- les champs d'une classe,
- le type des variables locales,
- le type des assignations,
- le retour des méthodes,
- les paramètres des méthodes,
- les classes instanciées, 
- les classes des méthodes invoquées,
- les paramètres des invocations de méthodes,
- les exceptions lancées par les méthodes,
- le type des constantes utilisés,
- les énumérations utilisées,
- les annotations,
- les exceptions interceptées (catch),
- la déclaration de classes anonymes.

Tous ces éléments (à l'exception des annotations) sont encapsulés par Spoon sous le type: CtTypedElement. Un processeur traitant les CtTypedElement a pu être réalisé. Ce processeur a comme rôle de générer l'arbre de dépendances. Cette arbre a été implémenté sous forme de deux Map. La première Map permet retrouver toutes les localisations où est utilisé une dépendances, exemple: le dépendance java.lang est utilisé par le paquet mypackake.entity dans le fichier mypackake/entity/User.class à la ligne 32, la seconde Map permet lister toutes les dépendances utilisées par un paquet ou une classe (en fonction du niveau d'analyse), exemple: le paquet mypackake.entity utilise java.lang).

A partir de ce graphe de dépendances, il a été possible de généré plusieurs représentations de ce graphe:

- le premier format de sorti est celui utilisé par Dependency Finder, ce choix a été fais afin de simplifier la validation de l'approche de ce projet,
- le second format est le format utilisé par la librairie Graphviz qui permet de générer graphes sous forme d'image (png, jpg, pdf, svg, ...).

## Réalisation

L'outil a été réalisé en Java et peut être soit via une interface d'utilisateur rudimentaire, soit via un programme en ligne de commandes.

### Usage

#### Interface d'utilisateur

Lancer l'interface d'utilisateur à partir de maven:
```bash
mvn exec:java
```

![depanalyzer](https://cloud.githubusercontent.com/assets/5577568/4861912/a3c49d9c-6107-11e4-8745-cd7f4a0e33e7.gif)


#### Ligne de commande

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

### Analyse des résultats 

La génération de graphe de dépendances au niveau des paquets est été assez probante et les résultats restent compréhensible. 

Les graphes de dépendances au niveau des classes est difficilement exploitable en état. En effet la quantité d'informations générées rendent la compréhension des graphes particulièrement difficile sur des projets ayant une certaine taille. De plus, la génération d'image à partir de Graphviz peut ne jamais aboutir sur des gros projets (par exemple la génération de l'image du graphe de dépendances du projet Spoon par Graphviz n'a jamais aboutie). 

Plusieurs solutions optionnelles ont été implémentées: 

- identifier et masquer les classes ne provenant pas du projet analysé (librairies externes, les classes Java, ...).
- ajout de la possibilité de filtrer les dépendances sur base d'expressions régulières. 
Ces solutions permettent d'améliorer la lisibilité des résultats mais elles induisent une perte d'informations. 

Une solution solution qui reste à investiguer est de produire une interface qui permet de filtrer dynamiquement la vue du graphe. Plusieurs types d'interfaces est envisageable:
- améliorer l'interface utilisateur actuelle en y ajoutant le support des filtres,
- créer une page HTML/JavaScript qui permettrait de filtrer dynamiquement les éléments du graphes. Plusieurs librairies JavaScript permettent de faciliter cette réalisation comme par exemple la librairie sigma.js. Cette solution offre l'avantage de pouvoir facilement partager le graphe de dépendances.

### Validation des résultats

Pour valider les résultats obtenus il a été décidé de comparer les résultats à ceux de Dependency Finder. Dependency Finder est un logiciel qui permet d'extraire des graphes de dépendances de code compilé Java.
Cette validation a été effectuée sur différents projets de taille variables. Les différents projets testés sont:

- ce projet
- le projet servant aux tests unitaires de ce projet,
- Spoon,
- et un projet faisant appel à de nombreuses classes anonymes.

Les graphes de dépendances au niveau des paquets sont très similaire. 
Il y a néanmoins quelques différences:

- ce projet considère les annotations comme des dépendances au contraire de Dependency Finder. 
- certain élément sont générer à la compilation et ne sont donc pas visible par le ce projet, par exemple les fichier package-info.java qui sont transformés en classe à la compilation. 
- l'ordre des dépendances est également légèrement différente nous avons fait le choix de séparer les dépendances entrante et sortante, Dependency Finder ne fait qu'un tris alphabétique.

Les quelques différences détectées au niveau des paquets sont également présentes au niveau des classes.

- Dependency Finder considère que tous les héritage, cet outil se limite au héritage des classes, interfaces déclarées dans le projet,
- Dependency Finder renomme les classes anonymes.

## Pistes d'améliorations

Plusieurs pistent d'amélioration sont envisageables:

- gérer dans les exports la position d'utilisation des dépendances,
- gérer dans les exports le type de dépendances (classe, interfaces, énumération, annotation,...)
- détecter dans quel contexte la dépendance est utilisée (variable locale, paramètre, variable de classe, héritage, ...),
- créer un format de sortie qui supporte les filtres dynamiques,
- analyser les dépendances au niveau des méthodes.

## Références

- Dependency Finder: [http://depfind.sourceforge.net/](http://depfind.sourceforge.net/)
- Spoon: [http://spoon.gforge.inria.fr/](http://spoon.gforge.inria.fr/)
- Graphviz: [http://www.graphviz.org/](http://www.graphviz.org/)
- Sigma.js: [https://github.com/jacomyal/sigma.js](https://github.com/jacomyal/sigma.js)
