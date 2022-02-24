package com.example.inj.readingStrategy.strategy;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.example.inj.attributes.AttributesField;
import com.example.inj.attributes.SelectAttributes;
import com.example.inj.commitBuilder.TryCommitDetails;
import com.example.inj.commitBuilder.TryFileDetails;
import com.example.inj.commitBuilder.TryFileDetails.TryFileDetailsBuilder;
import com.example.inj.commitRepository.CommitDetails;
import com.example.inj.global.ProjectNameContainer;
import com.example.inj.global.ProjectPathContainer;
import com.example.inj.model.File2FileDetails.File2FileDetails;
import com.example.inj.model.storage.DataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;


public class DefaultReadingStrategy implements IReadingStrategy {
	Logger logger = Logger.getLogger(this.getClass());
	private HashMap<Integer, String> dictionary = new HashMap<>();
	private Table<String, String, Map<String, List<Object>>> fileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy = HashBasedTable
			.create();
	private Table<String, String, Map<Integer, List<Object>>> readableMappingSameN = HashBasedTable.create();// Bug 001:
	private Map<String, Map<String, Boolean>> readableBugFixing = new LinkedHashMap<>();
	private HashMap<String, String> dictionaryString = new LinkedHashMap<>(); // Time, CommitId
	private HashMap<String, String> dictionaryTime = new HashMap<>(); // CommitId, Time
	private Table<String, String, Map<Integer, List<Object>>> fileId2FileID2OccurencesNumber2ListOfChanges3Copy = HashBasedTable
			.create();
	private Map<String, List<String>> fileCommits = new HashMap<>(); // Imp 004
	private DataRepository dataRepository;

	public DefaultReadingStrategy() {
		this.dataRepository = DataRepository.getInstance();
	}

	private boolean isBuilt() {
//		if (dataRepository.getDictionary().isEmpty()) {
//			System.out.println("dictionary variable is empty");
//			return false;
//		}
		if (dataRepository.getFileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy().isEmpty()) {
			System.out.println("fileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy variable is empty");
			return false;
		}
		if (dataRepository.getReadableMappingSameN().isEmpty()) {
			System.out.println("readableMappingSameN variable is empty");
			return false;
		}
		if (dataRepository.getReadableBugFixing().isEmpty()) {
			System.out.println("readableBugFixing variable is empty");
			return false;
		}
		if (dataRepository.getDictionaryString().isEmpty()) {
			System.out.println("dictionaryString variable is empty");
			return false;
		}
		if (dataRepository.getDictionaryTime().isEmpty()) {
			System.out.println("dictionaryTime variable is empty");
			return false;
		}
		if (dataRepository.getReadableMappingFinal().isEmpty()) {
			System.out.println("fileId2FileID2OccurencesNumber2ListOfChanges3Copy variable is empty");
			return false;
		}
		if (dataRepository.getFileCommits().isEmpty()) {
			System.out.println("fileCommits variable is empty");
			return false;
		} else {
			return true;
		}
	}

	public Map<String, List<String>> getFileCommits() {
		return fileCommits;
	}

	public void setFileCommits(Map<String, List<String>> fileCommits) {
		this.fileCommits = fileCommits;
	}

	@Override
	public void parseData() throws IOException, ParseException {

		// parser settings
		BeanListProcessor<AttributesField> rowProcessor = new BeanListProcessor<>(AttributesField.class);
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setRowProcessor(rowProcessor);

		parserSettings.setHeaderExtractionEnabled(true);
		// setting the headers as additions and deletions are two same column name
		parserSettings.setHeaders("id", "branch", "message", "parent_id", "author", "authored_at", "committer",
				"committed_at", "commit_additions", "commit_deletions", "changed_files", "is_bug_linked",
				"is_fix_related", "is_bug_fixing", "is_refactoring", "file_path", "previous_file_path",
				"file_additions", "file_deletions", "file_id");
		// Select Attributes from enum
		parserSettings.selectFields(SelectAttributes.values());
		CsvParser parser = new CsvParser(parserSettings);

		/*
		 * Parse the excel based on date parser.beginParsing(new FileReader(new
		 * File("C:\\Users\\Carnoll\\Desktop\\IBM Project Details\\CSV Files\\gcc.csv"))
		 * ); int count=0; String[] row; List<AttributesField> beanss= new
		 * ArrayList<>(); while((row=parser.parseNext())!= null) { AttributesField
		 * af=rowProcessor.createBean(row, parser.getContext());
		 * row=af.getCommitted_at().split(" "); if(row[0].compareTo("2013-11-13") <=0)
		 * //Hardcode the date: return a.compareTo(d) * d.compareTo(b) > 0; {
		 * beanss.add(af); } } Parse the excel based on date
		 */
		// D:\Thesis-Analysis\Extras-Thesis\Project_CSV_Files\NON-RECONCILED-DATA
		// D:\Thesis-Analysis\Extras-Thesis\Project_CSV_Files\Latest
		// Excel_11_9_2020\Without_Merge_Reconciled\Done
		try {
			parser.parse(
					new FileReader(new File(ProjectPathContainer.SOURCE_PATH + ProjectNameContainer.PROJECT_NAME + ".csv")));
		} catch (Exception e) {
			System.out.println("File Not Found" + e.getMessage());
		}
		List<AttributesField> beans = rowProcessor.getBeans();
		for (AttributesField af : beans) {
//        	Create a CommitDetails object using the id_field from the current AttributesField - object
			CommitDetails cm = new CommitDetails(af.getId());
//        	add the newly created commitDetails object to the Map of Commit_ids to Commit Detail Objects  
//        	TODO (there is nothing created here this method MUST BE RENAMED) 
			cm.createCommitHashMap(af.getId());

//          create a new TryCommitDetails object (the hell if I know what it's used for and how it's different from the CommitDetails Object...
//          TODO investigate if it's possible to safely delete
//			TryCommitDetails com = 
			new TryCommitDetails.UserBuilder(af.getId()).build();
//			create a new TryFileDetails object using the necessary fields from the current AttributesField object
//          TODO clean up the TryFileDetails class and calls, its a huge mess
			TryFileDetailsBuilder tfdBuilder = new TryFileDetails.TryFileDetailsBuilder(af.getFile_id(), af.getId());
			tfdBuilder.setAddition(af.getAdditions());
			tfdBuilder.setDeletion(af.getDeletions());
			tfdBuilder.setBugFixing(af.getIs_bug_fixing());
			tfdBuilder.setCommitDate(af.getCommitted_at());
			tfdBuilder.setCaddition(af.getCadditions());
			tfdBuilder.setCdeletion(af.getCdeletions());
//			TryFileDetails tryFileDetails = 
			tfdBuilder.build();
//			TODO this line should be possible to be safely deleted            
//            TryFileDetails tom = new TryFileDetails.TryFileDetailsBuilder(af.getFile_id(), af.getId()).setAddition(af.getAdditions()).setDeletion(af.getDeletions()).setBugFixing(af.getIs_bug_fixing()).setCommitDate(af.getCommitted_at()).setCaddition(af.getCadditions()).setCdeletion(af.getCdeletions()).build();
		}

		System.out.println("Calling Table Mapping");

		createTableMapping(TryFileDetails.getCommitId2FileDetailsMap());
	}

	// Method to create and populate data structure using GuavaTable
	public void createTableMapping(HashMap<CommitDetails, List<TryFileDetails>> commitDetails2TryFileDetailsMap)
			throws ParseException {
//    	table maintaining the relation between files (includes self relations)	
//		Table<TryFileDetails, TryFileDetails, Map<Integer, List<Object>>> tryFileDetails2Itself2NumberOfOccurrences2ListOfChangesMap = HashBasedTable.create();
		List<String> fileIds = new ArrayList<>();
//      TODO give a proper name
		Table<String, String, Map<Integer, List<Object>>> fileId2FileID2OccurencesNumber2ListOfChangesForSingleFileChanges = HashBasedTable
				.create();
//        Map<String, Map<String, Map<Integer, List<Object>>>> readableMappingPure = new HashMap<>();
//      TODO give a proper name
		Table<String, String, Map<Integer, List<Object>>> fileId2FileID2OccurencesNumber2ListOfChangesForAnyTwoFiles = HashBasedTable
				.create();
//        Map<String, Map<String, Map<Integer, List<Object>>>> readableMapping2Pure = new HashMap<>();
//      TODO give a proper name
//        Map<String, Map<String, Map<Integer, List<Object>>>> readableMapping3Pure = new HashMap<>();
		// Check4 is created to capture the commitID
		Table<String, String, Map<String, List<Object>>> fileId2FileID2OccurencesNumber2ListOfChangesCheck4 = HashBasedTable
				.create();
//        Map<String, Map<String, Map<Integer, List<Object>>>> readableMappingCheck4Pure = new HashMap<>();
		// For each commit we will have list of file objects
		// Integer for occurrence and List of changes
		@SuppressWarnings("unused")
		Map<String, List<Object>> commitId2ListofChangesMap = new HashMap<>();
		int cadd = 0;
//      TODO provide proper names to the following 2 Maps getting rid of the "samesies" conventions (STRONG ROLL OF EYES)
//      TODO probably the "same" in the original name signified that that's a Map between a file and itself to "their" data ...
		Table<String, String, Map<String, List<Object>>> fileId2fileId2CommitId2ListOfFileToFileData = HashBasedTable
				.create();
//        Map<String, Map<String, Map<Integer, List<Object>>>> readableMappingSamePure = new HashMap<>();
		@SuppressWarnings("unused")
		Table<String, String, Map<Integer, List<Object>>> readableMappingSameTwo = HashBasedTable.create();
//        Map<String, Map<String, Map<Integer, List<Object>>>> readableMappingSameTwoPure = new HashMap<>();
		Map<String, List<Object>> commitId2CollectionOfData = new HashMap<>();
//        Map<String, List<Object>> commitId2CollectionOfDataTemp = new HashMap<>();
		List<Object> fileToFileDataForAParticularCommitInListFormat = new LinkedList<>();
		try {
			for (CommitDetails commitDetails : commitDetails2TryFileDetailsMap.keySet()) {
				List<TryFileDetails> FileChangeList = new LinkedList<>();
//          sameFile will contain all the TryFileDetails Objects for a particular commitDetails object :O why is it called sameFile????
				FileChangeList = commitDetails2TryFileDetailsMap.get(commitDetails);
				commitId2CollectionOfData = new LinkedHashMap<>();
				fileToFileDataForAParticularCommitInListFormat = new LinkedList<>();
//          if current commitDetails has a single modified file then populate the statistics for this commit and add to the map	commitId2CollectionOfData
				if (FileChangeList.size() == 1) {
					TryFileDetails tf = FileChangeList.get(0);
					cadd = tf.getAddition() + tf.getDeletion();

//					TODO weave into logic
					@SuppressWarnings("unused")
					File2FileDetails detailedCrossFileData = new File2FileDetails(0, 0, cadd, cadd, cadd, cadd, cadd,
							0.0, 0.0, 0.0, tf.getDate(), "RRRR", tf.isBugFixing(), tf.getCaddition(), tf.getCdeletion(),
							tf.isBugFixing());
					// 0. buggy
					fileToFileDataForAParticularCommitInListFormat.add(0);
					// 1. non-buggy
					fileToFileDataForAParticularCommitInListFormat.add(0);
					// 2. How many lines of Fi is changed
					fileToFileDataForAParticularCommitInListFormat.add(cadd);
					// 3. How many lines of Fj is changed
					fileToFileDataForAParticularCommitInListFormat.add(cadd);
					// 4. Average number of lines changed in a particular commit
					fileToFileDataForAParticularCommitInListFormat.add(cadd);
					// 5. Minimum number of lines changed in a particular commit
					fileToFileDataForAParticularCommitInListFormat.add(cadd);
					// 6. Maximum number of lines changed in a particular commit
					fileToFileDataForAParticularCommitInListFormat.add(cadd);
					// 7. Median of lines changed in a particular commit
					fileToFileDataForAParticularCommitInListFormat.add(0.0);
					// 8. Percentile of Fi in a particular commit
					fileToFileDataForAParticularCommitInListFormat.add(0.0);
					// 9. Percentile of Fj in a particular commit
					fileToFileDataForAParticularCommitInListFormat.add(0.0);
					// 10. Date of committed file Fj
					fileToFileDataForAParticularCommitInListFormat.add(tf.getDate());
					// 11.
					fileToFileDataForAParticularCommitInListFormat.add("RRRR");
					// 12. Buggy List Fi
					fileToFileDataForAParticularCommitInListFormat.add(tf.isBugFixing());
					// 13. Number of lines in a commit has modified
					fileToFileDataForAParticularCommitInListFormat.add(tf.getCaddition());
					// 14. Number of lines in a commit is deleted
					fileToFileDataForAParticularCommitInListFormat.add(tf.getCdeletion());
					// 15. BugFixing Or Not
					fileToFileDataForAParticularCommitInListFormat.add(tf.isBugFixing());
					// 16. CommitID
					fileToFileDataForAParticularCommitInListFormat.add(commitDetails.getCommitID());

//              it looks like this is a Map from CommitId to a kind of List<Object> where each one of the elements of the List is for luck of a better word.. random  	
					commitId2CollectionOfData.put(tf.getCommitId(), fileToFileDataForAParticularCommitInListFormat);

					if (fileId2fileId2CommitId2ListOfFileToFileData.contains(tf.getFileId(), tf.getFileId())) {
						fileId2fileId2CommitId2ListOfFileToFileData.get(tf.getFileId(), tf.getFileId())
								.putAll(commitId2CollectionOfData);
					} else {
						fileId2fileId2CommitId2ListOfFileToFileData.put(tf.getFileId(), tf.getFileId(),
								commitId2CollectionOfData);
					}

//                commitId2CollectionOfDataTemp = new LinkedHashMap<>();

				} else {

					// End: Bug 001: Committed as part of the file that is committed alone.

					// Populating and Creating the data structure with "X" for the (FN,FN)

					// Iterator on FileDetails of HashMap
					int cAddition = 0;
					int cDeletion = 0;
					int avgLinesChangedInCommit = 0;
					int noOflinesChangedInSourceFile = 0;
					int noOfLinesChangedInTargetFile = 0;
					List<Object> buggyList = new ArrayList<>();
					int buggy = 0;
					int nonbuggy = 0;
//					boolean tryFileDetailsSourceIsNotBugFixing = false;
//					boolean tryFileDetailsTargetIsNotBugFixing = false;

//					create the map between a commit Date and a commitID <- that's supposed to be unique not sure it's the best approach
					dictionaryString.put(FileChangeList.get(0).getDate(), commitDetails.getCommitID());
//					create the map between a commitID and a date <- that's supposed to be unique not sure it's the best approach
					dictionaryTime.put(commitDetails.getCommitID(), FileChangeList.get(0).getDate());

					for (TryFileDetails tryFileDetailSource : FileChangeList) {
						for (TryFileDetails tryFileDetailTarget : FileChangeList) {
//						calculating characteristics of file interaction within given commit
//							number of lines added to all files in entire commit
							cAddition = tryFileDetailSource.getCaddition();
//							number of lines deleted from all files in entire commit
							cDeletion = tryFileDetailSource.getCdeletion();
//							Average number of lines modified in this commit
							avgLinesChangedInCommit = (tryFileDetailSource.getAddition()
									+ tryFileDetailSource.getDeletion()) / FileChangeList.size();
//							Total number of lines modified in SourceFile
							noOflinesChangedInSourceFile = tryFileDetailSource.getAddition()
									+ tryFileDetailSource.getDeletion();
//							The most complicated thing since PeterQuills' plan in Avengers:Infinity War
//							if (!tryFileDetailSource.isBugFixing()) {
//								tryFileDetailsSourceIsNotBugFixing = true;
//							}
//							Total number of lines modified in TargetFile
							noOfLinesChangedInTargetFile = tryFileDetailTarget.getAddition()
									+ tryFileDetailTarget.getDeletion();
////							The most complicated thing since the last time I referenced PeterQuills' plan in Avengers:Infinity War
//							if (!tryFileDetailTarget.isBugFixing()) {
//								tryFileDetailsTargetIsNotBugFixing = true;
//							}
//							Seriously???
							if (tryFileDetailTarget.isBugFixing() || tryFileDetailSource.isBugFixing()) {
								++buggy;
							} else {
								++nonbuggy;
							}
							// 0.
							buggyList.add(buggy);
							// 1.
							buggyList.add(nonbuggy);
							// 2. How many lines of Fi is changed
							buggyList.add(noOflinesChangedInSourceFile);
							// 3. How many lines of Fj is changed
							buggyList.add(noOfLinesChangedInTargetFile);
							// 4. Average number of lines changed in current commit
							buggyList.add(avgLinesChangedInCommit);
							// 5. Minimum number of lines changed in current commit
							buggyList.add(commitDetails.getSortedListOfChanges().get(0));
							// 6. Maximum number of lines changed in current commit
							buggyList.add(commitDetails.getSortedListOfChanges()
									.get(commitDetails.getSortedListOfChanges().size() - 1));
							// 7. Median of lines changed in current commit
							buggyList.add(commitDetails.getMedianModifiedLines());
							// 8. Percentile of SourceFiles lines changed in current commit
							buggyList.add(tryFileDetailSource.getPercentile());
							// 9. Percentile of TargetFiles lines changed in current commit
							buggyList.add(tryFileDetailTarget.getPercentile());
							// 10. Date of commit
							buggyList.add(tryFileDetailSource.getDate());
							// TODO 11. understand what the 11 spot in the list represents 
							buggyList.add("RRRR");
							// 12. is Buggy SourceFile
							buggyList.add(tryFileDetailSource.isBugFixing());
							// 13. Number of lines added in a commit
							buggyList.add(cAddition);
							// 14. Number of lines deleted in a commit
							buggyList.add(cDeletion);
							// 15. BugFixing Or Not combination of Files (in case we have file level granularity instead of Commit level granularity)
							buggyList.add(tryFileDetailSource.isBugFixing() && tryFileDetailTarget.isBugFixing());
							// 16. CommitID
							buggyList.add(commitDetails.getCommitID());

							String sourceFileId = tryFileDetailSource.getFileId();
							String targetFileId = tryFileDetailTarget.getFileId();

							if (!sourceFileId.equals(targetFileId)) {
								if (!fileId2FileID2OccurencesNumber2ListOfChangesForAnyTwoFiles.contains(sourceFileId,
										targetFileId)) {
									fileId2FileID2OccurencesNumber2ListOfChangesForAnyTwoFiles.put(sourceFileId,
											targetFileId, new HashMap<>());
									fileId2FileID2OccurencesNumber2ListOfChangesCheck4.put(sourceFileId, targetFileId,
											new HashMap<>());
									Map<Integer, List<Object>> listOfChanges = fileId2FileID2OccurencesNumber2ListOfChangesForAnyTwoFiles
											.get(sourceFileId, targetFileId);
									listOfChanges.put(listOfChanges.size(), buggyList);
									fileId2FileID2OccurencesNumber2ListOfChangesCheck4
											.get(sourceFileId, targetFileId).put(commitDetails.getCommitID(), buggyList);
								} else {
									fileId2FileID2OccurencesNumber2ListOfChangesCheck4
											.get(sourceFileId, targetFileId).put(commitDetails.getCommitID(), buggyList);
									Map<Integer, List<Object>> listOfChanges = fileId2FileID2OccurencesNumber2ListOfChangesForAnyTwoFiles
											.get(sourceFileId, targetFileId);
									listOfChanges.put(listOfChanges.size(), buggyList);
								}

								dictionary.put(
										fileId2FileID2OccurencesNumber2ListOfChangesForAnyTwoFiles
												.get(sourceFileId, targetFileId).size() - 1,
										commitDetails.getCommitID());

							}
						}
						String fileId = tryFileDetailSource.getFileId();
						if (!fileIds.contains(fileId)) {
							fileIds.add(fileId);
							fileId2FileID2OccurencesNumber2ListOfChangesForSingleFileChanges.put(fileId, fileId,
									new HashMap<>());
							dictionary.put(-(fileId2FileID2OccurencesNumber2ListOfChangesForSingleFileChanges
									.get(fileId, fileId).size() - 1), commitDetails.getCommitID());
						}
					}
				}
			}
//			setDictionary(dictionary);
//			Readable Mapping Final and fileId2FileID2OccurencesNumber2ListOfChanges3Copy and fileId2FileID2OccurencesNumber2ListOfChangesCheck4
//			all contain the exact same data
			
			dataRepository.setFileId2FileID2OccurencesNumber2ListOfChanges3Copy(
					fileId2FileID2OccurencesNumber2ListOfChangesForAnyTwoFiles);
			dataRepository.setReadableMappingSameN(fileId2FileID2OccurencesNumber2ListOfChangesForSingleFileChanges);
			dataRepository.setDictionary(dictionary);
			dataRepository.setDictionaryString(dictionaryString);
			dataRepository.setDictionaryTime(dictionaryTime);
//			setDictionaryString(dictionaryString); // Bug 002: Committed as part of commitID to be added in the sample data
//			setDictionaryTime(dictionaryTime);

			IsBugFixing();
			dataRepository.getReadableMappingFinal().putAll(fileId2FileID2OccurencesNumber2ListOfChangesForAnyTwoFiles);
			dataRepository.getFileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy()
					.putAll(fileId2FileID2OccurencesNumber2ListOfChangesCheck4);
			// Added for parameters in excel

			dataRepository.setFileCommits(makeFileIds2CommitIdsMap(commitDetails2TryFileDetailsMap));
			if (!isBuilt())
				System.exit(1);
		} catch (Exception e) {
			System.out.println(e.getClass() + "\n");
		}

//		setDictionary(dictionary);
//		setfileId2FileID2OccurencesNumber2ListOfChanges3Copy(fileId2FileID2OccurencesNumber2ListOfChanges3);
//		setReadableMappingSameN(readableMappingSameTwo);
//		setDictionaryString(dictionaryString); // Bug 002: Committed as part of commitID to be added in the sample data
//		setDictionaryTime(dictionaryTime);
//		IsBugFixing();
//		fileId2FileID2OccurencesNumber2ListOfChanges3Copy.putAll(fileId2FileID2OccurencesNumber2ListOfChanges3);
//		fileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy
//				.putAll(fileId2FileID2OccurencesNumber2ListOfChangesCheck4); // Added for parameters in excel
//
//		setFileCommits(makeFileIds2CommitIdsMap(commitDetails2TryFileDetailsMap));

		System.out.println("Generate");

		/* Start:Bug 003: Explicity using garbage Collector */
//		commitId2ListofChangesMap = null;
//		numberOfOccurences2ListOfChangesMap = null;
//		fileId2FileID2OccurencesNumber2ListOfChangesForSingleFileChanges = null;
//		fileId2FileID2OccurencesNumber2ListOfChangesForAnyTwoFiles = null;
//		fileId2fileId2CommitId2ListOfFileToFileData = null;
//		readableMappingSameTwo = null;
//		// dictionaryString=null;
//		// dictionary=null;
//		fileId2FileID2OccurencesNumber2ListOfChangesCheck4 = null;
////		tryFileDetails2Itself2NumberOfOccurrences2ListOfChangesMap = null;
//		commitDetails2TryFileDetailsMapIterator = null;

		System.out.println("Mapping is generated");
		/* End: Bug 003: Explicity using garbage Collector */

	}

	private Map<String, List<String>> makeFileIds2CommitIdsMap(
			Map<CommitDetails, List<TryFileDetails>> Commits2FileChangesMap) {
		Map<String, List<String>> efficientlyComputedCommitsPerFile = new HashMap<>();
		for (Entry<CommitDetails, List<TryFileDetails>> commitDetails2TryFileDetailsListEntry : Commits2FileChangesMap
				.entrySet())
			for (TryFileDetails currentFileWithinCommit : commitDetails2TryFileDetailsListEntry.getValue()) {
				List<String> currentFileCommitIds = efficientlyComputedCommitsPerFile
						.getOrDefault(currentFileWithinCommit.getFileId(), new ArrayList<>());
				currentFileCommitIds.add(commitDetails2TryFileDetailsListEntry.getKey().getCommitID());
				efficientlyComputedCommitsPerFile.put(currentFileWithinCommit.getFileId(), currentFileCommitIds);
			}
		return efficientlyComputedCommitsPerFile;
	}

	public void IsBugFixing() {
		Table<String, String, Map<Integer, List<Object>>> readableMappingPair = dataRepository.getReadableMappingFinal();
		Table<String, String, Map<Integer, List<Object>>> readableMappingSame = dataRepository.getReadableMappingSameN();
		Map<String, Map<String, Boolean>> outp = new LinkedHashMap<>();
//		subOut is a list from commitDates to CommitIsBugFixing values
		Map<String, Boolean> subOut = new LinkedHashMap<>();
		Map<String, Map<String, Map<Integer, List<Object>>>> readableMappingPairMap = readableMappingPair.rowMap();
		for (String row : readableMappingPairMap.keySet()) {
			for (String col : readableMappingPairMap.get(row).keySet()) {
				for (Entry<Integer, List<Object>> file2FileEntry : readableMappingPairMap.get(row).get(col).entrySet()) {
					if (!subOut.containsKey((String) file2FileEntry.getValue().get(10))) {
						subOut.put((String) file2FileEntry.getValue().get(10), (Boolean) file2FileEntry.getValue().get(15));
						/*
						 * if(row.equals("333f55df-1ed0-11eb-af13-482ae32cf5b4")); {
						 * System.out.println("Date "+ obj.get(10)+ " Boolean " + subOut.put((String)
						 * obj.get(10), (Boolean) obj.get(15))); }
						 */
					}
				}
			}

			if (readableMappingSame.contains(row, row)) {
				for (Entry<Integer, List<Object>> file2SelfEntry : readableMappingSame.get(row, row).entrySet()) {
					if (!subOut.containsKey((String) file2SelfEntry.getValue().get(10))) {
						subOut.put((String) file2SelfEntry.getValue().get(10), (Boolean) file2SelfEntry.getValue().get(15));
					}
				}
			}

			outp.put(row, subOut);
			subOut = new LinkedHashMap<>();

		}
//		outp (which is a horrible name) contains a Map from each fileId to a Map from dates to whether the change was bugFixing or not on that particular data...
//		go figure how this is used
		dataRepository.setReadableBugFixing(outp);
//		setReadableBugFixing(outp);

	}

	/*
	 * convertJson function will convert the List input to JSON output using Jackson
	 * Json API
	 */
	@Override
	public ArrayList<String> convertJson(List<AttributesField> attf) throws IOException {
		ListIterator<AttributesField> itr = attf.listIterator();
		ArrayList<String> jsonArray = new ArrayList<>();

		while (itr.hasNext()) {
			// Create ObjectMapper
			ObjectMapper mapper = new ObjectMapper();
			// Convert Object to JSON string
			String jsonFormat = mapper.writeValueAsString(itr.next());
			jsonArray.add(jsonFormat);
		}
		return jsonArray;
	}

	/*
	 * @Override public Table<String, String, Map<String, List<Object>>>
	 * getReadableMappingNI() { return getReadableMappingN(); }
	 * 
	 * @Override public Table<String, String, Map<Integer, List<Object>>>
	 * getReadableMappingI() { return getReadableMapping(); }
	 */

	@Override
	public Table<String, String, Map<Integer, List<Object>>> getReadableMappingFinal() {
		return fileId2FileID2OccurencesNumber2ListOfChanges3Copy;
	}

	public void setReadableMappingSameN(Table<String, String, Map<Integer, List<Object>>> readableMappingSameN) {
		this.readableMappingSameN = readableMappingSameN;
	}

	public Table<String, String, Map<String, List<Object>>> getfileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy() {
		return fileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy;
	}

	public Table<String, String, Map<Integer, List<Object>>> getfileId2FileID2OccurencesNumber2ListOfChanges3Copy() {
		return fileId2FileID2OccurencesNumber2ListOfChanges3Copy;
	}

	public void setfileId2FileID2OccurencesNumber2ListOfChanges3Copy(
			Table<String, String, Map<Integer, List<Object>>> readableMapping) {
		fileId2FileID2OccurencesNumber2ListOfChanges3Copy = readableMapping;
	}

	public void setfileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy(
			Table<String, String, Map<String, List<Object>>> readableMappingN) {
		this.fileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy = readableMappingN;
	}

	public void setDictionaryString(HashMap<String, String> dictionaryString) {
		this.dictionaryString = dictionaryString;
	}

	public void setDictionary(HashMap<Integer, String> dictionary) {
		this.dictionary = dictionary;
	}

	public void setReadableBugFixing(Map<String, Map<String, Boolean>> readableBugFixing) {
		this.readableBugFixing = readableBugFixing;
	}

	public Table<String, String, Map<String, List<Object>>> getFileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy() {
		return fileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy;
	}

	public void setFileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy(
			Table<String, String, Map<String, List<Object>>> fileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy) {
		this.fileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy = fileId2FileID2OccurencesNumber2ListOfChangesCheck4Copy;
	}

	public HashMap<String, String> getDictionaryTime() {
		return dictionaryTime;
	}

	public void setDictionaryTime(HashMap<String, String> dictionaryTime) {
		this.dictionaryTime = dictionaryTime;
	}

	public Table<String, String, Map<Integer, List<Object>>> getFileId2FileID2OccurencesNumber2ListOfChanges3Copy() {
		return fileId2FileID2OccurencesNumber2ListOfChanges3Copy;
	}

	public void setFileId2FileID2OccurencesNumber2ListOfChanges3Copy(
			Table<String, String, Map<Integer, List<Object>>> fileId2FileID2OccurencesNumber2ListOfChanges3Copy) {
		this.fileId2FileID2OccurencesNumber2ListOfChanges3Copy = fileId2FileID2OccurencesNumber2ListOfChanges3Copy;
	}

	public HashMap<Integer, String> getDictionary() {
		return dictionary;
	}

	public Table<String, String, Map<Integer, List<Object>>> getReadableMappingSameN() {
		return readableMappingSameN;
	}

	public Map<String, Map<String, Boolean>> getReadableBugFixing() {
		return readableBugFixing;
	}

	public HashMap<String, String> getDictionaryString() {
		return dictionaryString;
	}

}
