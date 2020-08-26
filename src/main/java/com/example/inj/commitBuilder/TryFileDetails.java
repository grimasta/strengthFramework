package com.example.inj.commitBuilder;


import com.example.inj.commitRepository.CommitDetails;

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

    public static HashMap<String, List<CommitDetails>> fileIdHashMap = new HashMap<>();
    private static HashMap<CommitDetails, List<TryFileDetails>> fileDetailsPojoHashMap = new HashMap<>();

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

    }

    public CommitDetails getCommit() {
        return commit;
    }

    public void setCommit(CommitDetails commit) {
        this.commit = commit;
    }

    public static HashMap<CommitDetails, List<TryFileDetails>> getFileDetailsPojoHashMap() {
        return fileDetailsPojoHashMap;
    }

    public static HashMap<String, List<CommitDetails>> getFileIdHashMap() {
        return fileIdHashMap;
    }

    public static void setFileIdHashMap(HashMap<String, List<CommitDetails>> fileIdHashMap) {
        TryFileDetails.fileIdHashMap = fileIdHashMap;
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
            TryFileDetails user= new TryFileDetails(this);
            createHashMap(user);
            getFileIds(user);
            return user;
        }

        /*createHashMap function populate the fileDetailsPojoHashMap with respective key and value pairs.
         * where key is commitId and value is list of files that are committed under the specified key */
        public List<TryFileDetails> createHashMap(TryFileDetails fileDetails) {
            CommitDetails commitDetails = CommitDetails.getCommitDetailsPojo().get(fileDetails.getCommitId());
            List<TryFileDetails> fileListPojo;
            if (fileDetailsPojoHashMap.containsKey(commitDetails)) {
                fileListPojo = fileDetailsPojoHashMap.get(commitDetails);
                fileListPojo.add(fileDetails);
                TryFileDetails.fileDetailsPojoHashMap.replace(commitDetails, fileListPojo);
            } else if ((fileDetailsPojoHashMap.isEmpty() || (!fileDetailsPojoHashMap.containsKey(fileDetails.getFileId())))) {
                fileListPojo = new ArrayList<>();
                fileListPojo.add(fileDetails);
                TryFileDetails.fileDetailsPojoHashMap.put(commitDetails, fileListPojo);
            }
            return fileDetailsPojoHashMap.get(commitDetails);
        }

        /*getFileIds function populate the fileIdHashMap with respective key and value pairs.
         * where key is fileId and value is list of commits that are performed for a particular file */
        public List<CommitDetails> getFileIds(TryFileDetails fileDetails) {
            String fileId = fileDetails.getFileId();
            List<CommitDetails> commitDetails;
            if (TryFileDetails.fileIdHashMap.containsKey(fileId)) {
                commitDetails = TryFileDetails.fileIdHashMap.get(fileId);
                commitDetails.add(new CommitDetails(fileDetails.getCommitId()));
                TryFileDetails.fileIdHashMap.replace(fileId, commitDetails);
            } else if (TryFileDetails.fileIdHashMap.isEmpty() || (!TryFileDetails.fileIdHashMap.containsKey(fileId))) {
                commitDetails = new ArrayList<>();
                commitDetails.add(new CommitDetails(fileDetails.getCommitId()));
                TryFileDetails.fileIdHashMap.put(fileId, commitDetails);
            }

            return TryFileDetails.fileIdHashMap.get(fileId);
        }
    }

}
