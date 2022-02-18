package com.example.inj.model.cases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.example.inj.model.storage.DataRepository;

public class CoCommittedFilesPrime {

    private DataRepository dataRepository;
    Map<String, Map<Integer, Map<String, Integer>>> yearMap = new HashMap<>();
    Map<String, Map<String, Map<String, Integer>>>  coCommittedFiles = new HashMap<>();

    public CoCommittedFilesPrime() {
    	dataRepository = DataRepository.getInstance();
    }
    
    public void coCommitABCD()
    {
        Map<String, Map<String, Map<Integer, List<Object>>>> readMap = dataRepository.getReadableMappingFinal().rowMap();
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



        dataRepository.setCoCommittedFiles(coCommittedFiles);

    }
}
