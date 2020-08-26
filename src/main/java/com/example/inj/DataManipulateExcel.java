package com.example.inj;


import com.example.inj.readingStrategy.strategy.ReadingStrategy;
import com.example.inj.readingStrategy.strategy.ReadingStrategyImp;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import javafx.util.Pair;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//Bug 001: Committed as part of the file that is committed alone.
//Bug 002: Committed as part of commitID to be added in the sample data
//Bug 003: Explicity using garbage Collector
@Component("dataManipulateExcel")
public class DataManipulateExcel {

    //Dependency Injection
    ReadingStrategyImp readingStrategyImp;

    @Autowired
    public void setReadingStrategyImp(ReadingStrategyImp readingStrategyImp) {
        this.readingStrategyImp = readingStrategyImp;
    }

    DataManipulateExcel dataManipulateExcel;


    Map<String, Map<String, Float>> accumulatedSt;
    Map<String, List<Map<String, Map<String, Float>>>> slopes;
    List<String> commitSchedule;
    //It contains the dates when the file is committed alone in a commit
    Map<String, List<String>> yearMapGlobalSame = new LinkedHashMap<>();
    //It contains the dates when the file is co-committed with another file --yearMapPair
    Map<String, List<String>> yearMapPair = new LinkedHashMap<>();
    //This will contain the segment width of each sample
    Map<String, Integer> segmentWidth = new LinkedHashMap<>();
    //This will contain the vector of segments along
    Map<String, Map<String, List<Object>>> vectorMapGlobal = new LinkedHashMap<>();
    //This will contain the vector of segments in different format
    Map<String, List<List<Object>>> vectorFinalMapGlobal = new LinkedHashMap<>();


    /*
           Case-1 Number of times the file A&B are co-committed
           Case-2 Number of time (A&B) are co-committed/ Number of time A is committed globally
           Case-3 Time difference when A&B are co-commited in a consecutive commit/ Count Difference
           Case-4 Number of lines of A has modified/Total number of lines in the commit has modified, excluding A&B
           Case-5 Number of lines of B has modified/Number of lines of the commit(except A&B)
            */
    public void dataToExcel() throws IOException, ParseException, InvalidFormatException {


        Table<String, String, Map<Integer, List<Object>>> guvaTable = readingStrategyImp.parseData();
        HashMap<Integer, String> dictionary = readingStrategyImp.getDictionary();
        /*System.out.println("READING MAPPING -3 ");
        guvaTable.cellSet().forEach(e-> System.out.print(e));*/
        /*System.out.println("Dictionary");
        dictionary.entrySet().stream().forEach(e-> System.out.print(" , "+e));*/
        Pair<Map<String, Map<String, Map<String, Double>>>, Map<String, List<String>>> globalDecayMap = globalDecay(guvaTable);


        //Case-1
        Pair<Map<String, Map<String, Map<Integer, Map<String, Integer>>>>, Map<String, Map<Integer, Map<String, Integer>>>> pairMaps = coCommitABCD(guvaTable);
        //Case-2
        Table<String, String, Map<Integer, Map<String, List<Float>>>> tableCommits = coCommittedExcel(guvaTable);
        //Case-3
        Map<String, Map<String, Map<Integer, Map<String, Integer>>>> timeDifference = timeDifferenceExcel(pairMaps);
        //Case-4
        Pair<Map<String, Map<String, Map<Integer, Map<String, Float>>>>, Map<String, Map<String, Map<Integer, Map<String, Float>>>>> caseFourLists = linesModifiedAAP(guvaTable);
        Map<String, Map<String, Map<Integer, Map<String, Float>>>> caseFourList = caseFourLists.getKey();
        //case-5
        Map<String, Map<String, Map<Integer, Map<String, Float>>>> caseFourListB = caseFourLists.getValue();

        Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMap = new LinkedHashMap<>();
        Map<String, List<Map<Integer, Map<String, Float>>>> pairStrengthSubMap = new LinkedHashMap<>();
        Map<Integer, Map<String, Float>> pairStrengthSubTwoMap = new LinkedHashMap<>();
        List<Map<Integer, Map<String, Float>>> pairSubTwoMapList = new LinkedList<>();
        Map<String, Float> pairStrengthSubThreeMap = new LinkedHashMap<>();

        Map<String, Map<String, Map<Integer, Map<String, Float>>>> pairStrengthMaps = new LinkedHashMap<>();

        //Start: FinalChange for excel file
        FileInputStream fis = null;
        FileOutputStream fos = null;
        Workbook wb = null;
        Sheet sh = null;
        int i = 0;
        //TO DO--> Convert into Strategy
        Map<String, Map<Integer, Map<String, Integer>>> excelYearMap = pairMaps.getValue();
        Map<String, Map<String, Map<Integer, Map<String, Integer>>>> excelCoCommitMap = pairMaps.getKey();
        for (String row : excelYearMap.keySet()) {
            Map<String, Map<Integer, Map<String, Integer>>> excelCoCommitColumnMap = excelCoCommitMap.get(row);
            for (String column : excelCoCommitColumnMap.keySet()) {
                Map<Integer, Map<String, Integer>> excelCommitID = excelCoCommitColumnMap.get(column);
                for (int commitKey : excelCommitID.keySet()) {
                    Map<String, Integer> excelCoCommitvalue = excelCommitID.get(commitKey);
                    for (String key : excelCoCommitvalue.keySet()) {
                        i = i + 1;
                        //Case-1 Number of times A&B are co-committed together
                        int case1 = excelCoCommitvalue.get(key);
                        //Case-2  Number of time (A&B) are co-committed/ Number of time A is committed globally
                        float case2 = 0.0f;
                        if (tableCommits.contains(row, column)) {
                            if (tableCommits.get(row, column).containsKey(commitKey)) {
                                if (tableCommits.get(row, column).get(commitKey).containsKey(key)) {
                                    case2 = tableCommits.get(row, column).get(commitKey).get(key).get(2);
                                }
                            }
                        }
                        //Case-3 Time difference when A&B are co-commited in a consecutive commit/ Count Difference
                        int case3 = 0;
                        if (!timeDifference.isEmpty() && timeDifference.containsKey(row)) {
                            if (timeDifference.get(row).containsKey(column)) {
                                if (timeDifference.get(row).get(column).containsKey(commitKey)) {
                                    if (timeDifference.get(row).get(column).get(commitKey).containsKey(key)) {
                                        case3 = timeDifference.get(row).get(column).get(commitKey).get(key);
                                    }
                                }
                            }
                        }
                        //Case-4 Number of lines of A has modified/Total number of lines in the commit has modified, excluding A&B
                        float case4 = 0.0f;
                        if (caseFourList.containsKey(row) && caseFourList.get(row).containsKey(column) && caseFourList.get(row).get(column).containsKey(commitKey) && caseFourList.get(row).get(column).get(commitKey).containsKey(key)) {
                            case4 = caseFourList.get(row).get(column).get(commitKey).get(key);
                        }
                        // Case-5 Number of lines of B has modified/Number of lines of the commit(except A&B)
                        float case5 = 0.0f;
                        if (caseFourListB.containsKey(row) && caseFourListB.get(row).containsKey(column) && caseFourListB.get(row).get(column).containsKey(commitKey) && caseFourListB.get(row).get(column).get(commitKey).containsKey(key)) {
                            case5 = caseFourListB.get(row).get(column).get(commitKey).get(key);
                        }
                        //Summation
                        float sum = (float) case1 + (float) case2 + (float) case3 + (float) case4 + (float) case5;

                        //System.out.println("SUMMATION" + sum);
                        String dict = "";
                        if (!dictionary.isEmpty()) {
                            dict = dictionary.get(commitKey);
                        }
                        //Start: Creating a HashMap for PairWise Strength
                        {
                            pairStrengthSubThreeMap.put(key, sum);
                            pairStrengthSubTwoMap.put(commitKey, pairStrengthSubThreeMap);
                            pairSubTwoMapList.add(pairStrengthSubTwoMap);
                            pairStrengthSubThreeMap = new LinkedHashMap<>();
                            pairStrengthSubTwoMap = new LinkedHashMap<>();
                        }
                        //End: Creating a HashMap for PairWise Strength
                        /*Writing Data into the Excel File */
                      /*  fis = new FileInputStream("Pair Strength.xlsx");
                        wb = WorkbookFactory.create(fis);
                        sh = wb.getSheet("Pair Strength");
                        Row rowExcel = sh.createRow(i);
                        Cell cell = rowExcel.createCell(0);
                        cell.setCellValue(row);
                        cell = rowExcel.createCell(1);
                        cell.setCellValue(column);
                        cell = rowExcel.createCell(2);
                        cell.setCellValue(commitKey);
                        cell = rowExcel.createCell(3);
                        cell.setCellValue(dict);
                        cell = rowExcel.createCell(4);
                        cell.setCellValue(key);
                        cell = rowExcel.createCell(5);
                        cell.setCellValue(sum);
                        fos = new FileOutputStream("Pair Strength.xlsx");
                        wb.write(fos);*/


                    }

                }
                pairStrengthSubMap.put(column, pairSubTwoMapList);
                pairSubTwoMapList = new LinkedList<>();
            }
            pairStrengthMap.put(row, pairStrengthSubMap);
            pairStrengthSubMap = new LinkedHashMap<>();
            i++;

        }


        Map<String, Map<String, Float>> overallStrength;
        //Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMapss;

        overallStrength = accumulatedStrength(pairStrengthMap, excelYearMap, globalDecayMap.getKey(), globalDecayMap.getValue());
        System.out.println("Overall Strength");
        //overallStrength.entrySet().forEach(e -> System.out.println(e));

        //Start: Bug 003: Explicity using garbage Collector
        pairStrengthMap= null;
        excelYearMap=null;
        //End:Bug 003: Explicity using garbage Collector


//Commenting to avoid second time excel insertion.
/*        Map<String, List<Map<Integer, Map<String, Float>>>> pairStr;
        List<Map<Integer, Map<String, Float>>> pairStrList;
        Map<Integer, Map<String, Float>> pairStrListMap;
        Map<String, Float> pairMap;
        float pairStrength;
        float accumulatedStrength;
        Map<String, Float> overallStrengthMap;
        i = 1;

        for (String row : overallStrength.keySet()) {
            pairStr = pairStrengthMap.get(row);
            overallStrengthMap = overallStrength.get(row);
            for (String col : pairStr.keySet()) {
                pairStrList = pairStr.get(col);
                Iterator pairStrListItr = pairStrList.iterator();
                while (pairStrListItr.hasNext()) {
                    pairStrListMap = (Map<Integer, Map<String, Float>>) pairStrListItr.next();
                    for (int key : pairStrListMap.keySet()) {
                        pairMap = pairStrListMap.get(key);
                        for (String val : pairMap.keySet()) {
                            String dict = "";
                            if (!dictionary.isEmpty()) {
                                dict = dictionary.get(key);
                            }
                            pairStrength = pairMap.get(val);
                            accumulatedStrength = overallStrengthMap.get(val);
                            try {
                                i++;
                                fis = new FileInputStream("Pair Strength.xlsx");
                                wb = WorkbookFactory.create(fis);
                                sh = wb.getSheet("PairAccumulated");
                                Row rowExcel = sh.createRow(i);
                                Cell cell = rowExcel.createCell(0);
                                cell.setCellValue(row);
                                cell = rowExcel.createCell(1);
                                cell.setCellValue(col);
                                cell = rowExcel.createCell(2);
                                cell.setCellValue(key);
                                cell = rowExcel.createCell(3);
                                cell.setCellValue(dict);
                                cell = rowExcel.createCell(4);
                                cell.setCellValue(val);
                                cell = rowExcel.createCell(5);
                                cell.setCellValue(pairStrength);
                                cell = rowExcel.createCell(6);
                                cell.setCellValue(accumulatedStrength);
                                fos = new FileOutputStream("Pair Strength.xlsx");
                                wb.write(fos);
                            } catch (Exception e) {
                                System.out.println("Some Error Occur while writing on file" + e);
                            } finally {
                                wb.close();
                                fos.close();
                                fis.close();
                            }
                        }
                    }


                }
            }
            i++;

        }*/

        List<String> commitDatesSchedule = commitSchedule(overallStrength);
        Map<String, Map<String, Float>> globalDecay = calculateGlobalDecay(commitDatesSchedule, overallStrength);
        //Map<String, Map<String, Float>> implementDecay = implementDecayInStrength(globalDecay, overallStrength);
        Map<String, Map<String, Float>> implementDecay = implementDecayInStrengthSecond(globalDecay, overallStrength);
        //System.exit(0);
        setAccumulatedSt(implementDecay);
        finalStrengthSingleFile(); // Includes Pair as well Single file
        createSegmentWidth();
        //calculateSlopeUsingSimpleRegression(10);
        createVector();
        createRandomSample();
        System.out.println("END OF PROGRAM");


        //insertIntoExcel(implementDecay);
        //calculateChange(getAccumulatedSt()); //To fill out series, not required as
    }

    /*
    AccumulatedStrength: Function will calculate the total strength of file A, when it is committed with B,C,D,E,F along with decay.
     */

    public Map<String, Map<String, Float>> accumulatedStrength(Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMap, Map<String, Map<Integer, Map<String, Integer>>> excelYearMap, Map<String, Map<String, Map<String, Double>>> globalDecayMap, Map<String, List<String>> yearMapCount) {
        Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMapSecond = new LinkedHashMap<>();
        pairStrengthMapSecond.putAll(pairStrengthMap);
        Map<String, Map<String, Map<String, Double>>> globalDecay = globalDecayMap;
        Double decay = 0.0;
        List<String> acceptString = null;
        int index = 0;


        Map<String, Map<String, Float>> overallStrengthDateMap = new HashMap<>();
        Map<String, Map<String, Map<String, Double>>> globalDecayRowUpdate = new LinkedHashMap();
        Map<String, Map<String, Double>> globalDecayUpdateColumn = null;
        Map<String, List<Map<Integer, Map<String, Float>>>> columnPairs;
        Map<String, Integer> countColumn;
        Map<String, Integer> countExistenceColumn;
        List<String> datesMap;
        for (String row : excelYearMap.keySet()) {
            columnPairs = pairStrengthMap.get(row);
            countColumn = new LinkedHashMap<>();
            countExistenceColumn = new LinkedHashMap<>();
            for (String cPair : columnPairs.keySet()) {
                countColumn.put(cPair, 0);
                countExistenceColumn.put(cPair, 0);
            }
            for (String rowPair : pairStrengthMap.keySet()) {

                if (rowPair.equals(row)) {
                    datesMap = new ArrayList<>();
                    Map<Integer, Map<String, Integer>> yearColumn = excelYearMap.get(row);
                    for (int key : yearColumn.keySet()) {
                        Map<String, Integer> yearSubColumn = yearColumn.get(key);
                        datesMap.addAll(yearSubColumn.keySet());
                    }
                    Collections.sort(datesMap);
                    //datesMap.stream().forEach(e->System.out.println("Dates Map"+e));

                    Map<String, List<Map<Integer, Map<String, Float>>>> columnPair = pairStrengthMap.get(rowPair);
                    List<String> listColumns = new ArrayList<>();
                    listColumns.addAll(columnPair.keySet());

                    ListIterator<String> datesMapIterator = datesMap.listIterator();
                    Map<String, Float> overallStrengthMap = new LinkedHashMap<>();
                    String firstDate = null;
                    float overallStrength = 0.0f;
                    while (datesMapIterator.hasNext()) {
                        overallStrength = 0.0f;
                        firstDate = datesMapIterator.next();
                        List<String> pairData = new ArrayList<>();
                        acceptString = new LinkedList<>();
                        for (String columnKey : columnPair.keySet()) {
                            //if (!columnKey.equals(rowPair))  //Bug 001: Committed as part of the file that is committed alone.
                            {

                                List<Map<Integer, Map<String, Float>>> columnPairList = columnPair.get(columnKey);
                                ListIterator columnPairListIterator = columnPairList.listIterator();

                                while (columnPairListIterator.hasNext()) {
                                    Map<Integer, Map<String, Float>> columnMapPair = (Map<Integer, Map<String, Float>>) columnPairListIterator.next();
                                    for (int keys : columnMapPair.keySet()) {
                                        Map<String, Float> pairStrength = columnMapPair.get(keys);
                                        if (pairStrength.containsKey(firstDate)) {
                                            float strength = 0.0f;
                                            strength = pairStrength.get(firstDate);
                                            countColumn.put(columnKey, 1);

                                            acceptString.add(columnKey);
                                            overallStrength += strength;
                                            //System.out.println(" Row Pair "+ rowPair + " columnKey "+columnKey+" firstDate "+ firstDate + "Strength - Pair " + strength + " Overall-Strength " + overallStrength);
                                        }

                                    }
                                }
                            }
                        }

                        List<String> validValue = new LinkedList<>();
                        if (!acceptString.isEmpty()) {
                            {

                                for (String rowCount : countColumn.keySet()) {
                                    if (!(acceptString.contains(rowCount)) && (countColumn.get(rowCount) == 1)) {
                                        //Valid Value contains the files that needs to decay
                                        validValue.add(rowCount);
                                    }
                                }

                            }


                            //validValue.stream().forEach(e -> System.out.print("VALID VALUE" + e));

                            Iterator validValueIterator = validValue.listIterator();
                            index = datesMap.indexOf(firstDate);


                            if (index > 0) {
                                //index= index-1;
                                String previous = datesMap.get(index);
                                //String previousDate= datesMap.get(index-1);

                                //System.out.println("PREVIOUS  " + previous);
                                globalDecayUpdateColumn = new LinkedHashMap<>();

                                while (validValueIterator.hasNext()) {
                                    String val = (String) validValueIterator.next();
                                    float decaySt = 0;
                                    float st = 0;


                                    //Start: Changes for final error


                                    Iterator pairIteratorTry = pairStrengthMap.get(row).get(val).iterator();

                                    Map<Integer, Map<String, Float>> pairMapping;
                                    Set<String> pairTry = new TreeSet<>();
                                    while (pairIteratorTry.hasNext()) {
                                        pairMapping = (Map<Integer, Map<String, Float>>) pairIteratorTry.next();
                                        for (int i : pairMapping.keySet()) {
                                            Map<String, Float> pairs = pairMapping.get(i);
                                            for (String is : pairs.keySet()) {
                                                pairTry.add(is);
                                                //pairTry.stream().forEach(e->System.out.println(" SET THIS IS" + e));
                                            }
                                        }
                                    }
                                    String prev = ((TreeSet<String>) pairTry).floor(firstDate);
                                    if (prev != null) {
                                        Iterator pairIterator = pairStrengthMap.get(row).get(val).iterator();
                                        Map<Integer, Map<String, Float>> pairMappings;
                                        while (pairIterator.hasNext()) {
                                            //System.out.println("It's Weird");
                                            pairMappings = (Map<Integer, Map<String, Float>>) pairIterator.next();
                                            for (int i : pairMappings.keySet()) {
                                                //System.out.println("Hi");
                                                Map<String, Float> pairs = pairMappings.get(i);
                                                if (pairs.containsKey(prev)) {
                                                    st = pairs.get(prev);
                                                }
                                            }
                                        }
                                    }

                                    decay = globalDecay.get(row).get(val).get(firstDate);
                                    //System.out.println("ROW " + row + " VALUE " +   val + " st " + st+ " Decay " + decay  +   " firstDate " + firstDate + "OverallStrength" + overallStrength);
                                    // decaySt = (float) (st - decay); //Commenting out as per the new decay fubction
                                    /*Start: Changes as per new decay function */
                                    decaySt = (float) (st * decay);
                                    /*End: Changes as per new decay function */
                                    overallStrength += decaySt;
                                    //System.out.println("Overall Strength " + overallStrength);

                                    //System.exit(0);

                                    //End: Changes for final error


                                }

                            }
                        }

                        overallStrengthMap.put(firstDate, overallStrength);

                    }


                    overallStrengthDateMap.put(row, overallStrengthMap);


                }

            }

        }
        /*System.out.println("Overall Strength for Same file");
        accumulatedHashMapSame.entrySet().stream().forEach(e -> System.out.print(e));*/
        System.out.println("Overall Strength for different file");
        overallStrengthDateMap.entrySet().stream().forEach(e -> System.out.print(e));
        //globalDecayRowUpdate.entrySet().stream().forEach(e-> System.out.println("Global Decay " + e));
        //System.exit(0);
        /*Start: Bug 003: Explicity using garbage Collector */
           countExistenceColumn=null;
           countColumn = null;
           columnPairs = null;
           globalDecayUpdateColumn=null;
           globalDecayRowUpdate=null;
           pairStrengthMapSecond=null;
           globalDecay=null;
           pairStrengthMap=null;
           excelYearMap=null;
           globalDecayMap=null;
           yearMapCount=null;
         /*End:  Bug 003: Explicity using garbage Collector */

        return overallStrengthDateMap;


    }


    /*
It is a Pair level decay based on below function and scenario:
If File A is committed with File B, File C, File D, File E in Commit X at 11-Sept-2010, and again in next Commit Y at 12-Sept-2010 File A is committed only with File B, and File C then we are applying decay for File D, and File E.
PAIR DECAY AFTER CALCULATING THE ACCUMULATED STRENGTH OF A FILE
NOTE: WE WILL AGAIN DECAY IT AT GLOBAL LEVEL TO NORMALIZE IT FURTHER
Pair Decay ( Multiply)->Math.exp( Number of commits passed since A&B are co-committed*0.5)
 */
    public Pair<Map<String, Map<String, Map<String, Double>>>, Map<String, List<String>>> globalDecay(Table<String, String, Map<Integer, List<Object>>> abc) {

        Map<String, Map<String, Integer>> timeSlotAccumulate;
        Map<String, Map<String, Map<String, Integer>>> timeFull = new LinkedHashMap<>();
        Map<String, Map<String, Map<Integer, List<Object>>>> cellMap = abc.rowMap();
        Map<String, List<String>> yearMap = new LinkedHashMap<>();
        Map<String, Integer> timeSlot;
        List<String> yearCount;
        int time = 0;
        int pair = 0;
        int index = 0;
        double decay = 0;
        for (String row : cellMap.keySet()) {
            yearCount = new LinkedList<>();
            Map<String, Map<Integer, List<Object>>> columnMap = cellMap.get(row);
            timeSlot = new LinkedHashMap<>();
            timeSlotAccumulate = new LinkedHashMap<>();
            for (String column : columnMap.keySet()) {
                Map<Integer, List<Object>> listObj = new LinkedHashMap<>();
                listObj.putAll(columnMap.get(column));
                time = 0;
                List<List<Object>> sortedList = new LinkedList<>();
                sortedList.addAll(listObj.values());
                Collections.sort(sortedList, Comparator.comparing(o -> String.valueOf(o.get(10))));

                Iterator listIterator = sortedList.listIterator();
                timeSlot = new LinkedHashMap<>();
                while (listIterator.hasNext()) {

                    List<Object> lst = (List<Object>) listIterator.next();
                    time = time + 1;
                    String dateMap = lst.get(10).toString();
                    //if (!row.equals(column))  //Bug 001: Committed as part of the file that is committed alone.
                    {
                        timeSlot.put(dateMap, time);
                        if (!yearCount.contains(dateMap)) {
                            yearCount.add(dateMap);
                        }
                    }

                }

                {
                    timeSlotAccumulate.put(column, timeSlot);
                }


            }


            {
                timeFull.put(row, timeSlotAccumulate);
            }

            {
                Collections.sort(yearCount);
                yearMap.put(row, yearCount);
            }


        }

        Map<String, Map<String, Map<String, Double>>> globalDecay = new LinkedHashMap<>();
        Map<String, Map<String, Double>> columnGlobalDecay;
        Map<String, Double> dateGlobalDecay;
        Iterator yearMapIterator;


        //Calculate Decay
        for (String row : yearMap.keySet()) //yearMap number of time A is committed
        {
            for (String subRow : timeFull.keySet()) //timeFull: 27c1feb9-3e41-11ea-b4ad-482ae32cf5b4={27c1b0a5-3e41-11ea-851b-482ae32cf5b4={2018-11-11 22:17:32+00:00=1, 2019-02-26 03:09:37+00:00=2, 2019-04-13 15:24:13+00:00=3}
            {
                if (row == subRow) {
                    timeSlotAccumulate = timeFull.get(row); //timeSlotAccumulate: {27c1b0a5-3e41-11ea-851b-482ae32cf5b4={2018-11-11 22:17:32+00:00=1, 2019-02-26 03:09:37+00:00=2, 2019-04-13 15:24:13+00:00=3}
                    columnGlobalDecay = new LinkedHashMap<>();
                    for (String column : timeSlotAccumulate.keySet()) {
                        dateGlobalDecay = new LinkedHashMap<>();
                        timeSlot = timeSlotAccumulate.get(column);


                        //Start:Fixing the global iterator
                        yearCount = yearMap.get(row);
                        yearMapIterator = yearCount.listIterator();

                        pair = 0; //Added for fixing last value

                        while (yearMapIterator.hasNext()) {
                            String yearMapValue = (String) yearMapIterator.next(); // 2017, 2018,2019, 2020, 2021

                            //Start: Adding as per new function of global decay
                            List<String> timeSlotList = new LinkedList<>();
                            timeSlotList.addAll(timeSlot.keySet());
                            Collections.sort(timeSlotList);
                            TreeSet<String> timeSlotTreeSet = new TreeSet<String>();
                            timeSlotTreeSet.addAll(timeSlotList);
                            String compareValue = timeSlotTreeSet.floor(yearMapValue);
                            int index1 = 0;
                            int index2 = 0;
                            int val = 0;
                            double finalIndex = 0;
                            //System.out.println("VALUE" + !timeSlotTreeSet.contains(yearMapValue));
                            if (timeSlotTreeSet.floor(yearMapValue) != null || timeSlotTreeSet.contains(yearMapValue)) {
                                index1 = yearCount.indexOf(compareValue) + 1;
                                index2 = yearCount.indexOf(yearMapValue) + 1;
                                finalIndex = Math.exp(-((index2 - index1) * 0.5));

                            } else {
                                val = 0;
                                finalIndex = Math.exp(val);
                            }
                            dateGlobalDecay.put(yearMapValue, finalIndex);


                            //End: Ending as per new function of global decay

                            //yearMap.entrySet().stream().forEach(e-> System.out.println("Year Map" + e));

                            //dateGlobalDecay.entrySet().stream().forEach(e-> System.out.println(" Global Decay" + e));


                        }
                        //End: Fixing the global iterator


                        columnGlobalDecay.put(column, dateGlobalDecay);

                    }

                    globalDecay.put(subRow, columnGlobalDecay);

                }

            }

        }
        setYearMapPair(yearMap);
        /*Start:  Bug 003: Explicity using garbage Collector */
        abc=null;
        timeSlotAccumulate=null;
        timeFull = null;
        cellMap = null;
        timeSlot=null;
        yearCount=null;
        columnGlobalDecay=null;
        dateGlobalDecay=null;
        yearMapIterator=null;

        /*End:  Bug 003: Explicity using garbage Collector */
        return new Pair(globalDecay, yearMap);

    }


    // Case 2 Start- Number of times A&B are co-committed together/ Number of time A is committed globally
    public Table<String, String, Map<Integer, Map<String, List<Float>>>> coCommittedExcel(Table<String, String, Map<Integer, List<Object>>> abc) {

        //Start: Contain final output
        Table<String, String, Map<String, List<Float>>> outputTable = HashBasedTable.create();
        List<Float> finalList = new ArrayList<>();
        Map<String, List<Float>> finalMap = new LinkedHashMap<>();
        //End: Contain final output

        //Start: For Excel
        Table<String, String, Map<Integer, Map<String, List<Float>>>> outputTable1 = HashBasedTable.create();
        Map<Integer, Map<String, List<Float>>> finalMap2 = new LinkedHashMap<>();
        //End: For Excel


        for (Table.Cell<String, String, Map<Integer, List<Object>>> cell : abc.cellSet()) {
            int tt = 0;
            int time = 0;
            finalMap2 = new LinkedHashMap<>();
            //It will have count of A&B committed together with associated key.(Equal)
            Map<String, Integer> timeSlot = new LinkedHashMap<>();
            //Start: For Excel
            Map<Integer, Map<String, Integer>> timeSlot2 = new LinkedHashMap<>();
            //End: For Excel

            Iterator<Map.Entry<Integer, List<Object>>> cellInterator = cell.getValue().entrySet().iterator();


            while (cellInterator.hasNext()) {
                Map.Entry<Integer, List<Object>> insideCellIterator = cellInterator.next();
                List<Object> lst = insideCellIterator.getValue();
                timeSlot.put(lst.get(10).toString(), ++time);
                timeSlot2.put(insideCellIterator.getKey(), timeSlot); //For Excel
                timeSlot = new LinkedHashMap<>();
            }
            //timeSlot2.entrySet().stream().forEach(e-> System.out.print("Iterator  " + e));
            //System.exit(0);

            Map<String, Map<String, Map<Integer, List<Object>>>> xyz = abc.rowMap();


            Map<String, Map<Integer, List<Object>>> fool = xyz.get(cell.getRowKey());
            //Number of time A is committed globally before and equal to mentioned key
            Map<String, Integer> currentTimeSlot = new LinkedHashMap<>();


            for (int key : timeSlot2.keySet()) {
                Map<String, Integer> timeSlots = timeSlot2.get(key);
                for (Map.Entry<String, Integer> ParentEntry : timeSlots.entrySet()) {
                    int countCurrent = 0;
                    for (Map.Entry<String, Map<Integer, List<Object>>> entry : fool.entrySet()) {
                        Map<Integer, List<Object>> foolish = entry.getValue();
                        {
                            for (Map.Entry<Integer, List<Object>> foolishEntry : foolish.entrySet()) {
                                int keyDict = foolishEntry.getKey();
                                List<Object> foolishList = foolishEntry.getValue();
                                //if s1 > s2, it returns positive number
                                if (foolishList.get(10).toString().compareTo(ParentEntry.getKey()) <= 0) {
                                    currentTimeSlot.put(ParentEntry.getKey(), ++countCurrent);
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                    if (!currentTimeSlot.isEmpty()) {
                        finalList.add((float) ParentEntry.getValue());
                        finalList.add((float) currentTimeSlot.get(ParentEntry.getKey()));
                        finalList.add((float) (((float) ParentEntry.getValue()) / ((float) currentTimeSlot.get(ParentEntry.getKey()))));
                        finalMap.put(ParentEntry.getKey(), finalList);
                        finalMap2.put(key, finalMap);
                        finalMap = new LinkedHashMap<>();
                    }
                    outputTable.put(cell.getRowKey(), cell.getColumnKey(), finalMap);
                    outputTable1.put(cell.getRowKey(), cell.getColumnKey(), finalMap2); //--Debugging
                    finalList = new ArrayList<>();
                    //finalMap2 = new LinkedHashMap<>();
                }
            }
            //outputTable1.put(cell.getRowKey(), cell.getColumnKey(), finalMap2);

        }

        //System.out.println("Before Output Table");
        //outputTable.cellSet().stream().forEach(e -> System.out.print(e));
        //System.out.println("Modify Output Table");


        //outputTable1.cellSet().stream().forEach(e -> System.out.print("CASE-2 : " +e));
        //outputTable1.rowMap().get("27c1b0a5-3e41-11ea-851b-482ae32cf5b4").entrySet().stream().forEach(e -> System.out.print("CASE-2 : " +e));
        System.out.println();
        /*Start:  Bug 003: Explicity using garbage Collector */
        abc=null;
        outputTable=null;
        finalMap=null;
        finalMap2=null;
        finalList=null;
        /*End:  Bug 003: Explicity using garbage Collector */
        return outputTable1;
    }


    //Case-1: Number of times the file A&B are co-committed
    public Pair<Map<String, Map<String, Map<Integer, Map<String, Integer>>>>, Map<String, Map<Integer, Map<String, Integer>>>> coCommitABCD(Table<String, String, Map<Integer, List<Object>>> guvaTable) {

        Map<String, Map<String, Map<Integer, List<Object>>>> xyz = guvaTable.rowMap();

        Map<String, Integer> yearMap = new HashMap<>();
        //Start: For Excel
        Map<String, Map<Integer, Map<String, Integer>>> yearMapExcel3 = new HashMap<>();
        Map<Integer, Map<String, Integer>> yearMapExcel33 = new HashMap<>();
        //End: For Excel

        //Start: Commit-ID
        Map<String, Map<String, Map<Integer, Map<String, Integer>>>> commitExcelxyzs = new LinkedHashMap<>();
        Map<String, Map<Integer, Map<String, Integer>>> commitExcelmaps = new LinkedHashMap<>();
        Map<Integer, Map<String, Integer>> commitFinalMaps = new LinkedHashMap<>();
        //End: Commit-ID


        List<Map<String, Integer>> ListAB = null;
        List<Map<String, Integer>> ListABC = null;

        for (String row : xyz.keySet()) {
            ListAB = new ArrayList<>();
            ListABC = new ArrayList<>();
            Map<String, Map<Integer, List<Object>>> tmp = xyz.get(row);
            String column = "";
            for (Map.Entry<String, Map<Integer, List<Object>>> pair : tmp.entrySet()) {
                int countAB = 0;
                int count = 0;
                column = pair.getKey();
                Map<String, Integer> MapAB = new TreeMap<>();
                commitFinalMaps = new TreeMap<>();
                Map<Integer, List<Object>> valuePair = tmp.get(pair.getKey());
                List<Object> metas = null;
                String year = "";
                for (Map.Entry<Integer, List<Object>> individualPair : valuePair.entrySet()) {
                    Map<String, Integer> MapABC = new TreeMap<>();//For excel
                    Map<String, Integer> yearMap33 = new TreeMap<>();//For excel
                    count = 0;
                    countAB = 0;
                    int commitKey = individualPair.getKey();
                    metas = individualPair.getValue();

                    year = metas.get(10).toString();

                    if (MapAB.isEmpty()) {
                        MapAB.put(year, ++countAB);
                        //For Excel
                        int countABC = MapAB.get(year);
                        MapABC.put(year, countABC);
                        commitFinalMaps.put(commitKey, MapABC);
                        //For Excel
                    } else {
                        if (MapAB.containsKey(year)) {
                            countAB = MapAB.get(year);
                            MapAB.put(year, ++countAB);
                            //For Excel
                            int countABC = MapAB.get(year);
                            MapABC.put(year, countABC);
                            commitFinalMaps.put(commitKey, MapABC);
                            //For Excel
                        } else {
                            MapAB.put(year, ++countAB);
                            //For Excel
                            int countABC = MapAB.get(year);
                            MapABC.put(year, countABC);
                            commitFinalMaps.put(commitKey, MapABC);
                            //For Excel
                        }
                    }

                    if (yearMap.isEmpty()) {
                        if (yearMapExcel33.containsKey(commitKey)) {

                        } else {
                            count = count + 1;
                            yearMap.put(year, count);
                            //For Excel
                            int yearMapCount = yearMap.get(year);
                            yearMap33.put(year, yearMapCount);
                            yearMapExcel33.put(commitKey, yearMap33);
                        }
                        //For Excel

                    } else {

                        if (yearMap.containsKey(year)) {
                            if (yearMapExcel33.containsKey(commitKey)) {

                            } else {
                                count = yearMap.get(year);
                                count = count + 1;
                                yearMap.put(year, count);
                                //For Excel
                                int yearMapCount = yearMap.get(year);
                                yearMap33.put(year, yearMapCount);
                                yearMapExcel33.put(commitKey, yearMap33);
                            }
                            //For Excel

                        } else {
                            if (yearMapExcel33.containsKey(commitKey)) {

                            } else {
                                count = count + 1;
                                yearMap.put(year, count);
                                //For Excel
                                int yearMapCount = yearMap.get(year);
                                yearMap33.put(year, yearMapCount);
                                yearMapExcel33.put(commitKey, yearMap33);
                            }
                            //For Excel

                        }

                    }
                }

                ListAB.add(MapAB);
                ListABC.add(MapAB);
                commitExcelmaps.put(column, commitFinalMaps); //For Excel

                ListABC = new ArrayList<>();

            }

            commitExcelxyzs.put(row, commitExcelmaps);
            yearMapExcel3.put(row, yearMapExcel33); //For Excel
            commitExcelmaps = new LinkedHashMap<>();
            yearMapExcel33 = new HashMap<>();
        }
     /*   System.out.println("Year Map With Change");
        yearMapExcel3.entrySet().stream().forEach(e -> System.out.println(e)); //For Excel
        //System.out.println();
        //System.out.println("#############################################################");
        //System.out.println("With Change");
        commitExcelxyzs.entrySet().stream().forEach(e -> System.out.println("CASE1  : " + e)); //For Excel
        System.out.println();
        System.exit(0);*/
        /*Start:  Bug 003: Explicity using garbage Collector */
        yearMapExcel33=null;
        commitExcelmaps=null;
        commitFinalMaps=null;
        yearMapExcel33=null;
        commitFinalMaps=null;
        guvaTable=null;
        xyz=null;
        yearMap=null;

        /*End:  Bug 003: Explicity using garbage Collector */

        return new Pair(commitExcelxyzs, yearMapExcel3);
    }


    //Fourth And Fifth Case
     /*Case-4 Start- Number of Lines of A is modified to the number of lines are modified in The COMMIT excluding A&B
     OR Number of lines of A is modified/ Total number of lines are modified in the commit excluding A&B
     And,
     Case-5 Number of lines of B is modified/ Total number of lines are modified in the commit excluding A&B
     */

    public Pair<Map<String, Map<String, Map<Integer, Map<String, Float>>>>, Map<String, Map<String, Map<Integer, Map<String, Float>>>>> linesModifiedAAP(Table<String, String, Map<Integer, List<Object>>> abc) {

        Map<String, Map<String, Map<String, Float>>> linesModifyAA = new LinkedHashMap<>();
        Map<String, Map<String, Map<String, Float>>> linesModifyBB = new LinkedHashMap<>();
        Map<String, Map<String, Float>> linesBB = new LinkedHashMap<>();
        Map<String, Map<String, Float>> linesAA = new LinkedHashMap<>();
        List<Object> xyz = new ArrayList<>();
        Map<Integer, List<Object>> hm = new HashMap<>();
        int modifiedLinesA = 0; //Number of lines of A has modified(not globally in a particular commit)
        int modifiedLinesB = 0; //Number of lines of B has modified(not globally in a particular commit) when A & B are committed together
        int modifiedA = 0;
        int modifiedB = 0;
        int cAddition = 0;
        float calA = 0;
        float calB = 0;
        String dates = "";
       /* Map<String, Float> linesModifiedA = new LinkedHashMap<>();*/
        Map<Integer, Map<String, Float>> linesModifiedAAP = new LinkedHashMap<>(); //For Excel
        Map<String, Map<Integer, Map<String, Float>>> linesAAP = new LinkedHashMap<>(); //For Excel
        Map<String, Map<String, Map<Integer, Map<String, Float>>>> linesAAAP = new LinkedHashMap<>(); //For Excel

        Map<Integer, Map<String, Float>> linesModifiedBBP = new LinkedHashMap<>(); //For Excel
        Map<String, Map<Integer, Map<String, Float>>> linesBBP = new LinkedHashMap<>(); //For Excel
        Map<String, Map<String, Map<Integer, Map<String, Float>>>> linesBBBP = new LinkedHashMap<>(); //For Excel

        /*Map<String, Float> linesModifiedB = new LinkedHashMap<>();*/
        Map<String, Map<String, Map<Integer, List<Object>>>> pqr = abc.rowMap();

        for (String row : pqr.keySet()) {
            Map<String, Map<Integer, List<Object>>> column = pqr.get(row);

            for (String c : column.keySet()) {
                hm = column.get(c);
                Iterator hmIterator = hm.entrySet().iterator();
                modifiedLinesA = 0;
                modifiedLinesB = 0;
                cAddition = 0;
                while (hmIterator.hasNext()) {
                    //cAddition = 0;  //NEED TO CONFIRM
                    Map.Entry mapElement = (Map.Entry) hmIterator.next();
                    Map<String, Float> linesModifiedAA = new LinkedHashMap<>(); //For Excel
                    Map<String, Float> linesModifiedBB = new LinkedHashMap<>(); //For Excel
                    xyz = (List<Object>) mapElement.getValue();
                    int key = (int) mapElement.getKey();
                    modifiedA = (Integer) xyz.get(2);
                    modifiedB = (Integer) xyz.get(3);
                    modifiedLinesA = modifiedLinesA + modifiedA;
                    modifiedLinesB = modifiedLinesB + modifiedB;
                    // CAddition- Total number of lines are committed in a global clock excluding the modfied lines of A &B
                    //4007 +

                    cAddition = cAddition + (Integer) xyz.get(13) - modifiedA - modifiedB;

                    dates = (String) xyz.get(10);

                    if (cAddition != 0) {
                        calA = (float) ((float) modifiedLinesA / (float) cAddition);
                        calB = ((float) modifiedLinesB / (float) cAddition);
                       /* linesModifiedA.put(dates, calA);*/
                        linesModifiedAA.put(dates, calA); //For Excel
                        linesModifiedAAP.put(key, linesModifiedAA); //For Excel
                        /*linesModifiedB.put(dates, calB);*/
                        linesModifiedBB.put(dates, calB); //For Excel
                        linesModifiedBBP.put(key, linesModifiedBB); //For Excel
                    }


                }
                /*linesAA.put(c, linesModifiedA);*/
                linesAAP.put(c, linesModifiedAAP); //For Excel
                /*linesBB.put(c, linesModifiedB);*/
                linesBBP.put(c, linesModifiedBBP); //For Excel

                //Clean it
               /* linesModifiedA = new LinkedHashMap<>();
                linesModifiedB = new LinkedHashMap<>();*/
                linesModifiedAAP = new LinkedHashMap<>(); //For Excel
                linesModifiedBBP = new LinkedHashMap<>(); //For Excel
            }

         /*   linesModifyAA.put(row, linesAA);
            linesModifyBB.put(row, linesBB);*/
            linesAAAP.put(row, linesAAP); //For Excel
            linesBBBP.put(row, linesBBP); //For Excel

          /*  linesAA = new LinkedHashMap<>();
            linesBB = new LinkedHashMap<>();*/
            linesAAP = new LinkedHashMap<>(); //For excel;
            linesBBP = new LinkedHashMap<>(); //For Excel
        }

        /*Start:  Bug 003: Explicity using garbage Collector */
        linesAAP=null;
        linesModifiedAAP=null;
        linesModifyAA = null;
        linesModifyBB = null;
        linesBB = null;
        linesAA = null;
        xyz = null;
        hm = null;
        linesModifiedBBP=null;
        linesBBP=null;
        /*End:  Bug 003: Explicity using garbage Collector */

        return new Pair(linesAAAP, linesBBBP);

    }

    /* Case-3 Time difference when A&B are co-commited in a consecutive commit/ Count Difference
 timeDifference function will return the time difference between each consecutive commit of A&B.
 TimeDifference-> How many times the A has been committed without B.
  */
    public Map<String, Map<String, Map<Integer, Map<String, Integer>>>> timeDifferenceExcel(Pair<Map<String, Map<String, Map<Integer, Map<String, Integer>>>>, Map<String, Map<Integer, Map<String, Integer>>>> mapPair) {
        Map<String, Map<String, Map<Integer, Map<String, Integer>>>> excelxyzz = new LinkedHashMap<>();
        Map<Integer, Map<String, Integer>> timeDifferenceAggregate = new TreeMap();
        Map<String, Map<String, Map<Integer, Map<String, Integer>>>> timeDifferenceAggregate2 = new TreeMap<>();
        Map<String, Map<String, Map<Integer, Map<String, Integer>>>> timeDiff3 = new TreeMap<>();
        Map<String, Map<Integer, Map<String, Integer>>> timeDifferenceAggregateColumn = new TreeMap<>();
        excelxyzz = mapPair.getKey();
        Map<String, Map<Integer, Map<String, Integer>>> excelxyzz1 = new LinkedHashMap<>();
        for (String row : excelxyzz.keySet()) {
            excelxyzz1 = excelxyzz.get(row);
            timeDifferenceAggregateColumn = new TreeMap<>();
            timeDifferenceAggregate2 = new TreeMap<>();
            for (String column : excelxyzz1.keySet()) {
                //Map<String, Integer> value= null;
                    /*Year Map27c1898a-3e41-11ea-9116-482ae32cf5b4={49={2018-11-11 22:17:32+00:00=110},
                     17={2015-10-16 01:01:07+00:00=18}, 130={2011-08-25 00:14:24+00:00=15},
                      36={2010-06-11 21:34:56+00:00=1}, 39={2011-08-02 16:37:57+00:00=15},
                       9={2011-06-12 22:00:51+00:00=15}, 73={2015-10-16 01:00:56+00:00=18}, 90={2009-09-09 00:43:47+00:00=1}, 91={2012-06-27 19:32:21+00:00=1}, 12={2015-10-26 23:16:08+00:00=18}, 44={2017-09-08 10:20:09+00:00=1}}Year Map27c1d7f5-3e41-11ea-8623-482ae32cf5b4={49={2018-11-11 22:17:32+00:00=308}}
                     */
                Map<String, Map<Integer, Map<String, Integer>>> yearMap22 = mapPair.getValue();
                //yearMap22.entrySet().stream().forEach(e-> System.out.print("Year Map" +e));
                //System.out.println();
                for (String yearMapRow : yearMap22.keySet()) {
                    if (yearMapRow == row) {
                        Map<Integer, Map<String, Integer>> yearMapExcel = yearMap22.get(yearMapRow);
                        Map<Integer, Map<String, Integer>> resultExcel = new LinkedHashMap<>();
                        for (int key : yearMapExcel.keySet()) {


                            //Map<Integer, Map<String, Integer>> resultExcel= new LinkedHashMap<>();
                            Map<String, Integer> yearMapExcel2 = yearMapExcel.get(key);
                            //yearMapExcel.entrySet().stream().forEach(e-> System.out.print("YEAR MAP" + e));
                            Map<Integer, Map<String, Integer>> valueXYZ = excelxyzz1.get(column);
                            LinkedList<String> listABKeySet = new LinkedList<>();
                            for (int i : valueXYZ.keySet()) {
                                listABKeySet.addAll(valueXYZ.get(i).keySet());
                            }

                            Collections.sort(listABKeySet);
                            ListIterator iteratorAB = listABKeySet.listIterator();
                            //System.out.println("SIZE-----" +listABKeySet.size());

                            while (iteratorAB.hasNext()) {
                                String mapElementPreviousKey = iteratorAB.next().toString();
                                //System.out.println("ARE YOU GOING INSIDE");
                                if (iteratorAB.hasNext()) {
                                    String mapElementNextKey = iteratorAB.next().toString();
                                    iteratorAB.previous();
                                    //System.out.println("ARE YOU GOING INSIDE");
                                    for (String keys : yearMapExcel2.keySet()) {
                                        Map<String, Integer> timeDifference = new TreeMap<>();
                                        if ((keys.compareTo(mapElementPreviousKey)) > 0 && (keys.compareTo(mapElementNextKey) < 0)) {
                                            timeDifference.put(keys, yearMapExcel2.get(keys));
                                            resultExcel.put(key, timeDifference);
                                        }
                                    }
                                    //resultExcel.entrySet().stream().forEach(e-> System.out.print(" CASE 5 RESULT EXCEL"+ e));

                                    if (!resultExcel.isEmpty()) {
                                        for (int keys : resultExcel.keySet()) {
                                            timeDifferenceAggregate.put(keys, resultExcel.get(keys));

                                        }
                                        //timeDifferenceAggregate.entrySet().stream().forEach(e->System.out.print("CASE 5  timeDifferenceAggregate" + e));

                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
                timeDifferenceAggregateColumn.put(column, timeDifferenceAggregate);

                timeDifferenceAggregate = new LinkedHashMap<>();
            }

            timeDifferenceAggregate2.put(row, timeDifferenceAggregateColumn);
            timeDiff3.putAll(timeDifferenceAggregate2);
        }
        //System.out.println("Start: Aggregate Time Difference");
        //timeDiff3.entrySet().stream().forEach(e->System.out.println("CASE 5" + e));
        //System.out.println();
        /*Start:  Bug 003: Explicity using garbage Collector */
        mapPair=null;
        excelxyzz = null;
        timeDifferenceAggregate = null;
        timeDiff3 = null;
        timeDifferenceAggregateColumn =null;
        excelxyzz = null;
        excelxyzz1 = null;
        /*End:  Bug 003: Explicity using garbage Collector */
        return timeDifferenceAggregate2;
    }

    public List<String> commitSchedule(Map<String, Map<String, Float>> accumulatedStrength) {
        List<String> finalCommitDates = new LinkedList<>();
        Map<String, Float> accumulatedStrengthCol = new LinkedHashMap<>();
        for (String row : accumulatedStrength.keySet()) {
            accumulatedStrengthCol = accumulatedStrength.get(row);
            for (String column : accumulatedStrengthCol.keySet()) {
                if (!finalCommitDates.contains(column)) {
                    finalCommitDates.add(column);
                }
            }
        }
        Collections.sort(finalCommitDates);
        accumulatedStrength.entrySet().stream().forEach(e -> System.out.println("Accumulated Strength" + e));
        System.out.println("Start Final Commit Date");
        finalCommitDates.stream().forEach(e -> System.out.print(" , " + e));
        System.out.println("END Final Commit Date");
        /*Map<String, Map<String, Float>> abc = calculateGlobalDecay(finalCommitDates, accumulatedStrength);
        implementDecayInStrength(abc, accumulatedStrength);
        System.exit(0);*/
        /*Start:  Bug 003: Explicity using garbage Collector */
        accumulatedStrength=null;
        accumulatedStrengthCol=null;
        /*End:  Bug 003: Explicity using garbage Collector */
        setCommitSchedule(finalCommitDates);

        return finalCommitDates;
    }

    /*
     Na-> Total Number of commit of A
     N-> Total Number of  commit
     Time Gap-> Number of commit passed(Last time file A is committed- Current Commit)
     Math.exp(Na/N(Time Gap))
     C1
     C2
     C3 <- F1
     C4
     C5
     C6 <- F1
     C7
     C8
     Then the time gap at time C4 will be 1, and at C8 will be 2
    */
    public Map<String, Map<String, Float>> calculateGlobalDecay(List<String> commitSchedule, Map<String, Map<String, Float>> pairStrengthMap) {
        Iterator commitScheduleIterator;
        Map<String, List<Map<Integer, Map<String, Float>>>> pairStrengthMapCol;
        List<Map<Integer, Map<String, Float>>> pairStrengthMapList;
        Iterator iterator;
        Map<Integer, Map<String, Float>> pairStrengthMapListCol;
        Map<String, Float> pairStrengthVal = new LinkedHashMap<>();
        TreeSet<String> pairSet = new TreeSet<>();
        List<String> listSet = new LinkedList<>();
        String iteratorValue = "";
        int index = 0;
        String previousValue = "";
        int timeElapse = 0;
        int currentIndex = 0;
        int lastIndex = 0;
        int currentCommit = 0;
        float decay = 0.0f;
        Map<String, Map<String, Float>> globalDecay = new LinkedHashMap<>();
        Map<String, Float> globalDecayColumn = new LinkedHashMap<>();
        Map<String, Float> pairStrengthRevise = new LinkedHashMap<>();

        for (String a : pairStrengthMap.keySet()) {

            pairStrengthRevise = pairStrengthMap.get(a);
            listSet.addAll(pairStrengthRevise.keySet());
            Collections.sort(listSet);
            pairSet.addAll(listSet);
            commitScheduleIterator = commitSchedule.iterator();


            while (commitScheduleIterator.hasNext()) {
                iteratorValue = (String) commitScheduleIterator.next();

                if (pairSet.floor(iteratorValue) != null) {
                    //Start:Calculation of global decay
                    if (!pairSet.contains(iteratorValue)) {
                        //Variable-1 Time Gap-> Number of commit passed(Last time file A is committed- Current Commit)
                        currentIndex = commitSchedule.indexOf(iteratorValue);
                        previousValue = pairSet.floor(iteratorValue);
                        lastIndex = commitSchedule.indexOf(previousValue);
                        timeElapse = lastIndex - currentIndex;

                        //System.out.println("Going Inside");
                        //Total Number of Commit of A
                        index = listSet.indexOf(previousValue);
                        index = index + 1;

                        //Total Number of Commit --currentIndex
                        currentCommit = currentIndex + 1;

                        /*System.out.println("Iterator Value " + iteratorValue +
                                " previousValue " + previousValue +
                                " last Index " + lastIndex + " Current Index "
                                + currentIndex + " timeElapse " + timeElapse + " currentCommit "
                                + currentCommit + " index " + index + " calc " + ((float) index / (float) currentCommit));*/

                        decay = (float) Math.exp(((float) index / (float) currentCommit) * (float) timeElapse);
                        globalDecayColumn.put(iteratorValue, decay);

                    } else {
                        globalDecayColumn.put(iteratorValue, 0.0f);
                    }

                    //End:Calculation of global decay
                } else {
                    globalDecayColumn.put(iteratorValue, 0.0f);
                }
            }

            globalDecay.put(a, globalDecayColumn);
            globalDecayColumn = new LinkedHashMap<>();
            listSet = new LinkedList<>();
            pairSet = new TreeSet<>();
            pairStrengthRevise = new LinkedHashMap<>();
        }

        globalDecay.entrySet().stream().forEach(e -> System.out.println("Global Decay " + e));
        return globalDecay;
    }


    public Map<String, Map<String, Float>> implementDecayInStrengthSecond(Map<String, Map<String, Float>> globalDecay, Map<String, Map<String, Float>> accumulatedStrength) {
        Map<String, Float> accumulatedStr;
        TreeSet<String> pairSet = new TreeSet<>();
        List<String> listSet = new LinkedList<>();
        Map<String, Float> globalDecayStr;
        String dateValue = "";
        Map<String, Map<String, Float>> finalStrength = new LinkedHashMap<>();
        Map<String, Float> finalRowStrength = new LinkedHashMap<>();
        Float accValue = 0.0f;
        Float decayValue;
        Float finalDecayStrength;

        for (String row : globalDecay.keySet()) {

            accumulatedStr = accumulatedStrength.get(row);

            listSet.addAll(accumulatedStr.keySet());
            Collections.sort(listSet);
            pairSet.addAll(listSet);

            globalDecayStr = globalDecay.get(row);

            for (String globalRow : globalDecayStr.keySet()) {

                dateValue = pairSet.floor(globalRow);

                if (dateValue != null) {

                    if (dateValue.compareTo(globalRow) != 0) //dateValue: 2011-08-02, globalRow: 2011-11-01
                    {
                        decayValue = globalDecayStr.get(globalRow); //0.56

                        if (accumulatedStr.containsKey(dateValue)) {
                            accValue = accumulatedStr.get(dateValue); //6.6
                        }
                        finalDecayStrength = accValue * decayValue;
                        finalRowStrength.put(globalRow, finalDecayStrength);

                        /*System.out.println(" dateValue " + dateValue + " globalRow " + globalRow + " decayValue " +
                                decayValue + " accValue " + accValue + " finalDecayStrength " + finalDecayStrength );*/

                    } else {
                        finalRowStrength.put(globalRow, accumulatedStr.get(globalRow));

                    }
                } else {
                    finalRowStrength.put(globalRow, 0.0f);
                }

            }

            listSet = new LinkedList<>();
            pairSet = new TreeSet<>();

            finalStrength.put(row, finalRowStrength);
            finalRowStrength = new LinkedHashMap<>();
        }
        System.out.println("Printing  Strength of Pair File");
        finalStrength.entrySet().stream().forEach(e -> System.out.println(" , " + e));
        return finalStrength;
    }

    /*
    InsertIntoExcel function will insert the value in the Excel.
     */
    public void insertIntoExcel(Map<String, Map<String, Float>> finalStrength) throws IOException, InvalidFormatException {
        {
            FileInputStream fis = null;
            FileOutputStream fos = null;
            Workbook wb = null;
            Sheet sh = null;
            int i = 1;
            Map<String, Float> finalColumnStrength = new LinkedHashMap<>();
            Float value = 0.0f;
            //Commenting for Readable Issue
            Map<String, String> dateToCommit = readingStrategyImp.getDictionaryString();
            String commitId = ""; //fix for commitId
            Table<String, String, Map<String, List<Object>>> rm = readingStrategyImp.getReadableMappingN();

            Map<String, Map<String, Map<String, List<Object>>>> readableMapping = new LinkedHashMap<>(); //= (Map<String, Map<String, Map<String, List<Object>>>>) readingStrategyImp.getReadableMappingN();

            readableMapping.putAll(rm.rowMap());
            Map<String, Map<String, List<Object>>> redableMappingCol = new LinkedHashMap<>();
            Map<String, List<Object>> readableMapCommit = new LinkedHashMap<>();
            List<Object> listOfVal = new LinkedList<>();
            boolean bugFix = false;
            boolean bugVa = false;
            boolean fileCommitted = false;

            Map<String, Map<String, List<Object>>> output = new LinkedHashMap<>();
            Map<String, List<Object>> outCol = new LinkedHashMap<>();
            List<Object> col = new LinkedList<>();

            //try {

             /*   fis = new FileInputStream("AccumulatedStrengthWithDecay.xlsx");
                wb = WorkbookFactory.create(fis);
                sh = wb.getSheet("Additional_Parameters");*/

            for (String row : finalStrength.keySet()) {
                //System.out.println("Hey Miss");
                finalColumnStrength = finalStrength.get(row);
                for (String column : finalColumnStrength.keySet()) {
                    value = finalColumnStrength.get(column);
                    commitId = dateToCommit.get(column);
                        /*Row rowExcel = sh.createRow(i);
                        Cell cell = rowExcel.createCell(0);
                        cell.setCellValue(row);
                        cell = rowExcel.createCell(1);
                        cell.setCellValue(column);
                        cell = rowExcel.createCell(2); //fix for commit Id
                        cell.setCellValue(commitId); //fix for commitId
                        cell = rowExcel.createCell(3);*/ //fix for bug fixing
                    {
                        redableMappingCol = readableMapping.get(row);
                        for (String key : redableMappingCol.keySet()) {
                            readableMapCommit = redableMappingCol.get(key);
                            for (String comm : readableMapCommit.keySet()) {
                                if (comm.equals(commitId)) {
                                    fileCommitted = true;
                                    listOfVal = readableMapCommit.get(comm);
                                    bugVa = (boolean) listOfVal.get(15);

                                }
                                    /*else
                                    {
                                        fileCommitted=false;
                                    } */
                            }

                        }

                    }
//                        cell.setCellValue(bugFix); //fix for bug fixing
//                        cell = rowExcel.createCell(4);
//                        cell.setCellValue(fileCommitted);
//                        cell = rowExcel.createCell(5);
//                        cell.setCellValue(value);
                    col.add(bugVa);
                    col.add(fileCommitted);
                    col.add(value);
                    outCol.put(column, col);
                    bugVa = false;
                    fileCommitted = false;
                    //fos = new FileOutputStream("AccumulatedStrengthWithDecay.xlsx");
                    //wb.write(fos);
                    col = new LinkedList<>();
                    i++;
                }
                output.put(row, outCol);
                outCol = new LinkedHashMap<>();
                i++;
            }
            /*} catch (Exception e) {
                System.out.println("Data is not inserted Properly into the excel" + e.getMessage() + e.getStackTrace().toString()) ;
            } finally {
                System.out.println("Successfully Inserted Data into excel");
               *//* wb.close();
                fos.close();
                fis.close();*//*
            }*/
           /*System.out.println("Here it is FIXXX ");
            output.entrySet().stream().forEach(e->System.out.print(" , " + e));*/
        }

    }


    /*
    This function is responsible to calculate the slope with given segment width
    */

    public void calculateSlopeUsingSimpleRegression(int segmentWidth) {
        Map<String, Float> accStrength;
        Map<String, Integer> segmentWidths = getSegmentWidth();
        int segmentWid = 0;
        Map<String, Map<String, Float>> acStren = getAccumulatedSt();
        Map<String, Map<String, Map<String, Double>>> slope = new LinkedHashMap<>();
        Map<String, Map<String, Double>> slopeStartDate = new LinkedHashMap<>();
        Map<String, Double> slopeEndDate = new LinkedHashMap<>();
        List<String> timeStamp = new LinkedList<>();
        int max = 0;
        max = timeStamp.size();
        int i = 0;
        int segmentI = 0;
        List<Double> xList = new LinkedList<>();
        List<Double> yList = new LinkedList<>();
        Double[][] xy = new Double[0][];
        for (String file : acStren.keySet()) {
            {
                SimpleRegression simpleRegression = new SimpleRegression(true);
                accStrength = acStren.get(file);
                timeStamp.addAll(accStrength.keySet());
                Collections.sort(timeStamp);
                max = timeStamp.size();
                i = 0;
                segmentWid = segmentWidths.get(file);
                while (i < max) {

                    segmentI = i + segmentWid;
                    if (!(segmentI < max)) {
                        segmentI = max;
                    }

                    while (i < segmentI) {

                        xList.add((double) accStrength.get(timeStamp.get(i)));
                        yList.add(Double.valueOf(i));
                        simpleRegression.addData(i, (double) accStrength.get(timeStamp.get(i)));
                        i++;
                    }
                    /* *//*System.out.println("XLIST");
                    xList.stream().forEach(e-> System.out.print(" , " + e) );
                    System.out.println();
                    System.out.println("YList");
                    yList.stream().forEach(e-> System.out.print(" , " + e) );


                    System.out.println(" The slope" + simpleRegression.getSlope());*/
                    slopeEndDate.put(timeStamp.get(i - 1), simpleRegression.getSlope());
                    slopeStartDate.put(timeStamp.get(i - segmentWid), slopeEndDate);
                    slopeEndDate = new LinkedHashMap<>();
                    simpleRegression.clear();
                    xList = new LinkedList<>();
                    yList = new LinkedList<>();
                    simpleRegression.clear();


                }
                slope.put(file, slopeStartDate);
                slopeStartDate = new LinkedHashMap<>();
                timeStamp = new LinkedList<>();

            }
        }
        System.out.println("Hash Map Slope");
        slope.entrySet().stream().forEach(e -> System.out.print(" , " + e));
    }

    /*
    This function will provide the accumulatedStrength of providedFileId
     */
    public Map<String, Float> getAccumulatedStrength(String fileId) {
        System.out.println("RIA");
        System.out.println(" INSS" + dataManipulateExcel.getAccumulatedSt());
        Map<String, Map<String, Float>> acc = dataManipulateExcel.getAccumulatedSt();
        Map<String, Float> accField = acc.get(fileId);
        return accField;
    }


    public void getSlopeBasedOnfileID(String fileId) {
        Map<String, List<Map<String, Map<String, Float>>>> fileSlope = dataManipulateExcel.getSlopes();
        List<Map<String, Map<String, Float>>> slope = fileSlope.get(fileId);
        System.out.println("Slope of file ID " + fileId);
        slope.stream().forEach(e -> System.out.println(" , " + e));

    }


    /*
    finalStrengthSingleFile()-This function will calculate the strength of file even if the file is
    committed alone, for initial phase if file is committed alone we are just tracking the commit from the
    previous value without any decay.
    Created to resolve the : Bug 001: Committed as part of the file that is committed alone.
     */
    public void finalStrengthSingleFile() {
        Map<String, Map<String, Float>> finalStrength = getAccumulatedSt();
        Table<String, String, Map<Integer, List<Object>>> readMappingSame = readingStrategyImp.getReadableMappingSameN();
        Map<String, Map<String, Map<Integer, List<Object>>>> readMapMap = readMappingSame.rowMap();
        Map<Integer, List<Object>> readMapVal = new LinkedHashMap<>();
        Map<String, Float> finalStrVal = new LinkedHashMap<>();
        Map<String, List<String>> readMapString = new LinkedHashMap<>();
        List<String> readMap = new LinkedList<>();
        List<String> finalStr = new LinkedList<>();
        Map<String, Float> finalMap = new LinkedHashMap<>();
        Map<String, List<String>> yearMapAloneSame = new LinkedHashMap<>();
        List<String> yearMapAloneListSame = new LinkedList<>();
        Map<String, Float> finalSubStrengthSorted = new TreeMap<>();
        Map<String, Map<String, Float>> finalStrengthSorted = new TreeMap<>();
        if (!finalStrength.isEmpty()) {
            for (String rowStr : finalStrength.keySet()) {
                if (readMappingSame.containsRow(rowStr)) {
                    readMapVal = readMappingSame.get(rowStr, rowStr);
                    for (Integer key : readMapVal.keySet()) {
                        readMap.add((String) readMapVal.get(key).get(10));
                    }
                    Collections.sort(readMap);
                    finalStrVal = finalStrength.get(rowStr);
                    finalStr.addAll(finalStrVal.keySet());
                    TreeSet<String> finalSetStr = new TreeSet<>();
                    Collections.sort(finalStr);
                    finalSetStr.addAll(finalStr);
                    Iterator readMapItr = readMap.iterator();
                    while (readMapItr.hasNext()) {
                        String val = (String) readMapItr.next();
                        String newVal = finalSetStr.floor(val);
                        Float str = finalStrVal.get(newVal);
                        finalStrVal.putIfAbsent(val, str);


                    }

                    finalStrength.putIfAbsent(rowStr, finalStrVal);
                    yearMapAloneSame.put(rowStr, readMap);
                    finalMap = new LinkedHashMap<>();
                }

                readMap = new LinkedList<>();
                finalStr = new LinkedList<>();
            }
        }

        for (String key : finalStrength.keySet()) {
            finalSubStrengthSorted.putAll(finalStrength.get(key));
            finalStrengthSorted.put(key, finalSubStrengthSorted);
            finalSubStrengthSorted = new TreeMap<>();
        }
        setYearMapGlobalSame(yearMapAloneSame);
        System.out.println("Final Sorted Strength Including Sorted file");
        finalStrengthSorted.entrySet().forEach(e -> System.out.println(" , " + e));
        setAccumulatedSt(finalStrengthSorted); //Setting the final Strength of file including the file that is committed alone
        /*  Start:Bug 003: Explicity using garbage Collector */
        finalStrength = null;
        readMappingSame = null;
         readMapMap = null;
         readMapVal = null;
        finalStrVal = null;
       readMapString =null;
       readMap =null;
        finalStr = null;
        finalMap = null;
         yearMapAloneSame = null;
         yearMapAloneListSame = null;
         finalSubStrengthSorted = null;
         finalStrengthSorted = null;



        /* End: Bug 003: Explicity using garbage Collector */



    }
    /*
    createVector() function is responsible for creating vector of desired segment. The segment width
    is obtained from createSegmentWidth function.
     */

    public void createVector() {
        Map<String, Float> accStrength;
        HashMap<String, String> dictionaryString= readingStrategyImp.getDictionaryString(); //Bug 002: Committed as part of commitID to be added in the sample data
        Map<String, Integer> segmentWidths = getSegmentWidth();
        Map<String, Map<String, Float>> acStren = getAccumulatedSt();
        List<String> timeStamp = new LinkedList<>();
        List<Object> xList = new LinkedList<>();
        Map<String, Map<String, List<Object>>> vectorMap = new LinkedHashMap<>();
        Map<String, List<Object>> vectorSubMap = new LinkedHashMap<>();
        Map<String, List<List<Object>>> vectorFinalMap = new LinkedHashMap<>();
        List<List<Object>> vectorDoubleList = new LinkedList<>();
        String dateVal = "";
        int max = 0;
        int i = 0;
        int segmentI = 0;
        int segmentWid = 0;
        max = timeStamp.size();
        Map<String, Map<String, Boolean>> booleanFix = readingStrategyImp.getReadableBugFixing();
        Map<String, Boolean> booleanSubFix = new LinkedHashMap<>();
        String anDate = "";

        for (String file : acStren.keySet()) {
            {

                accStrength = acStren.get(file);
                timeStamp.addAll(accStrength.keySet());
                Collections.sort(timeStamp);
                max = timeStamp.size();
                i = 0;
                segmentWid = segmentWidths.get(file);

                while (i < max) {

                    segmentI = i + segmentWid;
                    if (!(segmentI < max)) {
                        segmentI = max;
                    }
                    dateVal = timeStamp.get(i);
                    xList.add(dateVal);
                    //Start: Bug 002: Committed as part of commitID to be added in the sample data
                    if(dictionaryString.containsKey(dateVal))
                        xList.add(dictionaryString.get(dateVal));
                    //End:  Bug 002: Committed as part of commitID to be added in the sample data
                    while (i < segmentI) {
                        if(accStrength.containsKey(timeStamp.get(i))) {
                            xList.add((double) accStrength.get(timeStamp.get(i)));
                            i++;
                        }
                        else
                        {
                            System.out.println(i);
                            xList.add((double) 0);
                            i++;
                        }
                    }
                    if (i < max) {
                        xList.add((double) accStrength.get(timeStamp.get(i))); //First Date value of next segemnt
                        anDate = timeStamp.get(i);
                        xList.add(anDate);
                        //Start: Bug 002: Committed as part of commitID to be added in the sample data
                        if(dictionaryString.containsKey(anDate))
                            xList.add(dictionaryString.get(anDate));
                        //End:  Bug 002: Committed as part of commitID to be added in the sample data
                        booleanSubFix = booleanFix.get(file);

                        TreeSet<String> dateVals = new TreeSet<>();
                        dateVals.addAll(booleanSubFix.keySet());
                        anDate = dateVals.ceiling(anDate);
                        xList.add(booleanSubFix.get(anDate));

                    } else {
                        xList.add((double) -1);
                        xList.add(null);
                    }

                    vectorSubMap.put(dateVal, xList);
                    vectorDoubleList.add(xList);
                    xList = new LinkedList<>();
                }
                vectorFinalMap.put(file, vectorDoubleList);
                vectorMap.put(file, vectorSubMap);
                timeStamp = new LinkedList<>();
                vectorSubMap = new LinkedHashMap<>();
                vectorDoubleList = new LinkedList<>();


            }
        }
        setVectorMapGlobal(vectorMap);
        setVectorFinalMapGlobal(vectorFinalMap);
        System.out.println("Vector Map");
        vectorMap.entrySet().stream().forEach(e -> System.out.println(" , " + e));
        System.out.println("Vector Final Map");
        vectorFinalMap.entrySet().stream().forEach(e -> System.out.println(" ," + e));

    }

    /*CreateSegmentWidth() function is created to estimate the width of segment based on the mean of
    file is committed between the intervals, like t1, t4, t8, t12. Whereas, global clock tick from t1,
    t2, t3, t4, t5, t6, t7... t12.
    TD1: t4-t1
    TD2: t8-t4
    TD3: t12-t8
    Mean of (TD1, TD2, TD3) will be the segment width.
    */
    public void createSegmentWidth() throws ParseException {
        Map<String, List<String>> yearMapPairSame = getYearMapPair();
        Map<String, List<String>> yearMapAloneSame = getYearMapGlobalSame();
        List<String> commitYear = new LinkedList<>();
        Map<String, List<String>> commitYearMap = new LinkedHashMap<>();

        for (String row : yearMapPairSame.keySet()) {
            for (String col : yearMapAloneSame.keySet()) {
                if (row.equals(col)) {
                    commitYear.addAll(yearMapAloneSame.get(row));
                }

            }

            commitYear.addAll(yearMapPairSame.get(row));
            Collections.sort(commitYear);
            commitYearMap.put(row, commitYear);
            commitYear = new LinkedList<>();

        }

       /* System.out.println(" For Pair");
        yearMapPairSame.entrySet().stream().forEach(e-> System.out.print(e));
        System.out.println(" For Alone");
        yearMapAloneSame.entrySet().stream().forEach(e-> System.out.print(e));
        System.out.println(" Together");
        commitYearMap.entrySet().stream().forEach(e-> System.out.print(e));*/

        String prev = "";
        String next = "";
        List<Long> meanString = new LinkedList<>();
        int sub = 0;
        Map<String, Integer> meanMap = new LinkedHashMap<>();
        Date prevDate = null;
        Date nextDate = null;
        long difference_In_Time = 0;
        long difference_In_Hours = 0;
        Double avg = 0.0;


        for (String commitKey : commitYearMap.keySet()) {
            commitYear.addAll(commitYearMap.get(commitKey));
            Iterator<String> commitItera = commitYear.iterator();
            if (commitYear.size() > 1) {
                for (int i = 0; i < commitYear.size(); i++) {
                    prev = commitYear.get(i);
                    i++;
                    if (i < commitYear.size()) {
                        next = commitYear.get(i);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        prevDate = sdf.parse(prev);
                        nextDate = sdf.parse(next);
                        difference_In_Time = nextDate.getTime() - prevDate.getTime();
                        difference_In_Hours = (difference_In_Time
                                / (1000 * 60 * 60))
                                % 24;
                        //System.out.println(" row " + commitKey +" prev " + prevDate + " next " + nextDate  + "difference_In_Hours " + difference_In_Hours);

                    }
                    i--;
                    meanString.add(difference_In_Hours);
                    difference_In_Hours = 0;

                }
                avg = meanString.stream().mapToLong(i -> i).average().getAsDouble();

            }

            meanMap.put(commitKey, (int) Math.round(avg));
            commitYear = new LinkedList<>();
            meanString = new LinkedList<>();
        }


        System.out.println(" Mean Map ");
        meanMap.entrySet().stream().forEach(e -> System.out.print(" , " + e));
        setSegmentWidth(meanMap);
    }

    /*createRandomSample will create the starting point of the samples and put it in the list and make
    it available for machine learning algorithm.
    */
    public void createRandomSample() throws IOException {
        Map<String, Map<String, List<Object>>> vectorMap = getVectorMapGlobal();
        Map<String, List<Object>> vectorListMap;
        Map<String, List<List<Object>>> vectorFinalMap = getVectorFinalMapGlobal();
        List<List<Object>> vectorFinalList = new LinkedList<>();
        Map<String, List<List<Object>>> vectorSampling = new LinkedHashMap<>();
        List<List<Object>> vectorSamplingList = new LinkedList<>();
        List<String> vectorList = new LinkedList<>();
        Map<String, List<List<Object>>> vectorCleanFinalMap = new LinkedHashMap<>();
        List<List<Object>> vectorCleanSamplingList = new LinkedList<>();
        List<Object> vectorCleanList = new LinkedList<>();
        Random random = new Random();
        int value;
        int size = 0;
        int noOfSample = 5;
        int i = 0;
        int sub = 0;

        for (String file : vectorMap.keySet()) {
            vectorListMap = vectorMap.get(file);
            SimpleRegression simpleRegression = new SimpleRegression(true);
            if (!vectorListMap.isEmpty()) {
                vectorList.addAll(vectorListMap.keySet());
                size = vectorList.size();
                vectorFinalList = vectorFinalMap.get(file);
                // Obtain a number between [0 - 49], if random.nextInt(50).
                if (noOfSample < vectorList.size()) {
                    value = random.nextInt(vectorList.size());

                    sub = (size - noOfSample);
                    boolean k = (value <= (size - noOfSample));

                    while (value > (size - noOfSample)) {
                        value = random.nextInt(vectorList.size());

                    }
                    while (i < noOfSample) {
                        //System.out.println(" I don't want you " + value);
                        vectorSamplingList.add(vectorFinalList.get(value));

                        value++;
                        i++;
                    }
                } else {
                    vectorSamplingList.addAll(vectorFinalList);
                }
                //LOGIC TO ADD SLOPE
                for (int k = 0; k < vectorSamplingList.size(); k++) {
                    List<Object> abc = vectorSamplingList.get(k);

                    for (int m = 2; m < abc.size() - 3; m++) //Bug 002: Committed as part of commitID to be added in the sample data
                    {
                        simpleRegression.addData(m, (double) abc.get(m));

                    }
                    double slope = simpleRegression.getSlope();
                    vectorSamplingList.get(k).add(slope);
                    if (slope <= 0.0) {
                        vectorSamplingList.get(k).add("D");
                    } else {
                        vectorSamplingList.get(k).add("U");
                    }

                    simpleRegression.clear();
                    abc = new LinkedList<>();
                }
                //LOGIC TO ADD SLOPE


                vectorSampling.put(file, vectorSamplingList);
                vectorSamplingList = new LinkedList<>();
                vectorFinalList = new LinkedList<>();
                vectorList = new LinkedList<>();
                i = 0;
            }
        }
        System.out.println("Vector Sampling");
        vectorSampling.entrySet().stream().forEach(e -> System.out.println("  , " + e));


        for (String file : vectorSampling.keySet()) {
            List<List<Object>> vectorSampleDoubleList = new LinkedList<>();
            vectorSampleDoubleList.addAll(vectorSampling.get(file));
            for (int is = 0; is < vectorSampleDoubleList.size(); is++) {
                List<Object> insideBoolean = new LinkedList<>();
                insideBoolean.addAll(vectorSampleDoubleList.get(is));
                vectorCleanList.add(insideBoolean.get(0));
                vectorCleanList.add(insideBoolean.get(1));//Bug 002: Committed as part of commitID to be added in the sample data
                vectorCleanList.add(insideBoolean.get(insideBoolean.size() - 2)); //For slope value
                vectorCleanList.add(insideBoolean.get(insideBoolean.size() - 1)); //For slope notation
                vectorCleanList.add(insideBoolean.get(insideBoolean.size() - 3)); //For bugFixing Commit or not
                vectorCleanList.add(insideBoolean.get(insideBoolean.size() - 5)); //For commit ID //Bug 002: Committed as part of commitID to be added in the sample data
                vectorCleanList.add(insideBoolean.get(insideBoolean.size() - 4)); //For commitDate corresponsing to commit ID
                vectorCleanSamplingList.add(vectorCleanList);
                vectorCleanList = new LinkedList<>();
            }

            vectorCleanFinalMap.put(file, vectorCleanSamplingList);
            vectorCleanSamplingList = new LinkedList<>();
        }



        FileInputStream fis = null;
        FileOutputStream fos = null;
        Workbook wb = null;
        Sheet sh = null;
        try {
            fis = new FileInputStream("Output.xlsx");
            wb = WorkbookFactory.create(fis);
            sh = wb.getSheet("kmymoney_output");

            int k = 1;
            for (String row : vectorCleanFinalMap.keySet()) {
                List<List<Object>> vectorCl = vectorCleanFinalMap.get(row);
                Iterator<List<Object>> vectorClIter = vectorCl.iterator();

                Row rowExcel = sh.createRow(k);
                k++;
                String finalValues = "";
                while (vectorClIter.hasNext()) {
                    List<Object> vecListIt = vectorClIter.next();
                    Iterator<Object> vecItr = vecListIt.iterator();
                    String values = "";
                    while (vecItr.hasNext()) {
                        values = values + vecItr.next() + ",";

                    }
                    values = values.substring(0, values.length() - 1);
                    values = "[" + values + "]";

                    finalValues = finalValues + values + ",";
                }
                finalValues = finalValues.substring(0, finalValues.length() - 1);
                finalValues = "[" + finalValues + "]";
                //System.out.println(" row " + row + " finalValues " + finalValues);
                Cell cell = rowExcel.createCell(0);
                cell.setCellValue(row);
                Cell cell1  = rowExcel.createCell(1);
                cell1.setCellValue(finalValues);
                fos = new FileOutputStream("Output.xlsx");
                wb.write(fos);
            }
        } catch (Exception e) {
          System.out.println("Unable to Insert");
        }finally{
            fis.close();
            wb.close();
        }


    }

    public Map<String, Map<String, Float>> getAccumulatedSt() {
        return accumulatedSt;
    }

    public void setAccumulatedSt(Map<String, Map<String, Float>> accumulatedSt) {
        this.accumulatedSt = accumulatedSt;
    }

    public Map<String, List<Map<String, Map<String, Float>>>> getSlopes() {
        return slopes;
    }

    public void setSlopes(Map<String, List<Map<String, Map<String, Float>>>> slopes) {
        this.slopes = slopes;
    }

    public List<String> getCommitSchedule() {
        return commitSchedule;
    }

    public void setCommitSchedule(List<String> commitSchedule) {
        this.commitSchedule = commitSchedule;
    }

    public Map<String, List<String>> getYearMapGlobalSame() {
        return yearMapGlobalSame;
    }

    public void setYearMapGlobalSame(Map<String, List<String>> yearMapGlobalSame) {
        this.yearMapGlobalSame = yearMapGlobalSame;
    }

    public Map<String, List<String>> getYearMapPair() {
        return yearMapPair;
    }

    public void setYearMapPair(Map<String, List<String>> yearMapPair) {
        this.yearMapPair = yearMapPair;
    }

    public Map<String, Integer> getSegmentWidth() {
        return segmentWidth;
    }

    public void setSegmentWidth(Map<String, Integer> segmentWidth) {
        this.segmentWidth = segmentWidth;
    }


    public Map<String, Map<String, List<Object>>> getVectorMapGlobal() {
        return vectorMapGlobal;
    }

    public void setVectorMapGlobal(Map<String, Map<String, List<Object>>> vectorMapGlobal) {
        this.vectorMapGlobal = vectorMapGlobal;
    }

    public Map<String, List<List<Object>>> getVectorFinalMapGlobal() {
        return vectorFinalMapGlobal;
    }

    public void setVectorFinalMapGlobal(Map<String, List<List<Object>>> vectorFinalMapGlobal) {
        this.vectorFinalMapGlobal = vectorFinalMapGlobal;
    }



}




