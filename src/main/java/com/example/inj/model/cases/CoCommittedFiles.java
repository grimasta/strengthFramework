package com.example.inj.model.cases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import com.example.inj.model.storage.DataRepository;
import com.example.inj.readingStrategy.strategy.IReadingStrategy;

import javafx.util.Pair;

//Case-1 Number of times the file A&B are co-committed
public class CoCommittedFiles {

	private DataRepository dataRepository;
	private Pair<Map<String, Map<String, Map<Integer, Map<String, Integer>>>>, Map<String, Map<Integer, Map<String, Integer>>>> pairMaps;

	public CoCommittedFiles() {
		dataRepository = DataRepository.getInstance();
	}

	public Pair<Map<String, Map<String, Map<Integer, Map<String, Integer>>>>, Map<String, Map<Integer, Map<String, Integer>>>> getPairMaps() {
		return pairMaps;
	}

	public void setPairMaps(
			Pair<Map<String, Map<String, Map<Integer, Map<String, Integer>>>>, Map<String, Map<Integer, Map<String, Integer>>>> pairMaps) {
		this.pairMaps = pairMaps;
	}

	// Case-1 Number of times the file A&B are co-committed
	public void coCommitABCD() {

		Map<String, Map<String, Map<Integer, List<Object>>>> fileId2fileId2CoOccurrence2Data = dataRepository
				.getReadableMappingFinal().rowMap();

		Map<String, Integer> yearMap = new HashMap<>();
		// Start: For Excel
		Map<String, Map<Integer, Map<String, Integer>>> yearMapExcel3 = new HashMap<>();
		Map<Integer, Map<String, Integer>> yearMapExcel33 = new HashMap<>();
		// End: For Excel

		// Start: Commit-ID
		Map<String, Map<String, Map<Integer, Map<String, Integer>>>> commitExcelxyzs = new LinkedHashMap<>();
		Map<String, Map<Integer, Map<String, Integer>>> commitExcelmaps = new LinkedHashMap<>();
		Map<Integer, Map<String, Integer>> potentiallyTargetFileIdToDatesAndMultiplicitiesOfCoCommitsWithAnArbitrarySourceFileId = new LinkedHashMap<>();
		// End: Commit-ID

		List<Map<String, Integer>> ListAB;

		for (String sourceFileId : fileId2fileId2CoOccurrence2Data.keySet()) {
			ListAB = new ArrayList<>();
			Map<String, Map<Integer, List<Object>>> allTargetIds2CoOccurrence2DataSubMaps = fileId2fileId2CoOccurrence2Data
					.get(sourceFileId);
			String targetId = "";
			for (Map.Entry<String, Map<Integer, List<Object>>> targetId2CoOccurences2DataEntry : allTargetIds2CoOccurrence2DataSubMaps
					.entrySet()) {
				int numberOfTimesSourceFileIdAndTargetFileIdWereCoCommittedOnParticularDate = 0;
				int count = 0;
//                targetId = targetId2CoOccurences2DataEntry.getKey();
				Map<String, Integer> datesOfCoOccurrenceBetweenSourceFileIdAndTargetFileId = new TreeMap<>();
				potentiallyTargetFileIdToDatesAndMultiplicitiesOfCoCommitsWithAnArbitrarySourceFileId = new TreeMap<>();
//                Map<Integer, List<Object>> coOccurences2Data = 
				List<Object> coOccurrenceData = null;
				String coCommitDate = "";
				for (Map.Entry<Integer, List<Object>> coOccurrenceNumber2Data : targetId2CoOccurences2DataEntry
						.getValue().entrySet()) {
					Map<String, Integer> datesOfCoCommitsToNumberOfCommitsOnParticularDate = new TreeMap<>();// For excel
					Map<String, Integer> yearMap33 = new TreeMap<>();// For excel
					count = 0;
					numberOfTimesSourceFileIdAndTargetFileIdWereCoCommittedOnParticularDate = 0;
					int increasingIndexOfSourceTargetCoCommits = coOccurrenceNumber2Data.getKey();
					coOccurrenceData = coOccurrenceNumber2Data.getValue();

					coCommitDate = coOccurrenceData.get(10).toString();

					if (datesOfCoOccurrenceBetweenSourceFileIdAndTargetFileId.containsKey(coCommitDate))
						numberOfTimesSourceFileIdAndTargetFileIdWereCoCommittedOnParticularDate = datesOfCoOccurrenceBetweenSourceFileIdAndTargetFileId.get(coCommitDate);
					datesOfCoOccurrenceBetweenSourceFileIdAndTargetFileId.put(coCommitDate, ++numberOfTimesSourceFileIdAndTargetFileIdWereCoCommittedOnParticularDate);
					// For Excel
					datesOfCoCommitsToNumberOfCommitsOnParticularDate.put(coCommitDate, numberOfTimesSourceFileIdAndTargetFileIdWereCoCommittedOnParticularDate);
					potentiallyTargetFileIdToDatesAndMultiplicitiesOfCoCommitsWithAnArbitrarySourceFileId.put(increasingIndexOfSourceTargetCoCommits, datesOfCoCommitsToNumberOfCommitsOnParticularDate);

					if (yearMap.containsKey(coCommitDate))
						count = yearMap.get(coCommitDate);
					if (!yearMapExcel33.containsKey(increasingIndexOfSourceTargetCoCommits)) {
						count = count + 1;
						yearMap.put(coCommitDate, count);
						// For Excel
						yearMap33.put(coCommitDate, count);
						yearMapExcel33.put(increasingIndexOfSourceTargetCoCommits, yearMap33);
					}
					// For Excel
				}

				ListAB.add(datesOfCoOccurrenceBetweenSourceFileIdAndTargetFileId);
				commitExcelmaps.put(targetId, potentiallyTargetFileIdToDatesAndMultiplicitiesOfCoCommitsWithAnArbitrarySourceFileId); // For Excel


			}

			commitExcelxyzs.put(sourceFileId, commitExcelmaps);
			yearMapExcel3.put(sourceFileId, yearMapExcel33); // For Excel
			commitExcelmaps = new LinkedHashMap<>();
			yearMapExcel33 = new HashMap<>();
		}
		System.out.println("Co-Committed ABCD");

//		System.out.println(commitExcelxyzs);
//		System.out.println(yearMapExcel3);
		dataRepository.setPairMaps(new Pair(commitExcelxyzs, yearMapExcel3));

	}

}
