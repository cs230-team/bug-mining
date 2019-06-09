#!/bin/bash
# Generates change types by clustering a dataset
# Output: an .arff file containing the clusters
array=("1" "3" "5" "7" "10")
for eps in "${array[@]}"
do
	mvn exec:java -X -Dexec.mainClass="ca.ubc.ece.salt.pangor.learn.LearningDataSetMain" -Dexec.args="--dataset ./output/bugsjson.csv --complexity 6 --epsilon $eps --complexityWeight 0.2 --minClusterSize 3 --arff-path ./output/arff/$eps" > clusters_400_$eps
done

