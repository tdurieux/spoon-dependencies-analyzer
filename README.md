# Extraction du graphe de dépendances avec Spoon

```text
Jéremy Bossut
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

Nous allons traiter de la génération du graphe de dépendance de projets Java avec la librairie Spoon.

Un graphe de dépendance permet de visualisé les différentes dépendances d'un projet. 
Il est possible d'analyser les dépendances à plusieurs niveaux:

- au niveau des paquets,
- au niveau des classes,
- et au niveau des méthodes (non traité).

Les graphes des dépendances ont plusieurs intérêts, ils permettent de rapidement comprendre l'architecture d'un projet,
analyser la complexité d'un projet ou encore d'analyser la qualité de l'architecture (dépendances cycliques, nombre de dépendances, ...).

Nous allons utiliser la librairie Spoon qui permet de parcourir et de modifier efficacement du code source Java.

## Approche

Pour déterminer les dépendances d'un projet, il faut rechercher les éléments susceptibles d'amener ces dépendance. 
Une liste exhaustives de ces éléments a été réalisée:

- déclaration des classes, interfaces, énumérations, exceptions,
- interfaces implémentées,
- classe parente,
- variables locales,
- retour des méthodes,
- paramètres des méthodes,
- paramètres des invocations de méthodes,
- exceptions pouvant être lancées,
- retour des invocations des méthodes,
- paramètres des invocations,
- annotations,
- exceptions interceptées (catch).

Il a fallu déterminer la meilleur technique d'analyse de ces éléments avec Spoon. 
Spoon permet de réaliser des processeurs qui  de parcours le code source Java.
Des filtres peuvent être appliqués à ces processeurs afin de traiter uniquement les éléments d'un certain type.
Tous les éléments amenant des dépendances (à l'exception des annotations) ont une interface commune: CtTypedElement dans Spoon.

Les graphes de dépendances ont pu être réalisé à partir des informations obtenues grâce aux processeurs. Plusieurs représentations des graphes ont été sélectionnées:

- format utilisé par Dependency Finder afin de pouvoir comparer les résultats,
- Graphviz qui permet de générer graphes dans différents format (image, pdf, svg)

## Réalisation

L'outil a été réalisé en Java et peut soit être exécuté via une interface d'utilisateur basique et et soit via un programme en ligne de commandes.

### Usage

#### Interface d'utilisateur

```bash
mvn exec:java
```

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
    spoon/src \
    --ignore-external \
    --ignore '.*(Impl)$','^([^\.]+\.)*(?!Ct)[^\.]+$','.*Abstract.*'\
    --format dot \
    --level class;
```

### Analyse des résultats 

La génération de graphes de dépendances au niveau des paquets est été assez probante: les graphes restent généralement assez compréhensible. 

Les graphes de dépendances au niveau des classes est difficilement exploitable en état. En effet la quantité d'informations générées rendent la compréhension des graphes particulièrement difficile. 

Plusieurs solutions optionnelles ont été implémentées: identifications et masquage des classes provenant de librairies externes, ajout de filtre basé sur des expressions régulières. 
Ces solutions permettent d'améliorer les résultats mais elles induisent une perte d'information en sortie du logiciel. 

Une solution solution qui reste à investiguer est de produire un format de sortie qui permet de modifier dynamiquement la vue du graphe. Nous avons réfléchis à générer une page HTML/JavaScript qui permettrait de filtrer dynamiquement les éléments du graphes. Plusieurs librairies JavaScript pourrait facilité cette réalisation comme par exemple: sigma.js qui une librairie maintenue et qui offre les fonctionnalités nécessaire à cette tâche.

### Validation des résultats

Pour valider notre analyseur de dépendances nous avons décidé de comparer les résultats à ceux de Dependency Finder. Dependency Finder est un logiciel qui permet d'extraire des graphes de dépendance de code compilé Java.
Cette validation a été effectuée sur différents projets de taille variables. Les différents projets testés sont:

- cet outil,
- Spoon,
- Nopol,
- un projet faisant appel à de nombreuses classes anonymes.

Les graphes de dépendances au niveau des paquets sont très similaire. Il y a néanmoins quelques différences. Ce projet traite les annotations comme des dépendances au contraire de Dependency Finder. Étant donnée que Dependency Finder analyse du code compilé certains éléments ne sont pas détecté à l'analyse du code source comme par exemple les package-info.java qui sont transformés en classe à la compilation. L'ordre des dépendances est également légèrement différente nous avons fait le choix de séparer les dépendances entrante et sortante, Dependency Finder ne fait qu'un tris alphabétique.

Les quelques différences détectées au niveau des paquets sont également présentes au niveau des classes. Cet outil analyse le code source des projets et par conséquent ne traite pas la même chose que Dependency Finder. Par exemple Dependency Finder considère que toutes les classes héritent d'Object, cet outil ne considérera que si le développeur l'indique explicitement dans son code source.

## Pistes d'améliorations

Plusieurs pistent d'amélioration sont envisageables:

- gérer dans les exports la position d'utilisation des dépendances,
- détecter dans quel contexte la dépendance est utilisée (variable locale, paramètre, variable de classe, héritage, ...),
- créer un format de sortie qui supporte des filtres dynamiques,
- analyser les dépendances au niveau des méthodes.

## Références

- Dependency Finder: [http://depfind.sourceforge.net/](http://depfind.sourceforge.net/)
- Spoon: [http://spoon.gforge.inria.fr/](http://spoon.gforge.inria.fr/)
- Graphviz: [http://www.graphviz.org/](http://www.graphviz.org/)
- Sigma.js: [https://github.com/jacomyal/sigma.js](https://github.com/jacomyal/sigma.js)
