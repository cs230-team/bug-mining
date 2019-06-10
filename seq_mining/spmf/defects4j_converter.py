import tempfile
import sys
import os
from json import dump, load

def modify_file(filename):

  #Create output file read/write
  f= open("contextPrefixSpanStrings.txt","w+", encoding="utf-8")
  f.seek(0) #Rewind output file to beginning

  with open(filename, mode='r', encoding='utf-8') as i:
    defects = load(i)
  for d in defects:
    defect_id = d["bugId"]
    defect_diff = d["diff"]
    lines = defect_diff.split("\n")
    filtered_lines = lines
    #Copy input file to output file, modifying as we go
    for line in lines:
      if line and line[0] != '@':
        filtered_lines = filtered_lines[1:]
      else:
        break

    filtered_lines[0] = filtered_lines[0].split('@@')[-1].strip()
    for line in filtered_lines:
      prefix = ""
      if line and line[0] == '+':
        prefix = '+++'
      elif line and line[0] == '-':
          prefix = '---'
      for word in line.split():
        f.write(prefix + word+" -1 ")
        #t.write(line.rstrip()+" ")
    f.write("-2\n")

  i.close() #Close input file
  f.close() #Close output file, will cause it to be deleted

if __name__ == "__main__":
  modify_file(sys.argv[1])