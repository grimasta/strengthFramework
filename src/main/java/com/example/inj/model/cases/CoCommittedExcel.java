package com.example.inj.model.cases;

import com.example.inj.readingStrategy.strategy.ReadingStrategy;
import com.example.inj.readingStrategy.strategy.ReadingStrategyImp;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;
@Component
public class CoCommittedExcel {


    ReadingStrategy readingStrategy;


    @Autowired
        public void setReadingStrategy(ReadingStrategyImp readingStrategy) {
            this.readingStrategy = ReadingStrategyImp.getInstance();
        }

    Table<String, String, Map<Integer, Map<String, List<Float>>>> tableCommits;

    public Table<String, String, Map<Integer, Map<String, List<Float>>>> getTableCommits() {
        return tableCommits;
    }

    public void setTableCommits(Table<String, String, Map<Integer, Map<String, List<Float>>>> tableCommits) {
        this.tableCommits = tableCommits;
    }

    // Case 2 Start- Number of times A&B are co-committed together/ Number of time A is committed globally
    public void coCommitted() {

        Table<String, String, Map<Integer, List<Object>>> abc=readingStrategy.getReadableMappingFinalI();
        List<Float> finalList = new ArrayList<>();
        Map<String, List<Float>> finalMap = new LinkedHashMap<>();
        Table<String, String, Map<Integer, Map<String, List<Float>>>> outputTable1 = HashBasedTable.create();
        Map<Integer, Map<String, List<Float>>> finalMap2;
        int time = 0;
        Map<String, Integer> timeSlot;
        Map<Integer, Map<String, Integer>> timeSlot2;


        for (Table.Cell<String, String, Map<Integer, List<Object>>> cell : abc.cellSet()) {
            time = 0;
            finalMap2 = new LinkedHashMap<>();
            //It will have count of A&B committed together with associated key.(Equal)
            timeSlot = new LinkedHashMap<>();
            timeSlot2 = new LinkedHashMap<>();
            Iterator<Map.Entry<Integer, List<Object>>> cellInterator = cell.getValue().entrySet().iterator();


            while (cellInterator.hasNext()) {
                Map.Entry<Integer, List<Object>> insideCellIterator = cellInterator.next();
                List<Object> lst = insideCellIterator.getValue();
                timeSlot.put(lst.get(10).toString(), ++time);
                timeSlot2.put(insideCellIterator.getKey(), timeSlot); //For Excel
                timeSlot = new LinkedHashMap<>();
            }

            timeSlot = null; //Bug 003: Explicity using garbage Collector
            cellInterator = null; //Bug 003: Explicity using garbage Collector


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
                        finalList.add((((float) ParentEntry.getValue()) / ((float) currentTimeSlot.get(ParentEntry.getKey()))));
                        finalMap.put(ParentEntry.getKey(), finalList);
                        finalMap2.put(key, finalMap);
                        finalMap = new LinkedHashMap<>();
                    }
                    outputTable1.put(cell.getRowKey(), cell.getColumnKey(), finalMap2); //--Debugging
                    finalList = new ArrayList<>();
                }
            }
        }

        System.out.println("Inside Co-Committed Excel");
        /*Start:  Bug 003: Explicity using garbage Collector */
        abc = null;
        finalMap = null;
        finalMap2 = null;
        finalList = null;
        /*End:  Bug 003: Explicity using garbage Collector */
        setTableCommits(outputTable1);

    }


}
