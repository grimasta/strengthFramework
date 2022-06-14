import os
sourceFolder = "C:/Users/Rongji He/Desktop/data/"
sourceFolder2="C:/Users/Rongji He/Desktop/relation_extraction/"

fileList= os.listdir(sourceFolder)
fileList2= os.listdir(sourceFolder2)

deleteProject= [x for x in fileList2 if x+".csv" not in fileList]
commonProject= [x for x in fileList2 if x+".csv" in fileList]
print(commonProject)

for file in fileList:
    if file[:-4] not in commonProject:
        os.remove(sourceFolder+file)