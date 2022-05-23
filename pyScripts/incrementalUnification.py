import glob
import pandas as pd
import os


sourceFolder= "C:/Users/Rongji He/Desktop/relation_extraction/"     #incremental data folder
sourceFolder2="C:/Users/Rongji He/Desktop/data/"                    #process metric data folder
destinationFolder= "C:/Users/Rongji He/Desktop/incremental_data/"
folderList= os.listdir(sourceFolder)
folderList2= os.listdir(sourceFolder2)
textFileList = glob.glob(sourceFolder + '*/*.txt')

commonProject= [x for x in folderList if x+".csv" in folderList2]   #two types of data may have different projects


for filePath in textFileList:       #remove all the .txt files
    os.remove(filePath)



# incremental files user file directory path to identify files, 
# to change them to file_id, we need to first populate a path-fileId dictionary


for folder in commonProject:
    fileList= os.listdir(sourceFolder+folder)
    directory = destinationFolder+ folder
    os.makedirs(directory, exist_ok=True)
    mainDf= pd.read_csv(sourceFolder2 + folder + ".csv", encoding = "ISO-8859-1")
    
    path_id_dict={}
    containedId = mainDf['id'].unique()
    for file_id, file_path in zip(mainDf.file_id, mainDf.file_path):
        if file_path not in path_id_dict:
            path_id_dict[file_path] = file_id

    for fileName in fileList:
        df= pd.read_csv(sourceFolder+folder+"/"+fileName, encoding = "ISO-8859-1")
        
        df.rename(columns={" Source_file":"Source_file",
                            " Destination_File":"Destination_File"},inplace=True)  #really??!
        if "commit_id" in df.columns:
            df.rename(columns={"commit_id":"Commit_id"},inplace=True)   #consistentcy of naming columns is at risk!
        if " file_name" in df.columns:
            df.rename(columns={" file_name":"Source_file", " included_file_name":"Destination_File"},inplace=True)

        for i in range(0,df['Commit_id'].size):
            Source_file = df.at[i,'Source_file'].lstrip(' ')
            Destination_File = df.at[i,'Destination_File'].lstrip(' ')
            if Source_file == Destination_File:         #self inclusion/accesses is discarded 
                df.drop([i],inplace=True)
                continue
            if Source_file in path_id_dict and Destination_File in path_id_dict and df.at[i,'Commit_id'] in containedId:
                df.at[i,'Source_file']= path_id_dict[Source_file]
                df.at[i,'Destination_File']= path_id_dict[Destination_File]
            else:
                df.drop([i],inplace=True)

        df.reset_index(drop=True, inplace=True)
        for i in range(0,df['Commit_id'].size):          #for some reasons the same loop above can't remove all self relations
            Source_file = df.at[i,'Source_file'].lstrip(' ')
            Destination_File = df.at[i,'Destination_File'].lstrip(' ')
            if Source_file == Destination_File:
                df.drop([i],inplace=True)
        df.reset_index(drop=True, inplace=True)
        
        df.to_csv(directory+ "/"+fileName, index=False, encoding="ISO-8859-1")
        
    




 
