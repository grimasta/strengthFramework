import os
import pandas as pd

folder= "Z:/thesis_proj_Ria/data/"
folder2= "Z:/thesis_proj_Ria/modifiedData/"
fileList= os.listdir(folder)
i=0

for fileName in fileList:
    path= folder+fileName
    #b = math.ceil(os.path.getsize(path)/1024)
    #print(math.ceil(b/1024)," kb")
    df= pd.read_csv(path, encoding = "ISO-8859-1")
    df= df.drop(['message'], axis=1)
    df.dropna(inplace=True)
    destination= folder2+fileName
    df.to_csv(destination,index=False,encoding="ISO-8859-1")
    #print(df.shape[0])
    