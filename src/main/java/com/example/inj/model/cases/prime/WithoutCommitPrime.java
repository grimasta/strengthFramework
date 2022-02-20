package com.example.inj.model.cases.prime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	public void getTimeDifference() {
		Map<String, Map<String, Integer>> committedSoFarMap = dataRepository.getYearMap();
		Map<String, Map<String, Map<Integer, List<Object>>>> readMap = dataRepository.getReadableMappingFinal()
				.rowMap();
//		Map<Integer, String> dict = dataRepository.getDictionary();
//        Map<String,String> dictCommit=readingStrategy.getDictionaryStringI();
//		Map<String, String> dictDate = dataRepository.getDictionaryTime();
		Map<String, Map<String, Map<String, Float>>> coTimeDifferences = new HashMap<>();

		for (String source : readMap.keySet()) {
			Map<String, Map<String, Float>> destTimeDiffMap = new HashMap<>();
			/* System.out.println("Inside-1"); */
			for (String destination : readMap.get(source).keySet()) {

				/* System.out.println("Inside-2"); */
				Map<String, Float> subCoTimeDifference = new HashMap<>();
				List<Integer> committedTogetherKeys = new ArrayList<Integer>();
				committedTogetherKeys.addAll(readMap.get(source).get(destination).keySet());
				/* System.out.println("Inside-3"); */
				List<String> sourceCommitDates = new ArrayList<>();
				sourceCommitDates.addAll(committedSoFarMap.get(source).keySet());
				/* System.out.println("Inside-4"); */
				Collections.sort(sourceCommitDates);
				TreeSet<String> sourceSetCommitDates = new TreeSet<>();
				sourceSetCommitDates.addAll(committedSoFarMap.get(source).keySet());
				TreeSet<String> destinationCommitDates = new TreeSet<>();
				destinationCommitDates.addAll(committedSoFarMap.get(destination).keySet());
				/* System.out.println("Inside-5"); */
				List<String> committedTogetherDates = new ArrayList<>();
				for (int key : committedTogetherKeys) {
//                        if(dict.containsKey(key) && dictDate.containsKey(dict.get(key)))
//                        {
					committedTogetherDates.add((String) readMap.get(source).get(destination).get(key).get(10));
//                        }
				}
				Collections.sort(committedTogetherDates);
				TreeSet<String> committedTogetherSetDates = new TreeSet<>();
				committedTogetherSetDates.addAll(committedTogetherDates);
//				it looks like we calculate the time elapsed (in a very broad sense) between consecutive commits of 
//				file Source with file Target. and putting them in a Map according to present date, pointing to the difference from the past date
//				for each destination file of each source file.
//				the formula used is IndexOfCurrentCommit in list of Commits of Source file minus IndexOfLastCoCommit in list of Commits of Source File with Destination file
//				over IndexOfSourceFile... indexOfSource is the index of the date of the current commit in the list of a file's dates of commits
				
				for (String srcDate : sourceCommitDates) {
					if (committedTogetherSetDates.floor(srcDate) != null) {
						String destDate = committedTogetherSetDates.floor(srcDate);
						// System.out.println("Stage-3");
						if (destDate.equals(srcDate)) {
							int indexOfSource = sourceCommitDates.indexOf(srcDate) + 1; // 2+1 =3
							int idxs = sourceCommitDates.indexOf(srcDate); // 2
							// System.out.println("Stage-1");
							int indexOfDestination = 0;
							float difference = 0;
							for (int idx = idxs - 1; idx >= 0; idx--) {
								String sourceDate = sourceCommitDates.get(idx);
								if (destinationCommitDates.contains(sourceDate)) {
									indexOfDestination = sourceCommitDates.indexOf(sourceDate) + 1; // 0+1
									// System.out.println("Stage-2");
									difference = ((float) indexOfSource - (float) indexOfDestination - 1)
											/ (float) indexOfSource; // 3-1/3 =2/3
									break;
								}
							}
							subCoTimeDifference.put(srcDate, difference);
						}
					}

				}

				destTimeDiffMap.put(destination, subCoTimeDifference);

			}
			coTimeDifferences.put(source, destTimeDiffMap);
		}

		dataRepository.setCoTimeDifference(coTimeDifferences);
		// logger.info("Inside Co-Time Difference");
		// logger.info(coTimeDifferences.toString());
		// logger.info(coTimeDifferences.get("fd572165-1ed0-11eb-b50b-482ae32cf5b4").get("fd57217c-1ed0-11eb-9afd-482ae32cf5b4").toString());
		// System.exit(0);
	}
}
