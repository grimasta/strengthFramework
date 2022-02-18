package com.example.inj.model.cases.coupled;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.inj.model.storage.DataRepository;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;


//Case6:Number of calls between A to B/ Average number of calls from A to all other co-committed files.

public class ParseCoupledCSV {
    Logger logger = LoggerFactory.getLogger(ParseCoupledCSV.class);

    private DataRepository dataRepository;
    private HashMap<String,HashMap<String,HashMap<String, Float>>> finalCallsValue= new HashMap<>();
    private HashMap<String, HashMap<String, HashMap<String, Integer>>> mapCalls = new HashMap<>();
    private HashMap<String,HashMap<String,Integer>> maxCommitCalls = new HashMap<>();
    private HashMap<String,Float> avgCommitCalls= new HashMap<>();

    public ParseCoupledCSV() {
    	dataRepository = DataRepository.getInstance();
    }
    
    public void parseData() {

        try {

            System.out.println("I am inside parseData");
            BeanListProcessor<Attributes> rowProcessor = new BeanListProcessor<>(Attributes.class);
            CsvParserSettings parserSettings = new CsvParserSettings();
            parserSettings.setRowProcessor(rowProcessor);
            parserSettings.setHeaderExtractionEnabled(true);
            parserSettings.setHeaders("CommitID", "Source_File_ID", "Destination_File_ID", "Calls");
            parserSettings.selectFields(CallAttributes.values());
            CsvParser parser = new CsvParser(parserSettings);
//            TODO debug file location for calls information
            parser.parse(new FileReader(new File("D:\\Thesis-Analysis\\Extras-Thesis\\Project_CSV_Files\\Calls_Data_Latest\\Solved\\Korganizer_Calls.csv")));

            System.out.println("I am after parseData");
            List<Attributes> beans = rowProcessor.getBeans();
            for (Attributes attr : beans) {
                if (mapCalls.containsKey(attr.getSource_File_ID())) {
                    HashMap<String, HashMap<String, Integer>> subHashMap= mapCalls.get(attr.getSource_File_ID());
                    if(subHashMap.containsKey(attr.getDestination_File_ID()))
                    {
                        HashMap<String, Integer> doubleHashMap= subHashMap.get(attr.getDestination_File_ID());
                        doubleHashMap.put(attr.getCommitID(),attr.getCalls());
                        subHashMap.put(attr.getDestination_File_ID(),doubleHashMap);
                    }
                    else
                    {
                        HashMap<String, Integer> doubleHashMap= new HashMap<>();
                        doubleHashMap.put(attr.getCommitID(),attr.getCalls());
                        subHashMap.put(attr.getDestination_File_ID(),doubleHashMap);
                    }
                    mapCalls.put(attr.getSource_File_ID(),subHashMap);//Update
                }
                else {
                    HashMap<String, HashMap<String, Integer>> topMap= new HashMap<>();
                    HashMap<String, Integer> subHashMap= new HashMap<>();
                    subHashMap.put(attr.getCommitID(),attr.getCalls());
                    topMap.put(attr.getDestination_File_ID(),subHashMap);
                    mapCalls.put(attr.getSource_File_ID(),topMap);
                }
            }
            //Setting the hashmap
            dataRepository.setMapCalls(mapCalls);
            System.out.println("Here is the map:-");

            for(Attributes attr: beans)
            {
                String commitID= attr.getCommitID();

                if(!attr.getDestination_File_ID().equals(attr.getSource_File_ID())) {
                    if (avgCommitCalls.containsKey(commitID)) {
                        float oldCallValue = avgCommitCalls.get(commitID);
                        float finalCallValue = oldCallValue + attr.getCalls();
                        avgCommitCalls.put(commitID, finalCallValue);

                    } else {
                        avgCommitCalls.put(commitID, (float) attr.getCalls());
                    }
                }
            }

            List<String> commitList= new ArrayList<>();
            for(Attributes attr: beans)
            {
                if(!attr.getDestination_File_ID().equals( attr.getSource_File_ID())) {
                    commitList.add(attr.getCommitID());
                }
            }
            for(String key: avgCommitCalls.keySet())
            {
                int freq= Collections.frequency(commitList,key);
                avgCommitCalls.put(key,avgCommitCalls.get(key)/freq);
            }
            System.out.println("Average Number of Commit Calls");
            //avgCommitCalls.entrySet().forEach(e-> System.out.println(e));

            for(String sourceID: mapCalls.keySet() )
            {
                HashMap<String, HashMap<String, Float>> subMap= new HashMap<>();
                for(String destinationID: mapCalls.get(sourceID).keySet())
                {
                    for(String commitKey: mapCalls.get(sourceID).get(destinationID).keySet())
                    {
                        HashMap<String, Float> subCommmitMap= new HashMap<>();
                        if(avgCommitCalls.containsKey(commitKey))
                        {
                            int cals= mapCalls.get(sourceID).get(destinationID).get(commitKey);
                            subCommmitMap.put(commitKey,cals/avgCommitCalls.get(commitKey));
                        }
                        subMap.put(destinationID,subCommmitMap);
                    }
                    finalCallsValue.put(sourceID,subMap);
                }
            }

            dataRepository.setFinalCallsValue(finalCallsValue);

            System.out.println("Final Calls Value");
            finalCallsValue.entrySet().forEach(e->System.out.print(e));
            //System.exit(0);

            logger.info("Inside Parsed Coupled CSV");


        } catch (FileNotFoundException e) {
            System.out.println("File not found exception during parsing CSV");
            System.exit(0);
        }
    }

    public HashMap<String, HashMap<String, HashMap<String, Float>>> getFinalCallsValue() {
		return finalCallsValue;
	}
	public void setFinalCallsValue(HashMap<String, HashMap<String, HashMap<String, Float>>> finalCallsValue) {
		this.finalCallsValue = finalCallsValue;
	}
	public HashMap<String, HashMap<String, HashMap<String, Integer>>> getMapCalls() {
		return mapCalls;
	}
	public void setMapCalls(HashMap<String, HashMap<String, HashMap<String, Integer>>> mapCalls) {
		this.mapCalls = mapCalls;
	}
	public HashMap<String, HashMap<String, Integer>> getMaxCommitCalls() {
		return maxCommitCalls;
	}
	public void setMaxCommitCalls(HashMap<String, HashMap<String, Integer>> maxCommitCalls) {
		this.maxCommitCalls = maxCommitCalls;
	}
	public HashMap<String, Float> getAvgCommitCalls() {
		return avgCommitCalls;
	}
	public void setAvgCommitCalls(HashMap<String, Float> avgCommitCalls) {
		this.avgCommitCalls = avgCommitCalls;
	}

}
