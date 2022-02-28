package com.example.inj.commitRepository;

import com.example.inj.attributes.IncrementalField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class CommitDetails {

    private String commitID;
    private IncrementalField increF;

    public IncrementalField getIncreF() {
        return increF;
    }

    public void setIncreF(IncrementalField increF) {
        this.increF = increF;
    }
    private List<Integer> sortedListOfChanges = new ArrayList<Integer>(){
    	static final long serialVersionUID = 0;
        public boolean add(Integer mt) {
            super.add(mt);
            Collections.sort(sortedListOfChanges, Comparator.naturalOrder());
            return true;
       }
   }; ;
    public List<Integer> getSortedListOfChanges() {
		return sortedListOfChanges;
	}

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

    public void addFileModifiedLines(int added, int deleted) {
    	this.sortedListOfChanges.add(added+deleted);
    }
    
    public double getPercentile(int linesModifiedOfFile) {
    	return ((float) (this.sortedListOfChanges.indexOf(linesModifiedOfFile) + 1) / (this.sortedListOfChanges.size())) * 100; 
    }
    
    public double getMedianModifiedLines() {
    	return this.sortedListOfChanges.get(this.sortedListOfChanges.size()/2);
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