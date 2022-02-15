package com.example.inj.model.decays;

import com.example.inj.model.strength.pair.PairStrength;
import com.example.inj.readingStrategy.strategy.IReadingStrategy;
import com.example.inj.readingStrategy.strategy.DefaultReadingStrategy;
import com.google.common.collect.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PairLevelDecay {

    IReadingStrategy readingStrategy;
    @Autowired
    public void setReadingStrategy(IReadingStrategy readingStrategy) {
        this.readingStrategy = readingStrategy;
    }

    Logger logger = LoggerFactory.getLogger(PairStrength.class);


    private Map<String, Map<String, Map<String, Double>>> pairLevelDecayMap;

    public Map<String, Map<String, Map<String, Double>>> getPairLevelDecayMap() {
        return pairLevelDecayMap;
    }

    public void setPairLevelDecayMap(Map<String, Map<String, Map<String, Double>>> pairLevelDecayMap) {
        this.pairLevelDecayMap = pairLevelDecayMap;
    }

    private Map<String, List<String>> yearMapPair;

    public Map<String, List<String>> getYearMapPair() {
        return yearMapPair;
    }

    public void setYearMapPair(Map<String, List<String>> yearMapPair) {
        this.yearMapPair = yearMapPair;
    }



    /*
    It is a Pair level decay based on below function and scenario:
    If File A is committed with File B, File C, File D, File E in Commit X at 11-Sept-2010, and again in next Commit Y at 12-Sept-2010 File A is committed only with File B, and File C then we are applying decay for File D, and File E.
    PAIR DECAY AFTER CALCULATING THE ACCUMULATED STRENGTH OF A FILE
    NOTE: WE WILL AGAIN DECAY IT AT GLOBAL LEVEL TO NORMALIZE IT FURTHER
    Pair Decay ( Multiply)->Math.exp( Number of commits passed since A&B are co-committed*0.5)
    */
    public void globalDecay() {

        Map<String, Map<String, Integer>> timeSlotAccumulate;
        Map<String, Map<String, Map<String, Integer>>> timeFull = new LinkedHashMap<>();
        Map<String, Map<String, Map<Integer, List<Object>>>> cellMap = readingStrategy.getReadableMappingFinalI().rowMap();
        Map<String, List<String>> yearMap = new LinkedHashMap<>();
        Map<String, Integer> timeSlot;
        List<String> yearCount;
        int time = 0;
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
                    {
                        timeSlot.put(dateMap, time);
                        if (!yearCount.contains(dateMap)) {
                            yearCount.add(dateMap);
                        }
                    }

                }
                listIterator = null;//Bug 003: Explicity using garbage Collector

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

                        yearCount = yearMap.get(row);
                        yearMapIterator = yearCount.listIterator();


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

                            if (timeSlotTreeSet.floor(yearMapValue) != null || timeSlotTreeSet.contains(yearMapValue)) {
                                index1 = yearCount.indexOf(compareValue) + 1;
                                index2 = yearCount.indexOf(yearMapValue) + 1;
                                finalIndex = Math.exp(-((index2 - index1) * 0.5));

                            } else {
                                val = 0;
                                finalIndex = Math.exp(val);
                            }
                            dateGlobalDecay.put(yearMapValue, finalIndex);

                        }

                        yearMapIterator = null; //Bug 003: Explicity using garbage Collector

                        columnGlobalDecay.put(column, dateGlobalDecay);

                    }

                    globalDecay.put(subRow, columnGlobalDecay);

                }

            }

        }
        setYearMapPair(yearMap);
        /*Start:  Bug 003: Explicity using garbage Collector */
        timeSlotAccumulate = null;
        timeFull = null;
        cellMap = null;
        timeSlot = null;
        yearCount = null;
        columnGlobalDecay = null;
        dateGlobalDecay = null;
        yearMapIterator = null;

        System.out.println("Inside Pair Global Decay");

        /*End:  Bug 003: Explicity using garbage Collector */
        setPairLevelDecayMap(globalDecay);
        setYearMapPair(yearMap);
        logger.info("globalDecay");
        logger.info(globalDecay.toString());
        logger.info("yearMap");
        logger.info(yearMap.toString());

    }


}
