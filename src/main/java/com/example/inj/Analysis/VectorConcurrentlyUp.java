package com.example.inj.Analysis;

import com.example.inj.global.ProjectNameContainer;
import com.example.inj.model.storage.DataRepository;

import com.google.common.collect.Sets;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
    private Map<Integer, Set<Integer>> extremeValueMetric;
    private Map<Integer, Set<Integer>> extremeValueMetricBFC;

    private final int [][] vector;
    private Table combinationTable;
    private Map<String,Integer> mostConcurrentlyHigh;
    private Map<String,Integer> mostConcurrentlyLow;
    //private Map<String, Integer> vectorSize;
    private Set<Integer> vectorIndex;
    private DataRepository dataRepository;
    private ArrayList<Integer> metricToBeRemoved;
    private CommitsLeadToBFC cltBFC;
    private final Table tableSortedByCommitTime;
    private int size;
    double majorityThreshold = 0.4;

    public VectorConcurrentlyUp(CommitsLeadToBFC cltBFC){
        //Set<Integer> intersectionSet = Sets.intersection(vectorUp[1], vectorUp[2]);
        this.cltBFC = cltBFC;
        dataRepository = DataRepository.getInstance();;
        tableSortedByCommitTime = dataRepository.getTableSortedByCommitTime();
        metricToBeRemoved= new ArrayList<>();
        vectorIndex = new HashSet<>();
        size = dataRepository.getMetricSize(); //TODO
        mostConcurrentlyHigh = new HashMap<>();
        mostConcurrentlyLow = new HashMap<>();
        combinationTable = Table.create();
        vector = dataRepository.getVectorCommitView();
        //vectorSize = new HashMap<>();
        for(int i = 0; i< size; i++){    //remove metrics that have high correlation with others
            for(int j = i+1; j< size; j++){
                double correlation =
                        dataRepository.getMetricColumnArrayCommitView()[i].pearsons(dataRepository.getMetricColumnArrayCommitView()[j]);
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


    public void populatedExtremeValueMetricMap(){
        extremeValueMetric = new LinkedHashMap<>();
        extremeValueMetricBFC = new LinkedHashMap<>();
        int[][] vector = dataRepository.getVectorCommitView();
        for(Integer i:vectorIndex){
            Set<Integer> rowIndex = new LinkedHashSet<>();
            extremeValueMetric.put(i,rowIndex);
            rowIndex = new LinkedHashSet<>();
            extremeValueMetricBFC.put(i,rowIndex);
        }

        for(int i = 0; i<vector.length; i++ ){
            for(int j = 0; j<vector[i].length; j++){
                if(metricToBeRemoved.contains(j)){
                    continue;
                }
                boolean bfc = cltBFC.getCommitsLeadToBFC_indexRepresentation().contains(i);

                if(vector[i][j] == 0){
                    extremeValueMetric.get(-(j+1)).add(i);
                    if(bfc){
                        extremeValueMetricBFC.get(-(j+1)).add(i);
                    }
                }else if(vector[i][j] == 3){
                    extremeValueMetric.get(j+1).add(i);
                    if(bfc){
                        extremeValueMetricBFC.get(-(j+1)).add(i);
                    }
                }
            }
        }


    }

    public void populated(Map<String, List<String>> commitsLeadToBFC){

        vectorConcurrently= new LinkedHashMap<>();

        int[][] vector = dataRepository.getVectorCommitView();
        for (var entry : commitsLeadToBFC.entrySet()) {
            String buggyCommitId= entry.getKey();
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
                    for(int j=0; j<vector[rowIndex].length; j++){ //TODO
                        if(metricToBeRemoved.contains(j)){
                            continue;
                        }
                        if(vector[rowIndex][j] == 3){
                            vectorsPerPreBuggyCommit.get(j+1).add(rowIndex);
                            highCount++;
                        }else if(vector[rowIndex][j] == 0){
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


        }
    }

    private void vectorCombinations(Map<Integer, Set<Integer>> perBuggyCommitVector,int size) throws IOException {

        for(int i= 0; i<size*4; i=i+4 ){
            combinationTable.addColumns(
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
                    combinationTable.doubleColumn(i).append(rowData.get(i));    //column(i).append(rowData.get(i));
                }

            }

        }
        //System.out.println(resultTable.first(4));
        //System.out.println(resultTable.structure());
    }

    private void probabilityTransition( Map<Integer, Set<Integer>> perBuggyCommitVector, int size) throws IOException {
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


    private void sanityCheck( Integer... vectorIndexes){
        int voidnon = 7;
        int vectorLength = vectorIndexes.length;
        int[] quantile = new int[vectorLength];
        int[] realVectorIndexes = new int[vectorLength];

        for(int i = 0; i<vectorLength; i++){
            quantile[i] = (vectorIndexes[i] > 0) ? 3: 0;
            realVectorIndexes[i] = (vectorIndexes[i] > 0) ? vectorIndexes[i]-1 : -vectorIndexes[i]-1;
        }

        int overallBFCCounter = 0;
        int nonOverallBFCCounter = 0;
        int BFCSize = cltBFC.getCommitsPriorBFC().size();
        int [][] vector = dataRepository.getVectorCommitView();
        double majorityThreshold = 0.5;

        for(var entry: cltBFC.getCommitFileMap().entrySet()){
            int pBFCSize = entry.getValue().size();
            int BFCCounter= 0;
            int nonBFCCounter = 0;

            for(String pBFC: entry.getValue()){
                Selection matchCommitId = dataRepository.getTableSortedByCommitTime().stringColumn("id").isEqualTo(pBFC);
                Table pBFCTable= dataRepository.getTableSortedByCommitTime().where(matchCommitId);
                int rowSize=pBFCTable.rowCount();
                int[] rowCounter = new int[vectorLength];
                int[] nonRowCounter = new int[vectorLength];

                for(Row row: pBFCTable){
                    int rowIndex = row.getInt("Index");
                    for(int i=0 ; i<vectorLength; i++){
                        if(vector[rowIndex][realVectorIndexes[i]] == quantile[i]){
                            rowCounter[i]++;
                        }else{
                            nonRowCounter[i]++;
                        }
                    }

                }
//                if(rowCounter[0]+ oppositeRowCounter[0]+ otherRowCounter[0] +nonRowCounter[0]!= rowSize){
//                    System.out.println("BFC:                " + entry.getKey());
//                    System.out.println("pBFC      :         " + pBFC);
//                    System.out.println("rowCounter:         " + rowCounter[0]);
//                    System.out.println("nonIndexRowCounter: " + otherRowCounter[0]);
//                    System.out.println("oppositeRowCounter: " + oppositeRowCounter[0]);
//                    System.out.println("rowSize:            " + rowSize);
//                    System.out.println("____________________");
//                    System.exit(1234556);
//                }
                boolean all = true;
                for(int i=0 ; i<vectorLength; i++){
                    if(rowCounter[i]/1.0/rowSize < majorityThreshold){
                        all = false;
                        break;

                    }
                }

                if(all){
                    BFCCounter++;
                    //continue;
                }else{
                    nonBFCCounter++;
                }

                /*all = true;
                for(int i=0 ; i<vectorLength; i++){
                    if(oppositeRowCounter[i]/1.0/rowSize < majorityThreshold){
                        all = false;
                        break;

                    }
                }
                if(all){
                    oppositeBFCCounter++;
                    continue;
                }

                all = true;
                for(int i=0 ; i<vectorLength; i++){
                    if(otherRowCounter[i]/1.0/rowSize < majorityThreshold){
                        all = false;
                        break;

                    }
                }
                if(all){
                    otherBFCCounter++;
                    continue;
                }

                all = true;
                for(int i=0 ; i<vectorLength; i++){
                    if(nonRowCounter[i]/1.0/rowSize < majorityThreshold){
                        all = false;
                        break;

                    }
                }

                if(all){
                    nonBFCCounter++;
                    //continue;
                }*/


            }
//            if(BFCCounter + otherBFCCounter + oppositeBFCCounter + nonBFCCounter!= pBFCSize){
//                System.out.println("BFCCounter:         " + BFCCounter);
//                System.out.println("nonIndexBFCCounter: " + otherBFCCounter);
//                System.out.println("oppositeBFCCounter: " + oppositeBFCCounter);
//                System.out.println("nonBFCCounter     : " + nonBFCCounter);
//                System.out.println("pBFCSize:           " + pBFCSize);
//                System.exit(1234556);
//            }
            if(BFCCounter/1.0/pBFCSize >= majorityThreshold){
                overallBFCCounter++;
            }else if(nonBFCCounter/1.0/pBFCSize >= majorityThreshold)  {
                nonOverallBFCCounter++;
            }

        }

        //print
        if(overallBFCCounter>0){
            System.out.print("P( ");
            for(int i=0 ; i<vectorLength; i++){
                System.out.print(vectorIndexes[i]);
                if(i!= vectorLength-1)
                    System.out.print(", ");
            }
            System.out.print(" | BFC ) = ");
            System.out.print(overallBFCCounter + " / " + BFCSize + " = " + overallBFCCounter/1.0/BFCSize);
            System.out.println();

            /*System.out.print("P( -( ");
            for(int i=0 ; i<vectorLength; i++){
                System.out.print(vectorIndexes[i]);
                if(i!= vectorLength-1)
                    System.out.print(", ");
            }
            System.out.print(" ) | BFC ) = ");
            System.out.print(oppositeOverallBFCCounter + " / " + BFCSize + " = " + oppositeOverallBFCCounter/1.0/BFCSize);
            System.out.println();

            System.out.print("P( other( ±");
            for(int i=0 ; i<vectorLength; i++){
                System.out.print(vectorIndexes[i]>0?vectorIndexes[i]:-vectorIndexes[i]);
                if(i!= vectorLength-1)
                    System.out.print(", ");
            }
            System.out.print(" ) | BFC ) = ");
            System.out.print(otherOverallBFCCounter + " / " + BFCSize + " = " + otherOverallBFCCounter/1.0/BFCSize);
            System.out.println();
*/
            System.out.print("P( not( ");
            for(int i=0 ; i<vectorLength; i++){
                System.out.print(vectorIndexes[i]);
                if(i!= vectorLength-1)
                    System.out.print(", ");
            }
            System.out.print(" ) | BFC ) = ");
            System.out.print(nonOverallBFCCounter + " / " + BFCSize + " = " + nonOverallBFCCounter/1.0/BFCSize);
            System.out.println();
        }
        System.out.println("------------------");

    }

    // P( BFC | v=U ) =  P( v=U | BFC )* P(BFC) / P( v=U )
    private int[] conditionalProbabilityBFC2( Integer... vectorIndexes){

        int vectorLength = vectorIndexes.length;
        int[] quantile = new int[vectorLength];
        int[] realVectorIndexes = new int[vectorLength];
        for(int i = 0; i<vectorLength; i++){
            quantile[i] = (vectorIndexes[i] > 0) ? 3: 0;
            realVectorIndexes[i] = (vectorIndexes[i] > 0) ? vectorIndexes[i]-1 : -vectorIndexes[i]-1;
        }

        int overallBFCCounter = 0;
        int BFCSize = cltBFC.getCommitsPriorBFC().size();
        //int commitSize = cltBFC.getCommitsLeadToCommitS3().size()
        double majorityThreshold = 0.4;
        for(var entry: cltBFC.getBFCFileMap().entrySet()){
            int pBFCSize = entry.getValue().size();
            //int[] BFCCounter = new int[vectorLength];
            int BFCCounter= 0;

            for(String pBFC: entry.getValue()){
                Selection matchCommitId = dataRepository.getTableSortedByCommitTime().stringColumn("id").isEqualTo(pBFC);
                Table pBFCTable= dataRepository.getTableSortedByCommitTime().where(matchCommitId);
                //int rowCounter=0;
                int rowSize=pBFCTable.rowCount();

                int[] rowCounter = new int[vectorLength];

                for(Row row: pBFCTable){

                    int rowIndex = row.getInt("Index");
                    for(int i=0 ; i<vectorLength; i++){
                        if(vector[rowIndex][realVectorIndexes[i]] == quantile[i]){
                            rowCounter[i]++;
                        }
                    }

                }

                boolean all = true;
                for(int i=0 ; i<vectorLength; i++){
                    if(rowCounter[i]/1.0/rowSize < majorityThreshold){
                        all = false;
                        break;

                    }
                }

                if(all){
                    BFCCounter++;
                }
            }
//            if(BFCCounter + otherBFCCounter + oppositeBFCCounter + nonBFCCounter!= pBFCSize){
//                System.out.println("BFCCounter:         " + BFCCounter);
//                System.out.println("nonIndexBFCCounter: " + otherBFCCounter);
//                System.out.println("oppositeBFCCounter: " + oppositeBFCCounter);
//                System.out.println("nonBFCCounter     : " + nonBFCCounter);
//                System.out.println("pBFCSize:           " + pBFCSize);
//                System.exit(1234556);
//            }
            if(BFCCounter/1.0/pBFCSize >= majorityThreshold){
                overallBFCCounter++;
            }

        }

        //print
        /*if(overallBFCCounter>0) {
            System.out.print("P( ");
            for(int i=0 ; i<vectorLength; i++){
                System.out.print(vectorIndexes[i]);
                if(i!= vectorLength-1)
                    System.out.print(", ");
            }
            System.out.print(" | BFC ) = ");
            System.out.print(overallBFCCounter + " / " + BFCSize + " = " + overallBFCCounter/1.0/BFCSize);
            System.out.println();

        }*/

        return new int[]{overallBFCCounter, BFCSize};
    }

    // P( BFC | v=U ) =  P( v=U | BFC )* P(BFC) / P( v=U )
    private int[] given_BFC_Found_Pattern(ProbabilityStrategyEnum ps, Integer... vectorIndexes){

        int combinationSize = vectorIndexes.length;
        int[] quantile = new int[combinationSize];
        int[] realVectorIndexes = new int[combinationSize];
        for(int i = 0; i<combinationSize; i++){
            quantile[i] = (vectorIndexes[i] > 0) ? 3: 0;
            realVectorIndexes[i] = (vectorIndexes[i] > 0) ? vectorIndexes[i]-1 : -vectorIndexes[i]-1;
        }

        int extremeOccurrenceCount = 0;
        int notExtremeOccurrenceCount = 0;
        int BFCSize = cltBFC.getCommitsPriorBFC().size();


        for(var entry: cltBFC.getCommitsPriorBFC().entrySet()){

            int pBFCSize = entry.getValue().size();
            int[] rowCounter = new int[combinationSize];
            int[] rowOppositeCounter = new int[combinationSize];
            for(Integer integer: entry.getValue()){
                for(int i=0 ; i<combinationSize; i++){
                    if(vector[integer][realVectorIndexes[i]] == quantile[i]){
                        rowCounter[i]++;
                    }else{
                        rowOppositeCounter[i]++;
                    }
                }

            }
            boolean all = true;
            boolean oppositeAll = true;
            for(int i=0 ; i<combinationSize; i++){
                if(rowCounter[i]/1.0/pBFCSize < majorityThreshold){
                    all = false;
                }
                if(rowOppositeCounter[i]/1.0/pBFCSize< majorityThreshold){
                    oppositeAll= false;
                }
            }
            if(all){
                extremeOccurrenceCount++;
            }
            if(oppositeAll){
                notExtremeOccurrenceCount++;
            }


        }
        return new int[]{extremeOccurrenceCount, notExtremeOccurrenceCount, BFCSize};

    }

    private int[] given_pattern_found_BFC(ProbabilityStrategyEnum ps, Integer... vectorIndexes){

        int combinationSize = vectorIndexes.length;
        int[] quantile = new int[combinationSize];
        int[] realVectorIndexes = new int[combinationSize];
        for(int i = 0; i<combinationSize; i++){
            quantile[i] = (vectorIndexes[i] > 0) ? 3: 0;
            realVectorIndexes[i] = (vectorIndexes[i] > 0) ? vectorIndexes[i]-1 : -vectorIndexes[i]-1;
        }

        int extremeOccurrenceCount = 0;
        int extremeOccurrenceAndBFCCount=0;
        int extremeOccurrenceAndNotBFCCount=0;
        for(var entry: cltBFC.getCommitsPriorCommit().entrySet()){
            int pCommitSize = entry.getValue().size();
            int[] rowCounter = new int[combinationSize];

            for(Integer integer: entry.getValue()){
                for(int i=0 ; i<combinationSize; i++){
                    if(vector[integer][realVectorIndexes[i]] == quantile[i]){
                        rowCounter[i]++;
                    }
                }
            }
            boolean all = true;
            for(int i=0 ; i<combinationSize; i++){
                if(rowCounter[i]/1.0/pCommitSize < majorityThreshold){
                    all = false;
                    break;

                }
            }
            if(all){

                if(cltBFC.getBFCFileMap().containsKey(entry.getKey())){
                    extremeOccurrenceAndBFCCount++;
                }else{
                    extremeOccurrenceAndNotBFCCount++;
                }
                extremeOccurrenceCount++;
            }

        }

        return new int[]{extremeOccurrenceAndBFCCount,extremeOccurrenceAndNotBFCCount, extremeOccurrenceCount};
    }

    public void vectorCombinations(ProbabilityStrategyEnum ps, int size, String fileName){
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Sheet 1");
        org.apache.poi.ss.usermodel.Row row;
        org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
        int rowID = 1;

        //Map<String, Set<Integer>>
        switch (ps){
            case BFC:

                for(int i=0; i< size; i++){
                    int n = i+1;
                    header.createCell(i).setCellValue("Position "+ n);
                }
                header.createCell(header.getLastCellNum()).setCellValue("Pattern_Occurrence_Given_BFC");
                header.createCell(header.getLastCellNum()).setCellValue("not_Pattern_Occurrence_Given_BFC");
                header.createCell(header.getLastCellNum()).setCellValue("BFC_Occurrence");
                header.createCell(header.getLastCellNum()).setCellValue("Probability_Pattern_Occurrence_Given_BFC");
                break;
            case ALL:
                for(int i=0; i< size; i++){
                    int n = i+1;
                    header.createCell(i).setCellValue("Position_"+ n);
                }
                header.createCell(header.getLastCellNum()).setCellValue("BFC_Given_Pattern_Occurred");
                header.createCell(header.getLastCellNum()).setCellValue("not_BFC_Given_Pattern_Occurred");
                header.createCell(header.getLastCellNum()).setCellValue("Total_Pattern_Occurrence");
                header.createCell(header.getLastCellNum()).setCellValue("Probability_BFC_Given_Pattern_Occurred");
                break;


            case transition:
            case transition_ALL:
                populatedExtremeValueMetricMap();
                for(int i= 0; i<size*4; i=i+4 ){
                    header.createCell(i).setCellValue("Position_"+ (i/4+1));
                    header.createCell(i+1).setCellValue("Occurrence_"+ (i/4+1));
                    header.createCell(i+2).setCellValue("Probability_"+ (i/4+1));
                    header.createCell(i+3).setCellValue("Probability_Product"+ (i/4+1));
                }
                header.createCell(header.getLastCellNum()).setCellValue("BFC_Given_Pattern_Occurred");
                header.createCell(header.getLastCellNum()).setCellValue("not_BFC_Given_Pattern_Occurred");
                header.createCell(header.getLastCellNum()).setCellValue("Probability_BFC_Given_Pattern_Occurred");
                break;
            case transition_BFC:
                populatedExtremeValueMetricMap();
                for(int i= 0; i<size*4; i=i+4 ){
                    header.createCell(i).setCellValue("Position_"+ (i/4+1));
                    header.createCell(i+1).setCellValue("Occurrence_"+ (i/4+1));
                    header.createCell(i+2).setCellValue("Probability_"+ (i/4+1));
                    header.createCell(i+3).setCellValue("Probability_Product"+ (i/4+1));
                }
                header.createCell(header.getLastCellNum()).setCellValue("Pattern_Occurrence_Given_BFC");
                header.createCell(header.getLastCellNum()).setCellValue("not_Pattern_Occurrence_Given_BFC");
                header.createCell(header.getLastCellNum()).setCellValue("Probability_Pattern_Occurrence_Given_BFC");
            default:
                break;
        }

        for(Set<Integer> set: Sets.combinations(vectorIndex, size)){

            int cellID=0;
            int[] result;
            switch (ps){
                case sanityCheck:
                    sanityCheck(set.toArray(new Integer[0]));
                    break;
                case BFC:
                    result= given_BFC_Found_Pattern(ps,set.toArray(new Integer[0]));
                    if(result[0] == 0)
                        continue;

                    row = sheet.createRow(rowID++);
                    for(Integer i: set){
                        row.createCell(cellID++).setCellValue(i);
                    }
                    row.createCell(cellID++).setCellValue(result[0]);
                    row.createCell(cellID++).setCellValue(result[1]);
                    row.createCell(cellID++).setCellValue(result[2]);
                    row.createCell(cellID).setCellValue(result[0]/1.0/result[2]);
                    break;
                case ALL:
                    result= given_pattern_found_BFC(ps,set.toArray(new Integer[0]));
                    if(result[0] == 0)
                        continue;
                    row = sheet.createRow(rowID++);
                    for(Integer i: set){
                        row.createCell(cellID++).setCellValue(i);
                    }
                    row.createCell(cellID++).setCellValue(result[0]);
                    row.createCell(cellID++).setCellValue(result[1]);
                    row.createCell(cellID++).setCellValue(result[2]);
                    row.createCell(cellID).setCellValue(result[0]/1.0/result[2]);
                    break;
                case transition_ALL:
                    Integer[] indexes = new Integer[set.size()];
                    set.toArray(indexes);
                    Set<Integer> current = extremeValueMetric.get(indexes[0]);

                    row = sheet.createRow(rowID);


                    double currentSize = current.size()/1.0;
                    double overallProbability = currentSize/tableSortedByCommitTime.rowCount();
                    row.createCell(cellID++).setCellValue(indexes[0]);
                    row.createCell(cellID++).setCellValue(current.size());
                    row.createCell(cellID++).setCellValue(overallProbability);
                    row.createCell(cellID++).setCellValue(overallProbability);

                    Sets.SetView<Integer> intersection = null;
                    for(int i=1; i< indexes.length; i++){
                        intersection= Sets.intersection(current , extremeValueMetric.get(indexes[i]));
                        if(intersection.size() == 0){
                            break;
                        }
                        current= new HashSet<>();
                        intersection.copyInto(current);

                        overallProbability = overallProbability * current.size()/currentSize;

                        row.createCell(cellID++).setCellValue(indexes[i]);
                        row.createCell(cellID++).setCellValue(current.size());
                        row.createCell(cellID++).setCellValue(current.size()/currentSize);
                        row.createCell(cellID++).setCellValue(overallProbability);

                        currentSize= current.size();

                    }

                    if(row.getPhysicalNumberOfCells()/4 == size){
                        rowID++;
                        int count = 0;
                        for(Integer i : current){
                            if(cltBFC.getCommitsLeadToBFC_indexRepresentation().contains(i)){
                                count++;
                            }
                            //count += Collections.frequency(cltBFC.getCommitsLeadToBFC(), i);
                        }
                        row.createCell(cellID++).setCellValue(count);
                        row.createCell(cellID++).setCellValue(currentSize-count);
                        row.createCell(cellID).setCellValue(count/1.0/currentSize);
                    }

                    break;
                case transition_BFC:
                    indexes = new Integer[set.size()];
                    set.toArray(indexes);
                    current = extremeValueMetricBFC.get(indexes[0]);

                    row = sheet.createRow(rowID);


                    currentSize = current.size()/1.0;
                    overallProbability = currentSize/tableSortedByCommitTime.rowCount();
                    row.createCell(cellID++).setCellValue(indexes[0]);
                    row.createCell(cellID++).setCellValue(current.size());
                    row.createCell(cellID++).setCellValue(overallProbability);
                    row.createCell(cellID++).setCellValue(overallProbability);

                    intersection = null;
                    for(int i=1; i< indexes.length; i++){
                        intersection= Sets.intersection(current , extremeValueMetricBFC.get(indexes[i]));
                        if(intersection.size() == 0){
                            break;
                        }
                        current= new HashSet<>();
                        intersection.copyInto(current);

                        overallProbability = overallProbability * current.size()/currentSize;

                        row.createCell(cellID++).setCellValue(indexes[i]);
                        row.createCell(cellID++).setCellValue(current.size());
                        row.createCell(cellID++).setCellValue(current.size()/currentSize);
                        row.createCell(cellID++).setCellValue(overallProbability);

                        currentSize= current.size();

                    }

                    if(row.getPhysicalNumberOfCells()/4 == size){
                        rowID++;

                        row.createCell(cellID++).setCellValue(cltBFC.getCommitsPriorBFC().size());
                        row.createCell(cellID++).setCellValue(cltBFC.getCommitsPriorBFC().size());
                        row.createCell(cellID).setCellValue(currentSize/1.0/cltBFC.getCommitsPriorBFC().size());
                    }
                    break;
                default:
                    break;
            }

        }

        FileOutputStream fileOut = null;

        try {
            fileOut= new FileOutputStream("results\\"+fileName+"_"+ps+".xlsx" );
            wb.write(fileOut);
            fileOut.close();
            wb.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void vectorCombinationsSanityCheck(int size){
        int count = 0;
        /*try {
            FileWriter fw = new FileWriter(new File("./result.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        for(Set<Integer> set: Sets.combinations(vectorIndex, size)){

            sanityCheck(set.toArray(new Integer[0]));


            /*if(result > 0){
                System.out.println(result);
                for(Integer index : set){
                    System.out.print(index + " ");
                }
                System.out.println("\n--------");

            }*/

        }
    }

    public static void main(String[] args) {

    }
}
