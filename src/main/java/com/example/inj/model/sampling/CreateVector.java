package com.example.inj.model.sampling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.inj.model.decays.DecayImplementation;
import com.example.inj.model.storage.DataRepository;
import com.example.inj.model.strength.singlefile.ISingleFileStrength;
import com.example.inj.readingStrategy.strategy.IReadingStrategy;

//Modification 003- We have modify the current selection of segment to include a value if any file in the next segment is
//bugfixing or not.
//Modification 004- We are trying to analyze if the number of commits in the segment >2(Not equal)
// and hardcoding the increasing slope that helps to analyze whether the number of commit
// should be considered. How it can impact our research.

public class CreateVector {

    Logger logger = LoggerFactory.getLogger(CreateVector.class);

    private DataRepository dataRepository;
    
    public CreateVector() {
    	dataRepository = DataRepository.getInstance();
    }
/*
    createVector() function is responsible for creating vector of desired segment. The segment width
    is obtained from createSegmentWidth function.
     */
//Imp 004: File and it's associated commit details
//Imp 005: BugFixing Commit of Prev
//Imp 006: Check if current segment is buggy or not
    public void createVector() {
    	Map<String, Float> accStrength;
        HashMap<String, String> dictionaryString = dataRepository.getDictionaryString(); //Bug 002: Committed as part of commitID to be added in the sample data
        Map<String, Integer> segmentWidths = dataRepository.getSegmentWidth();
        Map<String, Map<String, Float>> acStren = dataRepository.getFinalStrength();
        Map<String,List<String>> fileCommits= dataRepository.getFileCommits(); //Imp 004: File and it's associated commit details
        System.out.println("Strength");
        List<Object> xList = new ArrayList<>();

        Map<String, Map<String, List<Object>>> vectorMap = new LinkedHashMap<>();
        Map<String, List<Object>> vectorSubMap = new LinkedHashMap<>();
        Map<String, List<List<Object>>> vectorFinalMap = new LinkedHashMap<>();
        List<List<Object>> vectorDoubleList = new ArrayList<>();
        String dateVal = "";
        int max = 0;
        int i = 0;
        int segmentI = 0;
        int segmentWid = 0;
        Map<String, Map<String, Boolean>> booleanFix = dataRepository.getReadableBugFixing(); //<FileId, <Date, Boolean>>
        Map<String, Boolean> booleanSubFix = new LinkedHashMap<>();
        String anDate = "";


        for (String file : acStren.keySet()) {
            {
                //String fixCount="Development_Fix"; //Imp 006: Check if current segment is buggy or not

                accStrength = acStren.get(file);

                accStrength=accStrength.entrySet()
                        .stream()
                        .map(entry -> {
                            if (entry.getValue() == null)
                                entry.setValue(0.0f);
                            return entry;
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                Map.Entry::getValue));


                List<String> timeStamp = new ArrayList<>(accStrength.keySet());
                Collections.sort(timeStamp);
                max = timeStamp.size();
                i = 0;
                segmentWid = segmentWidths.get(file);
                //segmentWid=3;
                List<String> values = new ArrayList<>();
                values.addAll(fileCommits.get(file));

                //System.out.println(" Inside Vector Creation File_ID " + file);
                //booleanFix.get(file).entrySet().forEach(e-> System.out.print(e));

                while (i < max && segmentWid > 0 ) { //&& segmentWid > 1
                    int commitCount=0; //Imp 004: File and it's associated commit details
                    segmentI = i + segmentWid;
                    if (!(segmentI < max)) {
                        segmentI = max;
                    }
                    dateVal = timeStamp.get(i);

                    xList.add(dateVal);// 1st value Date of starting segment
                    //Start: Bug 002: Committed as part of commitID to be added in the sample data
                    if (dictionaryString.containsKey(dateVal)) {
                        String commitId= dictionaryString.get(dateVal);
                        xList.add(commitId); //Start commit Id of segment 2nd value commit ID

                    }
                    else
                    {
                        xList.add("commitId"); //new change
                    }

                    List<Boolean> bugyList= new ArrayList<>();
                    //End:  Bug 002: Committed as part of commitID to be added in the sample data
                    while (i < segmentI) {
                        if (accStrength.containsKey(timeStamp.get(i))) {

                            if(accStrength.get(timeStamp.get(i)) == null) {
                                xList.add(0.0);
                            }
                            else
                            {
                                //Start: Imp 006: Check if current segment is buggy or not
                                if(booleanFix.containsKey(file) && booleanFix.get(file).containsKey(timeStamp.get(i)) && booleanFix.get(file).get(timeStamp.get(i)))
                                {
                                    //fixCount="Bug_Fix";
                                    bugyList.add(true);
                                }
                                else
                                {
                                    bugyList.add(false);
                                }
                                //End: Imp 006: Check if current segment is buggy or not
                                xList.add((double) accStrength.get(timeStamp.get(i))); //Strength of a file // 3rd value strength

                            }
                            //Start: Imp 004: File and it's associated commit details
                            try {
                                String dat = timeStamp.get(i);
                                if (dictionaryString.containsKey(dat)) {
                                    String commitId = dictionaryString.get(dat);
                                    //xList.add(commitId); //Start commit Id of segment
                                    if (fileCommits.containsKey(file)) {

                                        if (values.contains(commitId))
                                        {
                                            commitCount++;
                                        }
                                    }
                                    //End: Imp 004: File and it's associated commit details

                                }
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }

                            i++;
                        } else {
                            //System.out.println(i);
                            xList.add((double) 0);
                            i++;
                        }


                    }
                    if (i < max) {

                        if(accStrength.get(timeStamp.get(i)) != null) {

                            xList.add((double) accStrength.get(timeStamp.get(i))); //First Date value of next segemnt
                            anDate = timeStamp.get(i);
                            //Start: Modification 003
                            int endDateIndex = i + segmentWid;
                            int startDateIndex = i;
                            Boolean buggySegment = false;
                            booleanSubFix = booleanFix.get(file);


                            while (startDateIndex < endDateIndex && startDateIndex < max)  //TODO: check about equal to sign
                            {
                                String startSegmentDate = timeStamp.get(startDateIndex);
                                if (booleanSubFix.containsKey(startSegmentDate)) {
                                    Boolean bugFixing = booleanSubFix.get(startSegmentDate);
                                    //System.out.println("Oh Yeah");
                                    if (bugFixing) {

                                        buggySegment = true;
                                        //System.out.println("startSegmentDate " + startSegmentDate + "bugFixing" + bugFixing);
                                        break;   //If we find the instance of one bug fixing file, we are returning
                                    }
                                }
                                startDateIndex++;
                            }
                            //System.out.println(" buggySegment " + buggySegment);
                            //End: Modification 004
                            xList.add(anDate);
                            //Start: Bug 002: Committed as part of commitID to be added in the sample data
                            if (dictionaryString.containsKey(anDate))
                                xList.add(dictionaryString.get(anDate));
                            else
                                xList.add("dictionaryString.get(anDate)");
                            //End:  Bug 002: Committed as part of commitID to be added in the sample data

                            //Start: Modification 004
                            if (buggySegment) {
                                xList.add(buggySegment);
                            } else {
                                xList.add(buggySegment);
                            }
                            //xList.add(buggySegment);
                            //End:Modification 004;

                        }
                        else
                        {

                        }
                    } else {
                        xList.add((double) -1);
                        xList.add(false); //Modified to handled the bug of null while inserting in excel
                    }
                    //System.out.println("file ID " + file + " Commit Count " + commitCount);
                    xList.add(commitCount);
                    xList.add(bugyList);
                    //fixCount="Not A Bug Fixing";
                    vectorSubMap.put(dateVal, xList);
                    vectorDoubleList.add(xList);
                    xList = new ArrayList<>();
                }
                vectorFinalMap.put(file, vectorDoubleList);
                vectorMap.put(file, vectorSubMap);
                vectorSubMap = new LinkedHashMap<>();
                vectorDoubleList = new ArrayList<>();


            }
        }

        dataRepository.setVectorMapGlobal(vectorMap);
        dataRepository.setVectorFinalMapGlobal(vectorFinalMap);

        //logger.info(vectorMap.entrySet().forEach(e-> System.out.println(e)));
        //logger.info("Check the solution");
        //logger.info(vectorMap.entrySet().toString());
        //System.exit(0);
/*        System.out.println("caa7faee-1ed0-11eb-98c6-482ae32cf5b4 Vector Map");
        vectorMap.get("caa7faee-1ed0-11eb-98c6-482ae32cf5b4").entrySet().forEach(e-> System.out.print(e));

        System.out.println("caa7faee-1ed0-11eb-98c6-482ae32cf5b4 Vector Final Map");
        vectorFinalMap.get("caa7faee-1ed0-11eb-98c6-482ae32cf5b4").forEach(e->System.out.print(e));
        System.exit(0);*/

    }

    public void createVectorSecondStrategy() {
        Map<String, Float> accStrength;
        HashMap<String, String> dictionaryString = dataRepository.getDictionaryString(); //Bug 002: Committed as part of commitID to be added in the sample data
        Map<String, Integer> segmentWidths = dataRepository.getSegmentWidth();
        Map<String, Map<String, Float>> acStren = dataRepository.getFinalStrength();
        Map<String, List<String>> fileCommits = dataRepository.getFileCommits(); //Imp 004: File and it's associated commit details
        System.out.println("Strength");
        List<Object> xList = new ArrayList<>();
        List<List<Object>> xDoubleList = new ArrayList<>();

        Map<String, Map<String, List<Object>>> vectorMap = new LinkedHashMap<>();
        Map<String, List<Object>> vectorSubMap = new LinkedHashMap<>();
        Map<String, List<List<Object>>> vectorFinalMap = new LinkedHashMap<>();
        List<List<Object>> vectorDoubleList = new ArrayList<>();
        String dateVal = "";
        int max = 0;
        int i = 0;
        int segmentI = 0;
        int segmentWid = 0;
        Map<String, Map<String, Boolean>> booleanFix = dataRepository.getReadableBugFixing(); //<FileId, <Date, Boolean>>
        Map<String, Boolean> booleanSubFix = new LinkedHashMap<>();
        String anDate = "";

        for (String file : acStren.keySet()) {
            {
                //String fixCount="Development_Fix"; //Imp 006: Check if current segment is buggy or not

                accStrength = acStren.get(file);
                accStrength = accStrength.entrySet()
                        .stream()
                        .map(entry -> {
                            if (entry.getValue() == null)
                                entry.setValue(0.0f);
                            return entry;
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                Map.Entry::getValue));


                List<String> timeStamp = new ArrayList<>(accStrength.keySet());
                Collections.sort(timeStamp);
                max = timeStamp.size();
                i = 0;
                //segmentWid=3;
                List<String> values = new ArrayList<>();
                values.addAll(fileCommits.get(file));
                int commitTimeCount = 0;
                for (String commitTime : timeStamp) {
                    for (String commitTimeForFile : values) {
                        if (commitTimeCount <= 2) {
                            if (commitTime.equalsIgnoreCase(commitTimeForFile)) {
                                if (accStrength.containsKey(commitTimeForFile)) {
                                    xList.add(accStrength.get(commitTimeForFile));
                                    commitTimeCount++;
                                } else {
                                    xList.add(accStrength.get(0.0f));
                                }
                            } else {
                                if (accStrength.containsKey(commitTimeForFile)) {
                                    xList.add(accStrength.get(commitTimeForFile));
                                } else {
                                    xList.add(accStrength.get(0.0f));
                                }
                            }
                        } else if (commitTimeCount > 2) {
                            commitTimeCount = 0;
                            xDoubleList.add(xList);
                            xList = new ArrayList<>();
                        }
                    }

                }

            }

        }
    }

}
