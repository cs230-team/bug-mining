import pandas as pd
import json
import pickle
import gzip
import numpy as np
import collections

with open('defects4j-bugs.json','r',encoding='utf8') as read_file:
        d4_bugs = json.load(read_file)

def load_zipped_pickle(filename):
    with gzip.open(filename, 'rb') as f:
        loaded_object = pickle.load(f)
        return loaded_object

clusters = load_zipped_pickle('defects4jcluster.pickle')

with open('classification.json','r',encoding='utf8') as read_file:
        classification = json.load(read_file)

classification_types = set()
for category in classification['Repair Patterns']:
        for pattern in classification['Repair Patterns'][category]:
                classification_types.add(pattern)

bug_patterns = {}

for bug in d4_bugs:
        bug_patterns[bug['program'] + '-' + str(bug['bugId'])] = bug['repairPatterns']

categories = ['tokens','shapes','actions']
categories_dict = {}

for category in categories:
        category_dict = {}
        subset = clusters[clusters['type'] == category]
        subset = subset.reset_index()
        for i in range(len(subset)):
                idx = subset.loc[i]['cid'][0].rfind('-')
                cluster_num = int(subset.loc[i]['cid'][0][idx+1:])
                for bug in subset.loc[i]['defects4j']:
                        if cluster_num in category_dict:
                                patterns_before = category_dict[cluster_num]['pattern_count']
                                for pattern in bug_patterns[bug]:
                                        if pattern in classification_types:
                                                category_dict[cluster_num]['pattern_count'] += 1
                                                if pattern in category_dict[cluster_num]:
                                                        category_dict[cluster_num][pattern] += 1
                                                else:
                                                        category_dict[cluster_num][pattern] = 1
                                if category_dict[cluster_num]['pattern_count'] != patterns_before:
                                        category_dict[cluster_num]['bug_count'] += 1
                        else:
                                category_dict[cluster_num] = {}
                                category_dict[cluster_num]['pattern_count'] = 0
                                patterns_before = category_dict[cluster_num]['pattern_count']
                                for pattern in bug_patterns[bug]:
                                        if pattern in classification_types:
                                                category_dict[cluster_num]['pattern_count'] += 1
                                                category_dict[cluster_num][pattern] = 1
                                if category_dict[cluster_num]['pattern_count'] != patterns_before:
                                        category_dict[cluster_num]['bug_count'] = 1
        categories_dict[category] = category_dict

membership = {}

# calculate pattern membership in each cluster based on number of bugs detected
for tree_type in categories_dict:
        cluster_patterns = {}
        # print('Tree',tree_type)
        for cluster_num in categories_dict[tree_type]:
                if categories_dict[tree_type][cluster_num]['bug_count'] > 1:
                        # print('')
                        # print('\tCluster Number',cluster_num)
                        for pattern in categories_dict[tree_type][cluster_num]:
                                if pattern != 'pattern_count' and pattern != 'bug_count':
                                        tmp = categories_dict[tree_type][cluster_num][pattern] / categories_dict[tree_type][cluster_num]['bug_count']
                                        if tmp >= 0.3:
                                                # print('\t\t',pattern,'with confidence',tmp)
                                                if pattern in cluster_patterns:
                                                        if tmp > cluster_patterns[pattern][1]:
                                                                cluster_patterns[pattern] = (cluster_num,tmp)
                                                else:
                                                        cluster_patterns[pattern] = (cluster_num,tmp)
        membership[tree_type] = cluster_patterns
        # print('')
        # print('')

for tree_type in membership:
        membership[tree_type] = collections.OrderedDict(sorted(membership[tree_type].items(), key=lambda kv: kv[1]))

for tree_type in membership:
        print('Tree',tree_type)
        for pattern in membership[tree_type]:
                print('')
                print('\tCluster Number',membership[tree_type][pattern][0],'Pattern',pattern,'Precision',membership[tree_type][pattern][1])
        print('')
        print('')