package com.example.inj.model.cases.prime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.inj.readingStrategy.strategy.IReadingStrategy;

public class CommittedSoFar {

	private IReadingStrategy readingStrategy;
	private Map<String, Map<String, Integer>> yearMap = new HashMap<>();
	Logger logger = LoggerFactory.getLogger(CommittedSoFar.class);

	// Number of times a file is committed so far

	public void committedSoFar() {

		Map<String, Map<String, Map<Integer, List<Object>>>> readMap = readingStrategy.getReadableMappingFinalI()
				.rowMap();
		Map<Integer, String> dictionary = readingStrategy.getDictionaryI();
		// ID,Commit_ID
		// Commit_ID,Date
		System.out.println("Inside Committed So Far");
		for (String source : readMap.keySet()) {
			Map<String, Integer> subYearMap = new HashMap<>();

			List<String> commitDates = new ArrayList<>();
			List<String> commitKeys = new ArrayList<>();

			for (String destination : readMap.get(source).keySet()) {
				List<Integer> commitSequence = new ArrayList<>();

				commitSequence.addAll(readMap.get(source).get(destination).keySet());

				for (int sequence : commitSequence) {

//                if(dictionary.containsKey(sequence) && dictionaryTime.containsKey(dictionary.get(sequence)))
//                {
//                    if(!commitDates.contains(dictionaryTime.get(dictionary.get(sequence)))) {
					commitDates.add((String) readMap.get(source).get(destination).get(sequence).get(10));
//                    }
//                    if(!commitKeys.contains(dictionary.get(sequence)))
//                    {
					commitKeys.add((String) readMap.get(source).get(destination).get(sequence).get(16));
//                    }

				}

				// System.out.println("source_Key" + source + "----" + commitKeys.toString() + "
				// size " + commitKeys.size());
				Collections.sort(commitDates);
				// System.out.println("source" + source + "---" + commitDates.toString() + "
				// size " + commitDates.size());

				for (String initialDate : commitDates) {
					subYearMap.put(initialDate, commitDates.indexOf(initialDate) + 1);
				}

			}
			yearMap.put(source, subYearMap);
		}

		setYearMap(yearMap);
		/*
		 * logger.info("Year Map Value fd57217c-1ed0-11eb-9afd-482ae32cf5b4");
		 * logger.info(yearMap.get("fd57217c-1ed0-11eb-9afd-482ae32cf5b4").toString());
		 * logger.info("Year Map Value fd572165-1ed0-11eb-b50b-482ae32cf5b4");
		 * logger.info(yearMap.get("fd572165-1ed0-11eb-b50b-482ae32cf5b4").toString());
		 */

	}

	public IReadingStrategy getReadingStrategy() {
		return readingStrategy;
	}

	public void setReadingStrategy(IReadingStrategy readingStrategy) {
		this.readingStrategy = readingStrategy;
	}

	public Map<String, Map<String, Integer>> getYearMap() {
		return yearMap;
	}

	public void setYearMap(Map<String, Map<String, Integer>> yearMap) {
		this.yearMap = yearMap;
	}

}
