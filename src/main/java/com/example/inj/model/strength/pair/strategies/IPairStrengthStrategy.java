package com.example.inj.model.strength.pair.strategies;

import java.util.List;
import java.util.Map;

import com.example.inj.model.storage.DataRepository;

public interface IPairStrengthStrategy {
	
	public Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> calculate(DataRepository dataRepository);
	

}
