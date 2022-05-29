package com.example.inj.ReadingStrategy;

//import com.google.common.collect.Table;
import com.example.inj.model.storage.DataRepository;
import lombok.*;
import tech.tablesaw.api.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
@Getter
@Setter

@AllArgsConstructor
@ToString
public class TestReadingStrategy implements TestIReadingStrategy{

    //Tablesaw(Java's pandas) table instead of guava table,

    private DataRepository dataRepository;
    private Table tableSortedByFileId;      //table used to explore each file's history
    private Table tableSortedByCommitTime;  //table used to explore commit history

    //dimensions of the vector
    //final private int pastCommitSize= 5;
    final private int colSize = 30;              //FIXME: set desire metric size
    private int[][] vector;
    private double[] quantile = {0.15, 0.85, 1.00};   //FIXME: set desire quantile(ascending)
    //private Map<String, Integer>segmentMap;          //FIXME: always put 1 at the end
    //private List<String> watchList ;
    /*    //Map<FileId, PerCommitDetail>
    private Map<String, List<Object>> fileDetails;
    //Map<CommitId,FileIdParticipate>
    private Map<String, List<Object>> commitDetails;
    //private DataRepository dataRepository;
    //private LinkedHashMap<> commitOrder;
    //private LinkedList<String> file*/

    public TestReadingStrategy() {
        this.dataRepository = DataRepository.getInstance();


    }

    @Override
    public void parseData() throws IOException, ParseException {
        try {
            tableSortedByFileId = Table.read().csv("C:\\Users\\Rongji He\\Desktop\\data\\elisaFinalVersion.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int rowSize = tableSortedByFileId.column(0).size();
        vector = new int[rowSize][colSize];
        dataRepository.setFusedVector(new int[rowSize]);
        tableSortedByCommitTime = tableSortedByFileId.sortOn("committed_at");

        tableSortedByFileId.addColumns(IntColumn.indexColumn("Index", tableSortedByFileId.rowCount(), 0));
        tableSortedByCommitTime.addColumns(IntColumn.indexColumn("Index", tableSortedByCommitTime.rowCount(), 0));

        dataRepository.setTableSortedByFileId(tableSortedByFileId);
        dataRepository.setTableSortedByCommitTime(tableSortedByCommitTime);
        //watchList = new LinkedList<>();
        /*
        fileDetails= new LinkedHashMap<>();
        commitDetails= new LinkedHashMap<>();
        //columns that are unimportant/unrelated

        dataFrame.removeColumns("authored_at","commit_additions","commit_deletions",
                "changed_files","is_bug_linked","is_fix_related",
                "is_merge_commit","is_refactoring","file_path",
                "previous_file_path","file_additions","file_deletions",
                "fractal_value","fractal_value_over_lines","distinct_authors_to_now"
        );



        //populate data structure
        for (int i = 0; i < dataFrame.rowCount(); i++) {
            String commitId = dataFrame.stringColumn("id").get(i);
            String file_id = dataFrame.stringColumn("file_id").get(i);
            boolean is_bug_fixing= dataFrame.booleanColumn("is_bug_fixing").get(i);

            double project_LOC_change_ROC =dataFrame.doubleColumn("project_LOC_change_ROC").get(i);
            double project_LOC_change_percentage =dataFrame.doubleColumn("project_LOC_change_percentage").get(i);

            double file_LOC_change_ROC =dataFrame.doubleColumn("file_LOC_change_ROC").get(i);
            double file_LOC_change_percentage =dataFrame.doubleColumn("file_LOC_change_percentage").get(i);
            double file_proj_LOC_rito = dataFrame.doubleColumn("file_proj_LOC_rito").get(i);
            double file_proj_LOC_change_rito= dataFrame.doubleColumn("file_proj_LOC_change_rito").get(i);

            List<Object> commitData= new ArrayList<>();
            commitData.add(is_bug_fixing);         //index 0
            commitData.add(project_LOC_change_ROC);//index 1
            commitData.add(project_LOC_change_percentage);//index 2
            commitDetails.put(commitId, commitData);//index 3

            List<Object> fileData= new ArrayList<>();
            fileData.add(is_bug_fixing);
            fileData.add(file_LOC_change_ROC);
            fileData.add(file_LOC_change_percentage);
            fileData.add(file_proj_LOC_rito);
            fileData.add(file_proj_LOC_change_rito);
            fileDetails.put(file_id,fileData);

            //LocalDate d = dataFrame.dateColumn("d").get(i);
            //System.out.println(s + " happened on " + d);
        }
 */
        //monitoringCommit();
        //vectorization();
    }
    public void vectorization(){

        DoubleColumn commit_additions               = tableSortedByFileId.doubleColumn("commit_additions");
        DoubleColumn commit_deletions               = tableSortedByFileId.doubleColumn("commit_deletions");
        DoubleColumn changed_files                  = tableSortedByFileId.doubleColumn("changed_files");
        DoubleColumn file_additions                 = tableSortedByFileId.doubleColumn("file_additions");
        DoubleColumn file_deletions                 = tableSortedByFileId.doubleColumn("file_deletions");
        DoubleColumn fractal_value                  = tableSortedByFileId.doubleColumn("fractal_value");
        DoubleColumn fractal_value_over_lines       = tableSortedByFileId.doubleColumn("fractal_value_over_lines");
        DoubleColumn distinct_authors_to_now        = tableSortedByFileId.doubleColumn("distinct_authors_to_now");
        DoubleColumn project_LOC                    = tableSortedByFileId.intColumn("project_LOC").divide(1);
        DoubleColumn project_LOC_change             = tableSortedByFileId.doubleColumn("project_LOC_change");
        DoubleColumn file_LOC                       = tableSortedByFileId.doubleColumn("file_LOC");
        DoubleColumn file_LOC_change                = tableSortedByFileId.doubleColumn("file_LOC_change");
        DoubleColumn project_LOC_change_ROC         = tableSortedByFileId.doubleColumn("project_LOC_change_ROC");
        DoubleColumn project_LOC_change_percentage  = tableSortedByFileId.doubleColumn("project_LOC_change_percentage");
        DoubleColumn file_LOC_change_ROC            = tableSortedByFileId.doubleColumn("file_LOC_change_ROC");
        DoubleColumn file_LOC_change_percentage     = tableSortedByFileId.doubleColumn("file_LOC_change_percentage");
        DoubleColumn file_proj_LOC_ratio            = tableSortedByFileId.doubleColumn("file_proj_LOC_ratio");
        DoubleColumn file_proj_LOC_change_ratio     = tableSortedByFileId.doubleColumn("file_proj_LOC_change_ratio");
        DoubleColumn Total_Accesses                 = tableSortedByFileId.doubleColumn("Total_Accesses");
        DoubleColumn Added_Accesses                 = tableSortedByFileId.doubleColumn("Added_Accesses");
        DoubleColumn Deleted_Accesses               = tableSortedByFileId.doubleColumn("Deleted_Accesses");
        DoubleColumn Total_Calls                    = tableSortedByFileId.doubleColumn("Total_Calls");
        DoubleColumn Added_Calls                    = tableSortedByFileId.doubleColumn("Added_Calls");
        DoubleColumn Deleted_Calls                  = tableSortedByFileId.doubleColumn("Deleted_Calls");
        DoubleColumn Current_Status                 = tableSortedByFileId.doubleColumn("Current_Status");
        DoubleColumn Change_Added                   = tableSortedByFileId.doubleColumn("Change_Added");
        DoubleColumn Change_Deleted                 = tableSortedByFileId.doubleColumn("Change_Deleted");
        DoubleColumn Total_Sets                     = tableSortedByFileId.doubleColumn("Total_Sets");
        DoubleColumn Added_Sets                     = tableSortedByFileId.doubleColumn("Added_Sets");
        DoubleColumn Deleted_Sets                   = tableSortedByFileId.doubleColumn("Deleted_Sets");

        //file_proj_LOC_change_ratio.setMissing(0);
        //the same order as in the vector
        //DoubleColumn project_LOC = project_LOC_int.
        //project_LOC_int
        DoubleColumn[] columnArray = {
                commit_additions,
                commit_deletions,
                changed_files,
                file_additions,
                file_deletions,
                fractal_value,
                fractal_value_over_lines,
                distinct_authors_to_now,
                project_LOC,
                project_LOC_change,
                file_LOC,
                file_LOC_change,
                project_LOC_change_ROC,
                project_LOC_change_percentage,
                file_LOC_change_ROC,
                file_LOC_change_percentage,
                file_proj_LOC_ratio,
                file_proj_LOC_change_ratio,
                Total_Accesses,
                Added_Accesses,
                Deleted_Accesses,
                Total_Calls,
                Added_Calls,
                Deleted_Calls,
                Current_Status,
                Change_Added,
                Change_Deleted,
                Total_Sets,
                Added_Sets,
                Deleted_Sets,};

        for(int i= 0; i< columnArray.length; i++){
            columnArray[i].setMissingTo(0.0);
        }
        Double[][] quantileArray = quantileCalculation(columnArray);

        //print the quantile
        /*for(Double[] da:quantileArray){
            for(Double d: da){
                System.out.print(d);
                System.out.print("\t");
            }
            System.out.println();
        }*/
        //int j=0;
        //int quantileIndex=0;
        /*int[] arr = {2,3,4,};
        System.out.println(columnArray.length);
        System.exit(1);*/
        for(int i=0; i< columnArray.length; i++){
            for(int j=0; j< columnArray[i].size(); j++){
                Double metric = columnArray[i].get(j);
                for(int k=0; k< quantileArray.length; k++){

                    if(metric <= quantileArray[k][i]){
                        vector[j][i]= k;
                        break;
                    }
                }

            }

        }

        dataRepository.setVector(vector);


    }



    public Double[][] quantileCalculation (DoubleColumn[] metrics){
        DoubleColumn[] local = new DoubleColumn[metrics.length]; //= Arrays.copyOf(metrics, metrics.length);
        for(int i=0; i<metrics.length;i++ ){
            local[i]=  metrics[i].copy();
        }
        int quantileSize= quantile.length;
        Double[][] result = new Double[quantileSize][local.length];   //each row is the quantile
        for(int i=0; i<local.length; i++){
            DoubleColumn temp = local[i];
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
    /*public void monitoringCommit(){
        //Step 1: based on the first commit, add top 1/3 weighted files to watch list.
        StringColumn fileId = tableSortedByFileId.stringColumn("file_id");
        Table initialCommit= tableSortedByCommitTime.where(
                fileId.isEqualTo(tableSortedByCommitTime.stringColumn("id").get(0)));

        initialCommit = initialCommit.sortOn("file_proj_LOC_ratio");
        StringColumn topOneThirdWeightedFiles = fileId.first(initialCommit.rowCount()/3+1);
        for(String s: topOneThirdWeightedFiles){
            //watchList.add(s);
        }

        //Step2: monitoring subsequent commits. If a file in the watch list participate in a commit,
        //      its process metrics of that commit will be placed in the file detail watch list
        //      After a file has shown up in commits N times, the accumulated metrics are examined
        //      to see if it follows a quantitative pattern (initially, the pattern is fixed, but it
        //      will be updated as the file keeps participating in commits). If yes, it is expected
        //      that this file will be in a bug-fixing commit within next M commits.


         for(int i = 0; i < tableSortedByCommitTime.rowCount(); i++){

             quantitativePatternMatching(i);
         }

        //for(Row row: tableSortedByCommitTime){}
    }*/

    void segmentation(){
        
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
        int[][] vec = DataRepository.getInstance().getVector();
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
        /*Comparator<Row> tempComparator = new Comparator<Row>() {
            @Override
            public int compare(Row o1, Row o2) {
                String s1= o1.getString("committed_at");
                String s2= o1.getString("committed_at");
                DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                LocalDateTime parsedDate1 = LocalDateTime.parse(s1, formatter);
                LocalDateTime parsedDate2 = LocalDateTime.parse(s2, formatter);


                return parsedDate1.compareTo(parsedDate2);
            }
        };*/

    }

    static void buggyVectorSumUp(DataRepository dataRepository){
        int[][] vec = DataRepository.getInstance().getVector();
        //Table table= dataRepository.getTableSortedByFileId();
        //StringColumn uniqueId= table.stringColumn("file_id").unique();
        StringColumn fileId =DataRepository.getInstance().getTableSortedByFileId().stringColumn("file_id");
        BooleanColumn isBugFixing= DataRepository.getInstance().getTableSortedByFileId().booleanColumn("is_bug_fixing");
        StringColumn commitId =DataRepository.getInstance().getTableSortedByFileId().stringColumn("id");

        //for(String fileId: uniqueId ){}




        int count=0;
        for(int i =3; i<isBugFixing.size(); i++){
            if(isBugFixing.get(i)
                    &&fileId.get(i).equals(fileId.get(i - 1))
                    &&fileId.get(i).equals(fileId.get(i - 2))
                    &&fileId.get(i).equals(fileId.get(i - 3))
                    /*&&isBugFixing.get(i)==isBugFixing.get(i-1)
                    &&isBugFixing.get(i)==isBugFixing.get(i-2)
                    &&isBugFixing.get(i)==isBugFixing.get(i-3)*/
            ){
                count++;
                System.out.print("Sum-up 3: \t\t");
                for(int j= 0; j< vec[j].length; j++){

                    System.out.print(vec[i][j] +vec[i-1][j]+vec[i-2][j]);
                    System.out.print(" ");
                }
                System.out.println();
                System.out.print("Buggy vector: \t");
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
