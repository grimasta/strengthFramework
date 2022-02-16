package com.example.inj.model.strength.accumulators.strategies;

import java.util.List;
import java.util.Map;

public interface IStrengthAccumulatorStrategy {

	public Map<String, Map<String, Float>> calculate(
			Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMap,
			Map<String, Map<Integer, Map<String, Integer>>> excelYearMap, 
			Map<String, Map<String, Map<String, Double>>> globalDecay
			);
		
}
