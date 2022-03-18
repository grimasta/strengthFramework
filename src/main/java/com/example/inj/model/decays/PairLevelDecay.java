package com.example.inj.model.decays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.inj.model.storage.DataRepository;
import com.example.inj.model.strength.pair.PairStrength;

@Component
public class PairLevelDecay {

	private DataRepository dataRepository;
	private Map<String, Map<String, Map<String, Double>>> pairLevelDecayMap;
//	private Map<String, List<String>> yearMapPair;
	Logger logger = LoggerFactory.getLogger(PairStrength.class);

	public PairLevelDecay() {
		dataRepository = DataRepository.getInstance();
	}

	public Map<String, Map<String, Map<String, Double>>> getPairLevelDecayMap() {
		return pairLevelDecayMap;
	}

	public void setPairLevelDecayMap(Map<String, Map<String, Map<String, Double>>> pairLevelDecayMap) {
		this.pairLevelDecayMap = pairLevelDecayMap;
	}

//	public Map<String, List<String>> getYearMapPair() {
//		return yearMapPair;
//	}
//
//	public void setYearMapPair(Map<String, List<String>> yearMapPair) {
//		this.yearMapPair = yearMapPair;
//	}

	/*
	 * It is a Pair level decay based on below function and scenario: If File A is
	 * committed with File B, File C, File D, File E in Commit X at 11-Sept-2010,
	 * and again in next Commit Y at 12-Sept-2010 File A is committed only with File
	 * B, and File C then we are applying decay for File D, and File E. PAIR DECAY
	 * AFTER CALCULATING THE ACCUMULATED STRENGTH OF A FILE NOTE: WE WILL AGAIN
	 * DECAY IT AT GLOBAL LEVEL TO NORMALIZE IT FURTHER Pair Decay (
	 * Multiply)->Math.exp( Number of commits passed since A&B are co-committed*0.5)
	 */
	public void pairDecay() {

		Map<String, Map<String, Map<Integer, List<Object>>>> readMap = dataRepository.getReadableMappingFinal()
				.rowMap();
		Map<String, ArrayList<String>> sourceFileId2listOfCommitDates;
		sourceFileId2listOfCommitDates = dataRepository.getFileId2CommitDates();
		Map<String, Map<String, Map<String, Float>>> file2file2dateStrength = dataRepository.getAllPairStrengthsSimple();
		double exponent = 0;
		Map<String, TreeMap<String, Map<String, Float>>> allPairStrengthsWithDecay = new HashMap<>();
		// Calculate Decay
//		Map<String, Map<String, List<String>>> allCoCommitDates = new TreeMap<String, Map<String, List<String>>>();
//		for (String sourceFileId : readMap.keySet()){
//			for (String targetFileId : readMap.get(sourceFileId).keySet()){
//				List<String> coCommitDates = new ArrayList<>();
//				for (Entry<Integer, List<Object>> coOccurrenceEntry : readMap.get(sourceFileId).get(targetFileId).entrySet())
//					coCommitDates.add((String)coOccurrenceEntry.getValue().get(10));
//					allCoCommitDates.put(sourceFileId, allCoCommitDates.getOrDefault(sourceFileId, new HashMap<>()));
//					allCoCommitDates.get(sourceFileId).put(targetFileId, coCommitDates);
//					allCoCommitDates.put(targetFileId, allCoCommitDates.getOrDefault(targetFileId, new HashMap<>()));
//					allCoCommitDates.get(targetFileId).put(sourceFileId, coCommitDates);
//			}
//		}
		
		
		System.out.println(readMap.keySet().size());
		for (String sourceFileId : readMap.keySet()) { // yearMap number of time A is committed
			TreeMap<String, Map<String, Float>> allSourcePairStrengthsForDate = allPairStrengthsWithDecay.getOrDefault(sourceFileId, new TreeMap<>());
			for (String targetFileId : readMap.get(sourceFileId).keySet()) {
				List<String> coCommitOrderedDates = new ArrayList<>(); 
				for (List<Object> data : readMap.get(sourceFileId).get(targetFileId).values())
					coCommitOrderedDates.add((String) data.get(10));
				for (String sourceCommitDate : sourceFileId2listOfCommitDates.get(sourceFileId)) {
					Map<String, Float> allContributingDestinationFilePairStrengthsAndDecayedForDate = allSourcePairStrengthsForDate.getOrDefault(sourceCommitDate, new HashMap<>());
					allSourcePairStrengthsForDate.put(sourceFileId, allContributingDestinationFilePairStrengthsAndDecayedForDate);
					int commitsElapsedSinceLastCommit = 0;
					float strength = 0.0f;
					if (coCommitOrderedDates.contains(sourceCommitDate)) {
						commitsElapsedSinceLastCommit = 0;
						strength = file2file2dateStrength.get(sourceFileId).get(targetFileId).get(sourceCommitDate);
					} else {
						commitsElapsedSinceLastCommit += 1;
					}
					exponent = - 0.5 * commitsElapsedSinceLastCommit;
					double decayMultiplier = Math.exp(exponent);
					strength *= decayMultiplier;
					allContributingDestinationFilePairStrengthsAndDecayedForDate.put(targetFileId, strength);
				}
			}
			allPairStrengthsWithDecay.put(sourceFileId, allSourcePairStrengthsForDate);
		}
		dataRepository.setAllPairStrengthsWithDecay(allPairStrengthsWithDecay);	
		System.out.println("Inside Pair Decay");
		dataRepository.setYearMapPair(sourceFileId2listOfCommitDates);

	}

}
