import os
import pandas as pd
import math

folder2= "Z:/thesis_proj_Ria/modifiedData/"
folder3= "Z:/thesis_proj_Ria/truncatedData/"

fileList= os.listdir(folder2)
threshold= 1500 #kb

for fileName in fileList:
    path= folder2+fileName
    size = math.ceil(os.path.getsize(path)/1024)
    df= pd.read_csv(path,encoding = "ISO-8859-1")
    if size >threshold:
        #hder= df.iloc[0]
        truncatedRito= math.ceil(df.shape[0]*(size-threshold)/size)
        df= pd.read_csv(path,skiprows= range(1,truncatedRito),encoding = "ISO-8859-1")
    
    destination= folder3+fileName
    df.to_csv(destination,index=False,encoding="ISO-8859-1")
    #print(df.shape[0])