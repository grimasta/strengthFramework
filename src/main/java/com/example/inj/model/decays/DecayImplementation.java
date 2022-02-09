package com.example.inj.model.decays;

import com.example.inj.model.Strength.SingleFileStrength;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DecayImplementation {


    Logger logger = LoggerFactory.getLogger(DecayImplementation.class);

    Map<String, Map<String, Float>> accumulatedSt;

    public Map<String, Map<String, Float>> getAccumulatedSt() {
        return accumulatedSt;
    }

    public void setAccumulatedSt(Map<String, Map<String, Float>> accumulatedSt) {

        this.accumulatedSt = accumulatedSt;
    }

    public void implementDecayInStrengthSecond(Map<String, Map<String, Float>> globalDecay, Map<String, Map<String, Float>> accumulatedStrength) {
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
        System.out.println("implementDecayInStrengthSecond");
        setAccumulatedSt(finalStrength);
 /*       System.out.println("Implement Global Decay");
        finalStrength.get("caa7faee-1ed0-11eb-98c6-482ae32cf5b4").entrySet().forEach(e->System.out.print(e));*/

        logger.info("Decay Implementation");
//        logger.info(finalStrength.toString());

    }
}
