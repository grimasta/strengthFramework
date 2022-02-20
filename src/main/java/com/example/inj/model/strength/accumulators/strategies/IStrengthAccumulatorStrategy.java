package com.example.inj.model.strength.accumulators.strategies;

import java.util.Map;

import com.example.inj.model.storage.DataRepository;

public interface IStrengthAccumulatorStrategy {

	public Map<String, Map<String, Float>> calculate(DataRepository dataRepository);

		
}
