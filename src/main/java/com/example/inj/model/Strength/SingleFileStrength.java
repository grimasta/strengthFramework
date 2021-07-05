package com.example.inj.model.Strength;

import com.example.inj.model.decays.DecayImplementation;
import com.example.inj.model.sampling.InsertExcel;
import com.example.inj.readingStrategy.strategy.ReadingStrategy;
import com.example.inj.readingStrategy.strategy.ReadingStrategyImp;
import com.google.common.collect.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SingleFileStrength {


    Logger logger = LoggerFactory.getLogger(SingleFileStrength.class);
    AccumulatedStrength accumulatedStrength;

    ReadingStrategy readingStrategy;


    DecayImplementation decayImplementation;

    InsertExcel insertExcel;

    @Autowired
    public void setInsertExcel(InsertExcel insertExcel) {
        this.insertExcel = insertExcel;
    }

    @Autowired
    public void setDecayImplementation(DecayImplementation decayImplementation) {
        this.decayImplementation = decayImplementation;
    }

    @Autowired
    public void setAccumulatedStrength(AccumulatedStrength accumulatedStrength) {
        this.accumulatedStrength = accumulatedStrength;
    }

    Map<String, List<String>> yearMapAloneSame;
    Map<String, Map<String, Float>> finalStrength;

    public Map<String, List<String>> getYearMapAloneSame() {
        return yearMapAloneSame;
    }

    public void setYearMapAloneSame(Map<String, List<String>> yearMapAloneSame) {
        this.yearMapAloneSame = yearMapAloneSame;
    }

    public Map<String, Map<String, Float>> getFinalStrength() {
        return finalStrength;
    }

    public void setFinalStrength(Map<String, Map<String, Float>> finalStrengthSorted) {
        this.finalStrength = finalStrengthSorted;
    }

    @Autowired
    public void setReadingStrategyImp() {
        this.readingStrategy = ReadingStrategyImp.getInstance();
    }

    /*
        finalStrengthSingleFile()-This function will calculate the strength of file even if the file is
        committed alone, for initial phase if file is committed alone we are just tracking the commit from the
        previous value without any decay.
        Created to resolve the : Bug 001: Committed as part of the file that is committed alone.
         */
    public void finalStrengthSingleFile() {

        Map<String, Map<String, Float>> finalStrength = decayImplementation.getAccumulatedSt();

        Table<String, String, Map<Integer, List<Object>>> readMappingSame = readingStrategy.getReadableMappingSameNI();
        Map<String, Map<String, Boolean>>  bugFixMap= readingStrategy.getReadableBugFixingI();
        Map<Integer, List<Object>> readMapVal;
        Map<String, Float> finalStrVal;
        List<String> readMap = new LinkedList<>();
        List<String> finalStr = new LinkedList<>();
        Map<String, List<String>> yearMapAloneSame = new LinkedHashMap<>();
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

                        if(bugFixMap.containsKey(rowStr) && bugFixMap.get(rowStr).containsKey(val) && bugFixMap.get(rowStr).get(val))
                        {
                            //str=str*0.001f;
                            //The strength of the file should increase if it is going to be a part of bug fixing commit
                            str=1.2f*str;
                            //str=str;
                        }
                        else
                        {
                            str=0.001f;
                        }
                        finalStrVal.putIfAbsent(val, str);


                    }
                    readMapItr = null; //Bug 003: Explicity using garbage Collector

                    finalStrength.putIfAbsent(rowStr, finalStrVal);
                    yearMapAloneSame.put(rowStr, readMap);

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

        setYearMapAloneSame(yearMapAloneSame);
        System.out.println("Final Sorted Strength Including Sorted file");


        setFinalStrength(finalStrengthSorted);
        insertExcel.insertOverallStrength();

     /*   System.out.println("FINAL SORTED STRENGTH");

        finalStrengthSorted.get("43068a9a-1ed1-11eb-9a50-482ae32cf5b4").entrySet().forEach(e->System.out.print(e));


       System.exit(0);*/
        //
       //System.exit(0);


    }
}
