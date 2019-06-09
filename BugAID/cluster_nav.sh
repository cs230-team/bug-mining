#!/bin/bash

# This script makes it easier to inspect change types in .arff files. When run,
# it allows the user to sequentially view clustered commits in the browser.
#
# Supports OSX only
#
# USAGE: 
#  $ bash cluster_nav_projects_diff.sh <cluster number> <arff file>
#  $ ./cluster_nav.sh 0 datasets/arff/dataset.arff

ARFF_FILE=$2
CLIPBOARD_PROGRAM=pbcopy
BROWSER=open

if [ "$4" = "-s" ]; then
	skip=true
else
	skip=false
fi

# Get all attributes names
attributes=($(cat $ARFF_FILE | grep @attribute | awk -F " " '{print $2}'))

# Get all projects that has instances on this cluster
projects=($(cat $ARFF_FILE | grep -w "cluster"$1 | grep -v @attribute | awk -F "," '{print $3}' | sort | uniq))

echo "------------ Exploring ${#projects[@]} projects ------------"
echo ${projects[@]}

# Iterate over projects
for project in "${projects[@]}";
do
	# Get the ids of all instances of this project on this cluster
    ids_from_this_project=($(cat $ARFF_FILE | grep -w "cluster"$1 | grep -v @attribute | grep ,$project, | awk -F "," '{print $1}'))

    echo "--------- Project: ${project} (${#ids_from_this_project[@]} commits) ---------"

    # Iterate over th eids
    for id in "${ids_from_this_project[@]}";
    do
	    # Get feature vector
	    vector=$(cat $ARFF_FILE | grep -w "cluster"$1 | grep -v attribute | awk -F "," '$1=='$id)

			# Find the ids of the CommitURL and RepairedCommitID
			commitURLIndex=-1
			i=0
			for attribute in "${attributes[@]}";
			do
				if [ $attribute = "CommitURL" ]; then
					commitURLIndex=$i
				fi
				i=$((i+1))
			done

			# Build the URL for the diff on GitHub
			if [ $commitURLIndex -ge 0 ]; then
				values=($(echo $vector | tr "," " "))
				commit_url="${values[$commitURLIndex]}"
				echo "Commit URL - $commit_url"
			else
				commit_url=
			fi

	    # Iterate over features. This is only for printing
	    # If is not 0, print attribute name and value
	    i=0
	    for value in $(echo $vector | tr "," " ");
	    do
	        if [ "$value" != "0" ] 
	        then
	            echo "${attributes[$i]}" - $value
	        fi

	        i=$((i+1))
	    done 

	    # LINUX SPECIFIC: copy ID and projectID to clipboard. 
	    # \t so when we paste on Google Docs it is on 2 different cells
	    printf "$id \t $project" | $CLIPBOARD_PROGRAM -selection clipboard

			# Open the link to the GitHub diff
			if [ ! -z "$BROWSER" -a ! -z "$commit_url"  -a $skip = false ]; then
				$BROWSER $commit_url 
			fi

	    # Print options
	    echo ""
	    echo "[n] for [n]ext instance in same project."
	    echo "[s] for continue to next project and [s]kip loading diffs."
	    echo "[c] or [enter] for [c]ontinue to next project."
	    echo "[q] for [q]uit"
	    read -p "" input
	    case $input in
	        [n]* )	skip=false ;;
	        [s]* )	skip=true 
									break;;
					[c]* ) 	skip=false
									break;;
	        [q]* ) 	exit;;
	        * ) skip=false 
							break;;
	    esac
	done
done

