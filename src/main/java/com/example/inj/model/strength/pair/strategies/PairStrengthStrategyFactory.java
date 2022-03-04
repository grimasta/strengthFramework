package com.example.inj.model.strength.pair.strategies;

public class PairStrengthStrategyFactory {

	public static IPairStrengthStrategy createPairStrengthStrategy(PairStrengthStrategyEnum type) {
		IPairStrengthStrategy pairStrengthStrategy;
		switch(type) {
        case DEFAULT:
        	pairStrengthStrategy = new DefaultPairStrengthCalculatorStrategy();
        	return pairStrengthStrategy;
		case RONGJI:
			pairStrengthStrategy= new DummyPairStrengthStrategy();
			return pairStrengthStrategy;
        default:
        	System.err.println("ERROR CREATING PAIRWISE STRENGTH CALCULATOR");
        	System.exit(1);
        	return null;
        }
    }
	
}
