package com.example.inj.readingStrategy.strategy;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.example.inj.attributes.AttributesField;
import com.example.inj.attributes.SelectAttributes;
import com.example.inj.commitBuilder.TryCommitDetails;
import com.example.inj.commitBuilder.TryFileDetails;
import com.example.inj.commitRepository.CommitDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import lombok.Data;

//Bug 001: Committed as part of the file that is committed alone.
//Bug 002: Committed as part of commitID to be added in the sample data
//Bug 003: Explicity using garbage Collector
//Imp 004: File and it's associated commit details
@Component("reading")
@Data
public class OldReadingStrategy {
    private Logger logger = Logger.getLogger(this.getClass());

    private HashMap<Integer, String> dictionary = new HashMap<>();

    Table<String, String, Map<String, List<Object>>> readableMappingN = HashBasedTable.create();
    Table<String, String, Map<Integer, List<Object>>> readableMappingSameN = HashBasedTable.create();//Bug 001: Committed as part of the file that is committed alone.
    Map<String, Map<String, Boolean>> readableBugFixing = new LinkedHashMap<>();
    private HashMap<String, String> dictionaryString = new LinkedHashMap<>();
    HashMap<String, String> dictionaryTime = new HashMap<>(); //CommitID, Time
    Table<String, String, Map<Integer, List<Object>>> readableMappingFinal = HashBasedTable.create();
    Map<String, List<String>> fileCommits= new HashMap<>(); //Imp 004

    public Map<String, List<String>> getFileCommits() {
        return fileCommits;
    }

    public void setFileCommits(Map<String, List<String>> fileCommits) {
        this.fileCommits = fileCommits;
    }

    private OldReadingStrategy(){}

    private static volatile OldReadingStrategy instance;
    public static OldReadingStrategy getInstance(){
        if(instance==null)
        {
            synchronized(OldReadingStrategy.class)
            {
                if(instance==null)
                {
                    instance=new OldReadingStrategy();
                }
            }

        }

        return instance;
    }


    public void parseData() throws IOException, ParseException {


        //parser settings
        BeanListProcessor<AttributesField> rowProcessor = new BeanListProcessor<>(AttributesField.class);
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setRowProcessor(rowProcessor);


        parserSettings.setHeaderExtractionEnabled(true);
        //setting the headers as additions and deletions are two same column name
        parserSettings.setHeaders("id", "branch", "message", "parent_id", "author", "authored_at", "committer", "committed_at", "Cadditions", "Cdeletions"
                , "changed_files", "is_bug_linked , sd", "is_fix_related", "is_bug_fixing", "is_refactoring",
                "file_path", "previous_file_path", "additions", "deletions", "file_id");
        //Select Attributes from enum
        parserSettings.selectFields(SelectAttributes.values());
        CsvParser parser = new CsvParser(parserSettings);

        /*Parse the excel based on date
        parser.beginParsing(new FileReader(new File("C:\\Users\\Carnoll\\Desktop\\IBM Project Details\\CSV Files\\gcc.csv")));
        int count=0;
        String[] row;
        List<AttributesField> beanss= new ArrayList<>();
        while((row=parser.parseNext())!= null)
        {
            AttributesField af=rowProcessor.createBean(row, parser.getContext());
            row=af.getCommitted_at().split(" ");
            if(row[0].compareTo("2013-11-13") <=0)  //Hardcode the date: return a.compareTo(d) * d.compareTo(b) > 0;
            {
                beanss.add(af);
            }
        }
       Parse the excel based on date*/
        //D:\Thesis-Analysis\Extras-Thesis\Project_CSV_Files\NON-RECONCILED-DATA
        //D:\Thesis-Analysis\Extras-Thesis\Project_CSV_Files\Latest Excel_11_9_2020\Without_Merge_Reconciled\Done
        try {
            parser.parse(new FileReader(new File("D:\\Thesis-Analysis\\Extras-Thesis\\Project_CSV_Files\\Latest Excel_11_9_2020\\Without_Merge_Reconciled\\Done\\solid.csv")));
        }
        catch(Exception e)
        {
            System.out.println("File Not Found");
        }
        List<AttributesField> beans = rowProcessor.getBeans();
        ListIterator<AttributesField> listIterator = beans.listIterator();
        while (listIterator.hasNext()) {
            AttributesField af = listIterator.next();
            CommitDetails cm = new CommitDetails(af.getId());
            cm.createCommitHashMap(af.getId());

            @SuppressWarnings("unused")
            TryCommitDetails com = new TryCommitDetails.UserBuilder(af.getId()).build();
            @SuppressWarnings("unused")
            TryFileDetails tom = new TryFileDetails.TryFileDetailsBuilder(af.getFile_id(), af.getId()).setAddition(af.getAdditions()).setDeletion(af.getDeletions()).setBugFixing(af.getIs_bug_fixing()).setCommitDate(af.getCommitted_at()).setCaddition(af.getCadditions()).setCdeletion(af.getCdeletions()).build();

        }

        System.out.println("Calling Table Mapping");
        createTableMapping(TryFileDetails.getCommitId2FileDetailsMap());


    }


    /* convertJson function will convert the List input to JSON output using Jackson Json API */
    public ArrayList<String> convertJson(List<AttributesField> attf) throws IOException {
        ListIterator<AttributesField> itr = attf.listIterator();
        ArrayList<String> jsonArray = new ArrayList<>();

        while (itr.hasNext()) {
            //Create ObjectMapper
            ObjectMapper mapper = new ObjectMapper();
            //Convert Object to JSON string
            String jsonFormat = mapper.writeValueAsString(itr.next());
            jsonArray.add(jsonFormat);
        }
        return jsonArray;
    }

    public Table<String, String, Map<Integer, List<Object>>> getReadableMappingSameNI() {
        return getReadableMappingSameN();
    }

/*    @Override
    public Table<String, String, Map<String, List<Object>>> getReadableMappingNI() {
        return getReadableMappingN();
    }
    @Override
    public Table<String, String, Map<Integer, List<Object>>> getReadableMappingI() {
        return getReadableMapping();
    }*/

    public Table<String, String, Map<Integer, List<Object>>> getReadableMappingFinalI() {
        return getReadableMappingFinal();
    }

    public HashMap<String, String> getDictionaryStringI() {
        return getDictionaryString();
    }

    public HashMap<Integer, String> getDictionaryI() {
        return getDictionary();
    }

    public Map<String, Map<String, Boolean>> getReadableBugFixingI() {
        return getReadableBugFixing();
    }

    public Map<String, List<String>> getFileCommitsI() {
        return getFileCommits();
    }

    public Map<String, String> getDictionaryTimeI() {
        return dictionaryTime;
    }

    //Method to create and populate data structure using GuavaTable
    public void createTableMapping(HashMap<CommitDetails, List<TryFileDetails>> tryHashMap) throws ParseException {

        Table<TryFileDetails, TryFileDetails, Map<Integer, List<Object>>> fileTableMapping
                = HashBasedTable.create();
        List<String> fileIds = new ArrayList<>();
        Table<String, String, Map<Integer, List<Object>>> readableMapping = HashBasedTable.create();
        Table<String, String, Map<Integer, List<Object>>> readableMapping2 = HashBasedTable.create();
        Table<String, String, Map<Integer, List<Object>>> readableMapping3 = HashBasedTable.create();
        //Check4 is created to capture the commitID
        Table<String, String, Map<String, List<Object>>> readableMappingCheck4 = HashBasedTable.create();
        //For each commit we will have list of file objects
        Iterator<Map.Entry<CommitDetails, List<TryFileDetails>>> entrySet = tryHashMap.entrySet().iterator();
        //Integer for occurence and List of changes
        Map<Integer, List<Object>> scalarVector = new HashMap<>();
        Map<String, List<Object>> scalarVectorCheck4 = new HashMap<>();
        @SuppressWarnings("unused")
        int count2=0;
        @SuppressWarnings("unused")
        int count1 = 0;
        //Start: Bug 001: Committed as part of the file that is committed alone.
        @SuppressWarnings("unused")
        int caCount = 0;
        int cadd = 0;
        Table<String, String, Map<String, List<Object>>> readableMappingSame = HashBasedTable.create();
        Table<String, String, Map<Integer, List<Object>>> readableMappingSameTwo = HashBasedTable.create();
        Map<String, List<Object>> scalarVectorSame = new HashMap<>();
        Map<String, List<Object>> scalarVectorSameTwo = new HashMap<>();
        List<Object> sameObj = new LinkedList<>();
        for (CommitDetails row : tryHashMap.keySet()) {
            List<TryFileDetails> sameFile = new LinkedList<>();
            sameFile = tryHashMap.get(row);

            if (sameFile.size() == 1) {
                TryFileDetails tf = sameFile.get(0);
                cadd = tf.getAddition() + tf.getDeletion();
                caCount++;

                //1. buggy

                sameObj.add(0);
                //2. non-buggy
                sameObj.add(0);
                // 3. How many lines of Fi is changed
                sameObj.add(cadd);
                // 4. How many lines of Fj is changed
                sameObj.add(cadd);
                // 5. Average number of lines changed in a particular commit
                sameObj.add(cadd);
                // 6. Minimum number of lines changed in a particular commit
                sameObj.add(cadd);
                // 7. Maximum number of lines changed in a particular commit
                sameObj.add(cadd);
                // 8. Median of lines changed in a particular commit
                sameObj.add(0.0);
                // 9. Percentile of Fi in a particular commit
                sameObj.add(0.0);
                // 10. Percentile of Fj in a particular commit
                sameObj.add(0.0);
                // 11. Date of committed file Fj
                sameObj.add(tf.getDate());
                //12.
                sameObj.add("RRRR");
                //13. Buggy List Fi
                sameObj.add(tf.isBugFixing());
                //14. Number of lines in a commit has modified
                sameObj.add(tf.getCaddition());
                //15. Number of lines in a commit is deleted
                sameObj.add(tf.getCdeletion());
                //16. BugFixing Or Not
                sameObj.add(tf.isBugFixing());


                scalarVectorSame.put(tf.getCommitId(), sameObj);

                if (readableMappingSame.contains(tf.getFileId(), tf.getFileId())) {
                    scalarVectorSameTwo = readableMappingSame.get(tf.getFileId(), tf.getFileId());
                    scalarVectorSame.putAll(scalarVectorSameTwo);
                    readableMappingSame.put(tf.getFileId(), tf.getFileId(), scalarVectorSame);
                } else {
                    readableMappingSame.put(tf.getFileId(), tf.getFileId(), scalarVectorSame);
                }
                scalarVectorSame = new LinkedHashMap<>();
                scalarVectorSameTwo = new LinkedHashMap<>();
                sameObj = new LinkedList<>();
            }

        }

        //End: Bug 001: Committed as part of the file that is committed alone.

        //Populating and Creating the data structure with "X" for the (FN,FN)
        while (entrySet.hasNext()) {
            //Iterator on FileDetails of HashMap
            Iterator<TryFileDetails> listIterator = entrySet.next().getValue().iterator();
            while (listIterator.hasNext()) {
                TryFileDetails tfd = listIterator.next();
                if (!fileIds.contains(tfd.getFileId())) {
                    fileIds.add(tfd.getFileId());
                    fileTableMapping.put(tfd, tfd, scalarVector);

                    readableMapping.put(tfd.getFileId(), tfd.getFileId(), scalarVector);


                }
                tfd=null; //Bug 003: Explicity using garbage Collector
            }
            listIterator=null; //Bug 003: Explicity using garbage Collector
        }
        entrySet=null; //Bug 003: Explicity using garbage Collector




        //Removal of redundancy
        Iterator<String> columnReadableItr;
        Iterator<String> rowReadableItr = readableMapping.rowKeySet().iterator();
        String fileRow = "";
        String fileColumn = "";
        List<Object> list = new ArrayList<>();
        list.add("C");


        int i = 0;
        int buggy;
        int nonbuggy;
        int linesChangedFi = 0;
        int linesChangedFj = 0;
        int avgLinesChangedCi = 0;
        int FileCount = 0;
        int lines = 0;
        int index = 0;
        double percentileFi = 0;
        double percentileFj = 0;
        List<Integer> sortedListLineChanged = new ArrayList<>();
        double median = 0;


        while (rowReadableItr.hasNext()) {
            fileRow = rowReadableItr.next();
            columnReadableItr = readableMapping.columnKeySet().iterator();
            while (columnReadableItr.hasNext()) {
                fileColumn = columnReadableItr.next();
                if (!fileRow.equals(fileColumn) &&
                        ((!readableMapping2.contains(fileRow, fileColumn) || !readableMapping2.contains(fileColumn, fileRow)))) {
                    readableMapping2.put(fileRow, fileColumn, scalarVector);
                    count1++;
                } else {
                    count2++;
                }
            }
            columnReadableItr=null; //Bug 003: Explicity using garbage Collector
        }
        rowReadableItr=null; //Bug 003: Explicity using garbage Collector


        rowReadableItr = readableMapping2.rowKeySet().iterator();



        //Creating Sparse Vector using HashMap
        Iterator<Map.Entry<CommitDetails, List<TryFileDetails>>> entrySet1;

        int columnInside = 0;
        int rowInside = 0;
        int iDic = -1;
        while (rowReadableItr.hasNext()) {
            String rtfd = rowReadableItr.next();


            columnReadableItr = readableMapping.columnKeySet().iterator();

            while (columnReadableItr.hasNext()) {
                String ctfd = columnReadableItr.next();
                List<TryFileDetails> tfd = new ArrayList<>();
                entrySet1 = tryHashMap.entrySet().iterator();
                scalarVector = new HashMap<>();
                scalarVectorCheck4 = new HashMap<>();
                i = 0;
                String commitIdForAB = "";
                buggy = 0; //Number of times it appear as a buggy commit in a commit details
                nonbuggy = 0; //Number of times it appear as a non-buggy commit

                if (!rtfd.equals(ctfd)) {

                    while(entrySet1.hasNext()) {
                        tfd = entrySet1.next().getValue();
                        Iterator<TryFileDetails> itrTfd = tfd.listIterator();
                        List<Object> buggyList = new ArrayList<>();
                        int rowAppear = 0;
                        int colAppear = 0;
                        avgLinesChangedCi = 0;
                        FileCount = 0;
                        sortedListLineChanged.clear();
                        linesChangedFi = 0;
                        linesChangedFj = 0;
                        boolean rtfdBugFixing = false;
                        boolean ctfdBugFixing = false;
                        columnInside = 0;
                        rowInside = 0;
                        String date = null;
                        int bugFi = 0;
                        int cAddition = 0;
                        int cDeletion = 0;
                        @SuppressWarnings("unused")
                        boolean bug = false;
                        @SuppressWarnings("unused")
                        boolean nonBug = true;
                        boolean secondBug=false;
                        //Improvising

                        //Traversing against the list of a particular commit
                        while (itrTfd.hasNext()) {
                            TryFileDetails ttffd = itrTfd.next();
                            index = 0;

                            cAddition = ttffd.getCaddition();
                            cDeletion = ttffd.getCdeletion();
                            //Average number of lines changed in a particular commit
                            avgLinesChangedCi = avgLinesChangedCi + ttffd.getAddition() + ttffd.getDeletion();
                            FileCount++;
                            //Average number of lines changed in a particular commit

                            lines = ttffd.getAddition() + ttffd.getDeletion();

                            {
                                //Minimum lines changed in a particular commit
                                sortedListLineChanged.add(lines);
                                //Minimum lines changed in a particular commit

                            }

                            if(ttffd.getFileId().equals(rtfd))
                            {
                                secondBug=ttffd.isBugFixing();
                            }

                            if (ttffd.getFileId().equals(rtfd)) {
                                rowAppear++;
                                rowInside++;

                                linesChangedFi = ttffd.getAddition() + ttffd.getDeletion();
                                if (!ttffd.isBugFixing()) {
                                    rtfdBugFixing = true;
                                }
                            }
                            if (ttffd.getFileId().equals(ctfd)) {
                                colAppear++;
                                columnInside++;
                                date = ttffd.getDate();

                                //How many lines of Fj is changed
                                linesChangedFj = ttffd.getAddition() + ttffd.getDeletion();
                                if (!ttffd.isBugFixing()) {
                                    ctfdBugFixing = true;
                                }
                            }

                            commitIdForAB = ttffd.getCommitId();
                        }

                        itrTfd = null; //Bug 003: Explicity using garbage Collector

                        if ((rowAppear != 0 && colAppear != 0)) {

                            if (rtfdBugFixing && ctfdBugFixing) {

                                ++nonbuggy;
                                nonBug = true;

                            } else if (!ctfdBugFixing && !ctfdBugFixing) {

                                ++buggy;
                                ++bugFi;
                                bug = true;
                            }
                        }

                        //Average number of lines changed in a particular commit
                        avgLinesChangedCi = (int) avgLinesChangedCi / FileCount;
                        //Average number of lines changed in a particular commit

                        //Minimum lines and Maximum lines changed in a particular commit
                        Collections.sort(sortedListLineChanged);

                        {
                            //Median of a sorted list
                            if (sortedListLineChanged.size() % 2 == 0) {
                                median = (double) (sortedListLineChanged.get(((sortedListLineChanged.size() - 1) / 2)) + Math.abs(sortedListLineChanged.get((sortedListLineChanged.size()) / 2))) / 2.0;
                            } else {
                                median = (double) (sortedListLineChanged.get(sortedListLineChanged.size() / 2));
                            }
                            //Median of a sorted list
                        }
                        //Minimum lines and Maximum lines changed in a particular commit

                        //percentile of Fi in a sortedList
                        index = sortedListLineChanged.indexOf(linesChangedFi);

                        percentileFi = ((float) (index + 1) / (sortedListLineChanged.size())) * 100;
                        //percentile of Fi in a sortedList

                        //percentile of Fj in a sortedList
                        index = sortedListLineChanged.indexOf(linesChangedFj);
                        percentileFj = ((float) (index + 1) / (sortedListLineChanged.size())) * 100;
                        //percentile of Fj in a sortedList

                        if (/*(buggy != 0) || (nonbuggy != 0)) &&*/ (rowInside != 0 && columnInside != 0)) {

                            //System.out.println("Hey I am here");
                            //1.
                            buggyList.add(buggy);
                            //2.
                            buggyList.add(nonbuggy);
                            // 3. How many lines of Fi is changed
                            buggyList.add(linesChangedFi);
                            // 4. How many lines of Fj is changed
                            buggyList.add(linesChangedFj);
                            // 5. Average number of lines changed in a particular commit
                            buggyList.add(avgLinesChangedCi);
                            // 6. Minimum number of lines changed in a particular commit
                            buggyList.add(sortedListLineChanged.get(0));
                            // 7. Maximum number of lines changed in a particular commit
                            buggyList.add(sortedListLineChanged.get((sortedListLineChanged.size() - 1)));
                            // 8. Median of lines changed in a particular commit
                            buggyList.add(median);
                            // 9. Percentile of Fi in a particular commit
                            buggyList.add(percentileFi);
                            // 10. Percentile of Fj in a particular commit
                            buggyList.add(percentileFj);
                            // 11. Date of committed file Fj
                            buggyList.add(date);
                            //12.
                            buggyList.add("RRRR");
                            //13. Buggy List Fi
                            buggyList.add(bugFi);
                            //14. Number of lines in a commit has modified
                            buggyList.add(cAddition);
                            //15. Number of lines in a commit is deleted
                            buggyList.add(cDeletion);
                            //16. BugFixing Or Not
                            buggyList.add(secondBug);

                            scalarVector.put(i, buggyList);
                            scalarVectorCheck4.put(commitIdForAB, buggyList);
                            dictionary.put(i, commitIdForAB);
                            dictionaryString.put(date, commitIdForAB);
                            dictionaryTime.put(commitIdForAB,date);

                        }
                        i = i + 1;

                    }
                    entrySet1 = null; //Bug 003: Explicity using garbage Collector
                    if (!scalarVector.isEmpty()) {
                        //Sorting a map for the function
                        List<Map.Entry<Integer, List<Object>>> listSort = new LinkedList<>(scalarVector.entrySet());
                        Collections.sort(listSort, Comparator.comparing(o -> String.valueOf(o.getValue().get(10))));

                        //scalarVector.clear();
                        scalarVector = new LinkedHashMap<>();

                        for (Map.Entry<Integer, List<Object>> stu : listSort) {
                            //System.out.println("Key" + stu.getKey() + "value" +stu.getValue());
                            scalarVector.put(stu.getKey(), stu.getValue());
                        }

                        readableMapping3.put(rtfd, ctfd, scalarVector);
                        listSort = null; //Start:Bug 003: Explicity using garbage Collector

                    }
                    //Start-Repeated for check 4
                    if (!scalarVectorCheck4.isEmpty()) {
                        //Sorting a map for the function
                        List<Map.Entry<String, List<Object>>> listSort = new LinkedList<>(scalarVectorCheck4.entrySet());
                        Collections.sort(listSort, Comparator.comparing(o -> String.valueOf(o.getValue().get(10))));

                        scalarVectorCheck4 = new LinkedHashMap<>();

                        for (Map.Entry<String, List<Object>> stu : listSort) {

                            scalarVectorCheck4.put(stu.getKey(), stu.getValue());
                        }

                        readableMappingCheck4.put(rtfd, ctfd, scalarVectorCheck4);
                        listSort = null; //Start:Bug 003: Explicity using garbage Collector
                    }
                    //End-Repeated Check 4


                }
                //Start: Bug 001: Committed as part of the file that is committed alone.
                else {
                    //readableMappingSameTwo it will contain the records of file that are committed alone
                    if (readableMappingSame.contains(rtfd, ctfd)) {
                        {
                            Map<String, List<Object>> fixMap = readableMappingSame.get(rtfd, ctfd);
                            Map<Integer, List<Object>> doubFix = new LinkedHashMap<>();
                            for (String k : fixMap.keySet()) {
                                doubFix.put(iDic, fixMap.get(k));
                                String date = (String) fixMap.get(k).get(10);
                                dictionary.put(iDic, k);
                                dictionaryString.put(date, k);
                                dictionaryTime.put(k,date);
                                //readableMapping3.put(rtfd, ctfd, doubFix);
                                readableMappingSameTwo.put(rtfd, ctfd, doubFix);
                                iDic--;
                            }

                            //readableMappingCheck4.put(rtfd,ctfd, fixMap);


                        }
                        //Map<Integer, List<Object>> scalVec= readableMapping3.get(rtfd, ctfd);
                        Map<Integer, List<Object>> scalVec = readableMappingSameTwo.get(rtfd, ctfd);
                        List<Map.Entry<Integer, List<Object>>> listSort = new LinkedList<>(scalVec.entrySet());
                        Collections.sort(listSort, Comparator.comparing(o -> String.valueOf(o.getValue().get(10))));

                        //Sort the values
                        scalVec = new LinkedHashMap<>();

                        for (Map.Entry<Integer, List<Object>> stu : listSort) {
                            //System.out.println("Key" + stu.getKey() + "value" +stu.getValue());
                            scalVec.put(stu.getKey(), stu.getValue());

                        }
                        //readableMapping3.put(rtfd, ctfd, scalVec);
                        readableMappingSameTwo.put(rtfd, ctfd, scalVec);

                        scalVec = new LinkedHashMap<>();

                    }

                }
                //End:Bug 001: Committed as part of the file that is committed alone.


            }
            columnReadableItr = null; //Bug 003: Explicity using garbage Collector


        }
        @SuppressWarnings("unused")
        Map<String, Map<String, Map<String, List<Object>>>> printMap=  readableMappingCheck4.rowMap();

        rowReadableItr=null; //Bug 003: Explicity using garbage Collector
        setDictionary(dictionary);
        setReadableMapping(readableMapping3);
        setReadableMappingSameN(readableMappingSameTwo);
        setDictionaryString(dictionaryString); //Bug 002: Committed as part of commitID to be added in the sample data
        setDictionaryTime(dictionaryTime);
        IsBugFixing();
        readableMappingFinal.putAll(readableMapping3);
        readableMappingN.putAll(readableMappingCheck4); //Added for parameters in excel

        //Start: Imp 004: File and it's associated commit details
        Map<String, Map<String, Map<Integer, List<Object>>>> mapMe=readableMappingFinal.rowMap();
        for(String file_id: mapMe.keySet())
        {
            Set<String> commits= new TreeSet<>();
            if(mapMe.containsKey(file_id)) {
                Map<String, Map<Integer, List<Object>>> fileMap = mapMe.get(file_id);
                for (String subFile : fileMap.keySet()) {
                    if (fileMap.containsKey(subFile)) {
                        Map<Integer, List<Object>> inside = fileMap.get(subFile);
                        for (int dictKey : inside.keySet()) {
                            if (dictionary.containsKey(dictKey)) {

                                String value = dictionary.get(dictKey);
                                commits.add(value);
                            }
                        }
                    }

                }
            }
            if(readableMappingSameTwo.contains(file_id,file_id)) {
                Map<Integer, List<Object>> insideSameFile = readableMappingSameTwo.get(file_id, file_id);
                for (int dictSameKey : insideSameFile.keySet()) {
                    if (dictionary.containsKey(dictSameKey)) {
                        String value = dictionary.get(dictSameKey);
                        commits.add(value);
                    }
                }
            }
            fileCommits.put(file_id,new ArrayList<>(commits));
        }
        setFileCommits(fileCommits);
        /*System.out.println("Newly Added feature");
        fileCommits.entrySet().forEach(e-> System.out.print(e));*/

        //End : Imp 004: File and it's associated commit details
        //System.exit(0);

        System.out.println("Generate");

        /*Start:Bug 003: Explicity using garbage Collector*/
        scalarVectorCheck4=null;
        scalarVector= null;
        readableMapping=null;
        readableMapping2=null;
        readableMappingSame=null;
        readableMappingSameTwo=null;
        //dictionaryString=null;
        //dictionary=null;
        readableMappingCheck4=null;
        fileTableMapping=null;
        entrySet=null;

        System.out.println("Mapping is generated");
        /* End: Bug 003: Explicity using garbage Collector */

    }

    public void IsBugFixing() {
        Table<String, String, Map<Integer, List<Object>>> readableMappingPair = getReadableMapping();
        Table<String, String, Map<Integer, List<Object>>> readableMappingSame = getReadableMappingSameN();
        Map<Integer, List<Object>> readSubRow = new HashMap<>();
        List<Object> obj = new ArrayList<>();
        Map<String, Map<String, Boolean>> outp = new LinkedHashMap<>();
        Map<String, Boolean> subOut = new LinkedHashMap<>();
        Map<String, Map<String, Map<Integer, List<Object>>>> readableMappingPairMap = readableMappingPair.rowMap();
        Map<String, Map<Integer, List<Object>>> readableMappingPairSubMap = new LinkedHashMap<>();
        for (String row : readableMappingPairMap.keySet()) {
            readableMappingPairSubMap = readableMappingPairMap.get(row);

            for (String col : readableMappingPairSubMap.keySet()) {
                readSubRow = readableMappingPairSubMap.get(col);
                for (int i : readSubRow.keySet()) {
                    obj = readSubRow.get(i);

                    if (!subOut.containsKey((String) obj.get(10))) {
                        subOut.put((String) obj.get(10), (Boolean) obj.get(15));
                        /*if(row.equals("333f55df-1ed0-11eb-af13-482ae32cf5b4"));
                        {
                            System.out.println("Date "+  obj.get(10)+ " Boolean " + subOut.put((String) obj.get(10), (Boolean) obj.get(15)));
                        }*/
                    }
                    obj = new ArrayList<>();
                }
            }

            readSubRow = new HashMap<>();
            if (readableMappingSame.contains(row, row)) {
                readSubRow = readableMappingSame.get(row, row);
                for (int i : readSubRow.keySet()) {
                    obj = readSubRow.get(i);
                    if (!subOut.containsKey((String) obj.get(10))) {
                        subOut.put((String) obj.get(10), (Boolean) obj.get(15));
                    }
                    obj = new ArrayList<>();
                }
            }


            outp.put(row, subOut);
            readSubRow = new HashMap<>();
            subOut = new LinkedHashMap<>();

        }
        /*outp.get("333f55df-1ed0-11eb-af13-482ae32cf5b4").entrySet().forEach(e-> System.out.print(e));
        readableMappingPairMap.get("333f55df-1ed0-11eb-af13-482ae32cf5b4").entrySet().forEach(e-> System.out.print(e));
        System.exit(0);*/
        setReadableBugFixing(outp);

      /*  System.out.println("Bug Fixing Commit");
        outp.entrySet().forEach(e-> System.out.print(e));*/

        /*outp.entrySet().forEach(e->System.out.print(e));
        System.exit(0);
*/
        /*Start:Bug 003: Explicity using garbage Collector*/
        outp=null;
        subOut=null;
        readSubRow=null;
        obj=null;
        readableMappingPair=null;
        readableMappingSame=null;
        readSubRow=null;
        readableMappingPairSubMap=null;
        readableMappingPairMap=null;
        /*End:Bug 003: Explicity using garbage Collector*/


    }

    public Table<String, String, Map<Integer, List<Object>>> getReadableMappingSameN() {
        return readableMappingSameN;
    }

    public void setReadableMappingSameN(Table<String, String, Map<Integer, List<Object>>> readableMappingSameN) {
        this.readableMappingSameN = readableMappingSameN;
    }

    public Table<String, String, Map<String, List<Object>>> getReadableMappingN() {
        return readableMappingN;
    }

    public Table<String, String, Map<Integer, List<Object>>> getReadableMapping() {
        return readableMappingFinal;
    }

    public void setReadableMapping(Table<String, String, Map<Integer, List<Object>>> readableMapping) {
        readableMappingFinal = readableMapping;
    }

    public Table<String, String, Map<Integer, List<Object>>> getReadableMappingFinal() {
        return readableMappingFinal;
    }

    public void setReadableMappingN(Table<String, String, Map<String, List<Object>>> readableMappingN) {
        this.readableMappingN = readableMappingN;
    }

    public HashMap<String, String> getDictionaryString() {
        return dictionaryString;
    }

    public void setDictionaryString(HashMap<String, String> dictionaryString) {
        this.dictionaryString = dictionaryString;
    }

    public HashMap<Integer, String> getDictionary() {
        return dictionary;
    }

    public void setDictionary(HashMap<Integer, String> dictionary) {
        this.dictionary = dictionary;
    }

    public Map<String, Map<String, Boolean>> getReadableBugFixing() {
        return readableBugFixing;
    }

    public void setReadableBugFixing(Map<String, Map<String, Boolean>> readableBugFixing) {
        this.readableBugFixing = readableBugFixing;
    }

}