package com.example.inj.model.cases.prime;

import com.example.inj.readingStrategy.strategy.ReadingStrategy;
import com.example.inj.readingStrategy.strategy.ReadingStrategyImp;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Data
public class CommittedSoFar {
    ReadingStrategy readingStrategy;

    Logger logger= LoggerFactory.getLogger(CommittedSoFar.class);

    @Autowired
    public void setReadingStrategy(ReadingStrategyImp readingStrategy) {
        this.readingStrategy = ReadingStrategyImp.getInstance();
    }

    private Map<String,Map<String, Integer>> yearMap= new HashMap<>();
    //Number of times a file is committed so far

    public void committedSoFar()
    {
        Map<String, Map<String, Map<Integer, List<Object>>>> readMap=readingStrategy.getReadableMappingFinalI().rowMap();
        Map<Integer, String> dictionary= readingStrategy.getDictionaryI();
        //ID,Commit_ID
        Map<String, String> dictionaryTime= readingStrategy.getDictionaryTimeI();
        //Commit_ID,Date
        System.out.println("Inside Committed So Far");
        for(String source: readMap.keySet())
        {
            Map<String, Integer> subYearMap= new HashMap<>();

            List<String> commitDates= new ArrayList<>();
            List<Integer> commitSequence= new ArrayList<>();
            List<String> commitKeys = new ArrayList<>();

            for(String destination: readMap.get(source).keySet())
            {

                commitSequence.addAll(readMap.get(source).get(destination).keySet());

            }
            for(int sequence: commitSequence)
            {

                if(dictionary.containsKey(sequence) && dictionaryTime.containsKey(dictionary.get(sequence)))
                {
                    if(!commitDates.contains(dictionaryTime.get(dictionary.get(sequence)))) {
                        commitDates.add(dictionaryTime.get(dictionary.get(sequence)));
                    }
                    if(!commitKeys.contains(dictionary.get(sequence)))
                    {
                        commitKeys.add(dictionary.get(sequence));
                    }
                }
            }


            //System.out.println("source_Key" + source + "----" + commitKeys.toString() + " size " + commitKeys.size());
            Collections.sort(commitDates);
            //System.out.println("source" + source + "---" + commitDates.toString() + " size  " + commitDates.size());

            for(String initialDate: commitDates)
            {
                subYearMap.put(initialDate, commitDates.indexOf(initialDate)+1);
            }

            yearMap.put(source,subYearMap);
        }

        setYearMap(yearMap);
    /*    logger.info("Year Map Value fd57217c-1ed0-11eb-9afd-482ae32cf5b4");
        logger.info(yearMap.get("fd57217c-1ed0-11eb-9afd-482ae32cf5b4").toString());
        logger.info("Year Map Value fd572165-1ed0-11eb-b50b-482ae32cf5b4");
        logger.info(yearMap.get("fd572165-1ed0-11eb-b50b-482ae32cf5b4").toString());*/


    }

}
