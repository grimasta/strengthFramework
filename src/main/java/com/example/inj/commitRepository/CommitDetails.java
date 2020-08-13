package com.example.inj.commitRepository;

import java.util.HashMap;

public class CommitDetails {

    private String commitID;
    private static HashMap<String, CommitDetails> CommitDetailsPojo = new HashMap<>();

    public CommitDetails(String commitID) {
        this.commitID = commitID;
    }

    public String getCommitID() {
        return commitID;
    }

    public void setCommitID(String commitID) {
        this.commitID = commitID;
    }

    public static HashMap<String, CommitDetails> getCommitDetailsPojo() {
        return CommitDetailsPojo;
    }

    public static void setCommitDetailsPojo(HashMap<String, CommitDetails> commitDetailsPojo) {
        CommitDetailsPojo = commitDetailsPojo;
    }

    @Override
    public String toString() {
        return "CommitDetailsPojo{" +
                "commitID='" + commitID + '\'' +
                '}';
    }

    /*createCommitHashMap functions create the Hashmap with respective key values pair
     * where key is commitId, and values are Commit Object*/
    public void createCommitHashMap(String commitID) {
        if (!CommitDetailsPojo.containsKey(commitID)) {
            CommitDetailsPojo.put(commitID, new CommitDetails(commitID));
        }

    }

}