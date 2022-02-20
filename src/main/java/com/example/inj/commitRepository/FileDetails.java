package com.example.inj.commitRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileDetails {

    private String fileId;
    private String commitId;
    private String addition;
    private String deletion;
    private CommitDetails commit;

    public static HashMap<String, List<CommitDetails>> fileIdHashMap = new HashMap<>();
    private static HashMap<CommitDetails, List<FileDetails>> fileDetailsPojoHashMap = new HashMap<>();

    public FileDetails(String fileId, String commitId, String addition, String deletion) {
        this.fileId = fileId;
        this.commitId = commitId;
        this.addition = addition;
        this.deletion = deletion;
    }

    public CommitDetails getCommit() {
        return commit;
    }

    public void setCommit(CommitDetails commit) {
        this.commit = commit;
    }

    public static HashMap<CommitDetails, List<FileDetails>> getFileDetailsPojoHashMap() {
        return fileDetailsPojoHashMap;
    }

    public static HashMap<String, List<CommitDetails>> getFileIdHashMap() {
        return fileIdHashMap;
    }

    public static void setFileIdHashMap(HashMap<String, List<CommitDetails>> fileIdHashMap) {
        FileDetails.fileIdHashMap = fileIdHashMap;
    }

    public static void setFileDetailsPojoHashMap(HashMap<CommitDetails, List<FileDetails>> fileDetailsPojoHashMap) {
        FileDetails.fileDetailsPojoHashMap = fileDetailsPojoHashMap;
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

    public String getAddition() {
        return addition;
    }

    public void setAddition(String addition) {
        this.addition = addition;
    }

    public String getDeletion() {
        return deletion;
    }

    public void setDeletion(String deletion) {
        this.deletion = deletion;
    }

    @Override
    public String toString() {
        return "FileDetailsPojo{" +
                "fileId='" + fileId + '\'' +
                '}';
    }

    /*createHashMap function populate the fileDetailsPojoHashMap with respective key and value pairs.
     * where key is commitId and value is list of files that are committed under the specified key */
    public List<FileDetails> createHashMap(FileDetails fileDetails) {
        CommitDetails commitDetails = CommitDetails.getCommitDetailsPojo().get(fileDetails.getCommitId());
        List<FileDetails> fileListPojo;
        if (fileDetailsPojoHashMap.containsKey(commitDetails)) {
            fileListPojo = fileDetailsPojoHashMap.get(commitDetails);
            fileListPojo.add(fileDetails);
            FileDetails.fileDetailsPojoHashMap.replace(commitDetails, fileListPojo);
        } else if ((fileDetailsPojoHashMap.isEmpty() || (!fileDetailsPojoHashMap.containsKey(commitDetails)))) {
            fileListPojo = new ArrayList<>();
            fileListPojo.add(fileDetails);
            FileDetails.fileDetailsPojoHashMap.put(commitDetails, fileListPojo);
        }
        return fileDetailsPojoHashMap.get(commitDetails);
    }

    /*getFileIds function populate the fileIdHashMap with respective key and value pairs.
     * where key is fileId and value is list of commits that are performed for a particular file */
    public List<CommitDetails> getFileIds(FileDetails fileDetails) {
        String fileId = fileDetails.getFileId();
        List<CommitDetails> commitDetails;
        if (fileIdHashMap.containsKey(fileId)) {
            commitDetails = fileIdHashMap.get(fileId);
            commitDetails.add(new CommitDetails(fileDetails.getCommitId()));
            fileIdHashMap.replace(fileId, commitDetails);
        } else if (fileIdHashMap.isEmpty() || (!fileIdHashMap.containsKey(fileId))) {
            commitDetails = new ArrayList<>();
            commitDetails.add(new CommitDetails(fileDetails.getCommitId()));
            fileIdHashMap.put(fileId, commitDetails);
        }

        return fileIdHashMap.get(fileId);
    }


}
