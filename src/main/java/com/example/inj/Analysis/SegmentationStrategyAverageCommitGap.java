package com.example.inj.Analysis;

import com.example.inj.ReadingStrategy.TestIReadingStrategy;
import com.example.inj.ReadingStrategy.TestReadingStrategy;
import com.example.inj.StrategyFactory.SegmentationStrategy.ISegmentationStrategy;
import com.example.inj.model.storage.DataRepository;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SegmentationStrategyAverageCommitGap implements ISegmentationStrategy {


    @Override
    public Map<String, Integer> calculateSegment(DataRepository dataRepository) {
        Map<String, Integer> segmentMap= new HashMap<>();
        Table localReference = dataRepository.getTableSortedByFileId();
        StringColumn uniqueFileId=
                localReference.stringColumn("file_id").unique();

        StringColumn commitId = dataRepository.getTableSortedByCommitTime().stringColumn("id");
        StringColumn uniqueCommitId = StringColumn.create("uci");

        for(String id:commitId ){
            if(!uniqueCommitId.contains(id)){
                uniqueCommitId.append(id);
            }
        }
        System.out.println(uniqueCommitId.size());
        for(String file_id: uniqueFileId){
            StringColumn fileInCommitHistory = localReference.where(
                    localReference.stringColumn("file_id").isEqualTo(file_id))
                    .stringColumn("id");
            int beginIndex = uniqueCommitId.firstIndexOf(fileInCommitHistory.get(0));
            int endIndex = uniqueCommitId.firstIndexOf(fileInCommitHistory.get(fileInCommitHistory.size()-1));
            int averageGap;
            int commitSize= fileInCommitHistory.size();
            if(commitSize>1){
                averageGap= (endIndex - beginIndex)/(fileInCommitHistory.size()-1) +1;
            }else{
                averageGap= endIndex - beginIndex + 1;
            }

            segmentMap.put(file_id,averageGap);
        }

        return segmentMap;
    }

    public static Map<String, Integer> calculateSegmentTest (DataRepository dataRepository) {
        Map<java.lang.String, java.lang.Integer> segmentMap= new HashMap<>();
        Table localReference = dataRepository.getTableSortedByFileId();
        StringColumn uniqueFileId=
                localReference.stringColumn("file_id").unique();

        StringColumn commitId = dataRepository.getTableSortedByCommitTime().stringColumn("id");
        StringColumn uniqueCommitId = StringColumn.create("uci");

        for(String id:commitId ){
            if(!uniqueCommitId.contains(id)){
                uniqueCommitId.append(id);
            }
        }

        for(java.lang.String file_id: uniqueFileId){
            StringColumn fileInCommitHistory = localReference.where(
                            localReference.stringColumn("file_id").isEqualTo(file_id))
                    .stringColumn("id");
            int beginIndex = uniqueCommitId.firstIndexOf(fileInCommitHistory.get(0));
            int endIndex = uniqueCommitId.firstIndexOf(fileInCommitHistory.get(fileInCommitHistory.size()-1));

            int averageGap;
            int commitSize= fileInCommitHistory.size();
            if(commitSize>1){
                averageGap= (endIndex - beginIndex)/(fileInCommitHistory.size()-1) +1;
            }else{
                averageGap= endIndex - beginIndex + 1;
            }
            segmentMap.put(file_id,averageGap);
        }

        return segmentMap;
    }
    public static void main(String[] args) {
        TestIReadingStrategy trs = new TestReadingStrategy();
        SegmentationStrategyAverageCommitGap ss = new SegmentationStrategyAverageCommitGap();
        try {
            trs.parseData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataRepository.getInstance().setSegment(
                ss.calculateSegment(DataRepository.getInstance())
        );
        Map<String, Integer> segmentMap= DataRepository.getInstance().getSegment();

        //segmentMap.forEach((key, value) -> System.out.println(key + " " + value));
    }
}
