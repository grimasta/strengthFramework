package com.example.inj.ReadingStrategy;

//import com.google.common.collect.Table;
import com.example.inj.Analysis.MetricEnum;
import com.example.inj.model.storage.DataRepository;
import lombok.*;
import tech.tablesaw.api.*;

import java.io.IOException;
import java.text.ParseException;

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
    public void parseData() throws IOException, ParseException {
        try {
            tableSortedByFileId = Table.read().csv("C:\\Users\\Rongji He\\Desktop\\data\\elisaFinalVersion2.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }



        for(int i=0; i<tableSortedByFileId.structure().rowCount(); i++){
            if(tableSortedByFileId.structure().stringColumn("Column Type").get(i).equals("INTEGER")){

                DoubleColumn column = tableSortedByFileId.intColumn(i).divide(1.0);
                String name = tableSortedByFileId.structure().getString(i, "Column Name");
                column.setName(name);
                tableSortedByFileId.replaceColumn(name,column);
            }
        }


        //System.out.println(tableSortedByFileId.columnNames());
        //System.exit(12);
        rowSize = tableSortedByFileId.column(0).size();
        vector = new int[rowSize][metricSize];
        dataRepository.setFusedVector(new int[rowSize]);
        tableSortedByCommitTime = tableSortedByFileId.copy().sortOn("committed_at");


        StringColumn uniqueId= tableSortedByCommitTime.stringColumn("id");
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
            /*if(metricName[i].name().equals("project_LOC")){     //The framework can't parse this int column as double column
                metricColumnArray[i] = tableSortedByFileId.intColumn("project_LOC").divide(1).copy();
                metricColumnArray[i].setName("project_LOC");
                continue;
            }*/
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


        //print pearson correlation
        /*for(int i=0; i<metricColumnArray.length; i++){
            System.out.print(metricColumnArray[i].name()+":");
            for(int k=0; k<35-metricColumnArray[i].name().length();k++){
                System.out.print(" ");
            }
            for(int j=0; j<metricColumnArray.length; j++){

                double correlation= metricColumnArray[i].pearsons(metricColumnArray[j]);
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
        }*/

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

        TestReadingStrategy ts = new TestReadingStrategy();
        try {

            ts.parseData();

        } catch (Exception e) {
            System.out.println("error!");
            e.printStackTrace();
        }
        //System.out.println(ts.getTableSortedByFileId().structure());
        ts.vectorization();
        //Table dataTable = DataRepository.getInstance().getTableSortedByFileId();
        //Plot.show(Histogram.create("Distribution of commit_additions", table, "commit_additions"));
        int[][] vec = DataRepository.getInstance().getVectorFileView();
        StringColumn fileId =DataRepository.getInstance().getTableSortedByFileId().stringColumn("file_id");
        BooleanColumn isBugFixing= DataRepository.getInstance().getTableSortedByFileId().booleanColumn("is_bug_fixing");
        StringColumn commitId =DataRepository.getInstance().getTableSortedByFileId().stringColumn("id");

        buggyVectorSumUp( DataRepository.getInstance());
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
