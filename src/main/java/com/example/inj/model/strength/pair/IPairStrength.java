package com.example.inj.model.strength.pair;

import java.util.List;
import java.util.Map;

import com.example.inj.model.cases.CoCommittedExcel;
import com.example.inj.model.cases.CoCommittedFiles;
import com.example.inj.model.cases.LinesModified;
import com.example.inj.model.cases.TimeDifference;
import com.example.inj.model.cases.coupled.ParseCoupledCSV;
import com.example.inj.model.cases.prime.CoCommittedPrime;
import com.example.inj.model.cases.prime.CommittedSoFar;
import com.example.inj.model.cases.prime.LinesModifiedPrime;
import com.example.inj.model.cases.prime.WithoutCommitPrime;
import com.example.inj.model.strength.accumulators.IStrengthAccumulator;
import com.example.inj.model.strength.pair.strategies.IPairStrengthStrategy;
import com.example.inj.readingStrategy.strategy.IReadingStrategy;

public interface IPairStrength {

//	public void calculatePairStrength2()
    	
	public void setStrategy(IPairStrengthStrategy theStrategy);
	public void setUpObjects();
  	public void calculatePairStrength();
	public LinesModifiedPrime getLinesModifiedPrime();
	public void setLinesModifiedPrime(LinesModifiedPrime linesModifiedPrime);
	public CommittedSoFar getCommittedSoFar();
	public void setCommittedSoFar(CommittedSoFar committedSoFar);
	public CoCommittedPrime getCoCommittedPrime();
	public void setCoCommittedPrime(CoCommittedPrime coCommittedPrime);
	public WithoutCommitPrime getWithoutCommitPrime();
	public void setWithoutCommitPrime(WithoutCommitPrime withoutCommitPrime);
	public ParseCoupledCSV getParseCoupledCSV();
	public void setParseCoupledCSV(ParseCoupledCSV parseCoupledCSV);
	public CoCommittedExcel getCoCommittedExcel();
	public void setCoCommittedExcel(CoCommittedExcel coCommittedExcel);
	public CoCommittedFiles getCoCommittedFiles();
	public void setCoCommittedFiles(CoCommittedFiles coCommittedFiles);
	public LinesModified getLinesModified();
	public void setLinesModified(LinesModified linesModified);
	public TimeDifference getTimeDifferences();
	public void setTimeDifferences(TimeDifference timeDifferences);
	public IStrengthAccumulator getAccumulatedStrength();
	public void setAccumulatedStrength(IStrengthAccumulator accumulatedStrength);
	public Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> getPairStrengthMap();
	public void setPairStrengthMap(Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMap);
	public Map<String, Map<Integer, Map<String, Integer>>> getExcelYearMaps();
	public void setExcelYearMaps(Map<String, Map<Integer, Map<String, Integer>>> excelYearMaps);
	public IReadingStrategy getReadingStrategy();
	public void setReadingStrategy(IReadingStrategy readingStrategy);
	public Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> getPairStrengthMapIn();
	public Map<String, Map<Integer, Map<String, Integer>>> getExcelYearMapsIn();
	
	

}
