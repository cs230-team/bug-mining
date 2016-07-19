# BugAID
A tool for learning bug patterns. Currently supports JavaScript.

### Installation ###

BugAID is a Maven project.

* Clone BugAID.
* Build and install the project (`mvn install`).

Optional: Create the Eclipse project files (`mvn eclipse:eclipse`).

### Data Set Construction ###

Build a database of commits with BugAID:
```bash
mvn exec:java -Dexec.mainClass="ca.ubc.ece.salt.pangor.js.learn.LearningAnalysisMain" -Dexec.args="--repositories ./input/javascript_repositories.txt --regex \"fix\"  --dataset ./output/dataset.csv"
```

### Cluster Construction ###

Cluster change types with BugAID:
```bash
mvn exec:java -Dexec.mainClass="ca.ubc.ece.salt.pangor.learn.LearningDataSetMain" -Dexec.args="--dataset ./output/dataset.csv --complexity 6 --epsilon 0.3 --complexityWeight 0.2 --minClusterSize 5"
```
