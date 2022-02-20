package com.example.inj.model.strength.accumulators;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.inj.model.storage.DataRepository;
import com.example.inj.model.strength.accumulators.strategies.IStrengthAccumulatorStrategy;

public class AccumulatedStrength implements IStrengthAccumulator {

    Logger logger = LoggerFactory.getLogger(AccumulatedStrength.class);
    Map<String, Map<String, Float>> accumulatedStrength;
    private IStrengthAccumulatorStrategy theStrategy = null;
    
    public AccumulatedStrength() {
    }

    public void setStrategy(IStrengthAccumulatorStrategy theStrategy) {
    	this.theStrategy = theStrategy;
    }
    
    /*
    AccumulatedStrength: Function will calculate the total strength of file A, when it is committed with B,C,D,E,F along with decay.
     */
    
    @Override
	public void calculateAccumulatedStrength(DataRepository dataRepository) {

    	dataRepository.setAccumulatedStrength(theStrategy.calculate(dataRepository));
        System.out.println("Overall Strength for Same file");
        /*Start: Bug 003: Explicity using garbage Collector */
        
        /*End:  Bug 003: Explicity using garbage Collector */

      /* System.out.println("Before Decay caa7faee-1ed0-11eb-98c6-482ae32cf5b4");
       overallStrengthDateMap.get("caa7faee-1ed0-11eb-98c6-482ae32cf5b4").entrySet().forEach(e-> System.out.print(e));*/




    }
}
