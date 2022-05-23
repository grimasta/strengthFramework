package com.example.inj.Analysis;

import com.example.inj.StrategyFactory.SegmentationStrategy.ISegmentationStrategy;
import com.example.inj.model.storage.DataRepository;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SegmentationStrategyPercentage implements ISegmentationStrategy {
    @Override
    public Map<String, List<Integer>> calculateSegment(DataRepository dataRepository) {
        //String here is the file-id and
        // List<Integer> is a list holds the index number(segment boundary) in the table
        Map<String, List<Integer>> segmentMap= new HashMap<>();

        int percentage ; //what percentage of total commit number should each segment contain?


        Table localReference = dataRepository.getTableSortedByFileId();
        StringColumn uniqueFileId= localReference.stringColumn("file_id").unique();

        for(String file_id: uniqueFileId){
            List<Integer> segmentList = new ArrayList<>();
            Table table = localReference.where(
                    localReference.stringColumn("file_id").isEqualTo(file_id));

            IntColumn ic= table.intColumn("Index");
            int size= ic.size();
            if(size < 10){
                continue;
            }

            if( size<=50){
                percentage =25;     //25%
            }else if(size<= 100){
                percentage =20;
            }else if(size<=200){
                percentage =10;
            }else if(size<=500){
                percentage =5;
            }else {
                percentage =2;      //2%
            }

            int segmentSize= 100/percentage;
            segmentList.add(ic.get(0));
            //how many commits each segment should contain?
            for(int i = 1; i<segmentSize; i++){
                //segmentList.add(ic.get(i-1));
                segmentList.add(ic.get(i));
            }

            segmentMap.put(file_id, segmentList);
        }

        return segmentMap;

    }
}
