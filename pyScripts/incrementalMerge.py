import pandas as pd



p1="C:\\Users\\Rongji He\\Desktop\\data\\elisaFinalVersion.csv"

sourceFolder1="C:/Users/Rongji He/Desktop/elisa_incremental/elisa_accesses2.csv"
sourceFolder2="C:/Users/Rongji He/Desktop/elisa_incremental/elisa_calls.csv"
sourceFolder3="C:/Users/Rongji He/Desktop/elisa_incremental/elisa_file_inclusions.csv"
sourceFolder4="C:/Users/Rongji He/Desktop/elisa_incremental/elisa_sets.csv"

df= pd.read_csv(p1, encoding = "ISO-8859-1")
df1=pd.read_csv(sourceFolder1, encoding = "ISO-8859-1")
df2=pd.read_csv(sourceFolder2, encoding = "ISO-8859-1")
df3=pd.read_csv(sourceFolder3, encoding = "ISO-8859-1")
df4=pd.read_csv(sourceFolder4, encoding = "ISO-8859-1")


df.sort_values(by=['committed_at', 'file_id'],inplace=True)
index= 0
currentId= df.at[0, 'id']
df['Total_Accesses']=0
df['Added_Accesses']=0
df['Deleted_Accesses']=0

df1= df1.groupby(by=['Commit_id', 'Source_file']).sum()
df1 = pd.DataFrame(df1)
df1.reset_index( inplace=True)


for i in range(df['id'].size):
    id= df.at[i, 'id']
    file_id = df.at[i, 'file_id']
    temp = df1[(df1['Commit_id']== id) & (df1['Source_file']==file_id)]
    temp.reset_index(drop=True, inplace=True)
    if temp.shape[0]>0:
        
        df.at[i, 'Total_Accesses'] = temp.at[0, 'Total_Accesses']
        df.at[i, 'Added_Accesses'] = temp.at[0, 'Added_Accesses']
        df.at[i, 'Deleted_Accesses'] = temp.at[0, 'Deleted_Accesses']


df.to_csv(p1,index=False,encoding="ISO-8859-1")