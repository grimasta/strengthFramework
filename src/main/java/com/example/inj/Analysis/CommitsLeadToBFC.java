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
    private List<List<String>> BFCP;                //BFCP: Bug-Fixing-Commit-Period
    private Map<String, List<String>> commitsLeadToBFCe1;   //for experiment1
    private Map<String, List<String>> commitsLeadToBFCe2;   //for experiment2
    private Map<String, List<String>> commitsLeadToBFCe3;   //for experiment3
    private Map<String, List<String>> commitsLeadToBFCe4;   //for experiment4


    private final Table tableSortedByCommitTime;
    private BooleanColumn isBFC;
    public CommitsLeadToBFC() {
        dataRepository = DataRepository.getInstance();
        tableSortedByCommitTime = dataRepository.getTableSortedByCommitTime();
        isBFC = tableSortedByCommitTime.booleanColumn("is_bug_fixing");
        StringColumn commitId = dataRepository.getTableSortedByCommitTime().stringColumn("id");
        uniqueCommitId = StringColumn.create("uci");
        for(String id: commitId ){
            if(!uniqueCommitId.contains(id)){
                uniqueCommitId.append(id);
            }
        }
        BFCFileMap = new LinkedHashMap<>();

        commitsLeadToBFCe1 = new LinkedHashMap<>();
        commitsLeadToBFCe2 = new LinkedHashMap<>();
        commitsLeadToBFCe3 = new LinkedHashMap<>();
        commitsLeadToBFCe4 = new LinkedHashMap<>();
        identifyBFCFiles();
        //identifyBFCP();
        //all_past_commits();
        past_N_commits();
        each_BFC_File_showed_up();
        all_BFC_File_showed_up();

    }

    //BFC: Bug-Fixing-Commit
    void identifyBFCFiles(){
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

        /*for (var entry : BFCFileMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }*/
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


    //experiment 1: all commits prior BFC

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
            commitsLeadToBFCe2.put(buggyCommitId, commitsPriorBuggyCommit);
        }
    }


    //experiment 2: past 5(or 10) commits prior BFC
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
            commitsLeadToBFCe2.put(buggyCommitId, commitsPriorBuggyCommit);
        }

        /*for (var entry : commitsLeadToBFCe2.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }*/
    }


    //experiment 3: past 5 commits that each BFC files showed up
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

            commitsLeadToBFCe3.put(buggyCommitId, commitsPriorBuggyCommit);
        }

        /*for (var entry : commitsLeadToBFCe3.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.exit(123);*/
    }



    //experiment 4: past 5 commits in which all BFC files showed up together
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

            commitsLeadToBFCe4.put(buggyCommitId, commitsPriorBuggyCommit);
            //System.out.println(entry.getKey() + " " + entry.getValue());
        }


        /*for (var entry : commitsLeadToBFCe4.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }*/
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
