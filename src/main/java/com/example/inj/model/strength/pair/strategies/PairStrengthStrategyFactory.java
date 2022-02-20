package com.example.inj.model.strength.pair.strategies;

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
