#!/bin/bash
# Generates change types by clustering a dataset
# Output: an .arff file containing the clusters
array=("0.1" "0.3" "0.5" "0.7" "0.9" "1" "2" "10")
for eps in "${array[@]}"
do
	mvn exec:java -X -Dexec.mainClass="ca.ubc.ece.salt.pangor.learn.LearningDataSetMain" -Dexec.args="--dataset ./output/bugsjson.csv --complexity 6 --epsilon $eps --complexityWeight 0.2 --minClusterSize 2 --arff-path ./output/arff" > clusters_400_$eps
done

