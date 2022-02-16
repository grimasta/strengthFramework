package com.example.inj.model.strength.pair.strategies;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DefaultPairStrengthCalculatorStrategy implements IPairStrengthStrategy {

	@Override
	public Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> calculate(Map<String, Map<String, Map<Integer, List<Object>>>> readMap,
			Map<Integer, String> dictionaryKey, Map<String, String> dictionaryStringDate,
			Map<String, Map<String, Boolean>> bugFixingMap, Map<String, Map<String, Map<String, Float>>> coCommit,
			Map<String, Map<String, Map<String, Float>>> coCommitTogether,
			Map<String, Map<String, Map<String, Float>>> committedNotTogether,
			Map<String, Map<String, Map<String, Float>>> sourceLinesModified,
			Map<String, Map<String, Map<String, Float>>> destinationLinesModified) {
		float pairStrength = 0.0f;
		Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> localPairStrength = new HashMap<>();
		for (String source : readMap.keySet()) {
			Map<String, List<Map<Integer, Map<String, Float>>>> pairStrengthSubMap = new LinkedHashMap<>();
			for (String destination : readMap.get(source).keySet()) {
				Map<String, Float> pairStrengthSubThreeMap = new TreeMap<>();
				Map<Integer, Map<String, Float>> pairStrengthSubTwoMap = new TreeMap<>();
				List<Map<Integer, Map<String, Float>>> pairSubTwoMapList = new LinkedList<>();
				for (int commitKey : readMap.get(source).get(destination).keySet()) {
//					String commitTime = dictionaryStringDate.get(dictionaryKey.get(commitKey));
					String commitTime = (String) readMap.get(source).get(destination).get(commitKey).get(10);
					float coCommitValue = coCommit.get(source).get(destination).get(commitTime); // Case 1''
					float coCommitTogetherValue = coCommitTogether.get(source).get(destination).get(commitTime); // Case
																													// 2
//		                    float callsValue=0.0f;
					/*
					 * if(callsMap.containsKey(source) &&
					 * callsMap.get(source).containsKey(destination) &&
					 * callsMap.get(source).get(destination).containsKey(commitTime)) {
					 * callsValue=callsMap.get(source).get(destination).get(commitTime); }
					 */
					float commitNotTog = 0.0f;
					if (committedNotTogether.containsKey(source)
							&& committedNotTogether.get(source).containsKey(destination)
							&& committedNotTogether.get(source).get(destination).containsKey(commitTime)) {
						commitNotTog = committedNotTogether.get(source).get(destination).get(commitTime);
					}

					float sourceLinesModify = 0.0f;
					if (sourceLinesModified.containsKey(source)
							&& sourceLinesModified.get(source).containsKey(destination)
							&& sourceLinesModified.get(source).get(destination).containsKey(commitTime)) {
						// logger.info("Inside 1");
						sourceLinesModify = sourceLinesModified.get(source).get(destination).get(commitTime);
					}
					float destinationLinesModify = 0.0f;
					if (destinationLinesModified.containsKey(source)
							&& destinationLinesModified.get(source).containsKey(destination)
							&& destinationLinesModified.get(source).get(destination).containsKey(commitTime)) {
						// logger.info("Inside 2");
						destinationLinesModify = destinationLinesModified.get(source).get(destination).get(commitTime);
					}
					// replce- with+
					if ((bugFixingMap.containsKey(source) && bugFixingMap.get(source).containsKey(commitTime)
							&& bugFixingMap.get(source).get(commitTime))
							&& (bugFixingMap.containsKey(source)
									&& bugFixingMap.get(destination).containsKey(commitTime)
									&& bugFixingMap.get(destination).get(commitTime))) {
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
					pairStrengthSubTwoMap.put(commitKey, pairStrengthSubThreeMap);
					pairSubTwoMapList.add(pairStrengthSubTwoMap);
					pairStrengthSubThreeMap = new TreeMap<>();
					pairStrengthSubTwoMap = new TreeMap<>();
				}
				pairStrengthSubMap.put(destination, pairSubTwoMapList);

			}
			localPairStrength.put(source, pairStrengthSubMap);
		}
		return localPairStrength;
	}

}
