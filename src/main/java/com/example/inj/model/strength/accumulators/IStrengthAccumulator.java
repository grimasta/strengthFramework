package com.example.inj.model.strength.accumulators;

import java.util.List;
import java.util.Map;

import com.example.inj.model.decays.PairLevelDecay;
import com.example.inj.model.strength.pair.PairStrength;
import com.example.inj.readingStrategy.strategy.IReadingStrategy;

public interface IStrengthAccumulator {

	void setReadingStrategy(IReadingStrategy readingStrategy);

	void setPairStrength(PairStrength pairStrength);

	void setPairLevelDecay(PairLevelDecay pairLevelDecay);
	
	PairLevelDecay getPairLevelDecay();
	
	Map<String, Map<String, Float>> getAccumulatedStrength();

	void setAccumulatedStrength(Map<String, Map<String, Float>> accumulatedStrength);

	void setUpObjects();
	
	void calculateAccumulatedStrength(Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMap,
			Map<String, Map<Integer, Map<String, Integer>>> excelYearMap);

}