import pandas as pd



p1="C:\\Users\\Rongji He\\Desktop\\data\\elisaFinalVersion.csv"

sourceFolder1="C:\\Users\\Rongji He\\Desktop\\incremental_data\\elisa\\elisa_accesses.csv"
sourceFolder2="C:\\Users\\Rongji He\\Desktop\\incremental_data\\elisa\\elisa_calls.csv"
sourceFolder3="C:\\Users\\Rongji He\\Desktop\\incremental_data\\elisa\\elisa_file_inclusions.csv"
sourceFolder4="C:\\Users\\Rongji He\\Desktop\\incremental_data\\elisa\\elisa_sets.csv"

df= pd.read_csv(p1,            encoding = "ISO-8859-1")
df1=pd.read_csv(sourceFolder1, encoding = "ISO-8859-1")
df2=pd.read_csv(sourceFolder2, encoding = "ISO-8859-1")
df3=pd.read_csv(sourceFolder3, encoding = "ISO-8859-1")
df4=pd.read_csv(sourceFolder4, encoding = "ISO-8859-1")

"""df.drop(['Total_Accesses', 'Added_Accesses', 'Deleted_Accesses'
         ], axis=1, inplace=True)"""
df.sort_values(by=['committed_at', 'file_id'],inplace=True)


index= 0
currentId= df.at[0, 'id']
df['Total_Accesses']=0.0
df['Added_Accesses']=0.0
df['Deleted_Accesses']=0.0

df['Total_Calls']=0.0
df['Added_Calls']=0.0
df['Deleted_Calls']=0.0

df['Current_Status']=0.0
df['Change_Added']=0.0
df['Change_Deleted']=0.0

df['Total_Sets']=0.0
df['Added_Sets']=0.0
df['Deleted_Sets']=0.0


df1= df1.groupby(by=['Commit_id', 'Source_file']).sum()
df1 = pd.DataFrame(df1)
df1.reset_index(  inplace=True)

df2= df2.groupby(by=['Commit_id', 'Source_file']).sum()
df2 = pd.DataFrame(df2)
df2.reset_index( inplace=True)

df3= df3.groupby(by=['Commit_id', 'Source_file']).sum()
df3 = pd.DataFrame(df3)
df3.reset_index( inplace=True)

df4= df4.groupby(by=['Commit_id', 'Source_file']).sum()
df4 = pd.DataFrame(df4)
df4.reset_index( inplace=True)

df['commit_additions']       =df['commit_additions'].astype(float)
df['commit_deletions']       =df['commit_deletions'].astype(float)
df['changed_files']          =df['changed_files'].astype(float)
df['file_additions']         =df['file_additions'].astype(float)
df['file_deletions']         =df['file_deletions'].astype(float)
df['distinct_authors_to_now']=df['distinct_authors_to_now'].astype(float)
df['project_LOC']            =df['project_LOC'].astype(float)
df['project_LOC_change']     =df['project_LOC_change'].astype(float)
df['file_LOC']               =df['file_LOC'].astype(float)
df['file_LOC_change']        =df['file_LOC_change'] .astype(float)

last_non_zero_access=0
last_non_zero_call=0
last_non_zero_inclusion=0
last_non_zero_set=0
for i in range(df['id'].size):
    
    id= df.at[i, 'id']
    file_id = df.at[i, 'file_id']
    temp1 = df1[(df1['Commit_id']== id) & (df1['Source_file']==file_id)]
    temp1.reset_index(drop=True, inplace=True)
    
    temp2 = df2[(df2['Commit_id']== id) & (df2['Source_file']==file_id)]
    temp2.reset_index(drop=True, inplace=True)

    temp3 = df3[(df3['Commit_id']== id) & (df3['Source_file']==file_id)]
    temp3.reset_index(drop=True, inplace=True)

    temp4 = df4[(df4['Commit_id']== id) & (df4['Source_file']==file_id)]
    temp4.reset_index(drop=True, inplace=True)
    if temp1.shape[0]>0:
        df.at[i, 'Total_Accesses'] = temp1.at[0, ' Total_Accesses']+1.02 - 1.02
        df.at[i, 'Added_Accesses'] = temp1.at[0, ' Added_Accesses']+1.02 - 1.02
        df.at[i, 'Deleted_Accesses'] = temp1.at[0, ' Deleted_Accesses']+1.02 - 1.02
    if temp2.shape[0]>0:
        df.at[i, 'Total_Calls'] = temp2.at[0, ' Total_Calls']+1.02 - 1.02
        df.at[i, 'Added_Calls'] = temp2.at[0, ' Added_Calls']+1.02 - 1.02
        df.at[i, 'Deleted_Calls'] = temp2.at[0, ' Deleted_Calls']+1.02 - 1.02
    if temp3.shape[0]>0:
        df.at[i, 'Current_Status'] = temp3.at[0, ' current_status']+1.02 - 1.02
        df.at[i, 'Change_Added'] = temp3.at[0, ' change_added']+1.02 - 1.02
        df.at[i, 'Change_Deleted'] = temp3.at[0, ' change_deleted']+1.02 - 1.02
    if temp4.shape[0]>0:
        df.at[i, 'Total_Sets'] = temp4.at[0, ' Total_Sets']+1.02 - 1.02
        df.at[i, 'Added_Sets'] = temp4.at[0, ' Added_Sets']+1.02 - 1.02
        df.at[i, 'Deleted_Sets'] = temp4.at[0, ' Deleted_Sets']+1.02 - 1.02
#df['Total_Accesses'] = df['Total_Accesses'].astype(float)
#df['project_LOC']            =df['project_LOC'].astype(float)
df.sort_values(by=['file_id','committed_at'],inplace=True)
df.reset_index(drop=True,inplace=True)
df.fillna(0.0)
df.to_csv(p1,index=False,encoding="ISO-8859-1")




"""df.at[i,'commit_additions']         = df.at[i,'commit_additions'] +1.02-1.02
    df.at[i,'commit_deletions']         = df.at[i,'commit_deletions'] +1.02-1.02
    df.at[i,'changed_files']            = df.at[i,'changed_files'] +1.02-1.02
    df.at[i,'file_additions']           = df.at[i,'file_additions'] +1.02-1.02
    df.at[i,'file_deletions']           = df.at[i,'file_deletions'] +1.02-1.02
    df.at[i,'distinct_authors_to_now']  = df.at[i,'distinct_authors_to_now'] +1.02-1.02
    df.at[i,'project_LOC']              = df.at[i,'project_LOC'] +1.02-1.02 +3.678 -3.678
    df.at[i,'project_LOC_change']       = df.at[i,'project_LOC_change'] +1.02-1.02
    df.at[i,'file_LOC']                 = df.at[i,'file_LOC'] +1.02-1.02
    df.at[i,'file_LOC_change']          = df.at[i,'file_LOC_change'] +1.02-1.02
    df.at[i,'distinct_authors_to_now']  = df.at[i,'distinct_authors_to_now'] +1.02-1.02"""