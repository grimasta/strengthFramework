import os
import pandas as pd
import warnings
p="C:\\Users\\Rongji He\\Desktop\\data\\elisa.csv"
p2="C:\\Users\\Rongji He\\Desktop\\data\\elisa2.csv"

path="Z:/thesis_proj_Ria/truncatedData/elisa.csv"
path2="Z:/thesis_proj_Ria/truncatedData/akregator1.csv"

df= pd.read_csv(p,encoding="ISO-8859-1")
df= df.drop(['branch','message','parent_ids', 'committer', 'author'], axis=1)
df['project_LOC']=0
df['project_LOC_change']=0
#df['project_LOC_change_percentage']=0.0

df['file_LOC']=0
df['file_LOC_change']=0
#df['file_LOC_change_percentage']=0.0
#df['file_modification_percentage']=0.0

currentId= df.iloc[0]['id']
current_project_LOC= df.iloc[0]['commit_additions']
previous_project_LOC= df.iloc[0]['commit_additions']
previous_project_LOC= df.iloc[0]['commit_additions']
project_LOC_change= df.iloc[0]['commit_additions'] + df.iloc[0]['commit_deletions']
#project_LOC_change_percentage = project_LOC_change/previous_project_LOC

file_LOC=0
previous_file_LOC=0
file_LOC_change= 0
file_LOC_change_percentage=0
file_modification_percentage=0
fileLOCDict={}

for i in range(len(df['id'])):
    

    #print(i)
    if df.iloc[i]['id'] != currentId:
        
        currentId= df.iloc[i]['id']

        previous_project_LOC= current_project_LOC
        current_project_LOC= current_project_LOC + df.iloc[i]['commit_additions'] - df.iloc[i]['commit_deletions']
        project_LOC_change=df.iloc[i]['commit_additions'] + df.iloc[i]['commit_deletions']
        #project_LOC_change_percentage = project_LOC_change/previous_project_LOC

    fileId= df.iloc[i]['file_id']
    file_LOC_change= df.iloc[i]['file_additions'] + df.iloc[i]['file_deletions']
    if fileId in fileLOCDict:
        #previous_file_LOC = fileLOCDict[fileId]
        file_LOC = fileLOCDict[fileId] + df.iloc[i]['file_additions'] - df.iloc[i]['file_deletions']
        fileLOCDict[fileId]= file_LOC
        df.at[i, 'file_LOC_change']= file_LOC_change
    else:
        fileLOCDict[fileId] = df.iloc[i]['file_additions'] #- df.iloc[i]['file_deletions']
        file_LOC= fileLOCDict[fileId]
        #previous_file_LOC= file_LOC
        df.at[i, 'file_LOC_change']= 0

    df.at[i, 'project_LOC']=current_project_LOC
    df.at[i, 'project_LOC_change']=project_LOC_change
#    df.at[i, 'project_LOC_change_percentage']= project_LOC_change_percentage
    
    df.at[i, 'file_LOC']= file_LOC
    

"""    if previous_file_LOC == 0:
        df.at[i, 'file_LOC_change_percentage']= 0
    else:
        df.at[i, 'file_LOC_change_percentage']= file_LOC_change /previous_file_LOC
    #df.at[i, 'file_modification_percentage']="""

# for id in uniqueId:
     

#
df.to_csv(p2,index=False,encoding="ISO-8859-1")