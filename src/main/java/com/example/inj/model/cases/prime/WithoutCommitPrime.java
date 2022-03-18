package com.example.inj.model.cases.prime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.inj.model.storage.DataRepository;

//Case 3’- How many times the File A has been committed without File B/ Number of time A is committed so far
public class WithoutCommitPrime {

	DataRepository dataRepository;
	Map<String, Map<String, Map<String, Float>>> coTimeDifference = new HashMap<>();
	Logger logger = LoggerFactory.getLogger(WithoutCommitPrime.class);

	public WithoutCommitPrime() {
		dataRepository = DataRepository.getInstance();
	}

    /** 
     * getTimeDifference() get the dates of commits for each source and each destination file using the committedSoFarMap that was calculated in {@link CommittedSoFar#committedSoFar()}
     * and creates a map containing
     * for each SourceFile
     * 					---> for each DestinationFile
     * 						---> for each CoCommitDate
     * 							---> the number of times the SourceFile was committed without the DestinationFile/the total SourceFile commits so far 
     */
	public void getTimeDifference() {
		Map<String, Map<String, Integer>> committedSoFarMap = dataRepository.getYearMap();
		Map<String, Map<String, Map<Integer, List<Object>>>> readMap = dataRepository.getReadableMappingFinal()
				.rowMap();

		Map<String, Map<String, Map<String, Float>>> coTimeDifferences = new HashMap<>();
		for (String source : readMap.keySet()) {
			Map<String, Map<String, Float>> destTimeDiffMap = new HashMap<>();
			for (String destination : readMap.get(source).keySet()) {
				Map<String, Integer> sourceDatesMap = committedSoFarMap.get(source);
				Map<String, Integer> destinationDatesMap = committedSoFarMap.get(destination);
				Set<String> sourceDates = new TreeSet<>();
				sourceDates.addAll(sourceDatesMap.keySet());
				Set<String> destDates = new TreeSet<>();
				destDates.addAll(destinationDatesMap.keySet());
				sourceDates.retainAll(destDates);
				Map<String, Float> subCoTimeDifference = new HashMap<>();
				int i = 0;
				for (String date : sourceDates) {
					i ++;
					subCoTimeDifference.put(date, Float.valueOf((sourceDatesMap.get(date) - i)/sourceDatesMap.get(date)));
				}
				
				destTimeDiffMap.put(destination, subCoTimeDifference);
			}
			coTimeDifferences.put(source, destTimeDiffMap);
		}

		dataRepository.setCoTimeDifference(coTimeDifferences);

	}
}
