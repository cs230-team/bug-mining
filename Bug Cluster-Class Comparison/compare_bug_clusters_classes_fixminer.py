import pandas as pd
import json
import pickle
import gzip
import numpy as np

with open('defects4j-bugs.json','r',encoding='utf8') as read_file:
        d4_bugs = json.load(read_file)

def load_zipped_pickle(filename):
    with gzip.open(filename, 'rb') as f:
        loaded_object = pickle.load(f)
        return loaded_object

cluster = load_zipped_pickle('defects4jcluster.pickle')

bug_patterns = {}

for bug in d4_bugs:
        bug_patterns[bug['program'] + '-' + str(bug['bugId'])] = bug['repairPatterns']