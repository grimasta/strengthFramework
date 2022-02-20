package com.example.inj.model.strength.accumulators;

import com.example.inj.model.storage.DataRepository;
import com.example.inj.model.strength.accumulators.strategies.IStrengthAccumulatorStrategy;

public interface IStrengthAccumulator {

	public void setStrategy(IStrengthAccumulatorStrategy theStrategy);
	
	void calculateAccumulatedStrength(DataRepository dataRepository);

}