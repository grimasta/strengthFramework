package com.example.inj.model.strength.pair.strategies;

import com.example.inj.model.strength.pair.PairCalculatorEnum;
import com.example.inj.model.strength.pair.IPairStrength;
import com.example.inj.model.strength.pair.PairStrength;

public class PairStrengthStrategyFactory {

	public static IPairStrengthStrategy createPairStrengthStrategy(PairStrengthStrategyEnum type) {
        switch(type) {
        case DEFAULT:
        	IPairStrengthStrategy pairStrengthStrategy;
        	pairStrengthStrategy = new DefaultPairStrengthCalculatorStrategy();
        	return pairStrengthStrategy;
        default:
        	System.err.println("ERROR CREATING PAIRWISE STRENGTH CALCULATOR");
        	System.exit(1);
        	return null;
        }
    }
	
}
