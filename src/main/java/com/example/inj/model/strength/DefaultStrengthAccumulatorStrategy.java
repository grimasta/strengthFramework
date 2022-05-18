package com.example.inj.model.strength;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.example.inj.StrategyFactory.StrengthAccumulatorStrategy.IStrengthAccumulatorStrategy;
import com.example.inj.model.storage.DataRepository;

public class DefaultStrengthAccumulatorStrategy implements IStrengthAccumulatorStrategy {

	@Override
	public Map<String, Map<String, Float>> calculate(DataRepository dataRepository){			
		Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMap = dataRepository.getPairStrengthMap();
		Map<String, Map<Integer, Map<String, Integer>>> sourceFileId2SomeNumber2Date2CommitsOnDate = dataRepository.getExcelYearMaps();;
		Map<String, Map<String, Map<String, Double>>> globalDecay = dataRepository.getPairLevelDecayMap();

		Double decay = 0.0;
		List<String> contributingToTotalStrengthTargetFileIds = null;
		int indexOfCurrentDateInSourceFileSortedListOfCommitDates = 0;
		Map<String, Map<String, Float>> overallStrengthDateMap = new HashMap<>();
		Map<String, List<Map<Integer, Map<String, Float>>>> targetFileIdPairStrengthSubMap;
		Map<String, Integer> targetFileIdStrengthContributionCountMap;
		Map<String, Integer> targetFileIdCountExistenceMap;
		List<String> sortedListOfCommitDatesForFile;
/*
//		Map<String, Map<String, Float>> perFileTotalStrength = new HashMap<String, Map<String, Float>>();
//		List<String> listOfDatesRenamed = new ArrayList<>();
//		 figure out a way of maintaining the last valid value for a source - target fileId Pair to be contributed to the globalStrength for each date.
//		for (Entry<String, Map<String, List<Map<Integer, Map<String, Float>>>>> sourceFileEntry : pairStrengthMap
//				.entrySet()) {
//			for (Entry<String, List<Map<Integer, Map<String, Float>>>> targetFileEntry : sourceFileEntry.getValue()
//					.entrySet()) {
//				for (Map<Integer, Map<String, Float>> listOfDifferentRelationsForTarget : targetFileEntry.getValue()) {
//					for (Entry<Integer, Map<String, Float>> contents_unknown : listOfDifferentRelationsForTarget
//							.entrySet()) {
//						for (Entry<String, Float> pairWiseStrength : contents_unknown.getValue().entrySet()) {
//							if (!listOfDatesRenamed.contains(pairWiseStrength.getKey()))
//								listOfDatesRenamed.add(pairWiseStrength.getKey());
//						}
//					}
//				}
//			}
//			Collections.sort(listOfDatesRenamed);
//			Map<String, Float> strengthValues = new HashMap<String, Float>();
//			for (Entry<String, List<Map<Integer, Map<String, Float>>>> targetFileEntry : sourceFileEntry.getValue()
//					.entrySet()) {
//				for (Map<Integer, Map<String, Float>> listOfDifferentRelationsForTarget : targetFileEntry.getValue()) {
//					for (Entry<Integer, Map<String, Float>> contents_unknown : listOfDifferentRelationsForTarget
//							.entrySet()) {
//						List<Entry<String, Float>> pairWiseStrength = new ArrayList<Entry<String, Float>>();
//						pairWiseStrength.addAll(contents_unknown.getValue().entrySet());
//						for (int pairWiseStrengthEntrIndex = 0; pairWiseStrengthEntrIndex < pairWiseStrength.size()
//								- 1; pairWiseStrengthEntrIndex++) {
//							for (int validStrengthValues = listOfDatesRenamed.indexOf(pairWiseStrength
//									.get(pairWiseStrengthEntrIndex).getKey()); validStrengthValues < listOfDatesRenamed
//											.indexOf(pairWiseStrength.get(pairWiseStrengthEntrIndex + 1)
//													.getKey()); validStrengthValues++) {
//								float strength = strengthValues
//										.getOrDefault(listOfDatesRenamed.get(validStrengthValues), 0f);
//								strengthValues.put(pairWiseStrength.get(pairWiseStrengthEntrIndex).getKey(),
//										(float) (strength + globalDecay.get(sourceFileEntry.getKey())
//												.get(targetFileEntry.getKey())
//												.get(pairWiseStrength.get(pairWiseStrengthEntrIndex).getKey())
//												* pairWiseStrength.get(pairWiseStrengthEntrIndex).getValue()));
//							}
//						}
//					}
//				}
//			}
//			perFileTotalStrength.put(sourceFileEntry.getKey(), strengthValues);
//		}
//		for (Entry<String, Map<String, Float>> entryOfAccumulatedStrength : perFileTotalStrength.entrySet())
//			System.out.println(entryOfAccumulatedStrength.getValue());
//		System.out.println("Before Rias Strength Accumulation Code");
//		for each sourceFileId look into the list of pairwise strengths and find all dates for which there are calculated pairwise strengths 
//		for each of the dates add all the pairwise strenghts calculated for each date plus all pairwise strengths from previous pairwise calculations (appropriately decayed)
*/

		
		for (String sourceFileId : sourceFileId2SomeNumber2Date2CommitsOnDate.keySet()) {
			targetFileIdPairStrengthSubMap = pairStrengthMap.get(sourceFileId);
			targetFileIdStrengthContributionCountMap = new LinkedHashMap<>();
			targetFileIdCountExistenceMap = new LinkedHashMap<>();
			for (String targetFileId : targetFileIdPairStrengthSubMap.keySet()) {
				targetFileIdStrengthContributionCountMap.put(targetFileId, 0);
				targetFileIdCountExistenceMap.put(targetFileId, 0);
			}
			for (String sourceFileIdInPairStrengthMap : pairStrengthMap.keySet()) {

				if (sourceFileIdInPairStrengthMap.equals(sourceFileId)) {
					sortedListOfCommitDatesForFile = new ArrayList<>();
//					whats is yearColumn??
					Map<Integer, Map<String, Integer>> number2Date2NumberOfCommitsOnDate = sourceFileId2SomeNumber2Date2CommitsOnDate
							.get(sourceFileId);
					for (int unimportantKey : number2Date2NumberOfCommitsOnDate.keySet()) {
						Map<String, Integer> date2NumberOfCommitsOnDate = number2Date2NumberOfCommitsOnDate
								.get(unimportantKey);
						sortedListOfCommitDatesForFile.addAll(date2NumberOfCommitsOnDate.keySet());
					}
					Collections.sort(sortedListOfCommitDatesForFile);
					Map<String, List<Map<Integer, Map<String, Float>>>> targetFileId2ListOfCoCommitIndex2Date2PairWiseStrengthOnDate = pairStrengthMap
							.get(sourceFileIdInPairStrengthMap);

					Map<String, Float> overallStrengthMap = new LinkedHashMap<>();
					for (String currentDate : sortedListOfCommitDatesForFile) {
						float overallStrengthOnCurrentDate = 0.0f;
						contributingToTotalStrengthTargetFileIds = new LinkedList<>();
						for (String targetFileId : targetFileId2ListOfCoCommitIndex2Date2PairWiseStrengthOnDate
								.keySet()) {
							for (Map<Integer, Map<String, Float>> numberOfCoCommits2Dates2PairwiseStrengthOnDate : targetFileId2ListOfCoCommitIndex2Date2PairWiseStrengthOnDate
									.get(targetFileId)) {
								for (int unimportantKey : numberOfCoCommits2Dates2PairwiseStrengthOnDate.keySet()) {
									Map<String, Float> date2PairwiseStrengthMap = numberOfCoCommits2Dates2PairwiseStrengthOnDate
											.get(unimportantKey);
									if (date2PairwiseStrengthMap.containsKey(currentDate)) {
										float pairwiseStrengthOnCurrentDateForTargetFileId = 0.0f;
										pairwiseStrengthOnCurrentDateForTargetFileId = date2PairwiseStrengthMap
												.get(currentDate);
										targetFileIdStrengthContributionCountMap.put(targetFileId, 1);

										contributingToTotalStrengthTargetFileIds.add(targetFileId);
										overallStrengthOnCurrentDate += pairwiseStrengthOnCurrentDateForTargetFileId;
									}

								}
							}
						}

						List<String> listOfTargetFileIdsHavingValidValuesForCurrentDatesOverallStrengthCalculation = new LinkedList<>();
						if (!contributingToTotalStrengthTargetFileIds.isEmpty()) {
							for (String targetFileIdLocal : targetFileIdStrengthContributionCountMap.keySet()) {
//								if the targetFileIdLocal has been found in a previous date but not the current date then add it 
//								to listOfTargetFileIdsHavingValidValuesForCurrentDatesOverallStrengthCalculation
								if (!(contributingToTotalStrengthTargetFileIds.contains(targetFileIdLocal))
										&& (targetFileIdStrengthContributionCountMap.get(targetFileIdLocal) == 1)) {
									listOfTargetFileIdsHavingValidValuesForCurrentDatesOverallStrengthCalculation.add(targetFileIdLocal);
								}
							}
							indexOfCurrentDateInSourceFileSortedListOfCommitDates = sortedListOfCommitDatesForFile.indexOf(currentDate);
//							if this is not the first commit date for the sourceFileId
							if (indexOfCurrentDateInSourceFileSortedListOfCommitDates > 0) {
//								for each of the TargetFileIds in the listOfTargetFileIdsHavingValidValuesForCurrentDatesOverallStrengthCalculation
								for (String targetFileIdForValidValue : listOfTargetFileIdsHavingValidValuesForCurrentDatesOverallStrengthCalculation) {
									float decaySt = 0;
									float st = 0;
									Iterator<Map<Integer, Map<String, Float>>> pairIteratorTry = pairStrengthMap
											.get(sourceFileId).get(targetFileIdForValidValue).iterator();
									Map<Integer, Map<String, Float>> pairMapping;
									Set<String> pairTry = new TreeSet<>();
									while (pairIteratorTry.hasNext()) {
										pairMapping = (Map<Integer, Map<String, Float>>) pairIteratorTry.next();
										for (int i : pairMapping.keySet()) {
											Map<String, Float> pairs = pairMapping.get(i);
											for (String is : pairs.keySet()) {
												pairTry.add(is);

											}
										}
									}
									pairIteratorTry = null; // Bug 003: Explicity using garbage Collector
									String prev = ((TreeSet<String>) pairTry).floor(currentDate);
									if (prev != null) {
										Iterator<Map<Integer, Map<String, Float>>> pairIterator = pairStrengthMap
												.get(sourceFileId).get(targetFileIdForValidValue).iterator();
										Map<Integer, Map<String, Float>> pairMappings;
										while (pairIterator.hasNext()) {
											pairMappings = (Map<Integer, Map<String, Float>>) pairIterator.next();
											for (int i : pairMappings.keySet()) {
												Map<String, Float> pairs = pairMappings.get(i);
												if (pairs.containsKey(prev)) {
													st = pairs.get(prev);
												}
											}
										}
										pairIterator = null;// Bug 003: Explicity using garbage Collector
									}

									decay = globalDecay.get(sourceFileId).get(targetFileIdForValidValue).get(currentDate);
									decaySt = (float) (st * decay);
									overallStrengthOnCurrentDate += decaySt;

								}

							}
						}

						overallStrengthMap.put(currentDate, overallStrengthOnCurrentDate);

					}

					overallStrengthDateMap.put(sourceFileId, overallStrengthMap);
				}

			}

		}
		System.out.println("After Rias Strength Accumulation Code");

//		for (Entry<String, Map<String, Float>> strength : overallStrengthDateMap.entrySet())
//			for (Entry<String, Float> dateStrength : strength.getValue().entrySet())
//				if (perFileTotalStrength.get(strength.getKey()).get(dateStrength.getKey()) != dateStrength.getValue())
//					System.out.println("false");
		return overallStrengthDateMap;
	}

}
