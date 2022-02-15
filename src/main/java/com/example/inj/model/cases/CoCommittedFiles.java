package com.example.inj.model.cases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.inj.readingStrategy.strategy.IReadingStrategy;

import javafx.util.Pair;
//Case-1 Number of times the file A&B are co-committed
@Component
public class CoCommittedFiles {


    IReadingStrategy readingStrategy;

    @Autowired
    public void setReadingStrategy(IReadingStrategy readingStrategy) {
        this.readingStrategy = readingStrategy;
    }

    Pair<Map<String, Map<String, Map<Integer, Map<String, Integer>>>>, Map<String, Map<Integer, Map<String, Integer>>>> pairMaps;

    public Pair<Map<String, Map<String, Map<Integer, Map<String, Integer>>>>, Map<String, Map<Integer, Map<String, Integer>>>> getPairMaps() {
        return pairMaps;
    }

    public void setPairMaps(Pair<Map<String, Map<String, Map<Integer, Map<String, Integer>>>>, Map<String, Map<Integer, Map<String, Integer>>>> pairMaps) {
        this.pairMaps = pairMaps;
    }
   //Case-1 Number of times the file A&B are co-committed
    public void coCommitABCD() {

        Map<String, Map<String, Map<Integer, List<Object>>>> xyz = readingStrategy.getReadableMappingFinalI().rowMap();

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


        List<Map<String, Integer>> ListAB;
        List<Map<String, Integer>> ListABC;

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
        System.out.println("Co-Committed ABCD");

        setPairMaps( new Pair(commitExcelxyzs, yearMapExcel3));

    }

}
