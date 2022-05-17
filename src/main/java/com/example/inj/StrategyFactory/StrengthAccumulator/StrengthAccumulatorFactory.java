package com.example.inj.StrategyFactory.StrengthAccumulator;

import com.example.inj.model.strength.AccumulatedStrength;
import com.example.inj.model.strength.singlefile.NewStrengthAccumulator;

public class StrengthAccumulatorFactory {

	public static IStrengthAccumulator create(StrengthAccumulatorsEnumeration type) {
		switch(type) {
		case DEFAULT:
			return new AccumulatedStrength();
		case NEW_ACCUMULATOR:
			return new NewStrengthAccumulator();
		default:
			System.out.println("ERROR WHEN CREATING THE STRENGTH ACCUMULATOR OBJECT");
			System.exit(1);
			return null;
		}
	}
	
}
