package com.example.inj.model.cases.coupled;

import com.univocity.parsers.annotations.Parsed;

public class Attributes {
    @Parsed
    String CommitID;
    @Parsed
    String Source_File_ID;
    @Parsed
    String Destination_File_ID;
    @Parsed
    int Calls;
    
	public String getCommitID() {
		return CommitID;
	}
	public void setCommitID(String commitID) {
		CommitID = commitID;
	}
	public String getSource_File_ID() {
		return Source_File_ID;
	}
	public void setSource_File_ID(String source_File_ID) {
		Source_File_ID = source_File_ID;
	}
	public String getDestination_File_ID() {
		return Destination_File_ID;
	}
	public void setDestination_File_ID(String destination_File_ID) {
		Destination_File_ID = destination_File_ID;
	}
	public int getCalls() {
		return Calls;
	}
	public void setCalls(int calls) {
		Calls = calls;
	}

}
