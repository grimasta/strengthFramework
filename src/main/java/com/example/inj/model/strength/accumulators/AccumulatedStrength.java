package com.example.inj.model.strength.accumulators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.inj.model.decays.PairLevelDecay;
import com.example.inj.model.strength.accumulators.strategies.IStrengthAccumulatorStrategy;
import com.example.inj.model.strength.pair.PairStrength;
import com.example.inj.readingStrategy.strategy.IReadingStrategy;
@Component
public class AccumulatedStrength implements IStrengthAccumulator {

    Logger logger = LoggerFactory.getLogger(AccumulatedStrength.class);
    IReadingStrategy readingStrategy;
    Map<String, Map<String, Float>> accumulatedStrength;
    PairLevelDecay pairLevelDecay;
    PairStrength pairStrength;
    private IStrengthAccumulatorStrategy theStrategy = null;
    
    @Override
    public void setReadingStrategy(IReadingStrategy readingStrategy) {
        this.readingStrategy = readingStrategy;
    }

    public void setStrategy(IStrengthAccumulatorStrategy theStrategy) {
    	this.theStrategy = theStrategy;
    	
    }
    
   

    @Override
	
    public void setPairStrength(PairStrength pairStrength) {
        this.pairStrength = pairStrength;
    }
    
    

    @Override
    public void setPairLevelDecay(PairLevelDecay pairLevelDecay) {
        this.pairLevelDecay = pairLevelDecay;
    }

    @Override
    public PairLevelDecay getPairLevelDecay() {
    	return this.pairLevelDecay;
    }
    
    @Override
	public Map<String, Map<String, Float>> getAccumulatedStrength() {
        return accumulatedStrength;
    }

    @Override
	public void setAccumulatedStrength(Map<String, Map<String, Float>> accumulatedStrength) {
        this.accumulatedStrength = accumulatedStrength;
    }


    /*
    AccumulatedStrength: Function will calculate the total strength of file A, when it is committed with B,C,D,E,F along with decay.
     */
    @Override
    public void setUpObjects() {
    	pairLevelDecay = new PairLevelDecay();
    	pairLevelDecay.setReadingStrategy(readingStrategy);
    	System.out.println("initialized");
    }
    
    @Override
	public void calculateAccumulatedStrength(Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMap, Map<String, Map<Integer, Map<String, Integer>>> excelYearMap) {
        Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMapSecond = new LinkedHashMap<>();
        pairStrengthMapSecond.putAll(pairStrengthMap);

        pairLevelDecay.globalDecay();

        Map<String, Map<String, Map<String, Double>>> globalDecay = pairLevelDecay.getPairLevelDecayMap();
        Map<String, Map<String, Float>> strategyResults = theStrategy.calculate(pairStrengthMap, excelYearMap, globalDecay);
        
        System.out.println("Overall Strength for Same file");
        /*Start: Bug 003: Explicity using garbage Collector */
        
        /*End:  Bug 003: Explicity using garbage Collector */

        setAccumulatedStrength(strategyResults);
      /* System.out.println("Before Decay caa7faee-1ed0-11eb-98c6-482ae32cf5b4");
       overallStrengthDateMap.get("caa7faee-1ed0-11eb-98c6-482ae32cf5b4").entrySet().forEach(e-> System.out.print(e));*/




    }
}
