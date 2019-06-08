import os
import numpy as np

#f1 = open('test.csv','rb')
def find_next(a,start):
	s=a[start+1:]
	#print(s)
	if len(s)==0:
		return 0
	i=0
	while s[i]!=',' and i<len(s):
	#	print(i)
		i=i+1
	return i+start+1

f1 = open('bugsjsonrepos_all.csv', 'r')
f2 = open('bugsjson.csv','w')
ids = np.load('commits.npy')
bugs = f1.readlines()
index=1
for bug in bugs:
	bug_arr=bug.split(",")
	commit=bug_arr[4]
	if commit in ids or bug_arr[5] in ids:
		bugid=find_next(bug,0)
		f2.write('{}'.format(index)+bug[bugid:])
		index+=1
#f1.close()
f2.close()
f1.close()
