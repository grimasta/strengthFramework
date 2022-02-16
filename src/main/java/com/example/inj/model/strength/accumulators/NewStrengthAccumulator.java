package com.example.inj.model.strength.accumulators;

import java.util.List;
import java.util.Map;

import com.example.inj.model.decays.PairLevelDecay;
import com.example.inj.model.strength.accumulators.strategies.IStrengthAccumulatorStrategy;
import com.example.inj.model.strength.pair.PairStrength;
import com.example.inj.readingStrategy.strategy.IReadingStrategy;

public class NewStrengthAccumulator implements IStrengthAccumulator {

	@Override
	public void setReadingStrategy(IReadingStrategy readingStrategy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPairStrength(PairStrength pairStrength) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPairLevelDecay(PairLevelDecay pairLevelDecay) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public PairLevelDecay getPairLevelDecay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Map<String, Float>> getAccumulatedStrength() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAccumulatedStrength(Map<String, Map<String, Float>> accumulatedStrength) {
		// TODO Auto-generated method stub

	}

	@Override
	public void calculateAccumulatedStrength(
			Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMap,
			Map<String, Map<Integer, Map<String, Integer>>> excelYearMap) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUpObjects() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStrategy(IStrengthAccumulatorStrategy theStrategy) {
		// TODO Auto-generated method stub
		
	}



}
