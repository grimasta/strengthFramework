package com.example.inj.ReadingStrategy;

//import com.google.common.collect.Table;
import com.example.inj.Analysis.*;
import com.example.inj.global.ProjectNameContainer;
import com.example.inj.model.storage.DataRepository;
import lombok.*;
import org.apache.commons.lang3.EnumUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tech.tablesaw.api.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

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

            }

            if(columnType.equals("INTEGER")){

                DoubleColumn column = tableSortedByFileId.intColumn(i).divide(1.0);
                //String name = tableSortedByFileId.structure().getString(i, "Column Name");
                column.setName(columnName);
                tableSortedByFileId.replaceColumn(columnName,column);
            }
        }

        /*System.out.println(tableSortedByFileId.structure());
        System.exit(123);*/
        //System.out.println(tableSortedByFileId.columnNames());
        //System.exit(12);
        rowSize = tableSortedByFileId.column(0).size();
        vector = new int[rowSize][metricSize];
        dataRepository.setFusedVector(new int[rowSize]);
        tableSortedByCommitTime = tableSortedByFileId.copy().sortOn("committed_at");

        StringColumn uniqueId = StringColumn.create("uniqueId");
        StringColumn commitId= tableSortedByCommitTime.stringColumn("id");
        for(String id: commitId ){
            if(!uniqueId.contains(id)){
                uniqueId.append(id);
            }
        }

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
        */
        tableSortedByFileId.addColumns(IntColumn.indexColumn("Index", tableSortedByFileId.rowCount(), 0));
        tableSortedByCommitTime.addColumns(IntColumn.indexColumn("Index", tableSortedByCommitTime.rowCount(), 0));

        dataRepository.setTableSortedByFileId(tableSortedByFileId);
        dataRepository.setTableSortedByCommitTime(tableSortedByCommitTime);

        metricColumnArrayFileView = new DoubleColumn[metricSize];
        metricColumnArrayCommitView = new DoubleColumn[metricSize];
        MetricEnum[] metricName = MetricEnum.values();
        for(int i = 0; i< metricSize; i++){
            metricColumnArrayFileView[i] = tableSortedByFileId.doubleColumn(metricName[i].name()).copy();
            metricColumnArrayCommitView[i] = tableSortedByCommitTime.doubleColumn(metricName[i].name()).copy();
        }

        for (DoubleColumn dc : metricColumnArrayFileView) {
            dc.setMissingTo(0.0);
        }

        for (DoubleColumn dc : metricColumnArrayCommitView) {
            dc.setMissingTo(0.0);
        }
        dataRepository.setMetricColumnArrayFileView(metricColumnArrayFileView);
        dataRepository.setMetricColumnArrayCommitView(metricColumnArrayCommitView);

        vectorization();
    }
    private void vectorization(){

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
        Double[][] quantileArray = quantileCalculation(metricColumnArrayFileView);

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



    public Double[][] quantileCalculation (DoubleColumn[] metrics){
        /*DoubleColumn[] local = new DoubleColumn[metrics.length]; //= Arrays.copyOf(metrics, metrics.length);
        for(int i=0; i<metrics.length;i++ ){
            local[i]=  metrics[i].copy();
        }*/
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
            /*result[0][i]= temp.get((int)(temp.size()*0.25+1));
            result[1][i]= temp.get((int)(temp.size()*0.5+1));
            result[2][i]= temp.get((int)(temp.size()*0.75+1));*/
            //System.out.println();
        }

        return result;
    }

    public static void main(String[] args) {

        TestReadingStrategy trs = new TestReadingStrategy();
        //"C:\\Users\\Rongji He\\Desktop\\data\\elisaFinalVersion2.csv"
        String directory = "C:\\Users\\Rongji He\\Desktop\\data\\";

        try {

            for(int i=0; i < FileNameEnum.values().length; i++){
                String file = directory+FileNameEnum.values()[i].name()+"FinalVersion.csv";
                trs.parseData(file);
                CommitsLeadToBFC cltBFC =new CommitsLeadToBFC();
                VectorConcurrentlyUp vcp = new VectorConcurrentlyUp(cltBFC);
                vcp.vectorCombinations(ProbabilityStrategyEnum.ALL,3,FileNameEnum.values()[i].name());
                vcp.vectorCombinations(ProbabilityStrategyEnum.transition,3,FileNameEnum.values()[i].name());
                vcp.vectorCombinations(ProbabilityStrategyEnum.BFC,3,FileNameEnum.values()[i].name());
            }


        } catch (Exception e) {
            System.out.println("error!");
            e.printStackTrace();
        }
        //System.out.println(ts.getTableSortedByFileId().structure());
        //trs.vectorization();
        //Table dataTable = DataRepository.getInstance().getTableSortedByFileId();
        //Plot.show(Histogram.create("Distribution of commit_additions", table, "commit_additions"));
        //int[][] vec = DataRepository.getInstance().getVectorFileView();
        //StringColumn fileId =DataRepository.getInstance().getTableSortedByFileId().stringColumn("file_id");
        //BooleanColumn isBugFixing= DataRepository.getInstance().getTableSortedByFileId().booleanColumn("is_bug_fixing");
        //StringColumn commitId =DataRepository.getInstance().getTableSortedByFileId().stringColumn("id");

        //buggyVectorSumUp( DataRepository.getInstance());
        //System.out.println(vec.length);
        /*for(int i =0; i<vec.length; i++){
            System.out.print(commitId.get(i)+", \t");
            System.out.print(fileId.get(i)+ ": ");
            System.out.print(isBugFixing.get(i)+", \t");
            for(int j=0 ; j< vec[i].length; j++){
                System.out.print(vec[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }*/



        //System.out.println(vec.length);


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
