#!/bin/bash
# Generates change types by clustering a dataset
# Output: an .arff file containing the clusters

mvn exec:java -X -Dexec.mainClass="ca.ubc.ece.salt.pangor.learn.LearningDataSetMain" -Dexec.args="--dataset ./input/dataset_mediacenterjs_pm2.csv --arff-path ./output/arff --complexity 6 --epsilon 0.3 --complexityWeight 0.2 --minClusterSize 3"
