package com.example.inj.Analysis;

import com.example.inj.ReadingStrategy.TestReadingStrategy;
import com.example.inj.Tree.Tree;
import com.example.inj.Tree.TreeNode;
import com.example.inj.Tree.TreeTraversalOrderEnum;
import com.example.inj.model.storage.DataRepository;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import tech.tablesaw.api.DoubleColumn;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class VectorConcurrentlyUp {
    //private HashSet<?>[] vectorUp;
    private Map<Integer, Set<Integer>> vectorUp;
    private HashSet<Integer> vectorIndex;
    //private Tree[] vectorConcurrentlyUpTree;
    private DataRepository dataRepository;
    private ArrayList<DoubleColumn> metricColumnList;
    private ArrayList<String> metricColumnNameList;
    private DoubleColumn[] metricColumnArray;
    private ArrayList<Integer> metricToBeRemoved;
    private Map<Integer, Integer> shiftedIndexMap;
    private int length;


    VectorConcurrentlyUp(){
        //Set<Integer> intersectionSet = Sets.intersection(vectorUp[1], vectorUp[2]);

        dataRepository = DataRepository.getInstance();
        metricColumnList = new ArrayList<>();
        metricToBeRemoved= new ArrayList<>();
        shiftedIndexMap =new HashMap<>();
        vectorIndex = new HashSet<>();

        int size = dataRepository.getMetricSize();
        for(int i = 0; i< size; i++){    //remove metrics that have high correlation with others
            for(int j = i+1; j< size; j++){
                double correlation =
                        dataRepository.getMetricColumnArray()[i].pearsons(dataRepository.getMetricColumnArray()[j]);
                if(Math.abs(correlation)>0.8){
                    metricToBeRemoved.add(i);

                }
            }
        }

        vectorUp = new HashMap<>();
        for(int i=0; i<size; i++){
            if(metricToBeRemoved.contains(i)){
                continue;
            }
            vectorUp.put(i,new HashSet<>());
            vectorIndex.add(i);
        }

        populated();
    }

    public void populated(){
        int [][] vector = dataRepository.getVector();
        for(int i=0; i<vector.length; i++){
            for(int j =0; j<vector[i].length ; j++){
                if(metricToBeRemoved.contains(j)){
                    continue;
                }
                if(vector[i][j]==3){
                    vectorUp.get(j).add(i);
                }
            }

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
            Set<Integer> current = vectorUp.get(indexes[0]);
            double currentSize = current.size()/1.0;
            double currentProb = 1;
            System.out.print("Index " + indexes[0]+ ": "+ vectorUp.get(indexes[0]).size()+"(1, 1),");
            for(int k=0; k< 8- indexes[0].toString().length()-String.valueOf(vectorUp.get(indexes[0]).size()).length();k++){
                System.out.print(" ");
            }

            for(int i=1; i< indexes.length; i++){

                current= Sets.intersection(current , vectorUp.get(indexes[i]));
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
    /* VectorConcurrentlyUp(){

         dataRepository = DataRepository.getInstance();
         metricColumnList = new ArrayList<>();
         metricColumnNameList= new ArrayList<>();
         shiftedIndexMap =new HashMap<>();

         for(int i=0; i<dataRepository.getMetricColumnArray().length; i++){
             DoubleColumn temp =dataRepository.getMetricColumnArray()[i].copy();
             metricColumnList.add(temp.setName(temp.name()+ " "+i));
         }
         for(DoubleColumn dc: dataRepository.getMetricColumnArray()){      //Must be a deep copy to prevent modification of original array.
             DoubleColumn temp =dc.copy();
             //System.out.println(dataRepository.getTableSortedByFileId().columnIndex(temp.name()));

             metricColumnList.add(temp.setName(temp.name()+ " "+dataRepository.getTableSortedByFileId().columnIndex(temp.name())));

         }



         //metricColumnArray.forEach(s->System.out.println(s.name()));

         metricToBeRemoved= new ArrayList<>();

         for(int i = 0; i< metricColumnList.size(); i++){    //remove metrics that have high correlation with others
             for(int j = i+1; j< metricColumnList.size(); j++){
                 double correlation= metricColumnList.get(i).pearsons(metricColumnList.get(j));
                 if(Math.abs(correlation)>0.8){
                     metricToBeRemoved.add(i);
                 }
             }
         }


         Collections.reverse(metricToBeRemoved);
         for(Integer index : metricToBeRemoved){
             metricColumnList.remove((int)index);
         }
         for(int i=0; i<metricColumnList.size(); i++){
             shiftedIndexMap.put(
                     Integer.parseInt(metricColumnList.get(i).name().split(" ")[1]),
                     i);
         }


         for(DoubleColumn dc: metricColumnList){
             metricColumnNameList.add(dc.name());
         }
         length= metricColumnList.size();
         vectorConcurrentlyUpTree =new Tree[length];
         metricColumnArray = metricColumnList.toArray(new DoubleColumn[0]);
         //metricToRemoved.forEach(System.out::println);
     }


     /*public void vectorConcurrentlyUpTreesPopulated(){
         int[][] vector = dataRepository.getVector();

         for(int i=0; i<vector.length; i++){
             List<Integer> upList = new LinkedList<>();
             for(int j =0; j<vector[i].length && !metricToBeRemoved.contains(j); j++){
                 if( vector[i][j]==3 ){
                     upList.add(j);
                 }
             }
             for(int j=0; j< upList.size(); j++){
                 for(int k=j; k< upList.size(); k++){
                     int desireIndex = upList.get(j);
                     int shiftedIndex= shiftedIndexMap.get(desireIndex);
                     vectorConcurrentlyUpTree[shiftedIndex].find(shiftedIndex).incrementNumByOne();
                 }
             }
         }

     }

     public void vectorConcurrentlyUpTreesConstruction() {

         for (int i = 0; i < length; i++) {
             System.out.println(i + " ");
             vectorConcurrentlyUpTree[i] = new Tree();
             vectorConcurrentlyUpTree[i].setRoot(new TreeNode(i));

             HashMap<Integer, String> excludedIndex = new HashMap<>();
             excludedIndex.put(i, "");

             Gson gson = new Gson();
             String jsonString = gson.toJson(excludedIndex);
             Type type = new TypeToken<HashMap<Integer, String>>() {}.getType();
             HashMap<Integer, String> clonedMap = gson.fromJson(jsonString, type);

             vectorConcurrentlyUpTreesConstructionRecursively(vectorConcurrentlyUpTree[i].getRoot(), clonedMap);
             System.out.println(i + "done ");
         }
     }

     private void vectorConcurrentlyUpTreesConstructionRecursively(TreeNode root, HashMap<Integer, String> excludedIndex){

         for(int i=0; i<length; i++){
             if(excludedIndex.containsKey(i)){
                 continue;
             }
             //System.out.println(i + " ");

             TreeNode tn= new TreeNode(i);
             root.addChild(tn);
             excludedIndex.put(i, "");
             //next 4 lines makes a deep copy of the excludedIndex map, so that each recursion
             //functions frame works on independent copy
             Gson gson = new Gson();
             String jsonString = gson.toJson(excludedIndex);
             Type type = new TypeToken<HashMap<Integer, String>>(){}.getType();
             HashMap<Integer, String> clonedMap = gson.fromJson(jsonString, type);

             vectorConcurrentlyUpTreesConstructionRecursively(tn, clonedMap);
         }
         excludedIndex =null;
         System.out.println("--");
         try {
             Thread.sleep(1000);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
     }

     private static void vectorConcurrentlyUpTreesConstructionRecursivelyTest(TreeNode root, HashMap<Integer, String> excludedIndex){

         for(int i=0; i<5; i++){
             if(excludedIndex.containsKey(i)){
                continue;
             }
             TreeNode tn= new TreeNode(i);
             root.addChild(tn);
             excludedIndex.put(i, "");
             //next 4 lines makes a deep copy of the excludedIndex map, so that each recursion
             //functions frame works on independent copy
             Gson gson = new Gson();
             String jsonString = gson.toJson(excludedIndex);
             Type type = new TypeToken<HashMap<Integer, String>>(){}.getType();
             HashMap<Integer, String> clonedMap = gson.fromJson(jsonString, type);

             vectorConcurrentlyUpTreesConstructionRecursivelyTest(tn, clonedMap);
         }

     }
 */
    public static void main(String[] args) {
        //Test Tree validity
        /*Tree[] treeArray = new Tree[5];
        for(int i=0; i<5; i++){
            treeArray[i]= new Tree();
            treeArray[i].setRoot(new TreeNode(i));
            HashMap<Integer, String> excludedIndex = new HashMap<>();
            excludedIndex.put(i, "");

            Gson gson = new Gson();
            String jsonString = gson.toJson(excludedIndex);
            Type type = new TypeToken<HashMap<Integer, String>>(){}.getType();
            HashMap<Integer, String> clonedMap = gson.fromJson(jsonString, type);

            vectorConcurrentlyUpTreesConstructionRecursivelyTest(treeArray[i].getRoot(), clonedMap);
        }

        for(TreeNode gtn: treeArray[0].build(TreeTraversalOrderEnum.PRE_ORDER)) {
            System.out.println(gtn.getData());
        }*/


        TestReadingStrategy ts =new TestReadingStrategy();
        try {

            ts.parseData();

        } catch (Exception e) {
            System.out.println("error!");
            e.printStackTrace();
        }

        //ts.vectorization();
        VectorConcurrentlyUp vcp = new VectorConcurrentlyUp();


        /*for (var pair : vcp.vectorUp.entrySet()) {
            System.out.print("Feature index: "+ pair.getKey() +
                            ", Total up time: "+ pair.getValue().size());
            System.out.println();

        }
        System.out.println(vcp.metricToBeRemoved.size());*/
        vcp.vectorCombinations(4);
        //System.out.println(vcp.metricToBeRemoved.size());
    }
}
