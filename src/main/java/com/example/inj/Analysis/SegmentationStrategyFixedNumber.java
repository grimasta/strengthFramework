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



public class SegmentationStrategyFixedNumber implements ISegmentationStrategy {

    @Override
    public Map<String, Integer> calculateSegment(DataRepository dataRepository) {
        //String here is the file-id and
        // List<Integer> is a list holds the index number(segment boundary) in the table
        Map<String, Integer> segmentMap= new HashMap<>();

        //how many commits should each segment contain?
        int fixedNumber = 10;       //FIXME: set desire segment width

        Table localReference = dataRepository.getTableSortedByFileId();
        StringColumn uniqueFileId= localReference.stringColumn("file_id").unique();

        for(String file_id: uniqueFileId){
            List<Integer> segmentList = new ArrayList<>();

            Table table = localReference.where(
                    localReference.stringColumn("file_id").isEqualTo(file_id));

            IntColumn ic= table.intColumn("Index");
            int size= ic.size();

            if(size < fixedNumber*2){   //if we don't have at least two segment,
                                        // what we are predicting?
                continue;
            }
            segmentList.add(ic.get(0));

            for(int i = 9; i<size; i=i+ fixedNumber){
                //segmentList.add(ic.get(i-1));
                segmentList.add(ic.get(i));
            }
            //segmentMap.put(file_id, segmentList);
        }

        return segmentMap;
    }
}
