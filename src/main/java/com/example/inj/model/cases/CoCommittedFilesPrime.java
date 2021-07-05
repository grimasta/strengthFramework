package com.example.inj.model.cases;

import com.example.inj.readingStrategy.strategy.ReadingStrategy;
import com.example.inj.readingStrategy.strategy.ReadingStrategyImp;
import com.google.common.collect.Table;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Data
public class CoCommittedFilesPrime {

    ReadingStrategy readingStrategy;

    @Autowired
    public void setReadingStrategy(ReadingStrategyImp readingStrategy) {
        this.readingStrategy = ReadingStrategyImp.getInstance();
    }

    Map<String, Map<Integer, Map<String, Integer>>> yearMap = new HashMap<>();
    Map<String, Map<String, Map<String, Integer>>>  coCommittedFiles = new HashMap<>();

    public void coCommitABCD()
    {
        Map<String, Map<String, Map<Integer, List<Object>>>> readMap=readingStrategy.getReadableMappingFinalI().rowMap();
        TreeSet<String> yearSet= new TreeSet<>();

        for(String source: readMap.keySet())
        {
            Map<String,Map<String,Integer>> subCoCommittedFiles= new HashMap<>();
            for(String destination: readMap.get(source).keySet())
            {
                List<String> commitList= new ArrayList<>();
                for(int commitKey: readMap.get(source).get(destination).keySet())
                {
                    String commitYear=readMap.get(source).get(destination).get(commitKey).get(10).toString();
                    commitList.add(commitYear);
                    yearSet.add(commitYear);
                }
                Collections.sort(commitList);
                Map<String, Integer> commitTimesMap= new HashMap<>();
                for(String commit: commitList)
                {
                    commitTimesMap.put(commit, commitList.indexOf(commit) +1);
                }
                subCoCommittedFiles.put(destination, commitTimesMap);

            }
            coCommittedFiles.put(source,subCoCommittedFiles);
        }



        setCoCommittedFiles(coCommittedFiles);

    }
}
