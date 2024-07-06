# NFP103
## Projet de Serveur de Gestion de Requêtes
Ce projet est un serveur capable de gérer différents types de requêtes : des requêtes planifiées (scheduled), des requêtes importantes (priority) et des requêtes lourdes (heavy). L'objectif principal est de concevoir un système qui puisse simuler une connexion à une base de données et retourner des résultats de manière efficace, en allouant des ressources (taille de pool) différentes à chaque thread.

### Prérequis

Java 11 : Assurez-vous d'avoir Java 11 installé sur votre machine.
Docker : Docker doit être installé pour exécuter le programme dans un conteneur.

### Lancer le programme
Pour lancer le programme, exécutez simplement le script `build-and-run-docker.sh`. Ce script construira l'image Docker et démarrera le conteneur :
```
./build-and-run-docker.sh
```

### Requêtes URL
Le serveur peut être sollicité à travers les URL suivantes :

- Requêtes planifiées : http://localhost:8081/scheduled/[numberOfRequests]
- Requêtes lourdes : http://localhost:8081/heavy/[numberOfRequests]
- Requêtes importantes : http://localhost:8081/priority/[numberOfRequests]

La réponse sera un JSON contenant des informations sur le pool, indiquant combien de requêtes ont été lancées et quelle est la capacité restante. Par exemple :

```
{
"Pool": "scheduled",
"queryLaunched": 3,
"newCapacity": 29
}
```

### Détails de l'implémentation
Le serveur utilise trois types principaux de ThreadPoolExecutor pour gérer les différentes requêtes :
```
ParallelizeProcessingFn<TriggerEntry> scheduled = new ParallelizeProcessingFn<>("scheduled", options.getScheduledPoolSize());
ParallelizeProcessingFn<TriggerEntry> heavy = new ParallelizeProcessingFn<>("heavy", options.getHeavyPoolSize());
ParallelizeProcessingFn<TriggerEntry> priority = new ParallelizeProcessingFn<>("priority", options.getPriorityPoolSize());
```


Chaque ThreadPoolExecutor a une taille prédéfinie, ce qui signifie que le nombre maximum de threads qui peuvent fonctionner simultanément est limité par cette taille. Par exemple, le scheduled ThreadPoolExecutor ne peut pas traiter plus de requêtes que sa capacité maximale définie par options.getScheduledPoolSize().

Cependant, même si le scheduled ThreadPoolExecutor est plein, les requêtes priority et heavy peuvent continuer à fonctionner sans être affectées, car elles sont gérées par leurs propres ThreadPoolExecutor avec leurs propres tailles de pool. Cela assure que le serveur peut gérer les requêtes de manière efficace en allouant des ressources spécifiques et isolées à chaque type de requête, empêchant ainsi une saturation globale du système.

### PeriodicalLoggingTask
Chaque 10 secondes, le système exécute un thread appelé PeriodicalLoggingTask dédié à afficher la capacité de chaque pool. Voici un exemple de sortie :

````
18:05:58.095 [Thread-1] INFO com.cnam.monitoring.PeriodicalLoggingTask -- --------PeriodicalLoggingTask---------
18:05:58.097 [Thread-1] INFO com.cnam.monitoring.PeriodicalLoggingTask -- Pool scheduled has capacity 50
18:05:58.099 [Thread-1] INFO com.cnam.monitoring.PeriodicalLoggingTask -- Pool heavy has capacity 5
18:05:58.100 [Thread-1] INFO com.cnam.monitoring.PeriodicalLoggingTask -- Pool priority has capacity 10
18:05:58.101 [Thread-1] INFO com.cnam.monitoring.PeriodicalLoggingTask -- -----------------
````