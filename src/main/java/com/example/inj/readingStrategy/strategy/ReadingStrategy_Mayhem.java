package com.example.inj.readingStrategy.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.example.inj.commitBuilder.TryFileDetails;
import com.example.inj.commitRepository.CommitDetails;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class ReadingStrategy_Mayhem {

	Logger logger = Logger.getLogger(this.getClass());
	private HashMap<Integer, String> dictionary = new HashMap<>();
	private Table<String, String, Map<String, List<Object>>> fileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy = HashBasedTable
			.create();
	private Table<String, String, Map<Integer, List<Object>>> readableMappingSameN = HashBasedTable.create();// Bug 001:
	// Committed as
	// part of the
	// file that is
	// committed
	// alone.
	private Map<String, Map<String, Boolean>> readableBugFixing = new LinkedHashMap<>();
	private HashMap<String, String> dictionaryString = new LinkedHashMap<>(); // Time, CommitId
	private HashMap<String, String> dictionaryTime = new HashMap<>(); // CommitId, Time
	private Table<String, String, Map<Integer, List<Object>>> fileId2FileID2OccurencesNumber2ListOfChanges3Copy = HashBasedTable
			.create();
	private Map<String, List<String>> fileCommits = new HashMap<>(); // Imp 004
	private static DefaultReadingStrategy instance;
	
	
	
	public void storage(HashMap<CommitDetails, List<TryFileDetails>> commitDetails2TryFileDetailsMap) {
		List<String> fileIds = new ArrayList<>();
//      TODO give a proper name
		Table<String, String, Map<Integer, List<Object>>> fileId2FileID2OccurencesNumber2ListOfChanges = HashBasedTable
				.create();
//        Map<String, Map<String, Map<Integer, List<Object>>>> readableMappingPure = new HashMap<>();
//      TODO give a proper name
		Table<String, String, Map<Integer, List<Object>>> fileId2FileID2OccurencesNumber2ListOfChanges2 = HashBasedTable
				.create();
//        Map<String, Map<String, Map<Integer, List<Object>>>> readableMapping2Pure = new HashMap<>();
//      TODO give a proper name
		Table<String, String, Map<Integer, List<Object>>> fileId2FileID2OccurencesNumber2ListOfChanges3 = HashBasedTable
				.create();
//        Map<String, Map<String, Map<Integer, List<Object>>>> readableMapping3Pure = new HashMap<>();
		// Check4 is created to capture the commitID
		Table<String, String, Map<String, List<Object>>> fileId2FileID2OccurencesNumber2ListOfChangesCheck4 = HashBasedTable
				.create();
//        Map<String, Map<String, Map<Integer, List<Object>>>> readableMappingCheck4Pure = new HashMap<>();
		// For each commit we will have list of file objects
		Iterator<Map.Entry<CommitDetails, List<TryFileDetails>>> commitDetails2TryFileDetailsMapIterator = commitDetails2TryFileDetailsMap
				.entrySet().iterator();
		// Integer for occurrence and List of changes
		Map<Integer, List<Object>> numberOfOccurences2ListOfChangesMap = new HashMap<>();
		Map<String, List<Object>> commitId2ListofChangesMap = new HashMap<>();
		int cadd = 0;
//      TODO provide proper names to the following 2 Maps getting rid of the "samesies" conventions (STRONG ROLL OF EYES)
//      TODO probably the "same" in the original name signified that that's a Map between a file and itself to "their" data ...
		Table<String, String, Map<String, List<Object>>> fileId2fileId2CommitId2ListOfFileToFileData = HashBasedTable
				.create();
//        Map<String, Map<String, Map<Integer, List<Object>>>> readableMappingSamePure = new HashMap<>();
		Table<String, String, Map<Integer, List<Object>>> readableMappingSameTwo = HashBasedTable.create();
//        Map<String, Map<String, Map<Integer, List<Object>>>> readableMappingSameTwoPure = new HashMap<>();
		Map<String, List<Object>> commitId2CollectionOfData = new HashMap<>();
//        Map<String, List<Object>> commitId2CollectionOfDataTemp = new HashMap<>();
		List<Object> fileToFileDataForAParticularCommitInListFormat = new LinkedList<>();
		int i = 0;
		int buggy;
		int nonbuggy;
		int noOflinesChangedInSourceFile = 0;
		int noOfLinesChangedInTargetFile = 0;
		int avgLinesChangedInCommit = 0;
		int FileCount = 0;
		int lines = 0;
		int index = 0;
		double percentileSourceFile = 0;
		double percentileTargetFile = 0;
		List<Integer> sortedListLineChanged = new ArrayList<>();
		double medianOfNumberOfChangedLines = 0;

		Iterator<String> guavaInnerKeyIterator;
		Iterator<String> guavaOuterKeyIterator = fileId2FileID2OccurencesNumber2ListOfChanges.rowKeySet().iterator();
		
		guavaOuterKeyIterator = fileId2FileID2OccurencesNumber2ListOfChanges2.rowKeySet().iterator();

		// Creating Sparse Vector using HashMap
		Iterator<Map.Entry<CommitDetails, List<TryFileDetails>>> commitDetails2ListOfFileDetailsIterator;

		int columnInside = 0;
		int rowInside = 0;
		int iDic = -1;
		while (guavaOuterKeyIterator.hasNext()) {
			String sourceFileId = guavaOuterKeyIterator.next();

			guavaInnerKeyIterator = fileId2FileID2OccurencesNumber2ListOfChanges2.columnKeySet().iterator();

			while (guavaInnerKeyIterator.hasNext()) {
				String targetFileId = guavaInnerKeyIterator.next();
				List<TryFileDetails> listOfTryFileDetails = new ArrayList<>();
				commitDetails2ListOfFileDetailsIterator = commitDetails2TryFileDetailsMap.entrySet().iterator();
				numberOfOccurences2ListOfChangesMap = new HashMap<>();
				commitId2ListofChangesMap = new HashMap<>();
				i = 0;
				String commitIdForCurrentFilePair = "";
				buggy = 0; // Number of times it appear as a buggy commit in a commit details
				nonbuggy = 0; // Number of times it appear as a non-buggy commit
				if (!sourceFileId.equals(targetFileId)) {
					while (commitDetails2ListOfFileDetailsIterator.hasNext()) {
						listOfTryFileDetails = commitDetails2ListOfFileDetailsIterator.next().getValue();
						Iterator<TryFileDetails> iteratorOfFileDetailsList = listOfTryFileDetails.listIterator();
						List<Object> buggyList = new ArrayList<>();
						int rowAppear = 0;
						int colAppear = 0;
						avgLinesChangedInCommit = 0;
						FileCount = 0;
						sortedListLineChanged.clear();
						noOflinesChangedInSourceFile = 0;
						noOfLinesChangedInTargetFile = 0;
						boolean tempTryFileDetailsSourceIsNotBugFixing = false;
						boolean tempTryFileDetailsTargetIsNotBugFixing = false;
						columnInside = 0;
						rowInside = 0;
						String date = null;
						int bugFi = 0;
						int cAddition = 0;
						int cDeletion = 0;
						boolean bug = false;
						boolean nonBug = true;
						boolean secondBug = false;
						// Improvising
						// Traversing against the list of a particular commit
						while (iteratorOfFileDetailsList.hasNext()) {
							TryFileDetails tempTryFileDetails = iteratorOfFileDetailsList.next();
							index = 0;

							cAddition = tempTryFileDetails.getCaddition();
							cDeletion = tempTryFileDetails.getCdeletion();
							// Average number of lines changed in a particular commit
							avgLinesChangedInCommit = avgLinesChangedInCommit + tempTryFileDetails.getAddition()
									+ tempTryFileDetails.getDeletion();
							FileCount++;
							// Average number of lines changed in a particular commit

							lines = tempTryFileDetails.getAddition() + tempTryFileDetails.getDeletion();

							sortedListLineChanged.add(lines);

							if (tempTryFileDetails.getFileId().equals(sourceFileId)) {
								secondBug = tempTryFileDetails.isBugFixing();
								rowAppear++;
								rowInside++;
								noOflinesChangedInSourceFile = tempTryFileDetails.getAddition()
										+ tempTryFileDetails.getDeletion();
								if (!tempTryFileDetails.isBugFixing()) {
									tempTryFileDetailsSourceIsNotBugFixing = true;
								}
							}
							if (tempTryFileDetails.getFileId().equals(targetFileId)) {
								colAppear++;
								columnInside++;
								date = tempTryFileDetails.getDate();

								// How many lines of Fj are changed
								noOfLinesChangedInTargetFile = tempTryFileDetails.getAddition()
										+ tempTryFileDetails.getDeletion();
								if (!tempTryFileDetails.isBugFixing()) {
									tempTryFileDetailsTargetIsNotBugFixing = true;
								}
							}

							commitIdForCurrentFilePair = tempTryFileDetails.getCommitId();
						}

						if ((rowAppear != 0 && colAppear != 0)) {
							if (tempTryFileDetailsSourceIsNotBugFixing && tempTryFileDetailsTargetIsNotBugFixing) {
								++nonbuggy;
								nonBug = true;
							} else {
								++buggy;
								++bugFi;
								bug = true;
							}
						}
						// Average number of lines changed in a particular commit
						avgLinesChangedInCommit = (int) avgLinesChangedInCommit / FileCount;
						// Average number of lines changed in a particular commit

						// Minimum lines and Maximum lines changed in a particular commit
						Collections.sort(sortedListLineChanged);

						// Median of a sorted list
//							TODO this must be extracted to a separate class as static method .. Jesus
						if (sortedListLineChanged.size() % 2 == 0) {
							medianOfNumberOfChangedLines = (double) (sortedListLineChanged
									.get(((sortedListLineChanged.size() - 1) / 2))
									+ Math.abs(sortedListLineChanged.get((sortedListLineChanged.size()) / 2))) / 2.0;
						} else {
							medianOfNumberOfChangedLines = (double) (sortedListLineChanged
									.get(sortedListLineChanged.size() / 2));
						}
						// Median of a sorted list
						// Minimum lines and Maximum lines changed in a particular commit

						// percentile of Fi in a sortedList
						index = sortedListLineChanged.indexOf(noOflinesChangedInSourceFile);

						percentileSourceFile = ((float) (index + 1) / (sortedListLineChanged.size())) * 100;
						// percentile of Fi in a sortedList

						// percentile of Fj in a sortedList
						index = sortedListLineChanged.indexOf(noOfLinesChangedInTargetFile);
						percentileTargetFile = ((float) (index + 1) / (sortedListLineChanged.size())) * 100;
						// percentile of Fj in a sortedList

						if (/* (buggy != 0) || (nonbuggy != 0)) && */ (rowInside != 0 && columnInside != 0)) {

							// System.out.println("Hey I am here");
							// 1.
							buggyList.add(buggy);
							// 2.
							buggyList.add(nonbuggy);
							// 3. How many lines of Fi is changed
							buggyList.add(noOflinesChangedInSourceFile);
							// 4. How many lines of Fj is changed
							buggyList.add(noOfLinesChangedInTargetFile);
							// 5. Average number of lines changed in a particular commit
							buggyList.add(avgLinesChangedInCommit);
							// 6. Minimum number of lines changed in a particular commit
							buggyList.add(sortedListLineChanged.get(0));
							// 7. Maximum number of lines changed in a particular commit
							buggyList.add(sortedListLineChanged.get((sortedListLineChanged.size() - 1)));
							// 8. Median of lines changed in a particular commit
							buggyList.add(medianOfNumberOfChangedLines);
							// 9. Percentile of Fi in a particular commit
							buggyList.add(percentileSourceFile);
							// 10. Percentile of Fj in a particular commit
							buggyList.add(percentileTargetFile);
							// 11. Date of committed file Fj
							buggyList.add(date);
							// 12.
							buggyList.add("RRRR");
							// 13. Buggy List Fi
							buggyList.add(bugFi);
							// 14. Number of lines in a commit has modified
							buggyList.add(cAddition);
							// 15. Number of lines in a commit is deleted
							buggyList.add(cDeletion);
							// 16. BugFixing Or Not
							buggyList.add(secondBug);

							numberOfOccurences2ListOfChangesMap.put(i, buggyList);
							commitId2ListofChangesMap.put(commitIdForCurrentFilePair, buggyList);
							dictionary.put(i, commitIdForCurrentFilePair);
							dictionaryString.put(date, commitIdForCurrentFilePair);
							dictionaryTime.put(commitIdForCurrentFilePair, date);

						}
						i = i + 1;

					}
					if (!numberOfOccurences2ListOfChangesMap.isEmpty()) {
						// Sorting a map for the function
						List<Map.Entry<Integer, List<Object>>> listSort = new LinkedList<>(
								numberOfOccurences2ListOfChangesMap.entrySet());
						Collections.sort(listSort, Comparator.comparing(o -> String.valueOf(o.getValue().get(10))));

						// scalarVector.clear();
						numberOfOccurences2ListOfChangesMap = new LinkedHashMap<>();

						for (Entry<Integer, List<Object>> numberOfOccurences2ListOfChangesMapEntry : listSort) {
							// System.out.println("Key" + stu.getKey() + "value" +stu.getValue());
							numberOfOccurences2ListOfChangesMap.put(numberOfOccurences2ListOfChangesMapEntry.getKey(),
									numberOfOccurences2ListOfChangesMapEntry.getValue());
						}
//						put into the final map the sorted by date list of occurrences	
						fileId2FileID2OccurencesNumber2ListOfChanges3.put(sourceFileId, targetFileId,
								numberOfOccurences2ListOfChangesMap);

					}
					// Start-Repeated for check 4
					if (!commitId2ListofChangesMap.isEmpty()) {
						// Sorting a map for the function
						List<Map.Entry<String, List<Object>>> listSort = new LinkedList<>(
								commitId2ListofChangesMap.entrySet());
						Collections.sort(listSort, Comparator.comparing(o -> String.valueOf(o.getValue().get(10))));

						commitId2ListofChangesMap = new LinkedHashMap<>();

						for (Map.Entry<String, List<Object>> commitId2ListofChangesMapEntry : listSort) {

							commitId2ListofChangesMap.put(commitId2ListofChangesMapEntry.getKey(),
									commitId2ListofChangesMapEntry.getValue());
						}
//						insert into the 4th map the sorted by date list of occurrences
						fileId2FileID2OccurencesNumber2ListOfChangesCheck4.put(sourceFileId, targetFileId,
								commitId2ListofChangesMap);
					}
					// End-Repeated Check 4

				}
				// Start: Bug 001: Committed as part of the file that is committed alone.
				else {
					// readableMappingSameTwo it will contain the records of file that are committed
					// alone
					if (fileId2fileId2CommitId2ListOfFileToFileData.contains(sourceFileId, targetFileId)) {
						{
							Map<String, List<Object>> fixMap;
							fixMap = fileId2fileId2CommitId2ListOfFileToFileData.get(sourceFileId, targetFileId);
							Map<Integer, List<Object>> doubFix = new LinkedHashMap<>();
							for (String k : fixMap.keySet()) {
								doubFix.put(iDic, fixMap.get(k));
								String date = (String) fixMap.get(k).get(10);
								dictionary.put(iDic, k);
								dictionaryString.put(date, k);
								dictionaryTime.put(k, date);
								// readableMapping3.put(rtfd, ctfd, doubFix);
								readableMappingSameTwo.put(sourceFileId, targetFileId, doubFix);
								iDic--;
							}

							// readableMappingCheck4.put(rtfd,ctfd, fixMap);

						}
						// Map<Integer, List<Object>> scalVec= readableMapping3.get(rtfd, ctfd);
						Map<Integer, List<Object>> scalVec = readableMappingSameTwo.get(sourceFileId, targetFileId);
						List<Map.Entry<Integer, List<Object>>> listSort = new LinkedList<>(scalVec.entrySet());
						Collections.sort(listSort, Comparator.comparing(o -> String.valueOf(o.getValue().get(10))));

						// Sort the values
						scalVec = new LinkedHashMap<>();

						for (Map.Entry<Integer, List<Object>> stu : listSort) {
							// System.out.println("Key" + stu.getKey() + "value" +stu.getValue());
							scalVec.put(stu.getKey(), stu.getValue());

						}
						// readableMapping3.put(rtfd, ctfd, scalVec);
						readableMappingSameTwo.put(sourceFileId, targetFileId, scalVec);

						scalVec = new LinkedHashMap<>();

					}

				}
				// End:Bug 001: Committed as part of the file that is committed alone.

			}

		}
	}
	
}
