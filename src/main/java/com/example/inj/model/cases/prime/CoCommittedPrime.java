package com.example.inj.model.cases.prime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.inj.model.storage.DataRepository;
import com.example.inj.StrategyFactory.ReadingStrategy.IReadingStrategy;

//Case 1'' Number of times the file A&B are co-committed * 2/  Number of times A has been committed so far + Number of times B has been committed so far
public class CoCommittedPrime {

	private IReadingStrategy readingStrategy;
	private CommittedSoFar committedSoFar;
	private Map<String,Map<String,Map<String, Float>>> mapOfCoCommitOverSumOfCommitsRatioForAllFileCombinations= new HashMap<>();//Source,Destination,Commit_Date,Value
	private Map<String,Map<String, Map<String,Float>>> mapOfCoCommitOverSourceFileCommitRatioForAllFileCombinations= new HashMap<>();
	private DataRepository dataRepository;
    Logger logger= LoggerFactory.getLogger(CoCommittedPrime.class);
    
    
    public CoCommittedPrime() {
    	dataRepository = DataRepository.getInstance();
    }


    public void setCommittedSoFar(CommittedSoFar committedSoFar) {
        this.committedSoFar = committedSoFar;
    }


    //Number of time (A&B) are co-committed/ Number of time A is committed so far

    public void getCoCommittedFiles()
    {

        Map<String, Map<String, Map<Integer, List<Object>>>> readMap = dataRepository.getReadableMappingFinal().rowMap();
//        Map<Integer,String> dictMap = dataRepository.getDictionary(); //id,commit_ID
//        Map<String,String> dictDate = dataRepository.getDictionaryTime();//Commit_ID,Time&Date
        Map<String,Map<String, Integer>> yearMap = dataRepository.getYearMap();
       /*System.out.println("Inside getCoCommittedFiles");*/

        for(String source: readMap.keySet())
        {
            Map<String,Map<String,Float>> coCommitOverSumOfCommitsRatioForSpecificTargetFile= new HashMap<>();
            Map<String,Map<String,Float>> coCommitOverSourceFileCommitRatioForSpecificTargetFile= new HashMap<>();
            /*System.out.println("Before");*/
            for(String destination: readMap.get(source).keySet())
            {
                /*System.out.println("After");*/
                //logger.info("Inside co-committed Prime - STage 1");
                Map<String,Float> coCommitOverSumOfCommitsRatio= new HashMap<>();
                Map<String,Float> coCommitOverSourceFileCommitRatio= new HashMap<>();
                int sourceFileCommitsSoFar=0;
                int destinationFileCommitsSoFar=0;
//                boolean flag=false;
                List<Integer> commitKeys= new ArrayList<>();
                List<String> commitDates= new ArrayList<>();
                commitKeys.addAll(readMap.get(source).get(destination).keySet());
                for(int key: commitKeys)
                {
                        commitDates.add((String) readMap.get(source).get(destination).get(key).get(10));
                }
                Collections.sort(commitDates);
                //Number of times the file A&B are co-committed*2

                //logger.info("Inside co-committed Prime - STage 2");
                for(String cDate: commitDates)
                {
                    int countTogether=(commitDates.indexOf(cDate)+1)*2;
                    int countTogether2=(commitDates.indexOf(cDate)+1);
                    if(yearMap.containsKey(source))
                    {
//                        TreeSet<String> sourceSet= new TreeSet<>();
//                        sourceSet.addAll(yearMap.get(source).keySet());
//                        String sourceDate=sourceSet.floor(cDate);
//                        if(yearMap.get(source).containsKey(sourceDate)) {
                        sourceFileCommitsSoFar = yearMap.get(source).get(cDate);
//                        }
                    }
                    if(yearMap.containsKey(destination))
                    {
//                        TreeSet<String> destinationSet= new TreeSet<>();
//                        destinationSet.addAll(yearMap.get(destination).keySet());
//                        String destinationDate=destinationSet.floor(cDate);
//                        if(yearMap.get(destination).containsKey(destinationDate)) {
                            destinationFileCommitsSoFar = yearMap.get(destination).get(cDate);
//                        }
                    }

                    //logger.info("Inside co-committed Prime - STage 3");
                    int countIndividual= sourceFileCommitsSoFar + destinationFileCommitsSoFar;
//                  number of times the two files have been committed together over the times the have been committed in total so far 
//                  TimesCommitted(SourceFile With TargetFile) / (TimesCommitted(SourceFile) + TimesCommitted(TargetFile))
                    float overall=(float)countTogether/(float)countIndividual;
//                  2 * TimesCommitted(SourceFile With TargetFile) / TimesCommitted(SourceFile)
                    float overall2=(float)countTogether2/(float)sourceFileCommitsSoFar;
                    coCommitOverSumOfCommitsRatio.put(cDate,overall);
                    coCommitOverSourceFileCommitRatio.put(cDate,overall2);
                }
                coCommitOverSumOfCommitsRatioForSpecificTargetFile.put(destination,coCommitOverSumOfCommitsRatio);
                coCommitOverSourceFileCommitRatioForSpecificTargetFile.put(destination,coCommitOverSourceFileCommitRatio);

//                readMap.get(source).get(destination).entrySet().
//				stream().map(Map.Entry::getValue).collect(Collectors.toList()).
//				stream().map(x -> {return (String) x.get(10);}).collect(Collectors.toList());
                
            }

            mapOfCoCommitOverSumOfCommitsRatioForAllFileCombinations.put(source,coCommitOverSumOfCommitsRatioForSpecificTargetFile);
            mapOfCoCommitOverSourceFileCommitRatioForAllFileCombinations.put(source, coCommitOverSourceFileCommitRatioForSpecificTargetFile);
            dataRepository.setΜapOfCoCommitOverSumOfCommitsRatioForAllFileCombinations(mapOfCoCommitOverSumOfCommitsRatioForAllFileCombinations);
            dataRepository.setΜapOfCoCommitOverSourceFileCommitRatioForAllFileCombinations(mapOfCoCommitOverSourceFileCommitRatioForAllFileCombinations);

            /*logger.info("Inside co-committed Prime - STage 6");*/

        }
        /*logger.info("Inside committedPrimeValue");*/
        //logger.info(committedPrimeValue.toString());

    }


	public Map<String, Map<String, Map<String, Float>>> getCommittedPrimeValue() {
		return mapOfCoCommitOverSumOfCommitsRatioForAllFileCombinations;
	}


	public void setCommittedPrimeValue(Map<String, Map<String, Map<String, Float>>> committedPrimeValue) {
		this.mapOfCoCommitOverSumOfCommitsRatioForAllFileCombinations = committedPrimeValue;
	}


	public Map<String, Map<String, Map<String, Float>>> getCommittedTogetherValue() {
		return mapOfCoCommitOverSourceFileCommitRatioForAllFileCombinations;
	}


	public void setCommittedTogetherValue(Map<String, Map<String, Map<String, Float>>> committedTogetherValue) {
		this.mapOfCoCommitOverSourceFileCommitRatioForAllFileCombinations = committedTogetherValue;
	}


	public IReadingStrategy getReadingStrategy() {
		return readingStrategy;
	}


	public CommittedSoFar getCommittedSoFar() {
		return committedSoFar;
	}

}
