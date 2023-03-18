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
    private int bugFixingCommitGapSize = 5;
    private StringColumn uniqueCommitId;
    private Map<String, List<String>> BFCFileMap;       //BFC: Bug-Fixing-Commit
    private Map<String, List<String>> commitFileMap;    //BFCP: Bug-Fixing-Commit-Period
    private Map<Set<String>, Set<String>> BFCPFileMap;
//    private Map<String, List<Integer>> BFCFileMapRowIndex;   //BFC: Bug-Fixing-Commit
//    private Map<String, List<Integer>> commitFileMapRowIndex;

    private List<List<String>> BFCP;                //BFCP: Bug-Fixing-Commit-Period


    private Map<Set<String>, Set<Integer>> commitsPriorBFC;
    private Map<Set<String>, Set<Integer>> commitsPriorCommit;


    private List<Integer> commitsLeadToBFC_indexRepresentation;
    private List<Integer> commitsLeadToCommit_indexRepresentation;
    private final Table tableSortedByCommitTime;
    private BooleanColumn isBFC;
    public CommitsLeadToBFC(PBFC_StrategyEnum strategy) {
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
        BFCPFileMap = new LinkedHashMap<>();
        //commitFileMap =new LinkedHashMap<>();

        commitsPriorBFC = new LinkedHashMap<>();
        //commitsPriorCommit = new LinkedHashMap<>();

        identifyBFCP();


        //identifyBFCFiles();
        //identifyCommitFiles();

        switch (strategy){
            case past_N_commits:
                past_N_commits();
                break;
            case all_BFC_File_showed_up:
                all_BFC_File_showed_up();
                break;
            case each_BFC_File_showed_up:
                each_BFC_File_showed_up();
                break;
            default:
                break;
        }
        //all_past_commits();

        //each_BFC_File_showed_up();
        //all_BFC_File_showed_up();

    }
    //BFCP: Bug-Fixing-Commit-Period
    private void identifyBFCP(){
        int lastBFCPCommitIndex = -9999;

        Set<String> lastBFCPCommitIdSet = new LinkedHashSet<>();
        Set<String> lastBFCPFilesSet = new HashSet<>();;
        for(int i=0; i<tableSortedByCommitTime.rowCount();i++){
            if(tableSortedByCommitTime.row(i).getBoolean("is_bug_fixing")){
                String BFCCommitId = tableSortedByCommitTime.row(i).getString("id");
                String BFCFileId = tableSortedByCommitTime.row(i).getString("file_id");
                int currentBFCCommitIndex = uniqueCommitId.indexOf(BFCCommitId);
                if(currentBFCCommitIndex- lastBFCPCommitIndex > bugFixingCommitGapSize ){
                    lastBFCPCommitIdSet = new LinkedHashSet<>();
                    lastBFCPFilesSet = new HashSet<>();;
                    lastBFCPCommitIdSet.add(BFCCommitId);
                    lastBFCPFilesSet.add(BFCFileId);
                    BFCPFileMap.put(lastBFCPCommitIdSet,lastBFCPFilesSet);
                }else{
                    lastBFCPCommitIdSet.add(BFCCommitId);
                    lastBFCPFilesSet.add(BFCFileId);
                }

                lastBFCPCommitIndex = currentBFCCommitIndex;
            }

        }

//        for (var entry : BFCPFileMap.entrySet()) {
//            System.out.print("BFC Id: [ ");
//            for(String BFCid:entry.getKey()){
//                System.out.print(BFCid+", ");
//            }
//            System.out.print("]\n");
//            System.out.print("BFC file Id: [ ");
//            for(String fileId:entry.getValue()){
//                System.out.print(fileId+", ");
//            }
//            System.out.print("]\n\n\n");
//
//        }
//
//        System.out.println(BFCPFileMap.size());
//        System.out.println(dataRepository.getBFCRatio()*1.0*uniqueCommitId.size());
    }
    /*private void identifyBFCP(){
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

    }*/

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


    }

    private void identifyCommitFiles(){

        for(Row row: tableSortedByCommitTime){
            String commitId = row.getString("id");
            String fileId = row.getString("file_id");
            if(commitFileMap.containsKey(commitId)){
                commitFileMap.get(commitId).add(fileId);
            }else{
                List<String> fileList = new ArrayList<>();
                fileList.add(fileId);
                commitFileMap.put(commitId, fileList);
            }
        }

        /*for(String commitId: uniqueCommitId){
            if(BFCFileMap.containsKey(commitId)){
                continue;
            }
            Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(commitId);
            StringColumn fileIdColumn = tableSortedByCommitTime.where(matchCommitId).stringColumn("file_id");
            List<String> fileList = fileIdColumn.asList();
            commitFileMap.put(commitId, fileList);
        }*/

        /*System.out.println(commitFileMap.size());
        for (var entry : commitFileMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.exit(123);*/
    }


    //experiment 1: all commits prior BFC
    //experiment 2: past 5(or 10) commits prior BFC
    //experiment 3: past 5 commits for each BFC files
    //experiment 4: past 5 commits in which all BFC files showed up



    //strategy 2: past 5(or 10) commits prior BFC
    /*private void past_N_commits(){
        for (var entry : BFCFiString currentCommitId= uniqueCommitId.get(i);
                commitsPriorBuggyCommit.add(currentCommitId);
                counter++;
                if(counter == lookUpCommitSize){
                    break;
                }
            }
            commitsLeadToBFCs2.put(buggyCommitId, commitsPriorBuggyCommit);
        }


        for (var entry : commitFileMap.entrySet()) {
            String commitId= entry.getKey();leMap.entrySet()) {
            String buggyCommitId= entry.getKey();
            int buggyCommitIdIndex = uniqueCommitId.indexOf(buggyCommitId);
            //List<String> buggyFileList = entry.getValue();
            List<String> commitsPriorBuggyCommit= new ArrayList<>();
            int counter = 0;
            for(int i=buggyCommitIdIndex-1; i>=0; i--){

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


        for (var entry : commitsLeadToBFCe2.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.exit(123);
    }*/


    //strategy 2: past 5(or 10) commits prior BFC
    /*private void past_N_commits(){

        for (var entry : BFCFileMap.entrySet()) {
            String buggyCommitId= entry.getKey();
            int buggyCommitIdIndex = uniqueCommitId.indexOf(buggyCommitId);
            Set<Integer> commitsDataPriorBuggyCommit= new LinkedHashSet<>();
            int counter = 0;
            for(int i=buggyCommitIdIndex-1; i>=0; i--){
                String currentCommitId= uniqueCommitId.get(i);
                Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                Table commitTable= tableSortedByCommitTime.where(matchCommitId);
                for(Row row: commitTable){
                    commitsDataPriorBuggyCommit.add(row.getInt("Index"));
                }
                counter++;
                if(counter == lookUpCommitSize){
                    break;
                }
            }

            commitsPriorBFCs2.put(buggyCommitId,commitsDataPriorBuggyCommit);
        }


        for (var entry : commitFileMap.entrySet()) {
            String commitId= entry.getKey();
            int commitIdIndex = uniqueCommitId.indexOf(commitId);
            Set<Integer> commitsDataPriorCommit= new LinkedHashSet<>();
            int counter = 0;
            for(int i=commitIdIndex-1; i>=0; i--){
                String currentCommitId= uniqueCommitId.get(i);
                Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                Table commitTable= tableSortedByCommitTime.where(matchCommitId);
                for(Row row: commitTable){
                    commitsDataPriorCommit.add(row.getInt("Index"));
                }
                counter++;
                if(counter == lookUpCommitSize){
                    break;
                }
            }
            commitsPriorCommitS2.put(commitId,commitsDataPriorCommit);
        }

    }*/

    //strategy 2: past 5(or 10) commits prior BFC/commit
    private void past_N_commits(){
        commitsLeadToBFC_indexRepresentation = new ArrayList<>(500);
        //commitsLeadToCommit_indexRepresentation = new ArrayList<>(1000);

        for (var entry : BFCPFileMap.entrySet()) {
            Set<String> buggyCommitIdSet= entry.getKey();
            int buggyCommitIdIndex = uniqueCommitId.indexOf(buggyCommitIdSet.iterator().next());
            int counter = 0;
            Set<Integer> commitsDataPriorBuggyCommit= new LinkedHashSet<>();
            for(int i=buggyCommitIdIndex-1; i>=0; i--){
                String currentCommitId= uniqueCommitId.get(i);

                Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                Table commitTable= tableSortedByCommitTime.where(matchCommitId);

                for(Row row: commitTable){
                    commitsLeadToBFC_indexRepresentation.add(row.getInt("Index"));
                    commitsDataPriorBuggyCommit.add(row.getInt("Index"));
                }
                counter++;
                if(counter == lookUpCommitSize){
                    break;
                }
            }
            commitsPriorBFC.put(buggyCommitIdSet,commitsDataPriorBuggyCommit);
        }


        /*for (var entry : BFCFileMap.entrySet()) {
            String buggyCommitId= entry.getKey();
            int buggyCommitIdIndex = uniqueCommitId.indexOf(buggyCommitId);
            int counter = 0;
            Set<Integer> commitsDataPriorBuggyCommit= new LinkedHashSet<>();
            for(int i=buggyCommitIdIndex-1; i>=0; i--){
                String currentCommitId= uniqueCommitId.get(i);
                Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                Table commitTable= tableSortedByCommitTime.where(matchCommitId);
                for(Row row: commitTable){
                    commitsLeadToBFC_indexRepresentation.add(row.getInt("Index"));
                    commitsDataPriorBuggyCommit.add(row.getInt("Index"));
                }
                counter++;
                if(counter == lookUpCommitSize){
                    break;
                }
            }
            commitsPriorBFC.put(buggyCommitId,commitsDataPriorBuggyCommit);
        }

        for (var entry : commitFileMap.entrySet()) {
            String commitId= entry.getKey();
            int commitIdIndex = uniqueCommitId.indexOf(commitId);
            int counter = 0;
            Set<Integer> commitsDataPriorCommit= new LinkedHashSet<>();
            for(int i=commitIdIndex-1; i>=0; i--){
                String currentCommitId= uniqueCommitId.get(i);
                Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                Table commitTable= tableSortedByCommitTime.where(matchCommitId);
                for(Row row: commitTable){
                    commitsLeadToCommit_indexRepresentation.add(row.getInt("Index"));
                    commitsDataPriorCommit.add(row.getInt("Index"));
                }
                counter++;
                if(counter == lookUpCommitSize){
                    break;
                }
            }
            commitsPriorCommit.put(commitId,commitsDataPriorCommit);

        }*/

    }


    //strategy 3: past 5 commits that each BFC files showed up
    /*private void each_BFC_File_showed_up(){

        for (var entry : BFCFileMap.entrySet()) {
            String buggyCommitId= entry.getKey();
            int buggyCommitIdIndex = uniqueCommitId.indexOf(buggyCommitId);
            Set<Integer> commitsDataPriorBuggyCommit= new LinkedHashSet<>();
            for(String buggyFile: entry.getValue()){
                int counter =0;
                for(int i=buggyCommitIdIndex-1; i>=0; i--){
                    String currentCommitId= uniqueCommitId.get(i);
                    Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                    Table commitTable = tableSortedByCommitTime.where(matchCommitId);
                    StringColumn fileIdColumn = commitTable.stringColumn("file_id");

                    if(fileIdColumn.contains(buggyFile)){
                        for(Row row: commitTable){
                            commitsDataPriorBuggyCommit.add(row.getInt("Index"));
                        }

                        counter++;
                        if(counter == lookUpCommitSize){
                            break;
                        }
                    }

                }
            }

            commitsPriorBFCs3.put(buggyCommitId,commitsDataPriorBuggyCommit);
        }

        for (var entry : commitFileMap.entrySet()) {
            String commitId= entry.getKey();
            int commitIdIndex = uniqueCommitId.indexOf(commitId);
            Set<Integer> commitsDataPriorCommit= new LinkedHashSet<>();
            for(String fileId: entry.getValue()){
                int counter =0;
                for(int i=commitIdIndex-1; i>=0; i--){
                    String currentCommitId= uniqueCommitId.get(i);
                    Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                    Table commitTable = tableSortedByCommitTime.where(matchCommitId);
                    StringColumn fileIdColumn = commitTable.stringColumn("file_id");

                    if(fileIdColumn.contains(fileId)){
                        for(Row row: commitTable){
                            commitsDataPriorCommit.add(row.getInt("Index"));
                        }
                        counter++;
                        if(counter == lookUpCommitSize){
                            break;
                        }
                    }

                }
            }

            commitsPriorCommitS3.put(commitId,commitsDataPriorCommit);
        }

    }*/

    //strategy 3: past 5 commits that each BFC/commit files showed up
    private void each_BFC_File_showed_up(){
        commitsLeadToBFC_indexRepresentation = new ArrayList<>(500);
        //commitsLeadToCommit_indexRepresentation = new ArrayList<>(1000);
        for (var entry : BFCPFileMap.entrySet()) {
            Set<String> buggyCommitIdSet= entry.getKey();
            int buggyCommitIdIndex = uniqueCommitId.indexOf(buggyCommitIdSet.iterator().next());
            Set<Integer> commitsDataPriorBuggyCommit= new LinkedHashSet<>();
            for(String buggyFile: entry.getValue()){
                int counter =0;
                for(int i=buggyCommitIdIndex-1; i>=0; i--){
                    String currentCommitId= uniqueCommitId.get(i);
                    Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                    Table commitTable = tableSortedByCommitTime.where(matchCommitId);
                    StringColumn fileIdColumn = commitTable.stringColumn("file_id");

                    if(fileIdColumn.contains(buggyFile)){
                        for(Row row: commitTable){
                            commitsLeadToBFC_indexRepresentation.add(row.getInt("Index"));
                            commitsDataPriorBuggyCommit.add(row.getInt("Index"));
                        }
                        counter++;
                        if(counter == lookUpCommitSize){
                            break;
                        }
                    }

                }
            }
            commitsPriorBFC.put(buggyCommitIdSet,commitsDataPriorBuggyCommit);
        }

//        for (var entry : commitsPriorBFC.entrySet()) {
//            System.out.print("BFC Id: [ ");
//            for(String BFCid:entry.getKey()){
//                System.out.print(BFCid+", ");
//            }
//            System.out.print("]\n");
//            System.out.print("BFC file Id: [ ");
//            for(int fileId:entry.getValue()){
//                System.out.print(fileId+", ");
//            }
//            System.out.print("]\n\n\n");
//
//        }
//        System.exit(88995566);
        /*for (var entry : BFCFileMap.entrySet()) {
            String buggyCommitId= entry.getKey();
            int buggyCommitIdIndex = uniqueCommitId.indexOf(buggyCommitId);
            Set<Integer> commitsDataPriorBuggyCommit= new LinkedHashSet<>();
            for(String buggyFile: entry.getValue()){
                int counter =0;
                for(int i=buggyCommitIdIndex-1; i>=0; i--){
                    String currentCommitId= uniqueCommitId.get(i);
                    Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                    Table commitTable = tableSortedByCommitTime.where(matchCommitId);
                    StringColumn fileIdColumn = commitTable.stringColumn("file_id");

                    if(fileIdColumn.contains(buggyFile)){
                        for(Row row: commitTable){
                            commitsLeadToBFC_indexRepresentation.add(row.getInt("Index"));
                            commitsDataPriorBuggyCommit.add(row.getInt("Index"));
                        }
                        counter++;
                        if(counter == lookUpCommitSize){
                            break;
                        }
                    }

                }
            }
            commitsPriorBFC.put(buggyCommitId,commitsDataPriorBuggyCommit);
        }

        for (var entry : commitFileMap.entrySet()) {
            String commitId= entry.getKey();
            int commitIdIndex = uniqueCommitId.indexOf(commitId);
            Set<Integer> commitsDataPriorCommit= new LinkedHashSet<>();
            for(String fileId: entry.getValue()){
                int counter =0;
                for(int i=commitIdIndex-1; i>=0; i--){
                    String currentCommitId= uniqueCommitId.get(i);
                    Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                    Table commitTable = tableSortedByCommitTime.where(matchCommitId);
                    StringColumn fileIdColumn = commitTable.stringColumn("file_id");

                    if(fileIdColumn.contains(fileId)){
                        for(Row row: commitTable){
                            commitsLeadToCommit_indexRepresentation.add(row.getInt("Index"));
                            commitsDataPriorCommit.add(row.getInt("Index"));
                        }
                        counter++;
                        if(counter == lookUpCommitSize){
                            break;
                        }
                    }

                }
            }
            commitsPriorCommit.put(commitId,commitsDataPriorCommit);
        }*/

    }



    //strategy 4: past 5 commits in which all BFC files showed up together
    /*private void all_BFC_File_showed_up(){
        commitsLeadToBFC = new ArrayList<>(500);
        commitsLeadToCommit = new ArrayList<>(1000);
        for (var entry : BFCFileMap.entrySet()) {
            String buggyCommitId= entry.getKey();
            int buggyCommitIdIndex = uniqueCommitId.indexOf(buggyCommitId);
            List<String> buggyFileList = entry.getValue();
            Set<Integer> commitsDataPriorBuggyCommit= new LinkedHashSet<>();
            int counter =0;
            for(int i=buggyCommitIdIndex-1; i>=0; i--){
                String currentCommitId= uniqueCommitId.get(i);
                Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                Table commitTable = tableSortedByCommitTime.where(matchCommitId);
                StringColumn fileIdColumn = commitTable.stringColumn("file_id");
                if(fileIdColumn.size()<buggyFileList.size()){
                    continue;
                }
                if(fileIdColumn.asList().containsAll(buggyFileList)) {
                    counter++;
                    for(Row row: commitTable){
                        commitsDataPriorBuggyCommit.add(row.getInt("Index"));
                    }
                }

//                boolean containAll = true;
//                for(String buggyFile: buggyFileList){
//                    if(!fileIdColumn.contains(buggyFile)){
//                        containAll=false;
//                        break;
//                    }
//                }
//
//                if(containAll){
//                    commitsPriorBuggyCommit.add(currentCommitId);
//                    counter++;
//                }
                if(counter == lookUpCommitSize){
                    break;
                }
            }

            commitsPriorBFCs4.put(buggyCommitId, commitsDataPriorBuggyCommit);
            //System.out.println(entry.getKey() + " " + entry.getValue());
        }


        for (var entry : commitFileMap.entrySet()) {
            String commitId= entry.getKey();
            int commitIdIndex = uniqueCommitId.indexOf(commitId);
            List<String> fileList = entry.getValue();
            Set<Integer> commitsDataPriorCommit= new LinkedHashSet<>();
            int counter =0;
            for(int i=commitIdIndex-1; i>=0; i--){
                String currentCommitId= uniqueCommitId.get(i);
                Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                Table commitTable = tableSortedByCommitTime.where(matchCommitId);
                StringColumn fileIdColumn = commitTable.stringColumn("file_id");

                if(fileIdColumn.asList().containsAll(fileList)){
                    for(Row row: commitTable){
                        commitsDataPriorCommit.add(row.getInt("Index"));
                    }
                    counter++;
                }
                if(counter == lookUpCommitSize){
                    break;
                }

            }

            commitsPriorCommitS4.put(commitId, commitsDataPriorCommit);
            //System.out.println(entry.getKey() + " " + entry.getValue());
        }

    }*/

    //strategy 4: past 5 commits in which all BFC/commit files showed up together
    private void all_BFC_File_showed_up(){
        commitsLeadToBFC_indexRepresentation = new ArrayList<>(500);
        //commitsLeadToCommit_indexRepresentation = new ArrayList<>(1000);

        for (var entry : BFCPFileMap.entrySet()) {

            Set<String> buggyCommitIdSet= entry.getKey();
            int buggyCommitIdIndex = uniqueCommitId.indexOf(buggyCommitIdSet.iterator().next());
            Set<String> buggyFileList = entry.getValue();
            Set<Integer> commitsDataPriorBuggyCommit= new LinkedHashSet<>();
            int counter =0;
            for(int i=buggyCommitIdIndex-1; i>=0; i--){
                String currentCommitId= uniqueCommitId.get(i);
                Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                Table commitTable = tableSortedByCommitTime.where(matchCommitId);
                StringColumn fileIdColumn = commitTable.stringColumn("file_id");
                /*if(fileIdColumn.size()<buggyFileList.size()){
                    continue;
                }*/
                if(fileIdColumn.asList().containsAll(buggyFileList)) {
                    counter++;
                    for(Row row: commitTable){
                        commitsDataPriorBuggyCommit.add(row.getInt("Index"));
                        commitsLeadToBFC_indexRepresentation.add(row.getInt("Index"));
                    }
                }

                if(counter == lookUpCommitSize){
                    break;
                }
            }

            commitsPriorBFC.put(buggyCommitIdSet, commitsDataPriorBuggyCommit);
            //System.out.println(entry.getKey() + " " + entry.getValue());
        }



        /*for (var entry : BFCFileMap.entrySet()) {
            String buggyCommitId= entry.getKey();
            int buggyCommitIdIndex = uniqueCommitId.indexOf(buggyCommitId);
            List<String> buggyFileList = entry.getValue();
            Set<Integer> commitsDataPriorBuggyCommit= new LinkedHashSet<>();
            int counter =0;
            for(int i=buggyCommitIdIndex-1; i>=0; i--){
                String currentCommitId= uniqueCommitId.get(i);
                Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                Table commitTable = tableSortedByCommitTime.where(matchCommitId);
                StringColumn fileIdColumn = commitTable.stringColumn("file_id");

                if(fileIdColumn.asList().containsAll(buggyFileList)) {
                    counter++;
                    for(Row row: commitTable){
                        commitsDataPriorBuggyCommit.add(row.getInt("Index"));
                        commitsLeadToBFC_indexRepresentation.add(row.getInt("Index"));
                    }
                }

                if(counter == lookUpCommitSize){
                    break;
                }
            }

            commitsPriorBFC.put(buggyCommitId, commitsDataPriorBuggyCommit);
            //System.out.println(entry.getKey() + " " + entry.getValue());
        }


        for (var entry : commitFileMap.entrySet()) {
            String commitId= entry.getKey();
            int commitIdIndex = uniqueCommitId.indexOf(commitId);
            List<String> fileList = entry.getValue();
            Set<Integer> commitsDataPriorCommit= new LinkedHashSet<>();
            int counter =0;
            for(int i=commitIdIndex-1; i>=0; i--){
                String currentCommitId= uniqueCommitId.get(i);
                Selection matchCommitId = tableSortedByCommitTime.stringColumn("id").isEqualTo(currentCommitId);
                Table commitTable = tableSortedByCommitTime.where(matchCommitId);
                StringColumn fileIdColumn = commitTable.stringColumn("file_id");

                if(fileIdColumn.asList().containsAll(fileList)){
                    for(Row row: commitTable){
                        commitsLeadToCommit_indexRepresentation.add(row.getInt("Index"));
                    }
                    counter++;
                }
                if(counter == lookUpCommitSize){
                    break;
                }

            }
            commitsPriorCommit.put(commitId, commitsDataPriorCommit);
        }*/

    }

    public static void main(String[] args) {

        TestReadingStrategy trs = new TestReadingStrategy();
        try {

            //trs.parseData();

        } catch (Exception e) {
            System.out.println("error!");
            e.printStackTrace();
        }
        CommitsLeadToBFC pa =new CommitsLeadToBFC(PBFC_StrategyEnum.past_N_commits);

        /*for (var entry : pa.commitsLeadToBFCe4.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }*/
    }
}
