import os

#f1 = open('test.csv','rb')
f2 = open('bower.csv','r')
f3 = open('bugsjsonall.csv','a')

index=24633
bower_bugs = f2.readlines()
for bug in bower_bugs:
	i=0
	while bug[i]!=',':
		i+=1
	f3.write('{}'.format(index)+bug[i:])
	index+=1
	
#f1.close()
f2.close()
f3.close()
