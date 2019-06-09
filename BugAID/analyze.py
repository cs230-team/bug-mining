import numpy as np
import pandas as pd
import sys

file_name = sys.argv[1]
print(file_name)
f = open(file_name,'r')

lines = f.readlines()
print(len(lines))
lines = lines[400:]
lines = lines[:-8]
i=0
while(not lines[i][0].isdigit()):
	i+=1
lines = lines[i:]
data = [line.split("        ")[1] for line in lines]
data = [line.split(",") for line in data]
np.save(file_name+'.npy',np.array(data))


