package com.example.inj.model.sampling;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.inj.automate.Chi2Automate;
import com.example.inj.model.storage.DataRepository;

//Modification 004- We are trying to analyze if the number of commits in the segment >2(Not equal)
// and hardcoding the increasing slope that helps to analyze whether the number of commit
// should be considered. How it can impact our research.


public class CreateSample {

	
	private DataRepository dataRepository;
	@SuppressWarnings("unused")
    private CreateVector createVector;
	@SuppressWarnings("unused")
    private Map<String, List<List<Object>>> vectorsForExcel;
	@SuppressWarnings("unused")
    private Chi2Automate chi2Automate;
	@SuppressWarnings("unused")
    private InsertExcel insertExcel;
    Logger logger = LoggerFactory.getLogger(CreateSample.class);

    public CreateSample() {
    	dataRepository = DataRepository.getInstance();
    }
    
    /*createRandomSample will create the starting point of the samples and put it in the list and make
            it available for machine learning algorithm.
            */
    public void createRandomSample() throws IOException {
        Map<String, Map<String, List<Object>>> vectorMap = dataRepository.getVectorMapGlobal();
        Map<String, List<Object>> vectorListMap;
        Map<String, List<List<Object>>> vectorFinalMap = dataRepository.getVectorFinalMapGlobal();
        List<List<Object>> vectorFinalList;
        Map<String, List<List<Object>>> vectorSampling = new LinkedHashMap<>();
        List<List<Object>> vectorSamplingList = new LinkedList<>();
        List<String> vectorList = new LinkedList<>();
        Map<String, List<List<Object>>> vectorCleanFinalMap = new LinkedHashMap<>();
        List<List<Object>> vectorCleanSamplingList = new LinkedList<>();
        List<Object> vectorCleanList = new LinkedList<>();
        Random random = new Random();
        int value;
        int size = 0;
        int noOfSample = 36;
        int i = 0;

        for (String file : vectorMap.keySet()) {
            vectorListMap = vectorMap.get(file);
            SimpleRegression simpleRegression = new SimpleRegression(true);
            if (!vectorListMap.isEmpty()) {
                vectorList.addAll(vectorListMap.keySet());
                size = vectorList.size();
                vectorFinalList = vectorFinalMap.get(file);
                /*System.out.println("################");
                vectorFinalList.forEach(e->System.out.print(e));
                System.exit(0);*/
                // Obtain a number between [0 - 49], if random.nextInt(50).
                if (noOfSample < vectorList.size()) {
                    value = random.nextInt(vectorList.size());
                     //value=1; //Just to see the new fix For Hardcoded one's

                    while (value > (size - noOfSample)) {
                        value = random.nextInt(vectorList.size());

                    }
                    while (i < noOfSample) {
                        vectorSamplingList.add(vectorFinalList.get(value));

                        value++;
                        i++;
                    }
                } else {
                    vectorSamplingList.addAll(vectorFinalList);
                }
                //LOGIC TO ADD SLOPE
                for (int k = 0; k < vectorSamplingList.size(); k++) {
                    List<Object> abc = vectorSamplingList.get(k);


                    for (int m = 2; m < abc.size() - 6; m++) //Bug 002: Committed as part of commitID to be added in the sample data
                    {                                          //3 replace by 4 // 5 replace by 6
                        simpleRegression.addData(m, (double) abc.get(m));
                    }

                    double slope = simpleRegression.getSlope();

                    vectorSamplingList.get(k).add(slope);
                    if (slope <= 0.1) {
                        vectorSamplingList.get(k).add("D");
                    } else if(slope > 0.1) {

                        vectorSamplingList.get(k).add("U");
                    }
                    else
                    {
                        vectorSamplingList.get(k).add("D");
                    }

                    simpleRegression.clear();
                    abc = new LinkedList<>();
                    //vectorSamplingList.add(vectorFinalList.get(vectorList.size()-4));
                }
                //LOGIC TO ADD SLOPE


                vectorSampling.put(file, vectorSamplingList);
                vectorSamplingList = new LinkedList<>();
                vectorFinalList = new LinkedList<>();
                vectorList = new LinkedList<>();
                i = 0;
            }
        }


        for (String file : vectorSampling.keySet()) {
            List<List<Object>> vectorSampleDoubleList = new LinkedList<>();
            vectorSampleDoubleList.addAll(vectorSampling.get(file));
            //System.out.println("Here is vector sampling double");
            //vectorSampleDoubleList.forEach(e-> System.out.print("  " + e));
            //System.exit(0);
            //logger.info("My Sample Logger");
            //logger.info(vectorSampleDoubleList.toString());
            for (int is = 0; is < vectorSampleDoubleList.size(); is++) {
                List<Object> insideBoolean = new LinkedList<>();
                insideBoolean.addAll(vectorSampleDoubleList.get(is));

                vectorCleanList.add(insideBoolean.get(0));
                vectorCleanList.add(insideBoolean.get(1));//Bug 002: Committed as part of commitID to be added in the sample data
                vectorCleanList.add(insideBoolean.get(insideBoolean.size() - 1)); //For slope notation
                //Start: Modification 004 HardCoded the slope value
                vectorCleanList.add(insideBoolean.get(insideBoolean.size() - 2)); //For slope value
                vectorCleanList.add(insideBoolean.get(insideBoolean.size() - 3)); //For bugFixing Commit String Value
                vectorCleanList.add(insideBoolean.get(insideBoolean.size() - 4)); //For commit count
                vectorCleanList.add(insideBoolean.get(insideBoolean.size() - 5));//BugFixing Commit
                //Start: Added for Piyush
                vectorCleanList.add(insideBoolean.get(insideBoolean.size() - 6));
                vectorCleanList.add(insideBoolean.get(insideBoolean.size() - 7));
                //End: Added for Piyush
                /*System.out.println("Here is the Sample");
                vectorCleanList.forEach(e->System.out.print("   "+e));
                System.exit(0);*/

                vectorCleanSamplingList.add(vectorCleanList);
                vectorCleanList = new LinkedList<>();
            }

            vectorCleanFinalMap.put(file, vectorCleanSamplingList);
            vectorCleanSamplingList = new LinkedList<>();
        }

        //System.exit(0);


        dataRepository.setVectorsForExcel(vectorCleanFinalMap);
        //System.out.println("CLEAN THE SLATE");
        //vectorCleanFinalMap.entrySet().forEach(e-> System.out.print(e));
        //System.exit(0);

       
       //insertExcel.insertDataExcel(); //This will insert data into the excel.
    }


}
