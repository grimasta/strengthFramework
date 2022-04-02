import pandas as pd
import os
#import itertools

fileName=["elisa_accesses.csv", 
    "elisa_calls.csv",
    "elisa_file_inclusions.csv",
    "elisa_sets.csv"]
column= ["Accesses","Calls", "Inclusions", "Sets"]

metricsList=[["Total_Accesses",  "Added_Accesses", "Deleted_Accesses"],
    ["Total_Calls","Added_Calls","Deleted_Calls"],
    ["current_status","change_added","change_deleted"],
    ["Total_Sets","Added_Sets","Deleted_Sets"]]

sourceFolder="C:/Users/Rongji He/Desktop/elisa_incremental/"
destFolder="Z:/thesis_proj_Ria/truncatedData/elisa_increAdded.csv"
folder = "C:/Users/Rongji He/Desktop/elisa_incremental/aggreg.csv"
sourceFolder1="C:/Users/Rongji He/Desktop/elisa_incremental/elisa_accesses.csv"
sourceFolder2="C:/Users/Rongji He/Desktop/elisa_incremental/elisa_calls.csv"
sourceFolder3="C:/Users/Rongji He/Desktop/elisa_incremental/elisa_file_inclusions.csv"
sourceFolder4="C:/Users/Rongji He/Desktop/elisa_incremental/elisa_sets.csv"


df= pd.read_csv(destFolder, encoding = "ISO-8859-1")
df1=pd.read_csv(sourceFolder1, encoding = "ISO-8859-1")
df2=pd.read_csv(sourceFolder2, encoding = "ISO-8859-1")
df3=pd.read_csv(sourceFolder3, encoding = "ISO-8859-1")
df4=pd.read_csv(sourceFolder4, encoding = "ISO-8859-1")


uniqueId= df['id'].unique()
uniqueId1= df1['Commit_id'].unique()
uniqueId2= df2['Commit_id'].unique()
uniqueId3= df3['Commit_id'].unique()
uniqueId4= df4['Commit_id'].unique()


jointId = [ x for x in uniqueId1 
    if (x in uniqueId2) 
    and (x in uniqueId3)
    and (x in uniqueId4)
    and (x in uniqueId)]

#dfList= [df1, df2, df3, df4]



access=[]
calls=[]
inclusions=[]
sets= []
for id in jointId:
    temp=df1.loc[df1['Commit_id'] == id]
    aggreg=[]
    for index, row in temp.iterrows():
        entry=[]
        entry.append(row["Source_file"].lstrip(' '))
        entry.append(row["Destination_File"].lstrip(' '))
        entry.append(row["Total_Accesses"])
        entry.append(row["Added_Accesses"])
        entry.append(row["Deleted_Accesses"])
        aggreg.append(entry)
    access.append(aggreg)

    temp=df2.loc[df2['Commit_id'] == id]
    aggreg=[]
    for index, row in temp.iterrows():
        entry=[]
        entry.append(row["Source_file"].lstrip(' '))
        entry.append(row["Destination_File"].lstrip(' '))
        entry.append(row["Total_Calls"])
        entry.append(row["Added_Calls"])
        entry.append(row["Deleted_Calls"])
        aggreg.append(entry)
    calls.append(aggreg)

    temp=df3.loc[df3['Commit_id'] == id]
    aggreg=[]
    for index, row in temp.iterrows():
        entry=[]
        entry.append(row["Source_file"].lstrip(' '))
        entry.append(row["Destination_File"].lstrip(' '))
        entry.append(row["current_status"])
        entry.append(row["change_added"])
        entry.append(row["change_deleted"])
        aggreg.append(entry)
    inclusions.append(aggreg)

    temp=df4.loc[df4['Commit_id'] == id]
    aggreg=[]
    for index, row in temp.iterrows():
        entry=[]
        entry.append(row["Source_file"].lstrip(' '))
        entry.append(row["Destination_File"].lstrip(' '))
        entry.append(row["Total_Sets"])
        entry.append(row["Added_Sets"])
        entry.append(row["Deleted_Sets"])
        aggreg.append(entry)
    sets.append(aggreg)
     

dict={ 'Commit_id': jointId,
    'Accesses': access,
    'Calls': calls,
    'Inclusions':inclusions,
    'Sets':sets
}
resDf= pd.DataFrame(dict)

resDf.to_csv(folder,index=False,encoding="ISO-8859-1")
