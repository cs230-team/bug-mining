#!/bin/bash
# Builds a database of <commit,basic change type> pairs by mining git repos
# Output: a .csv file containing the relations

mvn exec:java -X -Dexec.mainClass="ca.ubc.ece.salt.pangor.js.learn.LearningAnalysisMain" -Dexec.args="--repositories ./input/test.txt --dataset ./output/test.csv --regex \"fix|repair|bug|error|resolve|close|issue\" --threads 4"
