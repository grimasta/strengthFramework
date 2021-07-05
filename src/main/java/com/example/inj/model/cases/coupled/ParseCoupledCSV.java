package com.example.inj.model.cases.coupled;

import com.example.inj.readingStrategy.strategy.ReadingStrategy;
import com.example.inj.readingStrategy.strategy.ReadingStrategyImp;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


//Case6:Number of calls between A to B/ Average number of calls from A to all other co-committed files.
@Component
@Getter
@Setter
public class ParseCoupledCSV {
    Logger logger = LoggerFactory.getLogger(ParseCoupledCSV.class);

    ReadingStrategy readingStrategy;

    @Autowired
    public void setReadingStrategy(ReadingStrategyImp readingStrategy) {
        this.readingStrategy = ReadingStrategyImp.getInstance();
    }


    HashMap<String,HashMap<String,HashMap<String, Float>>> finalCallsValue= new HashMap<>();
    HashMap<String, HashMap<String, HashMap<String, Integer>>> mapCalls = new HashMap<>();
    //SourceFileId, DestinationFileId, CommitID,Calls
    HashMap<String,HashMap<String,Integer>> maxCommitCalls = new HashMap<>();
    HashMap<String,Float> avgCommitCalls= new HashMap<>();
    //Commit_ID, AvgCalls
    //FileID, Commit_ID, MaxCalls
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
            //D:\Thesis-Analysis\Extras-Thesis\Project_CSV_Files\Calls_Data_Latest\Solved
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
            setMapCalls(mapCalls);
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

            setFinalCallsValue(finalCallsValue);

            System.out.println("Final Calls Value");
            finalCallsValue.entrySet().forEach(e->System.out.print(e));
            //System.exit(0);

            logger.info("Inside Parsed Coupled CSV");


        } catch (FileNotFoundException e) {
            System.out.println("File not found exception during parsing CSV");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Exception occurs while parsing the CSV");
            System.exit(0);
        }
    }

}
