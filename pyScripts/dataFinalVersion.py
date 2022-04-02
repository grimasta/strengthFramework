import pandas as pd
from sqlalchemy import true

p1="C:\\Users\\Rongji He\\Desktop\\data\\elisa2.csv"
p2="C:\\Users\\Rongji He\\Desktop\\data\\elisaFinalVersion.csv"

df= pd.read_csv(p1,encoding="ISO-8859-1")
#df.drop_duplicates(['id'],inplace=True)
df.drop(df.columns.difference(['id','committed_at','is_bug_fixing',
        'file_id','project_LOC', 
        'project_LOC_change','file_LOC',
        'file_LOC_change']), 1, inplace=True)

df['project_LOC_change_ROC'] = df['project_LOC_change'].pct_change()
df.at[0,'project_LOC_change_ROC']=0
project_LOC_change_ROC=df.iloc[0]['project_LOC_change_ROC']
df['project_LOC_change_percentage']=0.0
project_LOC_change_percentage=0
id= df.iloc[0]['id']
for i in range(1, len(df['id'])):
        if df.iloc[i]['id'] !=id:
                id=df.iloc[i]['id']
                project_LOC_change_ROC= df.iloc[i]['project_LOC_change_ROC']
                project_LOC_change_percentage = df.iloc[i]['project_LOC_change']/df.iloc[i-1]['project_LOC']
        df.at[i,'project_LOC_change_ROC']= project_LOC_change_ROC
        df.at[i,'project_LOC_change_percentage']= project_LOC_change_percentage


df.sort_values(by=['file_id','committed_at'],inplace=True)
df.reset_index(drop=True,inplace=True)
#df['file_LOC_change_ROC']= df['file_LOC_change'].pct_change()

df['file_LOC_change_ROC']=0.0
df['file_LOC_change_percentage']=0.0

file_LOC_change_ROC=df.iloc[0]['file_LOC_change_ROC']
file_LOC_change_percentage=0
id2= df.iloc[0]['file_id']

nonZeroFileLOC=0
for i in range(1, len(df['file_id'])):

        if df.iloc[i]['file_id'] != id2:
               
                id2=df.iloc[i]['file_id']
                file_LOC_change_ROC=0
                file_LOC_change_percentage=0
                nonZeroFileLOC=0
                df.at[i,'file_LOC_change_ROC']= 0
                df.at[i,'file_LOC_change_percentage']= 0
                continue
        if df.iloc[i]['file_LOC_change'] != 0:
                file_LOC=df.iloc[i-1]['file_LOC']
                if file_LOC ==0:
                        file_LOC_change_percentage=0
                else:
                        file_LOC_change_percentage=df.iloc[i]['file_LOC_change']/file_LOC
                if df.iloc[i-1]['file_LOC_change'] !=0:
                        file_LOC_change_ROC=(df.iloc[i]['file_LOC_change']-df.iloc[i-1]['file_LOC_change'])/df.iloc[i-1]['file_LOC_change']
                else:
                        if nonZeroFileLOC != 0:
                                file_LOC_change_ROC = (df.iloc[i]['file_LOC_change']-nonZeroFileLOC)/nonZeroFileLOC
                        else:
                                file_LOC_change_ROC=0
                nonZeroFileLOC =df.iloc[i]['file_LOC_change']
        else:
                file_LOC_change_percentage=0
                file_LOC_change_ROC=0
        df.at[i,'file_LOC_change_ROC']= file_LOC_change_ROC
        df.at[i,'file_LOC_change_percentage']= file_LOC_change_percentage

#df.drop(df[df['file_LOC_change'] == 0].index, inplace = True)
#df['file_LOC_change_ROC']= df['file_LOC_change'].pct_change()
df['file_proj_LOC_change_rito'] = df['file_LOC_change']/df['project_LOC_change']
df.to_csv(p2,index=False,encoding="ISO-8859-1")