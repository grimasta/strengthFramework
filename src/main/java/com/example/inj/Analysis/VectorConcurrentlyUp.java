package com.example.inj.Analysis;

import com.example.inj.ReadingStrategy.TestReadingStrategy;
import com.example.inj.model.storage.DataRepository;

import com.google.common.collect.Sets;

import java.util.*;

public class VectorConcurrentlyUp {
    private Map<Integer, Set<Integer>> vectorHigh;
    private HashSet<Integer> vectorIndex;
    private DataRepository dataRepository;
    private ArrayList<Integer> metricToBeRemoved;
    private Map<String, List<Integer>> vectorToBeStudied;
    private CommitsLeadToBFC cltBFC;
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

        int size = dataRepository.getMetricSize();
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

        for(String buggyCommitsId: cltBFC.getBFCFileMap().keySet()){
            List<Integer> indexList = new LinkedList<>();

        }
        //populated();
    }

    public void populated(){
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
    }
    public void populated(Map<String, List<String>> commitsLeadToBFC){
        for (var entry : commitsLeadToBFC.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
            System.out.println("Buggy Commit Id: " + entry.getKey());

        }
    }


    public void lookUp(){
        //System.out.println("There are " + );
    }

    public void vectorCombinations(int size){
        for(Set<Integer> set: Sets.combinations(vectorIndex, size)){
           // Set<Integer> tempSet = new HashSet<>();
            Integer[] indexes = new Integer[set.size()];
            set.toArray(indexes);
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
    }

    public static void main(String[] args) {

        TestReadingStrategy ts =new TestReadingStrategy();
        try {

            ts.parseData();

        } catch (Exception e) {
            System.out.println("error!");
            e.printStackTrace();
        }

        //VectorConcurrentlyUp vcp = new VectorConcurrentlyUp();


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
