package com.example.inj.model.cases.prime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.inj.readingStrategy.strategy.IReadingStrategy;

//Case 1'' Number of times the file A&B are co-committed * 2/  Number of times A has been committed so far + Number of times B has been committed so far
public class CoCommittedPrime {

	private IReadingStrategy readingStrategy;
	private CommittedSoFar committedSoFar;
	private Map<String,Map<String,Map<String, Float>>> committedPrimeValue= new HashMap<>();//Source,Destination,Commit_Date,Value
	private Map<String,Map<String, Map<String,Float>>> committedTogetherValue= new HashMap<>();
    Logger logger= LoggerFactory.getLogger(CoCommittedPrime.class);
    

    public void setReadingStrategy(IReadingStrategy readingStrategy) {
        this.readingStrategy = readingStrategy;
    }


    public void setCommittedSoFar(CommittedSoFar committedSoFar) {
        this.committedSoFar = committedSoFar;
    }


    //Number of time (A&B) are co-committed/ Number of time A is committed so far

    public void getCoCommittedFiles()
    {

        Map<String, Map<String, Map<Integer, List<Object>>>> readMap = readingStrategy.getReadableMappingFinalI().rowMap();
        Map<Integer,String> dictMap = readingStrategy.getDictionaryI(); //id,commit_ID
        Map<String,String> dictDate = readingStrategy.getDictionaryTimeI();//Commit_ID,Time&Date
        Map<String,Map<String, Integer>> yearMap=committedSoFar.getYearMap();
       /*System.out.println("Inside getCoCommittedFiles");*/

        for(String source: readMap.keySet())
        {
            Map<String,Map<String,Float>> subCommittedPrime= new HashMap<>();
            Map<String,Map<String,Float>> subCommittedPrime2= new HashMap<>();
            /*System.out.println("Before");*/
            for(String destination: readMap.get(source).keySet())
            {
                /*System.out.println("After");*/
                //logger.info("Inside co-committed Prime - STage 1");
                Map<String,Float> commitPrime= new HashMap<>();
                Map<String,Float> commitPrime2= new HashMap<>();
                int sourceFile=0;
                int destinationFile=0;
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
                    int coutTogether2=(commitDates.indexOf(cDate)+1);
                    if(yearMap.containsKey(source))
                    {
                        TreeSet<String> sourceSet= new TreeSet<>();
                        sourceSet.addAll(yearMap.get(source).keySet());
                        String sourceDate=sourceSet.floor(cDate);
                        if(yearMap.get(source).containsKey(sourceDate)) {
                            sourceFile = yearMap.get(source).get(sourceDate);
                        }
                        else
                        {
                           /* logger.info("Uff Key doesn't exist sourceFile " + sourceFile);*/
                        }

                        //logger.info("Inside co-committed Prime - STage 4");
                    }
                    if(yearMap.containsKey(destination))
                    {
                        TreeSet<String> destinationSet= new TreeSet<>();
                        destinationSet.addAll(yearMap.get(destination).keySet());
                        String destinationDate=destinationSet.floor(cDate);
                        if(yearMap.get(destination).containsKey(destinationDate)) {
                            destinationFile = yearMap.get(destination).get(destinationDate);
                        }
                        else
                        {

                           /* logger.info("Uff Key doesn't exist destinationFile " + destinationFile);*/
                        }

                        //logger.info("Inside co-committed Prime - STage 5");

                    }

                    //logger.info("Inside co-committed Prime - STage 3");
                    int countIndividual= sourceFile + destinationFile;
                    float overall=(float)countTogether/(float)countIndividual;
                    float overall2=(float)coutTogether2/(float)sourceFile;
                    commitPrime.put(cDate,overall);
                    commitPrime2.put(cDate,overall2);
                }
                subCommittedPrime.put(destination,commitPrime);
                subCommittedPrime2.put(destination,commitPrime2);

            }

            committedPrimeValue.put(source,subCommittedPrime);
            committedTogetherValue.put(source, subCommittedPrime2);
            setCommittedPrimeValue(committedPrimeValue);
            setCommittedTogetherValue(committedTogetherValue);

            /*logger.info("Inside co-committed Prime - STage 6");*/

        }
        /*logger.info("Inside committedPrimeValue");*/
        //logger.info(committedPrimeValue.toString());

    }


	public Map<String, Map<String, Map<String, Float>>> getCommittedPrimeValue() {
		return committedPrimeValue;
	}


	public void setCommittedPrimeValue(Map<String, Map<String, Map<String, Float>>> committedPrimeValue) {
		this.committedPrimeValue = committedPrimeValue;
	}


	public Map<String, Map<String, Map<String, Float>>> getCommittedTogetherValue() {
		return committedTogetherValue;
	}


	public void setCommittedTogetherValue(Map<String, Map<String, Map<String, Float>>> committedTogetherValue) {
		this.committedTogetherValue = committedTogetherValue;
	}


	public IReadingStrategy getReadingStrategy() {
		return readingStrategy;
	}


	public CommittedSoFar getCommittedSoFar() {
		return committedSoFar;
	}

}
