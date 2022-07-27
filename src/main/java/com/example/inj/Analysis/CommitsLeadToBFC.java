package com.example.inj.Analysis;

import com.example.inj.ReadingStrategy.TestReadingStrategy;
import com.example.inj.model.storage.DataRepository;
import lombok.Getter;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

import java.util.*;

@Getter
public class CommitsLeadToBFC {
    private DataRepository dataRepository;
    private int lookUpCommitSize= 5;
    private int bugFixingCommitGapSize = 3;
    private StringColumn uniqueCommitId;
    private Map<String, List<String>> BFCFileMap;   //BFC: Bug-Fixing-Commit
    private Map<String, List<String>> commitFileMap;
    private List<List<String>> BFCP;                //BFCP: Bug-Fixing-Commit-Period
    private Map<String, List<String>> commitsLeadToBFCs1;   //strategy1
    private Map<String, List<String>> commitsLeadToBFCs2;   //strategy2
    private Map<String, List<String>> commitsLeadToBFCs3;   //strategy3
    private Map<String, List<String>> commitsLeadToBFCs4;   //strategy4


    private Map<String, List<String>> commitsLeadToCommitS2;   //strategy2
    private Map<String, List<String>> commitsLeadToCommitS3;   //strategy3
    private Map<String, List<String>> commitsLeadToCommitS4;   //strategy4

    private final Table tableSortedByCommitTime;
    private BooleanColumn isBFC;
    public CommitsLeadToBFC() {
        dataRepository = DataRepository.getInstance();
        tableSortedByCommitTime = dataRepository.getTableSortedByCommitTime();
        isBFC = tableSortedByCommitTime.booleanColumn("is_bug_fixing");
        StringColumn commitId = dataRepository.getTableSortedByCommitTime().stringColumn("id");
        uniqueCommitId = dataRepository.getUniqueCommitId();
        /*for(String id: commitId ){
            if(!uniqueCommitId.contains(id)){
                uniqueCommitId.append(id);
            }
        }*/
        BFCFileMap = new LinkedHashMap<>();
        commitFileMap =new LinkedHashMap<>();
        commitsLeadToBFCs1 = new LinkedHashMap<>();
        commitsLeadToBFCs2 = new LinkedHashMap<>();
        commitsLeadToBFCs3 = new LinkedHashMap<>();
        commitsLeadToBFCs4 = new LinkedHashMap<>();

        commitsLeadToCommitS2= new LinkedHashMap<>();
        commitsLeadToCommitS3= new LinkedHashMap<>();
        commitsLeadToCommitS4= new LinkedHashMap<>();
        identifyBFCFiles();
        identifyCommitFiles();

        //identifyBFCP();
        //all_past_commits();


        //past_N_commits();
        each_BFC_File_showed_up();
        //all_BFC_File_showed_up();

    }

    //BFC: Bug-Fixing-Commit
    //not all files in a BFC are labelled "is_bug_fixing"
    private void identifyBFCFiles(){
        for(Row row: tableSortedByCommitTime){
            if(row.getBoolean("is_bug_fixing")){
                String commitId = row.getString("id");
                String fileId = row.getString("file_id");
                if(BFCFileMap.containsKey(commitId)){
                    BFCFileMap.get(commitId).add(fileId);
                }else{
                    List<String> tempList = new ArrayList<>();
                    tempList.add(fileId);
                    BFCFileMap.put(commitId, tempList);
                }
            }
        }

        /*
        System.out.println("*********BFCFileMap*********");
        System.out.println(BFCFileMap.size());
        for (var entry : BFCFileMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.out.println("*********BFCFileMap DONE*********");
        System.out.println("*********BFCFileMap DONE*********");
        System.out.println("*********BFCFileMap DONE*********");

        System.exit(123);*/
    }

    private void identifyCommitFiles(){
        for(String commitId: uniqueCommitId){
            if(BFCFileMap.containsKey(commitId)){
                continue;
            }
            Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(commitId);
            StringColumn fileIdColumn = tableSortedByCommitTime.where(matchCommitId).stringColumn("file_id");
            List<String> fileList = fileIdColumn.asList();
            commitFileMap.put(commitId, fileList);
        }

        //commitFileMap.putAll(BFCFileMap);


        /*System.out.println(commitFileMap.size());
        for (var entry : commitFileMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.exit(123);*/
    }



    //BFCP: Bug-Fixing-Commit-Period
    private void identifyBFCP(){
        BFCP = new ArrayList<>();
        List<String> tempList = new ArrayList<>();
        String[] buggyCommitIdArray = BFCFileMap.keySet().toArray(new String[0]);
        String previousCommitId= buggyCommitIdArray[0];
        tempList.add(previousCommitId);
        for(int i=1; i<buggyCommitIdArray.length; i++){

            int previousIndex = uniqueCommitId.indexOf(buggyCommitIdArray[i-1]);
            int currentIndex = uniqueCommitId.indexOf(buggyCommitIdArray[i]);

            if(currentIndex-previousIndex > bugFixingCommitGapSize){
                BFCP.add(tempList);
                tempList = new ArrayList<>();
            }
            tempList.add(buggyCommitIdArray[i]);
        }
        BFCP.add(tempList);

        //System.out.println(buggyCommitIdArray);
        //Arrays.stream(buggyCommitIdArray).forEach(System.out::println);
        /*for(String commitId : BFCFileMap.keySet()){
        }*/

        /*for(List<String> commitList: BFCP){
            for(String commitID: commitList){
                System.out.print(commitID+ " ");
            }
            System.out.println();
        }*/
    }
    //experiment 1: all commits prior BFC
    //experiment 2: past 5(or 10) commits prior BFC
    //experiment 3: past 5 commits for each BFC files
    //experiment 4: past 5 commits in which all BFC files showed up



    //strategy 1: all commits prior BFC
    private void all_past_commits(){
        for (var entry : BFCFileMap.entrySet()) {
            String buggyCommitId= entry.getKey();
            int buggyCommitIdIndex = uniqueCommitId.indexOf(buggyCommitId);
            //List<String> buggyFileList = entry.getValue();
            List<String> commitsPriorBuggyCommit= new ArrayList<>();
            for(int i=buggyCommitIdIndex-1; i>=0; i--){
                String currentCommitId= uniqueCommitId.get(i);
                commitsPriorBuggyCommit.add(currentCommitId);
            }
            commitsLeadToBFCs1.put(buggyCommitId, commitsPriorBuggyCommit);
        }
        /*for (var entry : commitsLeadToBFCe1.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.exit(123);*/
    }


    //strategy 2: past 5(or 10) commits prior BFC
    private void past_N_commits(){
        for (var entry : BFCFileMap.entrySet()) {
            String buggyCommitId= entry.getKey();
            int buggyCommitIdIndex = uniqueCommitId.indexOf(buggyCommitId);
            //List<String> buggyFileList = entry.getValue();
            List<String> commitsPriorBuggyCommit= new ArrayList<>();
            int counter = 0;
            for(int i=buggyCommitIdIndex-1; i>=0; i--){
                String currentCommitId= uniqueCommitId.get(i);
                commitsPriorBuggyCommit.add(currentCommitId);
                counter++;
                if(counter == lookUpCommitSize){
                    break;
                }
            }
            commitsLeadToBFCs2.put(buggyCommitId, commitsPriorBuggyCommit);
        }


        for (var entry : commitFileMap.entrySet()) {
            String commitId= entry.getKey();
            int commitIdIndex = uniqueCommitId.indexOf(commitId);
            //List<String> buggyFileList = entry.getValue();
            List<String> commitsPriorCommit= new ArrayList<>();
            int counter = 0;
            for(int i=commitIdIndex-1; i>=0; i--){
                String currentCommitId= uniqueCommitId.get(i);
                commitsPriorCommit.add(currentCommitId);
                counter++;
                if(counter == lookUpCommitSize){
                    break;
                }
            }
            commitsLeadToCommitS2.put(commitId, commitsPriorCommit);
        }


        /*for (var entry : commitsLeadToBFCe2.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.exit(123);*/
    }




    //strategy 3: past 5 commits that each BFC files showed up
    private void each_BFC_File_showed_up(){
        for (var entry : BFCFileMap.entrySet()) {
            String buggyCommitId= entry.getKey();
            int buggyCommitIdIndex = uniqueCommitId.indexOf(buggyCommitId);
            List<String> buggyFileList = entry.getValue();
            List<String> commitsPriorBuggyCommit= new ArrayList<>();

            for(String buggyFile: buggyFileList){
                int counter =0;
                for(int i=buggyCommitIdIndex-1; i>=0; i--){
                    String currentCommitId= uniqueCommitId.get(i);
                    Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                    StringColumn fileIdColumn = tableSortedByCommitTime.where(matchCommitId).stringColumn("file_id");
                    if(fileIdColumn.contains(buggyFile)){
                        if(!commitsPriorBuggyCommit.contains(currentCommitId)){
                            commitsPriorBuggyCommit.add(currentCommitId);
                        }
                        counter++;
                        if(counter == lookUpCommitSize){
                            break;
                        }
                    }

                }
            }

            commitsLeadToBFCs3.put(buggyCommitId, commitsPriorBuggyCommit);
        }

        for (var entry : commitFileMap.entrySet()) {
            String commitId= entry.getKey();
            int commitIdIndex = uniqueCommitId.indexOf(commitId);
            List<String> fileList = entry.getValue();
            List<String> commitsPriorCommit= new ArrayList<>();

            for(String fileId: fileList){
                int counter =0;
                for(int i=commitIdIndex-1; i>=0; i--){
                    String currentCommitId= uniqueCommitId.get(i);
                    Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                    StringColumn fileIdColumn = tableSortedByCommitTime.where(matchCommitId).stringColumn("file_id");
                    if(fileIdColumn.contains(fileId)){
                        if(!commitsPriorCommit.contains(currentCommitId)){
                            commitsPriorCommit.add(currentCommitId);
                        }
                        counter++;
                        if(counter == lookUpCommitSize){
                            break;
                        }
                    }

                }
            }

            commitsLeadToCommitS3.put(commitId, commitsPriorCommit);
            commitsLeadToCommitS3.putAll(commitsLeadToBFCs3);
        }


        /*for (var entry : commitsLeadToCommitS3.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.exit(123);*/
    }



    //strategy 4: past 5 commits in which all BFC files showed up together
    private void all_BFC_File_showed_up(){
        for (var entry : BFCFileMap.entrySet()) {
            String buggyCommitId= entry.getKey();
            int buggyCommitIdIndex = uniqueCommitId.indexOf(buggyCommitId);
            List<String> buggyFileList = entry.getValue();
            List<String> commitsPriorBuggyCommit= new ArrayList<>();
            int counter =0;
            for(int i=buggyCommitIdIndex-1; i>=0; i--){
                String currentCommitId= uniqueCommitId.get(i);
                Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                StringColumn fileIdColumn = tableSortedByCommitTime.where(matchCommitId).stringColumn("file_id");
                if(fileIdColumn.size()<buggyFileList.size()){
                    continue;
                }
                boolean containAll = true;
                for(String buggyFile: buggyFileList){
                    if(!fileIdColumn.contains(buggyFile)){
                        containAll=false;
                        break;
                    }
                }

                if(containAll){
                    commitsPriorBuggyCommit.add(currentCommitId);
                    counter++;
                }
                if(counter == lookUpCommitSize){
                    break;
                }
            }

            commitsLeadToBFCs4.put(buggyCommitId, commitsPriorBuggyCommit);
            //System.out.println(entry.getKey() + " " + entry.getValue());
        }


        for (var entry : commitFileMap.entrySet()) {
            String commitId= entry.getKey();
            int commitIdIndex = uniqueCommitId.indexOf(commitId);
            List<String> fileList = entry.getValue();
            List<String> commitsPriorCommit= new ArrayList<>();

            int counter =0;
            for(int i=commitIdIndex-1; i>=0; i--){
                String currentCommitId= uniqueCommitId.get(i);
                Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                StringColumn fileIdColumn = tableSortedByCommitTime.where(matchCommitId).stringColumn("file_id");
                if(fileIdColumn.size()<fileList.size()){
                    continue;
                }
                if(fileIdColumn.asList().containsAll(fileList)){
                    commitsPriorCommit.add(currentCommitId);
                    counter++;
                }
                if(counter == lookUpCommitSize){
                    break;
                }
                /*boolean containAll = true;
                for(String fileId: fileList){
                    if(!fileIdColumn.contains(fileId)){
                        containAll=false;
                        break;
                    }
                }

                if(containAll){
                    commitsPriorCommit.add(currentCommitId);
                    counter++;
                }
                if(counter == lookUpCommitSize){
                    break;
                }*/
            }

            commitsLeadToCommitS3.put(commitId, commitsPriorCommit);
            commitsLeadToCommitS3.putAll(commitsLeadToBFCs4);
            //System.out.println(entry.getKey() + " " + entry.getValue());
        }








        /*for (var entry : commitsLeadToBFCe4.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.exit(123);*/
    }

    public static void main(String[] args) {
        TestReadingStrategy trs = new TestReadingStrategy();
        try {

            trs.parseData();

        } catch (Exception e) {
            System.out.println("error!");
            e.printStackTrace();
        }
        CommitsLeadToBFC pa =new CommitsLeadToBFC();

        /*for (var entry : pa.commitsLeadToBFCe4.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }*/
    }
}
