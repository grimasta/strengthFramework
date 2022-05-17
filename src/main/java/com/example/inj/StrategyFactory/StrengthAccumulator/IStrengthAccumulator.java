package com.example.inj.StrategyFactory.StrengthAccumulator;

import com.example.inj.model.storage.DataRepository;
import com.example.inj.StrategyFactory.StrengthAccumulatorStrategy.IStrengthAccumulatorStrategy;

public interface IStrengthAccumulator {

	public void setStrategy(IStrengthAccumulatorStrategy theStrategy);
	
	void calculateAccumulatedStrength(DataRepository dataRepository);

}