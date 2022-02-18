package com.example.inj.model.strength.accumulators;

import java.util.List;
import java.util.Map;

import com.example.inj.model.strength.accumulators.strategies.IStrengthAccumulatorStrategy;

public interface IStrengthAccumulator {

	public void setStrategy(IStrengthAccumulatorStrategy theStrategy);
	
	void calculateAccumulatedStrength(Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMap,
			Map<String, Map<Integer, Map<String, Integer>>> excelYearMap);

}