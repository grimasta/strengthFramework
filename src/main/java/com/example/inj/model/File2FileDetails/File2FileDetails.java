package com.example.inj.model.File2FileDetails;

public class File2FileDetails {

//	buggyList.add(buggy);
//	// 2.
//	buggyList.add(nonbuggy);
//	// 3. How many lines of Fi is changed
//	buggyList.add(linesChangedFi);
//	// 4. How many lines of Fj is changed
//	buggyList.add(noOfLinesChangedInTargetFile);
//	// 5. Average number of lines changed in a particular commit
//	buggyList.add(avgLinesChangedCi);
//	// 6. Minimum number of lines changed in a particular commit
//	buggyList.add(sortedListLineChanged.get(0));
//	// 7. Maximum number of lines changed in a particular commit
//	buggyList.add(sortedListLineChanged.get((sortedListLineChanged.size() - 1)));
//	// 8. Median of lines changed in a particular commit
//	buggyList.add(median);
//	// 9. Percentile of Fi in a particular commit
//	buggyList.add(percentileFi);
//	// 10. Percentile of Fj in a particular commit
//	buggyList.add(percentileFj);
//	// 11. Date of committed file Fj
//	buggyList.add(date);
//	// 12.
//	buggyList.add("RRRR");
//	// 13. Buggy List Fi
//	buggyList.add(bugFi);
//	// 14. Number of lines in a commit has modified
//	buggyList.add(cAddition);
//	// 15. Number of lines in a commit is deleted
//	buggyList.add(cDeletion);
//	// 16. BugFixing Or Not
//	buggyList.add(secondBug);
	
	private int noBuggyCommits = 0;
	private int noCleanCommits = 0;
	private int noOfLinesChangedInSourceFile = 0;
	private int noOfLinesChangedInTargetFile = 0;
	private int avgLinesChangedInCommitId = 0;
	private int minimumNumberOfLinesModifiedInCommit = 0;
	private int maximumNumberOfLinesModifiedInCommit = 0;
	private double medianNumberOfLinesModifiedInCommit = 0.0;
	private double percentileOfSourceFileModificationInCommit = 0.0;
	private double percentileOfTargetFileModificationInCommit = 0.0;
	private String commitDateOfTargetFile = null;
	private String rrrrString = "RRRR";
	private boolean BuggyCommitIdsForSourceFile = false;
	private int numberOfLinesAddedInCommit = 0;
	private int numberOfLinesDeletedInCommit = 0;
	private boolean isTargetBugFixing = false;
	
	public File2FileDetails(int noBuggyCommits, int noCleanCommits, int noOfLinesChangedInSourceFile,
			int noOfLinesChangedInTargetFile, int avgLinesChangedInCommitId, int minimumNumberOfLinesModifiedInCommit,
			int maximumNumberOfLinesModifiedInCommit, double medianNumberOfLinesModifiedInCommit,
			double percentileOfSourceFileModificationInCommit, double percentileOfTargetFileModificationInCommit,
			String commitDateOfTargetFile, String rrrrString, boolean buggyCommitIdsForSourceFile,
			int numberOfLinesAddedInCommit, int numberOfLinesDeletedInCommit, boolean isTargetBugFixing) {
		super();
		this.noBuggyCommits = noBuggyCommits;
		this.noCleanCommits = noCleanCommits;
		this.noOfLinesChangedInSourceFile = noOfLinesChangedInSourceFile;
		this.noOfLinesChangedInTargetFile = noOfLinesChangedInTargetFile;
		this.avgLinesChangedInCommitId = avgLinesChangedInCommitId;
		this.minimumNumberOfLinesModifiedInCommit = minimumNumberOfLinesModifiedInCommit;
		this.maximumNumberOfLinesModifiedInCommit = maximumNumberOfLinesModifiedInCommit;
		this.medianNumberOfLinesModifiedInCommit = medianNumberOfLinesModifiedInCommit;
		this.percentileOfSourceFileModificationInCommit = percentileOfSourceFileModificationInCommit;
		this.percentileOfTargetFileModificationInCommit = percentileOfTargetFileModificationInCommit;
		this.commitDateOfTargetFile = commitDateOfTargetFile;
		this.rrrrString = rrrrString;
		BuggyCommitIdsForSourceFile = buggyCommitIdsForSourceFile;
		this.numberOfLinesAddedInCommit = numberOfLinesAddedInCommit;
		this.numberOfLinesDeletedInCommit = numberOfLinesDeletedInCommit;
		this.isTargetBugFixing = isTargetBugFixing;
	}

	public int getNoBuggyCommits() {
		return noBuggyCommits;
	}

	public int getNoCleanCommits() {
		return noCleanCommits;
	}

	public int getNoOfLinesChangedInSourceFile() {
		return noOfLinesChangedInSourceFile;
	}

	public int getNoOfLinesChangedInTargetFile() {
		return noOfLinesChangedInTargetFile;
	}

	public int getAvgLinesChangedInCommitId() {
		return avgLinesChangedInCommitId;
	}

	public int getMinimumNumberOfLinesModifiedInCommit() {
		return minimumNumberOfLinesModifiedInCommit;
	}

	public int getMaximumNumberOfLinesModifiedInCommit() {
		return maximumNumberOfLinesModifiedInCommit;
	}

	public double getMedianNumberOfLinesModifiedInCommit() {
		return medianNumberOfLinesModifiedInCommit;
	}

	public double getPercentileOfSourceFileModificationInCommit() {
		return percentileOfSourceFileModificationInCommit;
	}

	public double getPercentileOfTargetFileModificationInCommit() {
		return percentileOfTargetFileModificationInCommit;
	}

	public String getCommitDateOfTargetFile() {
		return commitDateOfTargetFile;
	}

	public String getRrrrString() {
		return rrrrString;
	}

	public boolean getBuggyCommitIdsForSourceFile() {
		return BuggyCommitIdsForSourceFile;
	}

	public int getNumberOfLinesAddedInCommit() {
		return numberOfLinesAddedInCommit;
	}

	public int getNumberOfLinesDeletedInCommit() {
		return numberOfLinesDeletedInCommit;
	}

	public boolean isTargetBugFixing() {
		return isTargetBugFixing;
	}
	
	
	
	
	
}
