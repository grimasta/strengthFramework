package com.example.inj.model.cases;

//OLD refer WithoutCommitPrime
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import com.example.inj.model.storage.DataRepository;

import javafx.util.Pair;
public class TimeDifference {

    private Map<String, Map<String, Map<Integer, Map<String, Integer>>>> timeDifference;
    private DataRepository dataRepository;
    
    public TimeDifference() {
    	dataRepository = DataRepository.getInstance();
    }
    
    public Map<String, Map<String, Map<Integer, Map<String, Integer>>>> getTimeDifference() {
        return timeDifference;
    }

    public void setTimeDifference(Map<String, Map<String, Map<Integer, Map<String, Integer>>>> timeDifference) {
        this.timeDifference = timeDifference;
    }

    /* Case-3 Time difference when A&B are co-commited in a consecutive commit/ Count Difference
timeDifference function will return the time difference between each consecutive commit of A&B.
TimeDifference-> How many times the A has been committed without B. --
*/
    public void timeDifferenceExcel(Pair<Map<String, Map<String, Map<Integer, Map<String, Integer>>>>, Map<String, Map<Integer, Map<String, Integer>>>> mapPair) {
        Map<String, Map<String, Map<Integer, Map<String, Integer>>>> excelxyzz = new LinkedHashMap<>();
        
        Map<Integer, Map<String, Integer>> timeDifferenceAggregate = new TreeMap<Integer, Map<String, Integer>>();
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
                            ListIterator<String> iteratorAB = listABKeySet.listIterator();


                            while (iteratorAB.hasNext()) {
                                String mapElementPreviousKey = iteratorAB.next().toString();

                                if (iteratorAB.hasNext()) {
                                    String mapElementNextKey = iteratorAB.next().toString();
                                    iteratorAB.previous();
                                    for (String keys : yearMapExcel2.keySet()) {
                                        Map<String, Integer> timeDifference = new TreeMap<>();
                                        if ((keys.compareTo(mapElementPreviousKey)) > 0 && (keys.compareTo(mapElementNextKey) < 0)) {
                                            timeDifference.put(keys, yearMapExcel2.get(keys));
                                            resultExcel.put(key, timeDifference);
                                        }
                                    }

                                    if (!resultExcel.isEmpty()) {
                                        for (int keys : resultExcel.keySet()) {
                                            timeDifferenceAggregate.put(keys, resultExcel.get(keys));

                                        }

                                    }
                                } else {
                                    break;
                                }
                            }
                            iteratorAB = null; //Bug 003: Explicity using garbage Collector
                        }
                    }
                }
                timeDifferenceAggregateColumn.put(column, timeDifferenceAggregate);

                timeDifferenceAggregate = new LinkedHashMap<>();
            }

            timeDifferenceAggregate2.put(row, timeDifferenceAggregateColumn);
            timeDiff3.putAll(timeDifferenceAggregate2);
        }

        /*Start:  Bug 003: Explicity using garbage Collector */
        mapPair = null;
        excelxyzz = null;
        timeDifferenceAggregate = null;
        timeDifferenceAggregateColumn = null;
        excelxyzz = null;
        excelxyzz1 = null;
        /*End:  Bug 003: Explicity using garbage Collector */
        System.out.println("Time Difference Excel");
        timeDiff3.entrySet().forEach(e->System.out.println(e));
        dataRepository.setTimeDifference(timeDifferenceAggregate2);
        //timeDifferenceAggregate2.get("caa84917-1ed0-11eb-99c6-482ae32cf5b4").entrySet().forEach(e->System.out.print(e));

    }

}
