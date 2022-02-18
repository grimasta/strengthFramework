package com.example.inj.model.strength.accumulators;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.inj.model.storage.DataRepository;
import com.example.inj.model.strength.accumulators.strategies.IStrengthAccumulatorStrategy;

public class AccumulatedStrength implements IStrengthAccumulator {

    Logger logger = LoggerFactory.getLogger(AccumulatedStrength.class);
    private DataRepository dataRepository;
    Map<String, Map<String, Float>> accumulatedStrength;
    private IStrengthAccumulatorStrategy theStrategy = null;
    
    public AccumulatedStrength() {
    	dataRepository = DataRepository.getInstance();
    }

    public void setStrategy(IStrengthAccumulatorStrategy theStrategy) {
    	this.theStrategy = theStrategy;
    }
    
    /*
    AccumulatedStrength: Function will calculate the total strength of file A, when it is committed with B,C,D,E,F along with decay.
     */
    
    @Override
	public void calculateAccumulatedStrength(Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMap, Map<String, Map<Integer, Map<String, Integer>>> excelYearMap) {
        Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMapSecond = new LinkedHashMap<>();
        pairStrengthMapSecond.putAll(pairStrengthMap);

        Map<String, Map<String, Float>> strategyResults = theStrategy.calculate(pairStrengthMap, excelYearMap, dataRepository.getPairLevelDecayMap());
        
        System.out.println("Overall Strength for Same file");
        /*Start: Bug 003: Explicity using garbage Collector */
        
        /*End:  Bug 003: Explicity using garbage Collector */

        dataRepository.setAccumulatedStrength(strategyResults);
      /* System.out.println("Before Decay caa7faee-1ed0-11eb-98c6-482ae32cf5b4");
       overallStrengthDateMap.get("caa7faee-1ed0-11eb-98c6-482ae32cf5b4").entrySet().forEach(e-> System.out.print(e));*/




    }
}
