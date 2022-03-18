package com.example.inj.model.cases.prime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.inj.model.storage.DataRepository;
import com.example.inj.readingStrategy.strategy.IReadingStrategy;

public class CommittedSoFar {

	private IReadingStrategy readingStrategy;
	private Map<String, Map<String, Integer>> yearMap = new HashMap<>();
	private DataRepository dataRepository;
	Logger logger = LoggerFactory.getLogger(CommittedSoFar.class);

	// Number of times a file is committed so far

	public CommittedSoFar() {
		dataRepository = DataRepository.getInstance();		
	}
	
	public void committedSoFar() {

		Map<String, Map<String, Map<Integer, List<Object>>>> readMap = dataRepository.getReadableMappingFinal()
				.rowMap();
		@SuppressWarnings("unused")
		Map<Integer, String> dictionary = dataRepository.getDictionary();
		// ID,Commit_ID
		// Commit_ID,Date
		System.out.println("Inside Committed So Far");
		for (String source : readMap.keySet()) {
//			Map maintaining the order of commitDates for the current source file
			Map<String, Integer> subYearMap = new HashMap<>();
//			list of commit dates for the current source file as seen in readMap
			List<String> commitDates = new ArrayList<>();
			for (String destination : readMap.get(source).keySet()) {
				for (int coCommitIndex : readMap.get(source).get(destination).keySet()) {
					commitDates.add((String) readMap.get(source).get(destination).get(coCommitIndex).get(10));
				}

			}
//			sort dates... (lol they are strings they are not dates) 
//			TODO restructure to use actual date datatype 
			Collections.sort(commitDates);
			for (String initialDate : commitDates) {
//		create a map between the dates and the ordering of them for a files commitsy?
				subYearMap.put(initialDate, commitDates.indexOf(initialDate) + 1);
			}
			yearMap.put(source, subYearMap);
		}
		dataRepository.setYearMap(yearMap);

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
