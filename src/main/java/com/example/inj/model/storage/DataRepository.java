package com.example.inj.model.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.inj.Analysis.MetricEnum;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import tech.tablesaw.api.DoubleColumn;
// TODO future refactoring make it an abstract class subclassed to specific subtypes (ADataRepository


@Getter
@Setter
public class DataRepository {
	// variables
	private Table<String, String, Map<String, List<Object>>> fileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy = HashBasedTable
			.create();
	private Map<Integer, String> dictionary;
	private Table<String, String, Map<Integer, List<Object>>> readableMappingSameN = HashBasedTable.create();// Bug 001:
	private Map<String, Map<String, Boolean>> readableBugFixing;
	private HashMap<String, String> dictionaryString; // Time, CommitId
	private HashMap<String, String> dictionaryTime; // CommitId, Time
	private Table<String, String, Map<Integer, List<Object>>> fileId2FileID2OccurencesNumber2ListOfChanges3Copy = HashBasedTable
			.create();
	private Map<String, List<String>> fileCommits; // Imp 004
	private Map<String, Map<String, Map<String, Float>>> linesModifiedSource;
	private Map<String, Map<String, Map<String, Float>>> linesModifiedDestination;
	private Map<String, Map<String, Integer>> yearMap;
	private Map<String, Map<String, Map<String, Float>>> mapOfCoCommitOverTotalCommitRatiosForAllFileCombinations;// Source,Destination,Commit_Date,Value
	private Map<String, Map<String, Map<String, Float>>> mapOfCoCommitOverSourceFileCommitRatioForAllFileCombinations;
	private Map<String, Map<String, Map<String, Float>>> coTimeDifference;
	private Table<String, String, Map<Integer, Map<String, List<Float>>>> tableCommits;
	private Pair<Map<String, Map<String, Map<Integer, Map<String, Integer>>>>, Map<String, Map<Integer, Map<String, Integer>>>> pairMaps;
	private Map<String, Map<String, Map<Integer, Map<String, Integer>>>> timeDifference;
	private Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMap;
    private Map<String, Map<Integer, Map<String, Integer>>> excelYearMaps;
    private Map<String, Map<String, Map<Integer, Map<String, Float>>>> linesModifyA;
    private Map<String, Map<String, Map<Integer, Map<String, Float>>>> linesModifyB;
    private Map<String, Map<String, Map<String, Double>>> pairLevelDecayMap;
    private Map<String, List<String>> yearMapPair;
    private Map<String, Map<String, Float>> accumulatedStrength;    
    private Map<String, Map<String, Float>> globalDecay;
    private Map<String, Map<String, Float>> accumulatedSt;
    private Map<String, List<String>> yearMapAloneSame;
    private Map<String, Map<String, Float>> finalStrength;
    private Map<String, Integer> segmentWidth;
    private Map<String, Map<String, List<Object>>> vectorMapGlobal;
    private Map<String, List<List<Object>>> vectorFinalMapGlobal;
    private Map<String, List<List<Object>>> vectorsForExcel;
    private Map<String, Map<String, Map<String, Integer>>>  coCommittedFiles;

//	SourceCodeMetrics
	private HashMap<String, HashMap<String, HashMap<String, Float>>> finalCallsValue;
	private HashMap<String, HashMap<String, HashMap<String, Integer>>> mapCalls;
	private HashMap<String, HashMap<String, Integer>> maxCommitCalls;
	private HashMap<String, Float> avgCommitCalls;

	private tech.tablesaw.api.Table tableSortedByFileId;
	private tech.tablesaw.api.Table tableSortedByCommitTime;
	private int[][] vectorFileView;
	private int[][] vectorCommitView;
	private Map<String, Integer>segment;
	private int[] fusedVector;
	private DoubleColumn[] metricColumnArrayFileView;
	private DoubleColumn[] metricColumnArrayCommitView;
	final private int metricSize = MetricEnum.values().length;
	private double[] quantile = {0.25, 0.50, 0.75, 1.00};   //FIXME: set desire quantile(ascending)
															//FIXME: always put 1 at the end

	// singleton DP
	private static DataRepository instance = null;

	private DataRepository() {
	}

	public static DataRepository getInstance() {
		if (instance == null)
			instance = new DataRepository();
		return instance;
	}

	public Table<String, String, Map<String, List<Object>>> getFileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy() {
		return fileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy;
	}

	public void setFileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy(
			Table<String, String, Map<String, List<Object>>> fileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy) {
		this.fileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy = fileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy;
	}

	public Table<String, String, Map<Integer, List<Object>>> getReadableMappingSameN() {
		return readableMappingSameN;
	}

	public void setReadableMappingSameN(Table<String, String, Map<Integer, List<Object>>> readableMappingSameN) {
		this.readableMappingSameN = readableMappingSameN;
	}

	public Map<String, Map<String, Boolean>> getReadableBugFixing() {
		return readableBugFixing;
	}

	public void setReadableBugFixing(Map<String, Map<String, Boolean>> readableBugFixing) {
		this.readableBugFixing = readableBugFixing;
	}

	public HashMap<String, String> getDictionaryString() {
		return dictionaryString;
	}

	public void setDictionaryString(HashMap<String, String> dictionaryString) {
		this.dictionaryString = dictionaryString;
	}

	public HashMap<String, String> getDictionaryTime() {
		return dictionaryTime;
	}

	public void setDictionaryTime(HashMap<String, String> dictionaryTime) {
		this.dictionaryTime = dictionaryTime;
	}

	public Table<String, String, Map<Integer, List<Object>>> getReadableMappingFinal() {
		return fileId2FileID2OccurencesNumber2ListOfChanges3Copy;
	}

	public void setFileId2FileID2OccurencesNumber2ListOfChanges3Copy(
			Table<String, String, Map<Integer, List<Object>>> fileId2FileID2OccurencesNumber2ListOfChanges3Copy) {
		this.fileId2FileID2OccurencesNumber2ListOfChanges3Copy = fileId2FileID2OccurencesNumber2ListOfChanges3Copy;
	}

	public Map<String, List<String>> getFileCommits() {
		return fileCommits;
	}

	public void setFileCommits(Map<String, List<String>> fileCommits) {
		this.fileCommits = fileCommits;
	}

	public Map<Integer, String> getDictionary() {
		return dictionary;
	}

	public void setDictionary(Map<Integer, String> dictionary) {
		this.dictionary = dictionary;
	}

	public Map<String, Map<String, Map<String, Float>>> getLinesModifiedSource() {
		return linesModifiedSource;
	}

	public void setLinesModifiedSource(Map<String, Map<String, Map<String, Float>>> linesModifiedSource) {
		this.linesModifiedSource = linesModifiedSource;
	}

	public Map<String, Map<String, Map<String, Float>>> getLinesModifiedDestination() {
		return linesModifiedDestination;
	}

	public void setLinesModifiedDestination(Map<String, Map<String, Map<String, Float>>> linesModifiedDestination) {
		this.linesModifiedDestination = linesModifiedDestination;
	}

	public Map<String, Map<String, Integer>> getYearMap() {
		return yearMap;
	}

	public void setYearMap(Map<String, Map<String, Integer>> yearMap) {
		this.yearMap = yearMap;
	}

	//Todo Warning: "Non-ASCII characters in an identifier Symbols from different languages found: [LATIN, GREEK]"
	public Map<String, Map<String, Map<String, Float>>> getΜapOfCoCommitOverSumOfCommitsRatioForAllFileCombinations() {
		return mapOfCoCommitOverTotalCommitRatiosForAllFileCombinations;
	}

	public void setΜapOfCoCommitOverSumOfCommitsRatioForAllFileCombinations(Map<String, Map<String, Map<String, Float>>> mapOfCoCommitOverTotalCommitRatiosForAllFileCombinations) {
		this.mapOfCoCommitOverTotalCommitRatiosForAllFileCombinations = mapOfCoCommitOverTotalCommitRatiosForAllFileCombinations;
	}

	public Map<String, Map<String, Map<String, Float>>> getΜapOfCoCommitOverSourceFileCommitRatioForAllFileCombinations() {
		return mapOfCoCommitOverSourceFileCommitRatioForAllFileCombinations;
	}

	public void setΜapOfCoCommitOverSourceFileCommitRatioForAllFileCombinations(Map<String, Map<String, Map<String, Float>>> mapOfCoCommitOverSourceFileCommitRatioForAllFileCombinations) {
		//setMapOfCoCommitOverSourceFileCommitRatioForAllFileCombinations;
		//
		this.mapOfCoCommitOverSourceFileCommitRatioForAllFileCombinations = mapOfCoCommitOverSourceFileCommitRatioForAllFileCombinations;
	}

	public Map<String, Map<String, Map<String, Float>>> getCoTimeDifference() {
		return coTimeDifference;
	}

	public void setCoTimeDifference(Map<String, Map<String, Map<String, Float>>> coTimeDifference) {
		this.coTimeDifference = coTimeDifference;
	}

	public HashMap<String, HashMap<String, HashMap<String, Float>>> getFinalCallsValue() {
		return finalCallsValue;
	}

	public void setFinalCallsValue(HashMap<String, HashMap<String, HashMap<String, Float>>> finalCallsValue) {
		this.finalCallsValue = finalCallsValue;
	}

	public HashMap<String, HashMap<String, HashMap<String, Integer>>> getMapCalls() {
		return mapCalls;
	}

	public void setMapCalls(HashMap<String, HashMap<String, HashMap<String, Integer>>> mapCalls) {
		this.mapCalls = mapCalls;
	}

	public HashMap<String, HashMap<String, Integer>> getMaxCommitCalls() {
		return maxCommitCalls;
	}

	public void setMaxCommitCalls(HashMap<String, HashMap<String, Integer>> maxCommitCalls) {
		this.maxCommitCalls = maxCommitCalls;
	}

	public HashMap<String, Float> getAvgCommitCalls() {
		return avgCommitCalls;
	}

	public void setAvgCommitCalls(HashMap<String, Float> avgCommitCalls) {
		this.avgCommitCalls = avgCommitCalls;
	}

	public Table<String, String, Map<Integer, Map<String, List<Float>>>> getTableCommits() {
		return tableCommits;
	}

	public void setTableCommits(Table<String, String, Map<Integer, Map<String, List<Float>>>> tableCommits) {
		this.tableCommits = tableCommits;
	}

	public Pair<Map<String, Map<String, Map<Integer, Map<String, Integer>>>>, Map<String, Map<Integer, Map<String, Integer>>>> getPairMaps() {
		return pairMaps;
	}

	public void setPairMaps(
			Pair<Map<String, Map<String, Map<Integer, Map<String, Integer>>>>, Map<String, Map<Integer, Map<String, Integer>>>> pairMaps) {
		this.pairMaps = pairMaps;
	}

	public Map<String, Map<String, Map<Integer, Map<String, Integer>>>> getTimeDifference() {
		return timeDifference;
	}

	public void setTimeDifference(Map<String, Map<String, Map<Integer, Map<String, Integer>>>> timeDifference) {
		this.timeDifference = timeDifference;
	}

	public Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> getPairStrengthMap() {
		return pairStrengthMap;
	}

	public void setPairStrengthMap(Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMap) {
		this.pairStrengthMap = pairStrengthMap;
	}

	public Map<String, Map<Integer, Map<String, Integer>>> getExcelYearMaps() {
		return excelYearMaps;
	}

	public void setExcelYearMaps(Map<String, Map<Integer, Map<String, Integer>>> excelYearMaps) {
		this.excelYearMaps = excelYearMaps;
	}

	public Map<String, Map<String, Map<Integer, Map<String, Float>>>> getLinesModifyA() {
		return linesModifyA;
	}

	public void setLinesModifyA(Map<String, Map<String, Map<Integer, Map<String, Float>>>> linesModifyA) {
		this.linesModifyA = linesModifyA;
	}

	public Map<String, Map<String, Map<Integer, Map<String, Float>>>> getLinesModifyB() {
		return linesModifyB;
	}

	public void setLinesModifyB(Map<String, Map<String, Map<Integer, Map<String, Float>>>> linesModifyB) {
		this.linesModifyB = linesModifyB;
	}

	public Map<String, Map<String, Map<String, Double>>> getPairLevelDecayMap() {
		return pairLevelDecayMap;
	}

	public void setPairLevelDecayMap(Map<String, Map<String, Map<String, Double>>> pairLevelDecayMap) {
		this.pairLevelDecayMap = pairLevelDecayMap;
	}

	public Map<String, List<String>> getYearMapPair() {
		return yearMapPair;
	}

	public void setYearMapPair(Map<String, List<String>> yearMapPair) {
		this.yearMapPair = yearMapPair;
	}

	public Map<String, Map<String, Float>> getAccumulatedStrength() {
		return accumulatedStrength;
	}

	public void setAccumulatedStrength(Map<String, Map<String, Float>> accumulatedStrength) {
		this.accumulatedStrength = accumulatedStrength;
	}

	public Map<String, Map<String, Float>> getGlobalDecay() {
		return globalDecay;
	}

	public void setGlobalDecay(Map<String, Map<String, Float>> globalDecay) {
		this.globalDecay = globalDecay;
	}

	public Map<String, Map<String, Float>> getAccumulatedSt() {
		return accumulatedSt;
	}

	public void setAccumulatedSt(Map<String, Map<String, Float>> accumulatedSt) {
		this.accumulatedSt = accumulatedSt;
	}

	public Map<String, List<String>> getYearMapAloneSame() {
		return yearMapAloneSame;
	}

	public void setYearMapAloneSame(Map<String, List<String>> yearMapAloneSame) {
		this.yearMapAloneSame = yearMapAloneSame;
	}

	public Map<String, Map<String, Float>> getFinalStrength() {
		return finalStrength;
	}

	public void setFinalStrength(Map<String, Map<String, Float>> finalStrength) {
		this.finalStrength = finalStrength;
	}

	public Map<String, Integer> getSegmentWidth() {
		return segmentWidth;
	}

	public void setSegmentWidth(Map<String, Integer> segmentWidth) {
		this.segmentWidth = segmentWidth;
	}

	public Map<String, Map<String, List<Object>>> getVectorMapGlobal() {
		return vectorMapGlobal;
	}

	public void setVectorMapGlobal(Map<String, Map<String, List<Object>>> vectorMapGlobal) {
		this.vectorMapGlobal = vectorMapGlobal;
	}

	public Map<String, List<List<Object>>> getVectorFinalMapGlobal() {
		return vectorFinalMapGlobal;
	}

	public void setVectorFinalMapGlobal(Map<String, List<List<Object>>> vectorFinalMapGlobal) {
		this.vectorFinalMapGlobal = vectorFinalMapGlobal;
	}

	public Map<String, List<List<Object>>> getVectorsForExcel() {
		return vectorsForExcel;
	}

	public void setVectorsForExcel(Map<String, List<List<Object>>> vectorsForExcel) {
		this.vectorsForExcel = vectorsForExcel;
	}

	public Map<String, Map<String, Map<String, Integer>>> getCoCommittedFiles() {
		return coCommittedFiles;
	}

	public void setCoCommittedFiles(Map<String, Map<String, Map<String, Integer>>> coCommittedFiles) {
		this.coCommittedFiles = coCommittedFiles;
	}

}
