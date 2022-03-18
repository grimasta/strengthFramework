package com.example.inj.model.strength.accumulators.strategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.example.inj.model.storage.DataRepository;

public class DefaultStrengthAccumulatorStrategy implements IStrengthAccumulatorStrategy {

	@Override
	public Map<String, Map<String, Float>> calculate(DataRepository dataRepository){
		
//		for each sourceFileId look into the list of pairwise strengths and find all dates for which there are calculated pairwise strengths 
//		for each of the dates add all the pairwise strenghts calculated for each date plus all pairwise strengths from previous pairwise calculations (appropriately decayed)
		Map<String, TreeMap<String, Map<String, Float>>> source2date2target2strength = dataRepository.getAllPairStrengthsWithDecay();
		Map<String, Map<String, Float>> overallStrengthDateMap = new HashMap<>();
		
		for (String sourceId : source2date2target2strength.keySet()) {
			Map<String, Float> overallStrengthMap = new TreeMap<>();
			for (String commitDate : source2date2target2strength.get(sourceId).keySet()) {
				for(String targetId : source2date2target2strength.get(sourceId).get(commitDate).keySet()) {
					float thisStrength = source2date2target2strength.get(sourceId).get(commitDate).get(targetId);
					overallStrengthMap.put(commitDate, thisStrength + overallStrengthMap.getOrDefault(commitDate, 0f));
				}
			}
			overallStrengthDateMap.put(sourceId, overallStrengthMap);
		}
			


		
//		for (String sourceFileId : fileIds2CommitDates.keySet()) {
//			targetFileIdPairStrengthMap = pairStrengthMap.get(sourceFileId);
//			targetFileIdStrengthContributionCountMap = new LinkedHashMap<>();
//			targetFileIdCountExistenceMap = new LinkedHashMap<>();
//			for (String targetFileId : targetFileIdPairStrengthMap.keySet()) {
//				targetFileIdStrengthContributionCountMap.put(targetFileId, 0);
//				targetFileIdCountExistenceMap.put(targetFileId, 0);
//			}
//			String sourceFileIdInPairStrengthMap = sourceFileId;
//			sortedListOfCommitDatesForFile = new ArrayList<>();
//			sortedListOfCommitDatesForFile.addAll(fileIds2CommitDates.get(sourceFileId));
////					TODO use Date sorting
//			Collections.sort(sortedListOfCommitDatesForFile);
//			Map<String, List<Map<Integer, Map<String, Float>>>> targetFileId2ListOfCoCommitIndex2Date2PairWiseStrengthOnDate; 
//			targetFileId2ListOfCoCommitIndex2Date2PairWiseStrengthOnDate = pairStrengthMap.get(sourceFileIdInPairStrengthMap);
//
//			Map<String, Float> overallStrengthMap = new LinkedHashMap<>();
//			for (String currentDate : sortedListOfCommitDatesForFile) {
//				float overallStrengthOnCurrentDate = 0.0f;
//				contributingToTotalStrengthTargetFileIds = new LinkedList<>();
//				for (String targetFileId : targetFileId2ListOfCoCommitIndex2Date2PairWiseStrengthOnDate.keySet()) {
//					for (Map<Integer, Map<String, Float>> numberOfCoCommits2Dates2PairwiseStrengthOnDate : targetFileId2ListOfCoCommitIndex2Date2PairWiseStrengthOnDate.get(targetFileId)) {
//						for (Entry<Integer, Map<String, Float>> entry : numberOfCoCommits2Dates2PairwiseStrengthOnDate.entrySet()) {
//							Map<String, Float> date2PairwiseStrengthMap = entry.getValue();
//							if (date2PairwiseStrengthMap.containsKey(currentDate)) {
//								float pairwiseStrengthOnCurrentDateForTargetFileId = 0.0f;
//								pairwiseStrengthOnCurrentDateForTargetFileId = date2PairwiseStrengthMap
//										.get(currentDate);
//								targetFileIdStrengthContributionCountMap.put(targetFileId, 1);
//
//								contributingToTotalStrengthTargetFileIds.add(targetFileId);
//								overallStrengthOnCurrentDate += pairwiseStrengthOnCurrentDateForTargetFileId;
//							}
//
//						}
//					}
//				}
//
//				List<String> listOfTargetFileIdsHavingValidValuesForCurrentDatesOverallStrengthCalculation = new LinkedList<>();
//				if (!contributingToTotalStrengthTargetFileIds.isEmpty()) {
//					for (String targetFileIdLocal : targetFileIdStrengthContributionCountMap.keySet()) {
////								if the targetFileIdLocal has been found in a previous date but not the current date then add it 
////								to listOfTargetFileIdsHavingValidValuesForCurrentDatesOverallStrengthCalculation
//						if (!(contributingToTotalStrengthTargetFileIds.contains(targetFileIdLocal))
//								&& (targetFileIdStrengthContributionCountMap.get(targetFileIdLocal) == 1)) {
//							listOfTargetFileIdsHavingValidValuesForCurrentDatesOverallStrengthCalculation.add(targetFileIdLocal);
//						}
//					}
//					indexOfCurrentDateInSourceFileSortedListOfCommitDates = sortedListOfCommitDatesForFile.indexOf(currentDate);
////							if this is not the first commit date for the sourceFileId
//					if (indexOfCurrentDateInSourceFileSortedListOfCommitDates > 0) {
////								for each of the TargetFileIds in the listOfTargetFileIdsHavingValidValuesForCurrentDatesOverallStrengthCalculation
//						for (String targetFileIdForValidValue : listOfTargetFileIdsHavingValidValuesForCurrentDatesOverallStrengthCalculation) {
//							float decaySt = 0;
//							float st = 0;
//							Iterator<Map<Integer, Map<String, Float>>> pairIteratorTry = pairStrengthMap
//									.get(sourceFileId).get(targetFileIdForValidValue).iterator();
//							Map<Integer, Map<String, Float>> pairMapping;
//							Set<String> pairTry = new TreeSet<>();
//							while (pairIteratorTry.hasNext()) {
//								pairMapping = (Map<Integer, Map<String, Float>>) pairIteratorTry.next();
//								for (int i : pairMapping.keySet()) {
//									Map<String, Float> pairs = pairMapping.get(i);
//									for (String is : pairs.keySet()) {
//										pairTry.add(is);
//
//									}
//								}
//							}
//							pairIteratorTry = null; // Bug 003: Explicity using garbage Collector
//							String prev = ((TreeSet<String>) pairTry).floor(currentDate);
//							if (prev != null) {
//								Iterator<Map<Integer, Map<String, Float>>> pairIterator = pairStrengthMap
//										.get(sourceFileId).get(targetFileIdForValidValue).iterator();
//								Map<Integer, Map<String, Float>> pairMappings;
//								while (pairIterator.hasNext()) {
//									pairMappings = (Map<Integer, Map<String, Float>>) pairIterator.next();
//									for (int i : pairMappings.keySet()) {
//										Map<String, Float> pairs = pairMappings.get(i);
//										if (pairs.containsKey(prev)) {
//											st = pairs.get(prev);
//										}
//									}
//								}
//								pairIterator = null;// Bug 003: Explicity using garbage Collector
//							}
//
//							decay = globalDecay.get(sourceFileId).get(targetFileIdForValidValue).get(currentDate);
//							decaySt = (float) (st * decay);
//							overallStrengthOnCurrentDate += decaySt;
//
//						}
//
//					}
//				}
//
//				overallStrengthMap.put(currentDate, overallStrengthOnCurrentDate);
//
//			}
//
//			overallStrengthDateMap.put(sourceFileId, overallStrengthMap);
//		}
		System.out.println("After Rias Strength Accumulation Code");

//		for (Entry<String, Map<String, Float>> strength : overallStrengthDateMap.entrySet())
//			for (Entry<String, Float> dateStrength : strength.getValue().entrySet())
//				if (perFileTotalStrength.get(strength.getKey()).get(dateStrength.getKey()) != dateStrength.getValue())
//					System.out.println("false");
		return overallStrengthDateMap;
	}

}
