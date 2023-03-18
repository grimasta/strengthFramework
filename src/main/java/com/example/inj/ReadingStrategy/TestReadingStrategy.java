package com.example.inj.ReadingStrategy;

//import com.google.common.collect.Table;
import com.example.inj.Analysis.*;
import com.example.inj.model.storage.DataRepository;
import lombok.*;
import org.apache.commons.lang3.EnumUtils;
import tech.tablesaw.api.*;
import tech.tablesaw.selection.Selection;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter

@AllArgsConstructor
@ToString
public class TestReadingStrategy implements TestIReadingStrategy{

    //Tablesaw(Java's pandas) table instead of guava table,

    private DataRepository dataRepository;
    private Table tableSortedByFileId;      //table used to explore each file's history
    private Table tableSortedByCommitTime;  //table used to explore commit history
    private int[][] vector;

    final private int metricSize;
    private int rowSize;
    private double[] quantile;
    private DoubleColumn[] metricColumnArrayFileView;
    private DoubleColumn[] metricColumnArrayCommitView;
    public TestReadingStrategy() {
        this.dataRepository = DataRepository.getInstance();
        metricSize = dataRepository.getMetricSize();
        quantile = dataRepository.getQuantile();

    }

    @Override
    public void parseData(String fileName) throws IOException, ParseException {
        try {

            tableSortedByFileId = Table.read().csv(fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }



        for(int i=0; i<tableSortedByFileId.structure().rowCount(); i++){
            String columnName = tableSortedByFileId.structure().stringColumn("Column Name").get(i);
            String columnType = tableSortedByFileId.structure().stringColumn("Column Type").get(i);
            boolean contains = false;
            for(MetricEnum me : MetricEnum.values()){
                if(me.name().equals(columnName))
                    contains=true;
            }


            if(columnType.equals("STRING") && contains ){
                int size = tableSortedByFileId.column(i).size();
                tableSortedByFileId.stringColumn(i).setMissingTo("0");
                Double[] values = new Double[size];

                for(int j =0; j <size; j++){

                    try {
                        if(tableSortedByFileId.stringColumn(i).get(j).equals("inf") ||
                                tableSortedByFileId.stringColumn(i).get(j).equals("-inf")){
                            values[j] = 0.0;
                        }else{
                            values[j] = Double.parseDouble(tableSortedByFileId.stringColumn(i).get(j));
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        System.exit(111222);
                    }

                }

                DoubleColumn column =  DoubleColumn.create(columnName, values );
                tableSortedByFileId.replaceColumn(columnName,column);

            }else if(columnType.equals("INTEGER")){

                DoubleColumn column = tableSortedByFileId.intColumn(i).divide(1.0);
                //String name = tableSortedByFileId.structure().getString(i, "Column Name");
                column.setName(columnName);
                tableSortedByFileId.replaceColumn(columnName,column);
            }
        }

        /*if(fileName.contains(FileNameEnum.amarok.name())   //|| fileName.contains(FileNameEnum.kdelibs.name())
                ){
            StringColumn stringColumn = tableSortedByFileId.textColumn("id").asStringColumn();
            stringColumn.setName("id");
            tableSortedByFileId.replaceColumn("id", stringColumn);
        }*/



        /*System.out.println(tableSortedByFileId.structure());
        System.exit(123);*/
        //System.out.println(tableSortedByFileId.columnNames());
        //System.exit(12);
        rowSize = tableSortedByFileId.column(0).size();
        vector = new int[rowSize][metricSize];
        dataRepository.setFusedVector(new int[rowSize]);
        tableSortedByFileId.addColumns(IntColumn.indexColumn("Index", tableSortedByFileId.rowCount(), 0));

        fileMetricCategorization();
        tableSortedByCommitTime = tableSortedByFileId.copy().sortOn("committed_at");
        tableSortedByCommitTime.removeColumns("Index");
        tableSortedByCommitTime.addColumns(IntColumn.indexColumn("Index", tableSortedByCommitTime.rowCount(), 0));

        StringColumn uniqueId = StringColumn.create("uniqueId");
        StringColumn commitId= tableSortedByCommitTime.stringColumn("id");
        for(String id: commitId ){
            if(!uniqueId.contains(id)){
                uniqueId.append(id);
            }
        }


        commitMetricCategorization();
        tableSortedByFileId = null;
        tableSortedByFileId = tableSortedByCommitTime.copy().sortOn("file_id");


        dataRepository.setUniqueCommitId(uniqueId);
        int BFCCount=0;
        for(String id: uniqueId){
            BooleanColumn is_bug_fixing = tableSortedByCommitTime.where(
                    tableSortedByCommitTime.stringColumn("id").isEqualTo(id))
                    .booleanColumn("is_bug_fixing");

            if(is_bug_fixing.contains(true))
                BFCCount++;
        }

        dataRepository.setBFCRatio(BFCCount/1.0/uniqueId.size());

        /*System.out.println(tableSortedByCommitTime.structure());
        System.out.println(fileName+": "+ BFCCount/1.0/uniqueId.size());
        */

        dataRepository.setTableSortedByFileId(tableSortedByFileId);
        dataRepository.setTableSortedByCommitTime(tableSortedByCommitTime);

        metricColumnArrayFileView = new DoubleColumn[metricSize];
        metricColumnArrayCommitView = new DoubleColumn[metricSize];
        MetricEnum[] metricName = MetricEnum.values();
        for(int i = 0; i< metricSize; i++){
            metricColumnArrayFileView[i] = tableSortedByFileId.doubleColumn(metricName[i].name()).copy();
            metricColumnArrayCommitView[i] = tableSortedByCommitTime.doubleColumn(metricName[i].name()).copy();
        }

        for(int i=0; i<MetricEnum.values().length; i++){
            DoubleColumn dc = tableSortedByCommitTime.doubleColumn(MetricEnum.values()[i].name());
            for(int j=0; j <rowSize; j++){
                vector[j][i] = dc.get(j).intValue();
            }
        }

        dataRepository.setVectorCommitView(vector);

        dataRepository.setMetricColumnArrayFileView(metricColumnArrayFileView);
        dataRepository.setMetricColumnArrayCommitView(metricColumnArrayCommitView);

        //vectorization();

    }
    private void commitMetricCategorization(){
        for(MetricEnum colName: MetricEnum.values()){
            if(EnumUtils.isValidEnum(FileNameEnum.class, colName.name())){
                continue;
            }
            DoubleColumn dc = tableSortedByCommitTime.doubleColumn(colName.name());
            dc.setMissingTo(0.0);
            Double[] quantileArray = quantileCalculation(dc);
            for(int i=0; i<dc.size();i++){
                for(int j=0; j<quantileArray.length; j++){
                    if(dc.get(i) <= quantileArray[j]){
                        dc.set(i, j);
                        break;
                    }
                }
            }
        }
    }
    private void fileMetricCategorization(){

        for(String id: dataRepository.getUniqueCommitId()){
            Selection matchCommitId = tableSortedByFileId.stringColumn("id").isEqualTo(id);
            Table commitTable = tableSortedByFileId.where(matchCommitId);

            for(FileNameEnum colName: FileNameEnum.values() ) {
                //commitTable.sortOn(colName.name());
                int rowCount= commitTable.rowCount();
                if(rowCount<4) {
                    for(int i=0; i<commitTable.rowCount();i++){
                        tableSortedByFileId.doubleColumn(colName.name()).set(
                                commitTable.row(0).getInt("Index"),3.0);
                    }
                    continue;
                }
                DoubleColumn dc = commitTable.doubleColumn(colName.name()).copy();
                dc.setMissingTo(0.0);
                Double[] quantileArray = quantileCalculation(dc);
                for(int i=0; i<commitTable.rowCount();i++){
                    for(int j=0; j<quantileArray.length; j++){
                        int index = commitTable.row(i).getInt("Index");
                        if(commitTable.doubleColumn(colName.name()).get(i) <= quantileArray[j]){
                            tableSortedByFileId.doubleColumn(colName.name()).set(index,j);
                            break;
                        }
                    }

                }
            }

        }

    }


    private void vectorization(int one){

        //print correlation to a file
        /*Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet(" Sheet 1 ");
        org.apache.poi.ss.usermodel.Row row;
        int rowID = 1;
        org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("");
        for(int i=0; i<metricColumnArrayCommitView.length-12; i++){
            header.createCell(i+1).setCellValue(metricColumnArrayCommitView[i].name());
        }
        for(int i=0; i<metricColumnArrayCommitView.length-12; i++){
            int cellID=0;
            row = sheet.createRow(rowID++);
            row.createCell(cellID++).setCellValue(metricColumnArrayCommitView[i].name());
            System.out.print(metricColumnArrayCommitView[i].name()+":");
            for(int k=0; k<35-metricColumnArrayCommitView[i].name().length();k++){
                System.out.print(" ");
            }
            for(int j=0; j<metricColumnArrayCommitView.length-12; j++){

                double correlation= metricColumnArrayCommitView[i].pearsons(metricColumnArrayCommitView[j]);
                row.createCell(cellID++).setCellValue(correlation);
                if(Math.abs(correlation)>0.8){
                    System.out.print("+++++++  ");
                    continue;
                }
                if(correlation>0){
                    System.out.print("+");
                }
                System.out.printf("%1.4f  ",correlation);
            }
            System.out.println();
        }
        FileOutputStream fileOut = null;
        try {
            fileOut= new FileOutputStream("results\\corr.xlsx" );
            wb.write(fileOut);
            fileOut.close();
            wb.close();
        }
         catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(1123);*/


        //print the quantile
        /*for(Double[] da:quantileArray){
            for(Double d: da){
                System.out.print(String.format("%09.3f\t", d));
                //System.out.print(d);
                System.out.print("\t");
            }
            System.out.println();
        }
        System.exit(1);*/
        Double[][] quantileArray = quantileCalculation(metricColumnArrayFileView);
        for(int i = 0; i< metricColumnArrayFileView.length; i++){
            for(int j = 0; j< metricColumnArrayFileView[i].size(); j++){
                Double metric = metricColumnArrayFileView[i].get(j);
                for(int k=0; k< quantileArray.length; k++){

                    if(metric <= quantileArray[k][i]){
                        vector[j][i]= k;
                        break;
                    }
                }
            }
        }
        dataRepository.setVectorFileView(vector);


        vector = new int[rowSize][metricSize];
        quantileArray = quantileCalculation(metricColumnArrayCommitView);
        for(int i = 0; i< metricColumnArrayCommitView.length; i++){
            for(int j = 0; j< metricColumnArrayCommitView[i].size(); j++){
                Double metric = metricColumnArrayCommitView[i].get(j);
                for(int k=0; k< quantileArray.length; k++){

                    if(metric <= quantileArray[k][i]){
                        vector[j][i]= k;
                        break;
                    }
                }
            }
        }
        dataRepository.setVectorCommitView(vector);

    }

    public Double[] quantileCalculation (DoubleColumn metrics){

        DoubleColumn temp = metrics.copy();
        temp.sortAscending();
        int quantileSize= quantile.length;
        Double[] result = new Double[quantileSize];
        //System.out.print("index "+i+": ");
        for(int i=0; i< quantileSize; i++){
            result[i]= temp.get((int)((temp.size()-1) * quantile[i]));

        }
        return result;
    }

    public Double[][] quantileCalculation (DoubleColumn[] metrics){

        int quantileSize= quantile.length;
        Double[][] result = new Double[quantileSize][metrics.length];   //each row is the quantile
        for(int i=0; i<metrics.length; i++){
            DoubleColumn temp = metrics[i].copy();
            temp.sortAscending();
            //System.out.print("index "+i+": ");
            for(int j=0; j< quantileSize; j++){
                result[j][i]= temp.get((int)((temp.size()-1) * quantile[j]));
                //System.out.print(result[j][i]+ ", ");
            }

        }

        return result;
    }

    public static void main(String[] args) {



        TestReadingStrategy trs = new TestReadingStrategy();
        String directory = "C:\\Users\\Rongji He\\Desktop\\data\\";


        int[] maxSize ={
                3384, 2177, 1573, 1100, 3006, 2330, 3091,
                2376, 1848, 566, 3152, 4055, 908, 3065,
                1968, 9990, 2302, 1774, 13248, 6995, 21831,
                29778, 11055, 8494, 12810
        };
        try {

            for(int i=0; i < FileNameEnum.values().length; i++){
                String file = directory+FileNameEnum.values()[i].name()+"FinalVersion.csv";
                trs.parseData(file);

//                CommitsLeadToBFC cltBFC =new CommitsLeadToBFC(PBFC_StrategyEnum.all_BFC_File_showed_up);
//                VectorConcurrentlyUp vcp = new VectorConcurrentlyUp(cltBFC);
                System.out.print(FileNameEnum.values()[i].name()+" ");
                VectorConcurrentlyUp vcp = new VectorConcurrentlyUp(null);

//                String file2 = "C:\\Users\\Rongji He\\Desktop\\strengthFramework-masterB2\\strengthFramework\\results\\best\\res3.csv";
//                vcp.coverage(file2,FileNameEnum.values()[i].name(),maxSize[i]);
//                vcp.vectorCombinations(ProbabilityStrategyEnum.ALL,3,FileNameEnum.values()[i].name());
//                vcp.vectorCombinations(ProbabilityStrategyEnum.BFC,3,FileNameEnum.values()[i].name());
//                vcp.vectorCombinations(ProbabilityStrategyEnum.transition_ALL,2,FileNameEnum.values()[i].name());
//                System.exit(223344879);
            }


        } catch (Exception e) {
            System.out.println("error!");
            e.printStackTrace();
        }




    }

    static void buggyVectorSumUp(DataRepository dataRepository){
        int[][] vec = DataRepository.getInstance().getVectorFileView();
        //Table table= dataRepository.getTableSortedByFileId();
        //StringColumn uniqueId= table.stringColumn("file_id").unique();
        StringColumn fileId =DataRepository.getInstance().getTableSortedByFileId().stringColumn("file_id");
        BooleanColumn isBugFixing= DataRepository.getInstance().getTableSortedByFileId().booleanColumn("is_bug_fixing");
        StringColumn commitId =DataRepository.getInstance().getTableSortedByFileId().stringColumn("id");

        //for(String fileId: uniqueId ){}

        int count=0;
        String file=fileId.get(0);
        for(int i =3; i<isBugFixing.size(); i++){
            if(isBugFixing.get(i)
                    &&fileId.get(i).equals(fileId.get(i - 1))
                    &&fileId.get(i).equals(fileId.get(i - 2))
                    &&fileId.get(i).equals(fileId.get(i - 3))
                    &&isBugFixing.get(i)!=isBugFixing.get(i-1)
                    &&isBugFixing.get(i)!=isBugFixing.get(i-2)
                    &&isBugFixing.get(i)!=isBugFixing.get(i-3)
            ){

                count++;
                if(!file.equals(fileId.get(i))){
                    file = fileId.get(i);
                    System.out.println("--------------------------------------------------------------------------------------");
                }
                System.out.print("Sum-up 3: \t\t\t\t\t\t\t\t");
                for(int j= 0; j< vec[j].length; j++){
                    if(vec[i][j] +vec[i-1][j]+vec[i-2][j] >=6){
                        System.out.print("+");
                    }else{
                        System.out.print("-");
                    }
                    System.out.print(" ");
                }
                System.out.println();


                System.out.print(file +": \t");
                for(int j=0; j<vec[i].length; j++){

                    System.out.print(vec[i][j]);
                    System.out.print(" ");
                }
                System.out.println();
            }


        }

        System.out.println("Total Buggy vector: "+ count);
    }
}
