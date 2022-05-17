package com.example.inj.model.strength;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.example.inj.StrategyFactory.PairStrengthStrategy.IPairStrengthStrategy;
import com.example.inj.model.storage.DataRepository;

public class DefaultPairStrengthStrategy implements IPairStrengthStrategy {

	@Override
	public Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> calculate(DataRepository dataRepository){
		Map<String, Map<String, Map<Integer, List<Object>>>> readMap = dataRepository.getReadableMappingFinal().rowMap();
		Map<String, Map<String, Boolean>> bugFixingMap = dataRepository.getReadableBugFixing();
		Map<String, Map<String, Map<String, Float>>> coCommit = dataRepository.getΜapOfCoCommitOverSumOfCommitsRatioForAllFileCombinations();
		Map<String, Map<String, Map<String, Float>>> coCommitTogether = dataRepository.getΜapOfCoCommitOverSourceFileCommitRatioForAllFileCombinations();
		Map<String, Map<String, Map<String, Float>>> committedNotTogether = dataRepository.getCoTimeDifference();
		Map<String, Map<String, Map<String, Float>>> sourceLinesModified = dataRepository.getLinesModifiedSource();
		Map<String, Map<String, Map<String, Float>>> destinationLinesModified = dataRepository.getLinesModifiedDestination();
		float pairStrength = 0.0f;
		Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> localPairStrength = new HashMap<>();
		for (String sourceFileId : readMap.keySet()) {
			Map<String, List<Map<Integer, Map<String, Float>>>> pairStrengthSubMap = new LinkedHashMap<>();
			for (String destinationFileId : readMap.get(sourceFileId).keySet()) {
				Map<String, Float> pairStrengthSubThreeMap = new TreeMap<>();
				Map<Integer, Map<String, Float>> pairStrengthSubTwoMap = new TreeMap<>();
				List<Map<Integer, Map<String, Float>>> pairSubTwoMapList = new LinkedList<>();
				for (int coCommitOccurrenceNumber : readMap.get(sourceFileId).get(destinationFileId).keySet()) {
//					String commitTime = dictionaryStringDate.get(dictionaryKey.get(commitKey));
					String commitTime = (String) readMap.get(sourceFileId).get(destinationFileId).get(coCommitOccurrenceNumber).get(10);
					float coCommitValue = coCommit.get(sourceFileId).get(destinationFileId).get(commitTime); // Case 1''
					float coCommitTogetherValue = coCommitTogether.get(sourceFileId).get(destinationFileId).get(commitTime); // Case
																													// 2
//		                    float callsValue=0.0f;
					/*
					 * if(callsMap.containsKey(source) &&
					 * callsMap.get(source).containsKey(destination) &&
					 * callsMap.get(source).get(destination).containsKey(commitTime)) {
					 * callsValue=callsMap.get(source).get(destination).get(commitTime); }
					 */
					float commitNotTog = 0.0f;
					if (committedNotTogether.containsKey(sourceFileId)
							&& committedNotTogether.get(sourceFileId).containsKey(destinationFileId)
							&& committedNotTogether.get(sourceFileId).get(destinationFileId).containsKey(commitTime)) {
						commitNotTog = committedNotTogether.get(sourceFileId).get(destinationFileId).get(commitTime);
					}

					float sourceLinesModify = 0.0f;
					if (sourceLinesModified.containsKey(sourceFileId)
							&& sourceLinesModified.get(sourceFileId).containsKey(destinationFileId)
							&& sourceLinesModified.get(sourceFileId).get(destinationFileId).containsKey(commitTime)) {
						// logger.info("Inside 1");
						sourceLinesModify = sourceLinesModified.get(sourceFileId).get(destinationFileId).get(commitTime);
					}
					float destinationLinesModify = 0.0f;
					if (destinationLinesModified.containsKey(sourceFileId)
							&& destinationLinesModified.get(sourceFileId).containsKey(destinationFileId)
							&& destinationLinesModified.get(sourceFileId).get(destinationFileId).containsKey(commitTime)) {
						// logger.info("Inside 2");
						destinationLinesModify = destinationLinesModified.get(sourceFileId).get(destinationFileId).get(commitTime);
					}
					// replce- with+
					if ((bugFixingMap.containsKey(sourceFileId) && bugFixingMap.get(sourceFileId).containsKey(commitTime)
							&& bugFixingMap.get(sourceFileId).get(commitTime))
							&& (bugFixingMap.containsKey(sourceFileId)
									&& bugFixingMap.get(destinationFileId).containsKey(commitTime)
									&& bugFixingMap.get(destinationFileId).get(commitTime))) {
						pairStrength = 1.5f
								* (coCommitValue + coCommitTogetherValue + sourceLinesModify + destinationLinesModify)
								- commitNotTog;
					} else {
						// pairStrength=coCommitValue+coCommitTogetherValue+ callsValue +
						// sourceLinesModify + destinationLinesModify - commitNotTog;
						// 0.50 //0.20
						pairStrength = 0.20f * (coCommitValue + coCommitTogetherValue + sourceLinesModify
								+ destinationLinesModify - commitNotTog);
					}
					// logger.info(" callsValue "+ callsValue + " coCommitValue " + coCommitValue +
					// " coCommitTogetherValue " + coCommitTogetherValue + " commitNotTog " +
					// commitNotTog + " sourceLinesModify " + sourceLinesModify + "
					// destinationLinesModify " + destinationLinesModify);

					pairStrengthSubThreeMap.put(commitTime, pairStrength);
					pairStrengthSubTwoMap.put(coCommitOccurrenceNumber, pairStrengthSubThreeMap);
					pairSubTwoMapList.add(pairStrengthSubTwoMap);
					pairStrengthSubThreeMap = new TreeMap<>();
					pairStrengthSubTwoMap = new TreeMap<>();
				}
				pairStrengthSubMap.put(destinationFileId, pairSubTwoMapList);

			}
			localPairStrength.put(sourceFileId, pairStrengthSubMap);
		}
		return localPairStrength;
	}

}
