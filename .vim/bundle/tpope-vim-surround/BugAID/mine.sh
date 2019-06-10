#!/bin/bash
# Builds a database of <commit,basic change type> pairs by mining git repos
# Output: a .csv file containing the relations

mvn exec:java -X -Dexec.mainClass="ca.ubc.ece.salt.pangor.js.learn.LearningAnalysisMain" -Dexec.args="--repositories ./input/bugsjson.txt --regex \"fix\" --dataset ./output/bugsjsonrepos_all.csv --threads 4"
