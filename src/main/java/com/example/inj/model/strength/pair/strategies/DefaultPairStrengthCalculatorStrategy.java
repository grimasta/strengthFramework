package com.example.inj.model.strength.pair.strategies;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.example.inj.model.storage.DataRepository;

public class DefaultPairStrengthCalculatorStrategy implements IPairStrengthStrategy {

	@Override
	public Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> calculate(DataRepository dataRepository){
		Map<String, Map<String, Map<Integer, List<Object>>>> readMap = dataRepository.getReadableMappingFinal().rowMap();
		Map<String, Map<String, Boolean>> bugFixingMap = dataRepository.getReadableBugFixing();
		Map<String, Map<String, Map<String, Float>>> coCommitOverSumOfCommitsRatioMap = dataRepository.getΜapOfCoCommitOverSumOfCommitsRatioForAllFileCombinations();
		Map<String, Map<String, Map<String, Float>>> coCommitOverSourceFileCommitsRatioMapTogether = dataRepository.getΜapOfCoCommitOverSourceFileCommitRatioForAllFileCombinations();
		Map<String, Map<String, Map<String, Float>>> notCommittedTogetherTimeInterval = dataRepository.getCoTimeDifference();
		Map<String, Map<String, Map<String, Float>>> sourceLinesModified = dataRepository.getLinesModifiedSource();
		Map<String, Map<String, Map<String, Float>>> destinationLinesModified = dataRepository.getLinesModifiedDestination();
		float pairStrength = 0.0f;
		Map<String, Map<String, Map<String, Float>>> allPairStrengths = new HashMap<>();
		Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> sourceFileID2DestinationFileID2ListOfcoCommitOccurenceIndex2perDatePairStrenghtMap = new HashMap<>();
		try {
		for (String sourceFileId : readMap.keySet()) {
			Map<String, List<Map<Integer, Map<String, Float>>>> destinationSpecificPairStrength = new LinkedHashMap<>();
			Map<String, Map<String, Float>> destinationPairStrengths = new HashMap<>(); 
			for (String destinationFileId : readMap.get(sourceFileId).keySet()) {
				
				Map<String, Float> perDatePairStrengthMap = new TreeMap<>();
				Map<Integer, Map<String, Float>> coCommitOccurenceIndex2perDatePairStrenghtMap = new TreeMap<>();
				List<Map<Integer, Map<String, Float>>> listOfCoCommitOccurenceIndex2perDatePairStrenghtMap = new LinkedList<>();
				for (int coCommitOccurrenceNumber : readMap.get(sourceFileId).get(destinationFileId).keySet()) {
//					String commitTime = dictionaryStringDate.get(dictionaryKey.get(commitKey));
					String coCommitDate = (String) readMap.get(sourceFileId).get(destinationFileId).get(coCommitOccurrenceNumber).get(10);
					float coCommitOverSourceCommitsRatioOnThisDate = coCommitOverSumOfCommitsRatioMap.get(sourceFileId).get(destinationFileId).get(coCommitDate); // Case 1''
					float coCommitTogetherValue = coCommitOverSourceFileCommitsRatioMapTogether.get(sourceFileId).get(destinationFileId).get(coCommitDate); // Case 2 might be the over SourceFile Variant of the cocommit ratio
					
//		                    float callsValue=0.0f;
					/*
					 * if(callsMap.containsKey(source) &&
					 * callsMap.get(source).containsKey(destination) &&
					 * callsMap.get(source).get(destination).containsKey(commitTime)) {
					 * callsValue=callsMap.get(source).get(destination).get(commitTime); }
					 */
					float notCommittedTogetherMetric = 0.0f;
//					if (notCommittedTogetherTimeInterval.containsKey(sourceFileId)
//							&& notCommittedTogetherTimeInterval.get(sourceFileId).containsKey(destinationFileId)
//							&& notCommittedTogetherTimeInterval.get(sourceFileId).get(destinationFileId).containsKey(coCommitDate)) {
						notCommittedTogetherMetric = notCommittedTogetherTimeInterval.get(sourceFileId).get(destinationFileId).get(coCommitDate);
//					}

					float sourceLinesModifiedOnDate = 0.0f;
//					if (sourceLinesModified.containsKey(sourceFileId)
//							&& sourceLinesModified.get(sourceFileId).containsKey(destinationFileId)
//							&& sourceLinesModified.get(sourceFileId).get(destinationFileId).containsKey(coCommitDate)) {
						// logger.info("Inside 1");
						sourceLinesModifiedOnDate = sourceLinesModified.get(sourceFileId).get(destinationFileId).get(coCommitDate);
//					}
					float destinationLinesModifiedOnDate = 0.0f;
//					if (destinationLinesModified.containsKey(sourceFileId)
//							&& destinationLinesModified.get(sourceFileId).containsKey(destinationFileId)
//							&& destinationLinesModified.get(sourceFileId).get(destinationFileId).containsKey(coCommitDate)) {
						// logger.info("Inside 2");
						destinationLinesModifiedOnDate = destinationLinesModified.get(sourceFileId).get(destinationFileId).get(coCommitDate);
//					}
					// replce- with+
					if ((Boolean) readMap.get(sourceFileId).get(destinationFileId).get(coCommitOccurrenceNumber).get(15)) {
//							(bugFixingMap.containsKey(sourceFileId) && bugFixingMap.get(sourceFileId).containsKey(coCommitDate)
//							&& bugFixingMap.get(sourceFileId).get(coCommitDate))
//							&& (bugFixingMap.containsKey(sourceFileId)
//									&& bugFixingMap.get(destinationFileId).containsKey(coCommitDate)
//									&& bugFixingMap.get(destinationFileId).get(coCommitDate))) {
						pairStrength = 1.5f
								* (coCommitOverSourceCommitsRatioOnThisDate + coCommitTogetherValue + sourceLinesModifiedOnDate + destinationLinesModifiedOnDate)
								- notCommittedTogetherMetric;
					} else {
						// pairStrength=coCommitValue+coCommitTogetherValue+ callsValue +
						// sourceLinesModify + destinationLinesModify - commitNotTog;
						// 0.50 //0.20
						pairStrength = 0.20f * (coCommitOverSourceCommitsRatioOnThisDate + coCommitTogetherValue + sourceLinesModifiedOnDate
								+ destinationLinesModifiedOnDate - notCommittedTogetherMetric);
					}
					// logger.info(" callsValue "+ callsValue + " coCommitValue " + coCommitValue +
					// " coCommitTogetherValue " + coCommitTogetherValue + " commitNotTog " +
					// commitNotTog + " sourceLinesModify " + sourceLinesModify + "
					// destinationLinesModify " + destinationLinesModify);

					perDatePairStrengthMap.put(coCommitDate, pairStrength);
					coCommitOccurenceIndex2perDatePairStrenghtMap.put(coCommitOccurrenceNumber, perDatePairStrengthMap);
					listOfCoCommitOccurenceIndex2perDatePairStrenghtMap.add(coCommitOccurenceIndex2perDatePairStrenghtMap);
					coCommitOccurenceIndex2perDatePairStrenghtMap = new TreeMap<>();
				}
				destinationSpecificPairStrength.put(destinationFileId, listOfCoCommitOccurenceIndex2perDatePairStrenghtMap);
				destinationPairStrengths.put(destinationFileId, perDatePairStrengthMap);
			}
			sourceFileID2DestinationFileID2ListOfcoCommitOccurenceIndex2perDatePairStrenghtMap.put(sourceFileId, destinationSpecificPairStrength);
			allPairStrengths.put(sourceFileId, destinationPairStrengths);
			dataRepository.setAllPairStrengthsSimple(allPairStrengths);
		}
		}catch(NullPointerException npe) {
			System.out.println(npe.getMessage());
		}
		return sourceFileID2DestinationFileID2ListOfcoCommitOccurenceIndex2perDatePairStrenghtMap;
	}

}
