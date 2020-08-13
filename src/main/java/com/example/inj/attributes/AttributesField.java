package com.example.inj.attributes;

import com.univocity.parsers.annotations.Parsed;

public class AttributesField {
    @Parsed
    String id;
    @Parsed
    String file_id;
    //Start: Added new attribute
    @Parsed
    String Cadditions;
    @Parsed
    String Cdeletions;
    //End: Added new attribute
    @Parsed
    String author;
    @Parsed
    String committed_at;
    @Parsed
    String is_bug_linked;
    @Parsed
    String sd;
    @Parsed
    String is_fix_related;
    @Parsed
    Boolean is_bug_fixing;
    @Parsed
    String is_refactoring;
    @Parsed
    String additions;
    @Parsed
    String deletions;


    public AttributesField() {
    }

    public AttributesField(String id, String file_id, String additions, String deletions, String author, String committed_at, String is_bug_linked, String sd, String is_fix_related, boolean is_bug_fixing, String is_refactoring,
                           String Cadditions, String Cdeletions ) {
        this.id = id;
        this.file_id = file_id;
        this.additions = additions;
        this.deletions = deletions;
        this.author = author;
        this.committed_at = committed_at;
        this.is_bug_linked = is_bug_linked;
        this.sd = sd;
        this.is_fix_related = is_fix_related;
        this.is_bug_fixing = is_bug_fixing;
        this.is_refactoring = is_refactoring;
        //Start: Added new attribute
        this.Cadditions= Cadditions;
        this.Cdeletions= Cdeletions;
        //End: Added new attribute
    }



    public void setId(String id) {
        this.id = id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public void setAdditions(String additions) {
        this.additions = additions;
    }

    public void setDeletions(String deletions) {
        this.deletions = deletions;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCommitted_at(String committed_at) {
        this.committed_at = committed_at;
    }

    public void setIs_bug_linked(String is_bug_linked) {
        this.is_bug_linked = is_bug_linked;
    }

    public void setSd(String sd) {
        this.sd = sd;
    }

    public void setIs_fix_related(String is_fix_related) {
        this.is_fix_related = is_fix_related;
    }

    public void setIs_bug_fixing(boolean is_bug_fixing) {
        this.is_bug_fixing = is_bug_fixing;
    }

    public void setIs_refactoring(String is_refactoring) {
        this.is_refactoring = is_refactoring;
    }

    public String getId() {
        return id;
    }

    public String getFile_id() {
        return file_id;
    }

    public String getAdditions() {
        return additions;
    }

    public String getDeletions() {
        return deletions;
    }

    public String getAuthor() {
        return author;
    }

    public String getCommitted_at() {
        return committed_at;
    }

    public String getIs_bug_linked() {
        return is_bug_linked;
    }

    public String getSd() {
        return sd;
    }

    public String getIs_fix_related() {
        return is_fix_related;
    }

    public boolean getIs_bug_fixing() {
        return is_bug_fixing;
    }

    public String getIs_refactoring() {
        return is_refactoring;
    }

    //Start: Added new attribute
    public String getCadditions() {
        return Cadditions;
    }
    public void setCadditions(String cadditions) {
        Cadditions = cadditions;
    }
    public String getCdeletions() {
        return Cdeletions;
    }
    public void setCdeletions(String cdeletions) {
        Cdeletions = cdeletions;
    }
    //End: Added new attribute


}
