package com.example.inj.attributes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncrementalData {

    //private String sourceFile;
    //private String destinationFile;
    private int total;
    private int added;
    private int deleted;
    private IncrementalType type;
    public IncrementalData( int total, int added, int deleted) {

        this.total = total;
        this.added = added;
        this.deleted = deleted;
    }

    public IncrementalType getType() {
        return type;
    }

    public void setType(IncrementalType type) {
        this.type = type;
    }
}
