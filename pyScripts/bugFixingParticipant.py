import pandas as pd
import os


folder= "C:\\Users\\Rongji He\\Desktop\\data\\"
fileList= os.listdir(folder)


for fileName in fileList:
    i=0
    path= folder+fileName
    df= pd.read_csv(path,encoding="ISO-8859-1")
    unique= df['file_id'].unique()
    fileNum= unique.size
    for id in unique:
        if True in df.loc[df['file_id'] == id]['is_bug_fixing'].values:
            i=i+1
    print(fileName, ": ", i/fileNum)


