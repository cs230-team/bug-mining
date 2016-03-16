#!/bin/bash
# Builds a database of <commit,basic change type> pairs by mining git repos
# Output: a .csv file containing the relations

mvn exec:java -X -Dexec.mainClass="ca.ubc.ece.salt.pangor.js.learn.LearningAnalysisMain" -Dexec.args="--repositories ./input/javascript_repositories.txt --dataset ./output/dataset.csv --regex \"fix|repair|bug|error|resolve|close|issue\" --threads 1"
