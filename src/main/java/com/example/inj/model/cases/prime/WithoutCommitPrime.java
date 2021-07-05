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
//Case 3’- How many times the File A has been committed without File B/ Number of time A is committed so far
public class WithoutCommitPrime {

    ReadingStrategy readingStrategy;
    CommittedSoFar committedSoFar;

    Logger logger = LoggerFactory.getLogger(WithoutCommitPrime.class);

    @Autowired
    public void setReadingStrategy(ReadingStrategyImp readingStrategy) {
        this.readingStrategy = ReadingStrategyImp.getInstance();
    }

    @Autowired
    public void setCommittedSoFar(CommittedSoFar committedSoFar) {
        this.committedSoFar = committedSoFar;
    }

    Map<String, Map<String, Map<String, Float>>> coTimeDifference = new HashMap<>();

    public void getTimeDifference()
    {
    {
        Map<String,Map<String,Integer>> commitYears= committedSoFar.getYearMap();
        Map<String,Map<String,Map<Integer, List<Object>>>> readMap= readingStrategy.getReadableMappingFinalI().rowMap();
        Map<Integer,String> dict= readingStrategy.getDictionaryI();
        Map<String,String> dictCommit=readingStrategy.getDictionaryStringI();
        Map<String,String> dictDate=readingStrategy.getDictionaryTimeI();
        Map<String, Map<String, Map<String, Float>>> coTimeDifferences = new HashMap<>();

        for(String source: readMap.keySet())
        {
            Map<String,Map<String,Float>> destTimeDiffMap= new HashMap<>();
            /*System.out.println("Inside-1");*/
            for(String destination: readMap.get(source).keySet())
            {

                    /*System.out.println("Inside-2");*/
                    Map<String, Float> subCoTimeDifference= new HashMap<>();
                    List<Integer> committedTogetherKeys= new ArrayList<>();
                    committedTogetherKeys.addAll(readMap.get(source).get(destination).keySet());
                    /*System.out.println("Inside-3");*/
                    List<String> sourceCommitKeys= new ArrayList<>();
                    sourceCommitKeys.addAll( commitYears.get(source).keySet());
                    /*System.out.println("Inside-4");*/
                    Collections.sort(sourceCommitKeys);
                    TreeSet<String> sourceSetCommitKeys= new TreeSet<>();
                    sourceSetCommitKeys.addAll(sourceCommitKeys);
                    TreeSet<String> destinationCommitKeys= new TreeSet<>();
                    if(commitYears.containsKey(destination)) {
                        destinationCommitKeys.addAll(commitYears.get(destination).keySet());
                    }
                    /*System.out.println("Inside-5");*/
                    List<String> togetherKeys= new ArrayList<>();
                    for(int key: committedTogetherKeys)
                    {
                        if(dict.containsKey(key) && dictDate.containsKey(dict.get(key)))
                        {
                            togetherKeys.add( dictDate.get(dict.get(key)));
                        }
                    }
                    Collections.sort(togetherKeys);
                    TreeSet<String> togetherSetKeys= new TreeSet<>();
                    togetherSetKeys.addAll(togetherKeys);

                    for(String src: sourceCommitKeys)
                    {
                               if(togetherSetKeys.floor(src)!=null) {
                               String dest = togetherSetKeys.floor(src);
                                   //System.out.println("Stage-3");
                               if(dest.equals(src)) {
                                   int indexOfSource= sourceCommitKeys.indexOf(src)+1; //2+1 =3
                                   int idxs=sourceCommitKeys.indexOf(src); // 2
                                   //System.out.println("Stage-1");
                                   int indexOfDestination=0;
                                   float difference=0;
                                   for(int idx=idxs-1; idx >= 0; idx--)
                                   {
                                       String sourceKey= sourceCommitKeys.get(idx);
                                       if(destinationCommitKeys.contains(sourceKey))
                                       {
                                           indexOfDestination=sourceCommitKeys.indexOf(sourceKey)+1; //0+1
                                           //System.out.println("Stage-2");
                                           difference=((float)indexOfSource-(float)indexOfDestination -1)/(float)indexOfSource; //3-1/3 =2/3
                                           break;
                                       }
                                   }
                                   subCoTimeDifference.put(src, difference);
                               }
                           }

                    }

                    destTimeDiffMap.put(destination,subCoTimeDifference);

            }
            coTimeDifferences.put(source,destTimeDiffMap);
        }

        setCoTimeDifference(coTimeDifferences);
        //logger.info("Inside Co-Time Difference");
        //logger.info(coTimeDifferences.toString());
        //logger.info(coTimeDifferences.get("fd572165-1ed0-11eb-b50b-482ae32cf5b4").get("fd57217c-1ed0-11eb-9afd-482ae32cf5b4").toString());
        //System.exit(0);
    }
}
}
