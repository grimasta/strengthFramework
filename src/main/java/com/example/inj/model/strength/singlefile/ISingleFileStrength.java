package com.example.inj.model.strength.singlefile;

import java.util.List;
import java.util.Map;

import com.example.inj.model.decays.DecayImplementation;
import com.example.inj.model.sampling.InsertExcel;
import com.example.inj.model.strength.accumulators.IStrengthAccumulator;
import com.example.inj.readingStrategy.strategy.IReadingStrategy;

public interface ISingleFileStrength{

	public void finalStrengthSingleFile();

	public IStrengthAccumulator getAccumulatedStrength();

	public void setAccumulatedStrength(IStrengthAccumulator accumulatedStrength);

	public IReadingStrategy getReadingStrategy();

	public void setReadingStrategy(IReadingStrategy readingStrategy);

	public DecayImplementation getDecayImplementation();

	public void setDecayImplementation(DecayImplementation decayImplementation);

	public InsertExcel getInsertExcel();

	public void setInsertExcel(InsertExcel insertExcel);

	public Map<String, List<String>> getYearMapAloneSame();

	public void setYearMapAloneSame(Map<String, List<String>> yearMapAloneSame);

	public Map<String, Map<String, Float>> getFinalStrength();

	public void setFinalStrength(Map<String, Map<String, Float>> finalStrength);

}