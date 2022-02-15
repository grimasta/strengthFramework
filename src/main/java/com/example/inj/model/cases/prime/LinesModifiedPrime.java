package com.example.inj.model.cases.prime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.inj.readingStrategy.strategy.IReadingStrategy;


//Fourth And Fifth Case
     /*Case-4 Start- Number of Lines of A is modified to the number of lines are modified in The COMMIT excluding A&B
     OR Number of lines of A is modified/ Total number of lines are modified in the commit excluding A&B
     And,
     Case-5 Number of lines of B is modified/ Total number of lines are modified in the commit excluding A&B
     */
@Component
public class LinesModifiedPrime {


	private IReadingStrategy readingStrategy;

    Logger logger= LoggerFactory.getLogger(CoCommittedPrime.class);

    private Map<String, Map<String, Map<String, Float>>> linesModifiedSource= new HashMap<>();
    private Map<String, Map<String, Map<String, Float>>> linesModifiedDestination= new HashMap<>();

    public void getLinesModified()
    {
        Map<String, Map<String, Map<Integer, List<Object>>>> readMap= readingStrategy.getReadableMappingFinalI().rowMap();
        Map<Integer,String> dictMap= readingStrategy.getDictionaryI(); //id,commit_ID
        Map<String,String> dictDate=readingStrategy.getDictionaryTimeI();//Commit_ID,Time&Date

        for(String source: readMap.keySet())
        {
            Map<String, Map<String, Float>> destinationModifiedLines=new HashMap<>();
            Map<String, Map<String, Float>>  sourceModifiedLines= new HashMap<>();
            for(String destination: readMap.get(source).keySet())
            {
                Map<String, Float> linesModfiedMapSource= new HashMap<>();
                //Date and Time, Value
                Map<String,Float> linesModifiedMapDestination= new HashMap<>();
                //Date and Time, Value
                for(int key: readMap.get(source).get(destination).keySet()) {

                    List<Object> listObj= readMap.get(source).get(destination).get(key);
                    float modifiedSource = (int) listObj.get(2); //Source
                    float modifiedDestination = (int) listObj.get(3); //Destination
                    float commit=  (int)listObj.get(13) + (int)listObj.get(14);//Number of Lines added in the commit + Number of lines Deleted in the commit
                    float denominator=(float)( commit-modifiedSource-modifiedDestination);
                    if(denominator!=0) {
                        float valueOfA = (float) modifiedSource / (float) (commit - modifiedSource - modifiedDestination);
                        float valueOfB = (float) modifiedDestination / (float) (commit - modifiedSource - modifiedDestination);
                        if (valueOfA < 0.0f) {
                            //logger.info("Inside valueOfA");
                            valueOfA = 0.0f;
                        }
                        if (valueOfB < 0.0f) {

                            //logger.info("Inside valueOfB");
                            valueOfB = 0.0f;
                        }

                        if (dictMap.containsKey(key) && dictDate.containsKey(dictMap.get(key))) {
                            String dateAndTime = dictDate.get(dictMap.get(key));
                            linesModfiedMapSource.put(dateAndTime, valueOfA);
                            linesModifiedMapDestination.put(dateAndTime, valueOfB);
                        }
                    }
                    else
                    {
                        if (dictMap.containsKey(key) && dictDate.containsKey(dictMap.get(key))) {
                            String dateAndTime = dictDate.get(dictMap.get(key));
                            linesModfiedMapSource.put(dateAndTime, 0.0f);
                            linesModifiedMapDestination.put(dateAndTime, 0.0f);
                        }
                    }

                }

                destinationModifiedLines.put(destination, linesModifiedMapDestination);
                sourceModifiedLines.put(destination, linesModfiedMapSource);
            }

            linesModifiedSource.put(source,destinationModifiedLines );
            linesModifiedDestination.put(source, sourceModifiedLines);
        }

        setLinesModifiedDestination(linesModifiedDestination);
        setLinesModifiedSource(linesModifiedSource);
        logger.info("Populated Lines Modified");
        ////logger.info(linesModifiedDestination.toString());
        //System.exit(0);
    }

    public Map<String, Map<String, Map<String, Float>>> getLinesModifiedSource() {
		return linesModifiedSource;
	}

	public void setLinesModifiedSource(Map<String, Map<String, Map<String, Float>>> linesModifiedSource) {
		this.linesModifiedSource = linesModifiedSource;
	}

	public Map<String, Map<String, Map<String, Float>>> getLinesModifiedDestination() {
		return linesModifiedDestination;
	}

	public void setLinesModifiedDestination(Map<String, Map<String, Map<String, Float>>> linesModifiedDestination) {
		this.linesModifiedDestination = linesModifiedDestination;
	}

	public IReadingStrategy getReadingStrategy() {
		return readingStrategy;
	}

	public void setReadingStrategy(IReadingStrategy readingStrategy) {
        this.readingStrategy = readingStrategy;
    }

    
}
