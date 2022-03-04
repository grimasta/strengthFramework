package com.example.inj.commitBuilder;


import com.example.inj.attributes.IncrementalData;
import com.example.inj.commitRepository.CommitDetails;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TryFileDetails {

    private String fileId;
    private String commitId;
    private int addition;
    private int deletion;
    private CommitDetails commit;
    private boolean bugFixing;
    private String date;
    //Start: Added new attribute
    private int Caddition;
    private int Cdeletion;
    //End: Added new attribute

    private Table<String, String, IncrementalData> access ;
    private Table <String, String, IncrementalData> call ;
    private Table <String, String, IncrementalData> inclusion ;
    private Table <String, String, IncrementalData> set ;
    public String getDate() {
        return date;
    }

    public String setDate(String date) {
        this.date = date;
        return date;
    }

    public boolean isBugFixing() {
        return bugFixing;
    }

    public void setBugFixing(boolean bugFixing) {
        this.bugFixing = bugFixing;
    }

    public double getPercentile() {
    	return CommitDetails.getCommitDetailsPojo().get(this.getCommitId()).getPercentile(addition+deletion);
    }
    
    public static HashMap<String, List<CommitDetails>> fileIDtoCommitDetailsMap = new HashMap<>();
    private static HashMap<CommitDetails, List<TryFileDetails>> CommitDetails2TryFileDetailsMap = new HashMap<>();

    public TryFileDetails(TryFileDetailsBuilder builder) {
        this.fileId = builder.fileId;
        this.commitId = builder.commitId;
        this.addition = builder.addition;
        this.deletion= builder.deletion;
        this.bugFixing = builder.bugFixing;
        this.date=builder.commitAt;
        //Start: Added new attribute
        this.Caddition= builder.Cadditions;
        this.Cdeletion=builder.Cdeletion;
        //End: Added new attribute
        this.access=builder.access;
        this.inclusion= builder.inclusion;
        this.call= builder.call;
        this.set= builder.set;
    }

    public CommitDetails getCommit() {
        return commit;
    }

    public void setCommit(CommitDetails commit) {
        this.commit = commit;
    }

    public static HashMap<CommitDetails, List<TryFileDetails>> getCommitId2FileDetailsMap() {
        return CommitDetails2TryFileDetailsMap;
    }

    public static HashMap<String, List<CommitDetails>> getFileId2CommitDetailsMap() {
        return fileIDtoCommitDetailsMap;
    }

    public static void setFileId2CommitDetailsMap(HashMap<String, List<CommitDetails>> fileIdHashMap) {
        TryFileDetails.fileIDtoCommitDetailsMap = fileIdHashMap;
    }


    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public int getAddition() {
        return addition;
    }

    public void setAddition(int addition) {
        this.addition = addition;
    }

    public int getDeletion() {
        return deletion;
    }

    public void setDeletion(int deletion) {
        this.deletion = deletion;
    }
    public int getCaddition() {
        return Caddition;
    }

    public void setCaddition(int caddition) {
        this.Caddition = caddition;
    }

    public int getCdeletion() {
        return Cdeletion;
    }

    public void setCdeletion(int cdeletion) {
        this.Cdeletion = cdeletion;
    }

    public Table<String, String, IncrementalData> getAccess() {return access;}

    public Table<String, String, IncrementalData> getCall() {return call;}

    public Table<String, String, IncrementalData> getInclusion() {return inclusion;}

    public Table<String, String, IncrementalData> getSet() {return set;}

    @Override
    public String toString() {
        return "TryFileDetails{" +
                "fileId='" + fileId + '\'' +
                ", commitId='" + commitId + '\'' +
                ", addition='" + addition + '\'' +
                ", deletion='" + deletion + '\'' +
                ", commit=" + commit +
                ", bugFixing=" + bugFixing +
                ", date='" + date + '\'' +
                ", caddition='" + Caddition + '\'' +
                ", cdeletion='" + Cdeletion + '\'' +
                '}';
    }




    public static class TryFileDetailsBuilder
    {
        private String fileId;
        private String commitId;
        private int addition;
        private int deletion;
        private String commitAt;
        //private CommitDetails commit;
        private boolean bugFixing;
        //Start: Added New Attribute
        private int Cadditions;
        private int Cdeletion;
        //End: Added New Attribute

        //Start: Added New Attribute
        private Table <String, String, IncrementalData> access;
        private Table <String, String, IncrementalData> call ;
        private Table <String, String, IncrementalData> inclusion ;
        private Table <String, String, IncrementalData> set ;

        public TryFileDetailsBuilder setAccess(Table<String, String, IncrementalData> access) {
            this.access = access;
            return this;
        }

        public TryFileDetailsBuilder setCall(Table<String, String, IncrementalData> call) {
            this.call = call;
            return this;
        }

        public TryFileDetailsBuilder setInclusion(Table<String, String, IncrementalData> inclusion) {
            this.inclusion = inclusion;
            return this;
        }

        public TryFileDetailsBuilder setSet(Table<String, String, IncrementalData> set) {
            this.set = set;
            return this;
        }

        public TryFileDetailsBuilder setCaddition(int caddition) {
            this.Cadditions = caddition;
            return this;
        }


        public TryFileDetailsBuilder setCdeletion(int cdeletion) {
            this.Cdeletion = cdeletion;
            return this;
        }
        //End: Added New Attribute
        public TryFileDetailsBuilder(String fileId, String commitId) {
            this.fileId = fileId;
            this.commitId = commitId;
        }

        public TryFileDetailsBuilder setAddition(int addition) {
            this.addition = addition;
            return this;
        }

        public TryFileDetailsBuilder setDeletion(int deletion) {
            this.deletion = deletion;
            return this;
        }


        public TryFileDetailsBuilder setBugFixing(boolean bugFixing) {
            this.bugFixing = bugFixing;
            return this;
        }
        public TryFileDetailsBuilder setCommitDate(String commitAt)
        {
            this.commitAt= commitAt;
            return this;
        }

        public TryFileDetails build()
        {
            TryFileDetails newTryFileDetails= new TryFileDetails(this);
            createCommit2FileDetailsMap(newTryFileDetails);
            createFileID2CommitDetailsMap(newTryFileDetails);
            return newTryFileDetails;
        }

        /*createHashMap function populate the fileDetailsPojoHashMap with respective key and value pairs.
         * where key is commitId and value is list of files that are committed under the specified key */
        private void createCommit2FileDetailsMap(TryFileDetails tryFileDetails) {
            CommitDetails commitDetails = CommitDetails.getCommitDetailsPojo().get(tryFileDetails.getCommitId());
            commitDetails.addFileModifiedLines(addition, deletion);
            List<TryFileDetails> fileListPojo;
            if (CommitDetails2TryFileDetailsMap.containsKey(commitDetails)) {
                CommitDetails2TryFileDetailsMap.get(commitDetails).add(tryFileDetails);
//                this is not necessary a simple comment would suffice to show that the value is replaced
//                TryFileDetails.fileDetailsPojoHashMap.replace(commitDetails, fileListPojo); 
            } else { // this doesn't make sense, aren't we supposed to have a map from commitIDs to list of FileDetails?
                fileListPojo = new ArrayList<>();
                fileListPojo.add(tryFileDetails);
                TryFileDetails.CommitDetails2TryFileDetailsMap.put(commitDetails, fileListPojo);
            }
            return;
        }

        /*getFileIds function populate the fileIdHashMap with respective key and value pairs.
         * where key is fileId and value is list of commits that are performed for a particular file */
        private void createFileID2CommitDetailsMap(TryFileDetails tryFileDetails) {
            String fileId = tryFileDetails.getFileId();
            List<CommitDetails> commitDetailsList;
            if (TryFileDetails.fileIDtoCommitDetailsMap.containsKey(fileId)) {
                TryFileDetails.fileIDtoCommitDetailsMap.get(fileId).add(new CommitDetails(tryFileDetails.getCommitId()));
            } else {
                commitDetailsList = new ArrayList<>();
                commitDetailsList.add(new CommitDetails(tryFileDetails.getCommitId()));
                TryFileDetails.fileIDtoCommitDetailsMap.put(fileId, commitDetailsList);
            }

            return;
        }
    }

}
