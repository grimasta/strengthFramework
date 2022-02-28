package com.example.inj.attributes;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.univocity.parsers.annotations.Parsed;

import java.util.Map;


public class IncrementalField {
    @Parsed
    private String Commit_id;

    @Parsed
    private String Accesses;
    @Parsed
    private String Calls;
    @Parsed
    private String Inclusions;
    @Parsed
    private String Sets;

    /*public String getAccesses() {
        return Accesses;
    }*/
    //todo change to hashmap from String to incrementalData,where String is fileID
    //
    /*private List<IncrementalData>  access= new ArrayList<>();
    private List<IncrementalData>  call= new ArrayList<>();
    private List<IncrementalData>  inclusion= new ArrayList<>();
    private List<IncrementalData>  set= new ArrayList<>();
    private static boolean hasSet=false;*/

    //      Table <sourceFileId, destFileId, IncrementalData>
    private Table <String, String, IncrementalData> access = HashBasedTable.create();
    private Table <String, String, IncrementalData> call = HashBasedTable.create();
    private Table <String, String, IncrementalData> inclusion = HashBasedTable.create();
    private Table <String, String, IncrementalData> set = HashBasedTable.create();

    /*public IncrementalField(String commit_id, String accesses, String calls, String inclusions, String sets) {
        Commit_id = commit_id;
        Accesses = accesses;
        Calls = calls;
        Inclusions = inclusions;
        Sets = sets;}*/

    public void setField(){
        String[] entry = Accesses.split("],");
        //System.out.println(Arrays.toString(entry));
        for(String data:entry){
            String[] element= data.split(",");

            access.put(element[0].trim().replace("'", "").replace("[", ""),
                    element[1].trim().replace("'", ""),
                    new IncrementalData(Integer.parseInt(element[2].trim()),
                            Integer.parseInt(element[3].trim()),
                            Integer.parseInt(element[4].trim().replace("]", "")))
            );
            /*IncrementalData temp = new IncrementalData(
                    element[0].trim().replace("'", "").replace("[", ""),
                    element[1].trim().replace("'", ""),
                    Integer.parseInt(element[2].trim()),
                    Integer.parseInt(element[3].trim()),
                    Integer.parseInt(element[4].trim().replace("]", "")));*/
            //access.add(temp);
        }

        entry = Calls.split("],");
        //System.out.println(Arrays.toString(entry));
        for(String data: entry){
            String[] element= data.split(",");
            call.put(element[0].trim().replace("'", "").replace("[", ""),
                    element[1].trim().replace("'", ""),
                    new IncrementalData(Integer.parseInt(element[2].trim()),
                            Integer.parseInt(element[3].trim()),
                            Integer.parseInt(element[4].trim().replace("]", "")))
            );

            //call.add(temp);
        }

        entry = Inclusions.split("],");
        //System.out.println(Arrays.toString(entry));
        for(String data:entry){
            String[] element= data.split(",");
            inclusion.put(element[0].trim().replace("'", "").replace("[", ""),
                    element[1].trim().replace("'", ""),
                    new IncrementalData(Integer.parseInt(element[2].trim()),
                            Integer.parseInt(element[3].trim()),
                            Integer.parseInt(element[4].trim().replace("]", "")))
            );

            //inclusion.add(temp);
        }

        entry = Sets.split("],");
        //System.out.println(Arrays.toString(entry));
        for(String data:entry){
            String[] element= data.split(",");
            set.put(element[0].trim().replace("'", "").replace("[", ""),
                    element[1].trim().replace("'", ""),
                    new IncrementalData(Integer.parseInt(element[2].trim()),
                            Integer.parseInt(element[3].trim()),
                            Integer.parseInt(element[4].trim().replace("]", "")))
            );

            //set.add(temp);
        }
    }

    public boolean contains(String fileId){

        return access.containsColumn(fileId)||access.containsRow(fileId) ||
                call.containsColumn(fileId)||call.containsRow(fileId) ||
                inclusion.containsColumn(fileId)||inclusion.containsRow(fileId) ||
                set.containsColumn(fileId)||set.containsRow(fileId) ;
    }

    public  Table<String, String, IncrementalData> returnContained(String fileId){
        Table<String, String, IncrementalData> result=HashBasedTable.create();
        if(access.containsColumn(fileId)){
            Map<String,IncrementalData> columnMap= access.column(fileId);
            for(var entry : columnMap.entrySet()){
                result.put(entry.getKey(),fileId,entry.getValue());
                result.get(entry.getKey(),fileId).setType(IncrementalType.access);
            }
        }
        if(access.containsRow(fileId)){
            Map<String,IncrementalData> rowMap= access.row(fileId);
            for(var entry : rowMap.entrySet()){
                result.put(fileId, entry.getKey(),entry.getValue());
                result.get(fileId,entry.getKey()).setType(IncrementalType.access);
            }
        }

        if(call.containsColumn(fileId)){
            Map<String,IncrementalData> columnMap= call.column(fileId);
            for(var entry : columnMap.entrySet()){
                result.put(entry.getKey(),fileId,entry.getValue());
                result.get(entry.getKey(),fileId).setType(IncrementalType.call);
            }
        }
        if(call.containsRow(fileId)){
            Map<String,IncrementalData> rowMap= call.row(fileId);
            for(var entry : rowMap.entrySet()){
                result.put(fileId, entry.getKey(),entry.getValue());
                result.get(fileId, entry.getKey()).setType(IncrementalType.call);
            }
        }

        if(inclusion.containsColumn(fileId)){
            Map<String,IncrementalData> columnMap= inclusion.column(fileId);
            for(var entry : columnMap.entrySet()){
                result.put(entry.getKey(),fileId,entry.getValue());
                result.get(entry.getKey(),fileId).setType(IncrementalType.inclusion);
            }
        }
        if(inclusion.containsRow(fileId)){
            Map<String,IncrementalData> rowMap= inclusion.row(fileId);
            for(var entry : rowMap.entrySet()){
                result.put(fileId, entry.getKey(),entry.getValue());
                result.get(fileId,entry.getKey()).setType(IncrementalType.inclusion);
            }
        }

        if(set.containsColumn(fileId)){
            Map<String,IncrementalData> columnMap= set.column(fileId);
            for(var entry : columnMap.entrySet()){
                result.put(entry.getKey(),fileId,entry.getValue());
                result.get(entry.getKey(),fileId).setType(IncrementalType.set);
            }
        }
        if(set.containsRow(fileId)){
            Map<String,IncrementalData> rowMap= set.row(fileId);
            for(var entry : rowMap.entrySet()){
                result.put(fileId, entry.getKey(),entry.getValue());
                result.get(fileId,entry.getKey()).setType(IncrementalType.set);
            }
        }
        return result;
    }
    public String getCommit_id() {
        return Commit_id;
    }

    public Table<String, String, IncrementalData> getAccess() {
        return access;
    }

    public Table<String, String, IncrementalData> getCall() {
        return call;
    }

    public Table<String, String, IncrementalData> getInclusion() {
        return inclusion;
    }

    public Table<String, String, IncrementalData> getSet() {
        return set;
    }
}
