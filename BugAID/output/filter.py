import os

#f1 = open('test.csv','rb')
def find_next(a,start):
	s=a[start:]
	if len(s)==0:
		return 0
	i=0
	while s[i]!=',':
		i=i+1
	return i+1

f1 = open('bugsjsonall.csv', 'rb')
f2 = open('bugsjson.csv','w')
bugs = f1.readlines()
index=1
for bug in bugs:
	bugid = find_next(bug,0)
	commit = find_next(bug,find_next(bug,find_next(bug,bugid)))
	if True:
		f2.write('{},'.format(index)+bug[bugid:])
		index+=1
#f1.close()
f2.close()
f1.close()
