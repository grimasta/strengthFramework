package com.example.inj.Analysis;

import com.example.inj.ReadingStrategy.TestReadingStrategy;
import com.example.inj.model.storage.DataRepository;

import com.google.common.collect.Sets;
import lombok.Getter;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

import java.util.*;

@Getter
public class VectorConcurrentlyUp {
    private Map<Integer, Set<Integer>> vectorHigh;
    private Map<String, Map<Integer, Set<Integer>>> vectorConcurrentlyHigh;
    private Map<String, Map<Integer, Set<Integer>>> vectorConcurrentlyLow;
    private Map<String,Integer> mostConcurrentlyHigh;
    private Map<String,Integer> mostConcurrentlyLow;
    //private int mostConcurrentlyHigh;
    //private int mostConcurrentlyLow;
    private HashSet<Integer> vectorIndex;
    private DataRepository dataRepository;
    private ArrayList<Integer> metricToBeRemoved;
    private Map<String, List<Integer>> vectorToBeStudied;
    private CommitsLeadToBFC cltBFC;
    private int size;
/*    private ArrayList<DoubleColumn> metricColumnList;
    private Map<Integer, Integer> shiftedIndexMap;
    private ArrayList<String> metricColumnNameList;
    private DoubleColumn[] metricColumnArray;
    private Tree[] vectorConcurrentlyUpTree;
    private int length;*/


    VectorConcurrentlyUp(CommitsLeadToBFC cltBFC){
        //Set<Integer> intersectionSet = Sets.intersection(vectorUp[1], vectorUp[2]);
        this.cltBFC = cltBFC;
        dataRepository = DataRepository.getInstance();;
        metricToBeRemoved= new ArrayList<>();
        vectorIndex = new HashSet<>();
        size = dataRepository.getMetricSize();
        mostConcurrentlyHigh = new HashMap<>();
        mostConcurrentlyLow = new HashMap<>();
        for(int i = 0; i< size; i++){    //remove metrics that have high correlation with others
            for(int j = i+1; j< size; j++){
                double correlation =
                        dataRepository.getMetricColumnArrayFileView()[i].pearsons(dataRepository.getMetricColumnArrayFileView()[j]);
                if(Math.abs(correlation)>0.8){
                    metricToBeRemoved.add(i);

                }
            }
        }

        vectorHigh = new HashMap<>();
        for(int i=0; i<size; i++){
            if(metricToBeRemoved.contains(i)){
                continue;
            }
            vectorHigh.put(i,new HashSet<>());
            vectorIndex.add(i);
        }



    }

    /*public void populated(){
        int [][] vector = dataRepository.getVectorFileView();
        for(int i=0; i<vector.length; i++){
            for(int j =0; j<vector[i].length ; j++){
                if(metricToBeRemoved.contains(j)){
                    continue;
                }
                if(vector[i][j]==3){
                    vectorHigh.get(j).add(i);
                }
            }

        }
    }*/
    public void populated(Map<String, List<String>> commitsLeadToBFC){
        vectorConcurrentlyHigh= new LinkedHashMap<>();
        vectorConcurrentlyLow= new LinkedHashMap<>();
        int [][] vector = dataRepository.getVectorCommitView();
        for (var entry : commitsLeadToBFC.entrySet()) {
            String buggyCommitId= entry.getKey();
            Map<Integer, Set<Integer>> highVectorsPerPreBuggyCommit = new LinkedHashMap<>();
            Map<Integer, Set<Integer>> lowVectorsPerPreBuggyCommit = new LinkedHashMap<>();
            for(int i=0; i<size; i++){
                if(metricToBeRemoved.contains(i)){
                    continue;
                }
                highVectorsPerPreBuggyCommit.put(i, new LinkedHashSet<>());
                lowVectorsPerPreBuggyCommit.put(i, new LinkedHashSet<>());
            }
            //for (String commitsId: commitsLeadToBFC.get(buggyCommitId) ){

            int mostHigh= 0;
            int mostLow= 0;
            for (String preBuggyCommitsId: entry.getValue() ){

                Selection matchCommitId = dataRepository.getTableSortedByCommitTime().stringColumn("id").isEqualTo(preBuggyCommitsId);
                Table tempTable= dataRepository.getTableSortedByCommitTime().where(matchCommitId);
                for(Row row: tempTable){
                    int rowIndex = row.getInt("Index");
                    int highCount=0;
                    int lowCount=0;
                    for(int j=0; j<vector[rowIndex].length; j++){
                        if(metricToBeRemoved.contains(j)){
                            continue;
                        }
                        if(vector[rowIndex][j] == 3){
                            highVectorsPerPreBuggyCommit.get(j).add(rowIndex);
                            highCount++;
                        }else if(vector[rowIndex][j] == 0){
                            lowVectorsPerPreBuggyCommit.get(j).add(rowIndex);
                            lowCount++;
                        }
                    }
                    if(highCount > mostHigh){
                        mostHigh = highCount;
                    }
                    if(lowCount > mostLow){
                        mostLow = lowCount;
                    }
                }
            }
            mostConcurrentlyHigh.put(buggyCommitId, mostHigh);
            mostConcurrentlyLow.put(buggyCommitId, mostLow);

            vectorConcurrentlyHigh.put(buggyCommitId, highVectorsPerPreBuggyCommit);
            vectorConcurrentlyLow.put(buggyCommitId,lowVectorsPerPreBuggyCommit);
        }
    }

    public void vectorSimultaneously(){
        int counter = 0;
        for(var entry: vectorConcurrentlyHigh.entrySet()){
            /*if(counter < 20){
                counter++;
                continue;
            }*/

            vectorCombinations(entry.getValue(), mostConcurrentlyHigh.get(entry.getKey()));
            System.out.println("Buggy Commit Id: "+ entry.getKey());
            System.out.println("Maximum combination size(high): " + mostConcurrentlyHigh.get(entry.getKey()));
            break;
        }
    }

    private void vectorCombinations(Map<Integer, Set<Integer>> perBuggyCommitVector,int size){
        for(Set<Integer> set: Sets.combinations(vectorIndex, size)){
           // Set<Integer> tempSet = new HashSet<>();
            Integer[] indexes = new Integer[set.size()];
            set.toArray(indexes);
            Set<Integer> current = perBuggyCommitVector.get(indexes[0]);
            if(current.size() ==0){
                continue;
            }
            double currentSize = current.size()/1.0;
            double currentProb = 1;
            System.out.print("Index " + indexes[0]+ ": "+ perBuggyCommitVector.get(indexes[0]).size()+"(1, 1),");
            for(int k = 0; k< 8- indexes[0].toString().length()-String.valueOf(perBuggyCommitVector.get(indexes[0]).size()).length(); k++){
                System.out.print(" ");
            }

            for(int i=1; i< indexes.length; i++){

                current= Sets.intersection(current , perBuggyCommitVector.get(indexes[i]));
                currentProb = currentProb * current.size()/currentSize;
                System.out.print("Index " + indexes[i]+ ": " + current.size()+"(");
                System.out.printf("%1.4f ",current.size()/currentSize);
                System.out.printf("%1.4f",currentProb);
                System.out.print("),");
                //System.out.print("Index " + indexes[i]+ ": "+current.size()+"("+ +"),");
                int whitespace= 10- indexes[i].toString().length()
                        -String.valueOf(current.size()).length()
                        ;

                currentSize= current.size();
                for(int k=0; k< whitespace;k++){
                    System.out.print(" ");
                }
                if(current.size()==0){
                    break;
                }
            }

            System.out.println();

        }
    }

    /*public void vectorCombinations(int size){
        for(Set<Integer> set: Sets.combinations(vectorIndex, size)){
            // Set<Integer> tempSet = new HashSet<>();
            Integer[] indexes = new Integer[set.size()];
            //set.toArray(indexes);
            Set<Integer> current = vectorHigh.get(indexes[0]);
            double currentSize = current.size()/1.0;
            double currentProb = 1;
            System.out.print("Index " + indexes[0]+ ": "+ vectorHigh.get(indexes[0]).size()+"(1, 1),");
            for(int k = 0; k< 8- indexes[0].toString().length()-String.valueOf(vectorHigh.get(indexes[0]).size()).length(); k++){
                System.out.print(" ");
            }

            for(int i=1; i< indexes.length; i++){

                current= Sets.intersection(current , vectorHigh.get(indexes[i]));
                currentProb = currentProb * current.size()/currentSize;
                System.out.print("Index " + indexes[i]+ ": " + current.size()+"(");
                System.out.printf("%1.4f ",current.size()/currentSize);
                System.out.printf("%1.4f",currentProb);
                System.out.print("),");
                //System.out.print("Index " + indexes[i]+ ": "+current.size()+"("+ +"),");
                int whitespace= 10- indexes[i].toString().length()
                        -String.valueOf(current.size()).length()
                        ;

                currentSize= current.size();
                for(int k=0; k< whitespace;k++){
                    System.out.print(" ");
                }
                if(current.size()==0){
                    break;
                }
            }

            System.out.println();

        }
    }*/


    public static void main(String[] args) {

        TestReadingStrategy ts =new TestReadingStrategy();
        try {

            ts.parseData();

        } catch (Exception e) {
            System.out.println("error!");
            e.printStackTrace();
        }

        //VectorConcurrentlyUp vcp = new VectorConcurrentlyUp();
        CommitsLeadToBFC cltBFC =new CommitsLeadToBFC();
        VectorConcurrentlyUp vcp = new VectorConcurrentlyUp(cltBFC);
        vcp.populated(cltBFC.getCommitsLeadToBFCe3());
        vcp.vectorSimultaneously();
        /*for(String buggyCommitId: vcp.getVectorConcurrentlyHigh().keySet()){
            System.out.println("Buggy Commit Id: "+ buggyCommitId);
            System.out.println("Maximum combination size(high): " + vcp.getMostConcurrentlyHigh().get(buggyCommitId));
            for(Integer metric: vcp.getVectorConcurrentlyHigh().get(buggyCommitId).keySet()){
                System.out.println("Metric "+metric+ " is 3 in: ");
                for(Integer index: vcp.getVectorConcurrentlyHigh().get(buggyCommitId).get(metric)){
                    System.out.print(index+ " ");
                }
                System.out.println();
            }

            break;
        }*/
        /*for (var pair : vcp.vectorUp.entrySet()) {
            System.out.print("Feature index: "+ pair.getKey() +
                            ", Total up time: "+ pair.getValue().size());
            System.out.println();

        }
        System.out.println(vcp.metricToBeRemoved.size());*/
        //vcp.vectorCombinations(4);
        //System.out.println(vcp.metricToBeRemoved.size());
    }
}
