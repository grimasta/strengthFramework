package com.example.inj.commitBuilder;


import com.example.inj.commitRepository.CommitDetails;

import java.util.HashMap;

public class TryCommitDetails {
    private String commitID;
    private static HashMap<String, CommitDetails> CommitDetailsPojo = new HashMap<>();

    private TryCommitDetails(UserBuilder builder) {
        this.commitID = builder.commitID;
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

    public static class UserBuilder {
        private String commitID;

        public UserBuilder(String commitID) {
            this.commitID = commitID;
        }

        public TryCommitDetails build() {
            TryCommitDetails user = new TryCommitDetails(this);
            createCommitHashMap(user, this.commitID);
            return user;
        }

        public void createCommitHashMap(TryCommitDetails user, String commitID) {
            if (!TryCommitDetails.CommitDetailsPojo.containsKey(commitID)) {
            	TryCommitDetails.CommitDetailsPojo.put(commitID, new CommitDetails(commitID));
            }
        }
    }
}