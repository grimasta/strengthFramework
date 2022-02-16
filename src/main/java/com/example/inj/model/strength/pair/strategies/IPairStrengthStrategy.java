package com.example.inj.model.strength.pair.strategies;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public interface IPairStrengthStrategy {

	public Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> calculate(Map<String, Map<String, Map<Integer, List<Object>>>> readMap,
			Map<Integer, String> dictionaryKey, Map<String, String> dictionaryStringDate,
			Map<String, Map<String, Boolean>> bugFixingMap, Map<String, Map<String, Map<String, Float>>> coCommit,
			Map<String, Map<String, Map<String, Float>>> coCommitTogether,
			Map<String, Map<String, Map<String, Float>>> committedNotTogether,
			Map<String, Map<String, Map<String, Float>>> sourceLinesModified,
			Map<String, Map<String, Map<String, Float>>> destinationLinesModified);
	

}
