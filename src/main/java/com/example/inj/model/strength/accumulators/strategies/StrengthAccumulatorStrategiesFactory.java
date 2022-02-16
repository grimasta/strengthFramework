package com.example.inj.model.strength.accumulators.strategies;

public class StrengthAccumulatorStrategiesFactory {
	
	public static IStrengthAccumulatorStrategy createStrengthAccumulatorStrategy(StrengthAccumulatorStrategiesEnum type) {
		switch(type) {
		case DEFAULT:
			return new DefaultStrengthAccumulatorStrategy();
		default:
			System.out.println("NO SUCH STRATEGY IMPLEMENTED YET");
			return null;
		}
	}
	
}
