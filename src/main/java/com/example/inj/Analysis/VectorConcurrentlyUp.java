package com.example.inj.Analysis;

import com.example.inj.ReadingStrategy.TestReadingStrategy;
import com.example.inj.global.ProjectNameContainer;
import com.example.inj.model.storage.DataRepository;

import com.google.common.collect.Sets;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Getter
public class VectorConcurrentlyUp {
    //private Map<String, Map<Integer, Set<Integer>>> vectorConcurrentlyHigh;
    //private Map<String, Map<Integer, Set<Integer>>> vectorConcurrentlyLow;
    private Map<String, Map<Integer, Set<Integer>>> vectorConcurrently;
    private Table resultTable;
    private Map<String,Integer> mostConcurrentlyHigh;
    private Map<String,Integer> mostConcurrentlyLow;
    //private Map<String, Integer> vectorSize;
    private Set<Integer> vectorIndex;
    private DataRepository dataRepository;
    private ArrayList<Integer> metricToBeRemoved;
    private CommitsLeadToBFC cltBFC;
    private int size;

    VectorConcurrentlyUp(CommitsLeadToBFC cltBFC){
        //Set<Integer> intersectionSet = Sets.intersection(vectorUp[1], vectorUp[2]);
        this.cltBFC = cltBFC;
        dataRepository = DataRepository.getInstance();;
        metricToBeRemoved= new ArrayList<>();
        vectorIndex = new HashSet<>();
        size = dataRepository.getMetricSize()-12; //TODO
        mostConcurrentlyHigh = new HashMap<>();
        mostConcurrentlyLow = new HashMap<>();
        resultTable = Table.create();
        //vectorSize = new HashMap<>();
        for(int i = 0; i< size; i++){    //remove metrics that have high correlation with others
            for(int j = i+1; j< size; j++){
                double correlation =
                        dataRepository.getMetricColumnArrayFileView()[i].pearsons(dataRepository.getMetricColumnArrayFileView()[j]);
                if(Math.abs(correlation)>0.8){
                    metricToBeRemoved.add(i);

                }
            }
        }

        for(int i=0; i<size; i++){
            if(metricToBeRemoved.contains(i)){
                continue;
            }
            vectorIndex.add(i+1);
            vectorIndex.add(-(i+1));
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
        //vectorConcurrentlyHigh= new LinkedHashMap<>();
        //vectorConcurrentlyLow= new LinkedHashMap<>();
        vectorConcurrently= new LinkedHashMap<>();
        int [][] vector = dataRepository.getVectorCommitView();
        for (var entry : commitsLeadToBFC.entrySet()) {
            String buggyCommitId= entry.getKey();
            //Map<Integer, Set<Integer>> highVectorsPerPreBuggyCommit = new LinkedHashMap<>();
            //Map<Integer, Set<Integer>> lowVectorsPerPreBuggyCommit = new LinkedHashMap<>();
            Map<Integer, Set<Integer>> vectorsPerPreBuggyCommit = new LinkedHashMap<>();
            for(int i=0; i<size; i++){
                if(metricToBeRemoved.contains(i)){
                    continue;
                }
                //highVectorsPerPreBuggyCommit.put(i, new LinkedHashSet<>());
                //lowVectorsPerPreBuggyCommit.put(i, new LinkedHashSet<>());
                vectorsPerPreBuggyCommit.put(i+1, new LinkedHashSet<>());
                vectorsPerPreBuggyCommit.put(-(i+1), new LinkedHashSet<>());
            }
            //for (String commitsId: commitsLeadToBFC.get(buggyCommitId) ){

            int mostHigh= 0;
            int mostLow= 0;
            int rowSize=0;
            for (String preBuggyCommitsId: entry.getValue() ){

                Selection matchCommitId = dataRepository.getTableSortedByCommitTime().stringColumn("id").isEqualTo(preBuggyCommitsId);
                Table tempTable= dataRepository.getTableSortedByCommitTime().where(matchCommitId);
                rowSize+=tempTable.rowCount();
                for(Row row: tempTable){
                    int rowIndex = row.getInt("Index");
                    int highCount=0;
                    int lowCount=0;
                    for(int j=0; j<vector[rowIndex].length-12; j++){ //TODO
                        if(metricToBeRemoved.contains(j)){
                            continue;
                        }
                        if(vector[rowIndex][j] == 3){
                            //highVectorsPerPreBuggyCommit.get(j).add(rowIndex);
                            vectorsPerPreBuggyCommit.get(j+1).add(rowIndex);
                            highCount++;
                        }else if(vector[rowIndex][j] == 0){
                            //lowVectorsPerPreBuggyCommit.get(j).add(rowIndex);
                            vectorsPerPreBuggyCommit.get(-(j+1)).add(rowIndex);
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
            //vectorSize.put(buggyCommitId,rowSize);
            mostConcurrentlyHigh.put(buggyCommitId, mostHigh);
            mostConcurrentlyLow.put(buggyCommitId, mostLow);
            Set<Integer> tempSet = new HashSet<>();
            tempSet.add(rowSize);
            vectorsPerPreBuggyCommit.put(0,tempSet);
            //vectorConcurrentlyHigh.put(buggyCommitId, highVectorsPerPreBuggyCommit);
            //vectorConcurrentlyLow.put(buggyCommitId,lowVectorsPerPreBuggyCommit);
            vectorConcurrently.put(buggyCommitId,vectorsPerPreBuggyCommit);

            String a =null;

        }
    }

    public void vectorSimultaneously() throws IOException {
        /*System.out.println(vectorIndex.size());
        System.exit(123);*/
        for(var entry: vectorConcurrently.entrySet()){
            /*if(counter < 20){
                counter++;
                continue;
            }*/

            vectorCombinations(entry.getValue(), 4);
            //mostConcurrentlyHigh.get(entry.getKey())
            System.out.println("Buggy Commit Id: "+ entry.getKey());
            System.out.println("Maximum combination size(high): " + 4);
            //mostConcurrentlyLow.get(entry.getKey())
            break;
        }


        double firstIndex = -2.0;
        double secondIndex = 3.0;
        double thirdIndex=0;
        double fourthIndex =0;
        Selection firstMatchMetric = resultTable.doubleColumn("Metric_0").isEqualTo(firstIndex) ;
        Selection secondMatchMetric = resultTable.doubleColumn("Metric_1").isEqualTo(secondIndex);
        Selection thirdMatchMetric = resultTable.doubleColumn("Metric_2").isEqualTo(thirdIndex);
        Selection fourthMatchMetric = resultTable.doubleColumn("Metric_3").isEqualTo(fourthIndex);

        Table result = resultTable.where(firstMatchMetric.
                        and(secondMatchMetric)
                 );

        System.out.println(result);
    }

    private void vectorCombinations(Map<Integer, Set<Integer>> perBuggyCommitVector,int size) throws IOException {

        for(int i= 0; i<size*4; i=i+4 ){
            resultTable.addColumns(
                    DoubleColumn.create("Metric_"+i/4),
                    DoubleColumn.create("Occurrence_"+ i/4),
                    DoubleColumn.create("IntermediateProbability_"+i/4),
                    DoubleColumn.create("Overall_Probability_"+ i/4));
        }

        for(Set<Integer> set: Sets.combinations(vectorIndex, size)){
            Integer[] indexes = new Integer[set.size()];
            set.toArray(indexes);
            Set<Integer> current = perBuggyCommitVector.get(indexes[0]);

            List<Double> rowData= new ArrayList<>();
            double currentSize = current.size()/1.0;
            double overallProbability = currentSize/perBuggyCommitVector.get(0).iterator().next();

            if(current.size()!=0){
                rowData.add(indexes[0]/1.0);
                rowData.add(currentSize/1.0);
                rowData.add(overallProbability);
                rowData.add(overallProbability);

            }


            for(int i=1; i< indexes.length; i++){
                Sets.SetView<Integer> temp= Sets.intersection(current , perBuggyCommitVector.get(indexes[i]));
                if(temp.size() == 0){
                    continue;
                }

                current= new HashSet<>();
                temp.copyInto(current);

                overallProbability = overallProbability * current.size()/currentSize;
                rowData.add(indexes[i]/1.0);
                rowData.add(currentSize/1.0);
                rowData.add(current.size()/currentSize);
                rowData.add(overallProbability);
                currentSize= current.size();


            }

            if(rowData.size()/4 == size){
                for(int i=0; i<rowData.size();i++){
                    resultTable.doubleColumn(i).append(rowData.get(i));    //column(i).append(rowData.get(i));
                }

            }


        }
        //System.out.println(resultTable.first(4));
        //System.out.println(resultTable.structure());
    }


    private void vectorCombinationsToExcel(Map<Integer, Set<Integer>> perBuggyCommitVector,int size) throws IOException {
        Workbook workbook = new SXSSFWorkbook(1000);;
        Sheet sheet = workbook.createSheet("Elisa");
        int rowIndex = 1;

        org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
        for(int i= 0; i<size*4; i=i+4 ){
            header.createCell(i).setCellValue("Metric_index_"+ i/4);
            header.createCell(1+i).setCellValue("Occurrence_"+ i/4);
            header.createCell(2+i).setCellValue("Probability_"+ i/4);
            header.createCell(3+i).setCellValue("Overall_Probability_"+ i/4);
        }

        for(Set<Integer> set: Sets.combinations(vectorIndex, size)){
            Integer[] indexes = new Integer[set.size()];
            set.toArray(indexes);
            Set<Integer> current = perBuggyCommitVector.get(indexes[0]);

            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIndex);
            int colIndex = 0;

            double currentSize = current.size()/1.0;
            double overallProbability = currentSize/perBuggyCommitVector.get(0).iterator().next();
            row.createCell(colIndex++).setCellValue(indexes[0]);
            row.createCell(colIndex++).setCellValue(current.size());
            row.createCell(colIndex++).setCellValue(overallProbability);
            row.createCell(colIndex++).setCellValue(overallProbability);


            for(int i=1; i< indexes.length; i++){
                Sets.SetView<Integer> temp= Sets.intersection(current , perBuggyCommitVector.get(indexes[i]));
                if(temp.size() == 0){
                    continue;
                }
                current= new HashSet<>();
                temp.copyInto(current);

                overallProbability = overallProbability * current.size()/currentSize;

                row.createCell(colIndex++).setCellValue(indexes[i]);
                row.createCell(colIndex++).setCellValue(current.size());
                row.createCell(colIndex++).setCellValue(current.size()/currentSize);
                row.createCell(colIndex++).setCellValue(overallProbability);

                currentSize= current.size();


            }

//            if(row.getPhysicalNumberOfCells()/4<3){
//                sheet.removeRow(row);
//
//            }else{
//                rowIndex++;
//            }

            rowIndex++;
            //System.out.println();

        }

        FileOutputStream fileOut = new FileOutputStream("results\\" + ProjectNameContainer.PROJECT_NAME + "_vector.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
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
;
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
        vcp.populated(cltBFC.getCommitsLeadToBFCs3());
        try {
            vcp.vectorSimultaneously();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
