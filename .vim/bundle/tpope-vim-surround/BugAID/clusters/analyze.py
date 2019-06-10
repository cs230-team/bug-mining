import numpy as np
import pandas as pd
import sys

clusters = pd.DataFrame(np.load('clusters.npy', allow_pickle=True))
file_name = sys.argv[1]
output = np.load(file_name+'.npy').tolist()
for idx,row in enumerate(output):
	#changes = row[0][2:][:-17]
	if row[0][1]=='{':
		changes = row[0][2:][:-16].split(" ")
	else:
		changes = row[0][1:][:-16].split(" ")
	for i,change in enumerate(changes):
		changes[i]=changes[i][:-2]
		if changes[i][-1]=='.':
			changes[i] = changes[i][:-1]
		changes[i]=changes[i].replace(":global","")
	changes = set(changes)
	#print(changes)
	try:
		pattern_id=clusters[clusters[1].eq(changes)].values[0][0]
	except:
		pattern_id='0'
	#print(output[idx])
	for colidx,col in enumerate(row):
		output[idx][colidx]=col.split("=")[-1].strip()
	cluster = output[idx][0]
	output[idx].append(cluster)
	output[idx][0]="\n".join(sorted(changes))
	output[idx].append(pattern_id)
d=pd.DataFrame(output, columns=['changes','instances','complexity','basic changes','project count','clusterid','pattern id'])
d.to_csv(file_name+'.csv',index=False)
#np.save(file_name+'_c', output)

