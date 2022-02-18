package com.example.inj.model.decays;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.inj.model.storage.DataRepository;

public class GlobalDecay {

    private Map<String, Map<String, Float>> globalDecay;
    private DataRepository dataRepository;
    Logger logger = LoggerFactory.getLogger(DecayImplementation.class);

    public GlobalDecay() {
    	dataRepository = DataRepository.getInstance();
    }
    
    public Map<String, Map<String, Float>> getGlobalDecay() {
        return globalDecay;
    }

    public void setGlobalDecay(Map<String, Map<String, Float>> globalDecay) {
        this.globalDecay = globalDecay;
    }

    /*
        Na-> Total Number of commit of A
        N-> Total Number of  commit
        Time Gap-> Number of commit passed(Last time file A is committed- Current Commit)
         Time Gap->Number of commit has passed since A is committed
        Math.exp(Na/(N*(Time Gap)))
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
    public void calculateGlobalDecay(List<String> commitSchedule, Map<String, Map<String, Float>> pairStrengthMap) {
        Iterator<String> commitScheduleIterator;
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
        Map<String,Map<String, Boolean>> booleanMapFix= dataRepository.getReadableBugFixing();



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

                        if(booleanMapFix.containsKey(a) && booleanMapFix.get(a).containsKey(iteratorValue) && booleanMapFix.get(a).get(iteratorValue) ) {
                            decay = decay/1.7f;
                           /* System.out.println("Decay More  " + decay );
                            System.exit(0);*/
                        }
                        else
                        {
                            decay=decay/1.5f;
                            //System.out.println("Decay Less  " + decay + " iteratorValue " + iteratorValue);
                        }
                        //logger.info("Decay " + decay + " index " + index + " currentCommit " + currentCommit + " timeElapse " + timeElapse);
                        globalDecayColumn.put(iteratorValue, decay);

                    } else {
                        globalDecayColumn.put(iteratorValue, 0.0f);
                    }

                    //End:Calculation of global decay
                } else {
                    globalDecayColumn.put(iteratorValue, 0.0f);
                }
            }
            commitScheduleIterator = null; //Bug 003: Explicity using garbage Collector
            globalDecay.put(a, globalDecayColumn);
            globalDecayColumn = new LinkedHashMap<>();
            listSet = new LinkedList<>();
            pairSet = new TreeSet<>();
            pairStrengthRevise = new LinkedHashMap<>();
        }
/*        System.out.println("Calculate Global Decay");
        globalDecay.get("caa7faee-1ed0-11eb-98c6-482ae32cf5b4").entrySet().forEach(e->System.out.print(e));*/
        dataRepository.setGlobalDecay(globalDecay);

       /* booleanMapFix.entrySet().forEach(e-> System.out.print(  e));

        System.out.println("Here is pair strength map");
        pairStrengthMap.entrySet().forEach(e-> System.out.print(e));*/
        //logger.info(globalDecay.toString());
        //logger.info(globalDecay.toString());
        //System.exit(0);
    }

}
