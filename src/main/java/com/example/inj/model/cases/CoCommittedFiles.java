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
		Map<Integer, Map<String, Integer>> commitFinalMaps = new LinkedHashMap<>();
		// End: Commit-ID

		List<Map<String, Integer>> ListAB;

		for (String sourceId : fileId2fileId2CoOccurrence2Data.keySet()) {
			ListAB = new ArrayList<>();
			Map<String, Map<Integer, List<Object>>> allTargetIds2CoOccurrence2DataSubMaps = fileId2fileId2CoOccurrence2Data
					.get(sourceId);
			String targetId = "";
			for (Map.Entry<String, Map<Integer, List<Object>>> targetId2CoOccurences2DataEntry : allTargetIds2CoOccurrence2DataSubMaps
					.entrySet()) {
				int countAB = 0;
				int count = 0;
//                targetId = targetId2CoOccurences2DataEntry.getKey();
				Map<String, Integer> MapAB = new TreeMap<>();
				commitFinalMaps = new TreeMap<>();
//                Map<Integer, List<Object>> coOccurences2Data = 
				List<Object> coOccurrenceData = null;
				String coOccurrenceDate = "";
				for (Map.Entry<Integer, List<Object>> coOccurrenceNumber2Data : targetId2CoOccurences2DataEntry
						.getValue().entrySet()) {
					Map<String, Integer> MapABC = new TreeMap<>();// For excel
					Map<String, Integer> yearMap33 = new TreeMap<>();// For excel
					count = 0;
					countAB = 0;
					int coOccurrenceNumber = coOccurrenceNumber2Data.getKey();
					coOccurrenceData = coOccurrenceNumber2Data.getValue();

					coOccurrenceDate = coOccurrenceData.get(10).toString();

					if (MapAB.containsKey(coOccurrenceDate))
						countAB = MapAB.get(coOccurrenceDate);
					MapAB.put(coOccurrenceDate, ++countAB);
					// For Excel
					MapABC.put(coOccurrenceDate, countAB);
					commitFinalMaps.put(coOccurrenceNumber, MapABC);

					if (yearMap.containsKey(coOccurrenceDate))
						count = yearMap.get(coOccurrenceDate);
					if (!yearMapExcel33.containsKey(coOccurrenceNumber)) {
						count = count + 1;
						yearMap.put(coOccurrenceDate, count);
						// For Excel
						yearMap33.put(coOccurrenceDate, count);
						yearMapExcel33.put(coOccurrenceNumber, yearMap33);
					}
					// For Excel
				}

				ListAB.add(MapAB);
				commitExcelmaps.put(targetId, commitFinalMaps); // For Excel


			}

			commitExcelxyzs.put(sourceId, commitExcelmaps);
			yearMapExcel3.put(sourceId, yearMapExcel33); // For Excel
			commitExcelmaps = new LinkedHashMap<>();
			yearMapExcel33 = new HashMap<>();
		}
		System.out.println("Co-Committed ABCD");

		System.out.println(commitExcelxyzs);
		System.out.println(yearMapExcel3);
		dataRepository.setPairMaps(new Pair(commitExcelxyzs, yearMapExcel3));

	}

}
