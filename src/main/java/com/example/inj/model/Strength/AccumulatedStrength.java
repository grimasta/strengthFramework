package com.example.inj.model.Strength;

import com.example.inj.model.decays.PairLevelDecay;
import com.example.inj.readingStrategy.strategy.ReadingStrategy;
import com.example.inj.readingStrategy.strategy.ReadingStrategyImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
@Component
public class AccumulatedStrength {

    Logger logger = LoggerFactory.getLogger(AccumulatedStrength.class);
    ReadingStrategy readingStrategy;
    @Autowired
    public void setReadingStrategy(ReadingStrategyImp readingStrategy) {
        this.readingStrategy = ReadingStrategyImp.getInstance();
    }

    Map<String, Map<String, Float>> accumulatedStrength;

    PairLevelDecay pairLevelDecay;

    PairStrength pairStrength;

    @Autowired
    public void setPairStrength(PairStrength pairStrength) {
        this.pairStrength = pairStrength;
    }

    @Autowired
    public void setPairLevelDecay(PairLevelDecay pairLevelDecay) {
        this.pairLevelDecay = pairLevelDecay;
    }

    public Map<String, Map<String, Float>> getAccumulatedStrength() {
        return accumulatedStrength;
    }

    public void setAccumulatedStrength(Map<String, Map<String, Float>> accumulatedStrength) {
        this.accumulatedStrength = accumulatedStrength;
    }


    /*
    AccumulatedStrength: Function will calculate the total strength of file A, when it is committed with B,C,D,E,F along with decay.
     */

    public void calculateAccumulatedStrength(Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMap, Map<String, Map<Integer, Map<String, Integer>>> excelYearMap) {
        Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMapSecond = new LinkedHashMap<>();
        pairStrengthMapSecond.putAll(pairStrengthMap);

        pairLevelDecay.globalDecay();

        Map<String, Map<String, Map<String, Double>>> globalDecay = pairLevelDecay.getPairLevelDecayMap();

        Double decay = 0.0;
        List<String> acceptString = null;
        int index = 0;
        Map<String, Map<String, Float>> overallStrengthDateMap = new HashMap<>();
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
                        acceptString = new LinkedList<>();
                        for (String columnKey : columnPair.keySet()) {
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
                                        }

                                    }
                                }
                                columnPairListIterator = null; //Bug 003: Explicity using garbage Collector
                            }
                        }

                        List<String> validValue = new LinkedList<>();
                        if (!acceptString.isEmpty()) {
                            {

                                for (String rowCount : countColumn.keySet()) {
                                    if (!(acceptString.contains(rowCount)) && (countColumn.get(rowCount) == 1)) {
                                        validValue.add(rowCount);
                                    }
                                }

                            }
                            Iterator validValueIterator = validValue.listIterator();
                            index = datesMap.indexOf(firstDate);
                            if (index > 0) {

                                while (validValueIterator.hasNext()) {
                                    String val = (String) validValueIterator.next();
                                    float decaySt = 0;
                                    float st = 0;
                                    Iterator pairIteratorTry = pairStrengthMap.get(row).get(val).iterator();
                                    Map<Integer, Map<String, Float>> pairMapping;
                                    Set<String> pairTry = new TreeSet<>();
                                    while (pairIteratorTry.hasNext()) {
                                        pairMapping = (Map<Integer, Map<String, Float>>) pairIteratorTry.next();
                                        for (int i : pairMapping.keySet()) {
                                            Map<String, Float> pairs = pairMapping.get(i);
                                            for (String is : pairs.keySet()) {
                                                pairTry.add(is);

                                            }
                                        }
                                    }
                                    pairIteratorTry = null; //Bug 003: Explicity using garbage Collector
                                    String prev = ((TreeSet<String>) pairTry).floor(firstDate);
                                    if (prev != null) {
                                        Iterator pairIterator = pairStrengthMap.get(row).get(val).iterator();
                                        Map<Integer, Map<String, Float>> pairMappings;
                                        while (pairIterator.hasNext()) {
                                            pairMappings = (Map<Integer, Map<String, Float>>) pairIterator.next();
                                            for (int i : pairMappings.keySet()) {
                                                Map<String, Float> pairs = pairMappings.get(i);
                                                if (pairs.containsKey(prev)) {
                                                    st = pairs.get(prev);
                                                }
                                            }
                                        }
                                        pairIterator = null;//Bug 003: Explicity using garbage Collector
                                    }

                                    decay = globalDecay.get(row).get(val).get(firstDate);
                                    decaySt = (float) (st * decay);
                                    overallStrength += decaySt;

                                }
                                validValueIterator = null; //Bug 003: Explicity using garbage Collector

                            }
                        }

                        overallStrengthMap.put(firstDate, overallStrength);

                    }

                    datesMapIterator = null; //Bug 003: Explicity using garbage Collector

                    overallStrengthDateMap.put(row, overallStrengthMap);
                }

            }

        }
        System.out.println("Overall Strength for Same file");
        /*Start: Bug 003: Explicity using garbage Collector */
        countExistenceColumn = null;
        countColumn = null;
        columnPairs = null;
        pairStrengthMapSecond = null;
        globalDecay = null;
        pairStrengthMap = null;
        excelYearMap = null;
        /*End:  Bug 003: Explicity using garbage Collector */

        setAccumulatedStrength(overallStrengthDateMap);
      /* System.out.println("Before Decay caa7faee-1ed0-11eb-98c6-482ae32cf5b4");
       overallStrengthDateMap.get("caa7faee-1ed0-11eb-98c6-482ae32cf5b4").entrySet().forEach(e-> System.out.print(e));*/




    }
}
