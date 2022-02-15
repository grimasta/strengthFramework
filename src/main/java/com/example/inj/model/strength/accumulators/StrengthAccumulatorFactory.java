package com.example.inj.model.strength.accumulators;

public class StrengthAccumulatorFactory {

	public static IStrengthAccumulator create(StrengthAccumulators type) {
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
