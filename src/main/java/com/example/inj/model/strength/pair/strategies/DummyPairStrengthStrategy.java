package com.example.inj.model.strength.pair.strategies;

import com.example.inj.attributes.IncrementalData;
import com.example.inj.attributes.IncrementalField;
import com.example.inj.model.storage.DataRepository;
import com.google.common.collect.Table;

import java.util.*;

public class DummyPairStrengthStrategy implements IPairStrengthStrategy{
    @Override
    public Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> calculate(DataRepository dataRepository){
        Map<String, Map<String, Map<Integer, List<Object>>>> readMap = dataRepository.getReadableMappingFinal().rowMap();
        Map<String, Map<String, Boolean>> bugFixingMap = dataRepository.getReadableBugFixing();
        Map<String, Map<String, Map<String, Float>>> coCommit = dataRepository.getΜapOfCoCommitOverSumOfCommitsRatioForAllFileCombinations();
        Map<String, Map<String, Map<String, Float>>> coCommitTogether = dataRepository.getΜapOfCoCommitOverSourceFileCommitRatioForAllFileCombinations();
        Map<String, Map<String, Map<String, Float>>> committedNotTogether = dataRepository.getCoTimeDifference();
        Map<String, Map<String, Map<String, Float>>> sourceLinesModified = dataRepository.getLinesModifiedSource();
        Map<String, Map<String, Map<String, Float>>> destinationLinesModified = dataRepository.getLinesModifiedDestination();
        float pairStrength = 0.0f;
        Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> localPairStrength = new HashMap<>();
        for (String sourceFileId : readMap.keySet()) {
            Map<String, List<Map<Integer, Map<String, Float>>>> pairStrengthSubMap = new LinkedHashMap<>();
            for (String destinationFileId : readMap.get(sourceFileId).keySet()) {
                Map<String, Float> pairStrengthSubThreeMap = new TreeMap<>();
                Map<Integer, Map<String, Float>> pairStrengthSubTwoMap = new TreeMap<>();
                List<Map<Integer, Map<String, Float>>> pairSubTwoMapList = new LinkedList<>();
                for (int coCommitOccurrenceNumber : readMap.get(sourceFileId).get(destinationFileId).keySet()) {
                    String commitTime = (String) readMap.get(sourceFileId).get(destinationFileId).get(coCommitOccurrenceNumber).get(10);
                    float coCommitValue = coCommit.get(sourceFileId).get(destinationFileId).get(commitTime); // Case 1''
                    float coCommitTogetherValue = coCommitTogether.get(sourceFileId).get(destinationFileId).get(commitTime); // Case

                    IncrementalField incrementalField =
                            (IncrementalField)readMap.get(sourceFileId).get(destinationFileId).get(coCommitOccurrenceNumber).
                                    get(17);

                    if(incrementalField !=null){
                        Table <String, String, IncrementalData> access =incrementalField.getAccess();
                        if(access.contains(sourceFileId,destinationFileId)){
                            IncrementalData incrementalData= access.get(sourceFileId,destinationFileId);
                            pairStrength= 0.5f*(incrementalData.getTotal()+
                                    incrementalData.getAdded() +
                                    incrementalData.getDeleted());
                        }else{
                            pairStrength=-1000f ;
                        }
                    }else{
                        pairStrength=-1000f ;
                    }
                    pairStrengthSubThreeMap.put(commitTime, pairStrength);
                    pairStrengthSubTwoMap.put(coCommitOccurrenceNumber, pairStrengthSubThreeMap);
                    pairSubTwoMapList.add(pairStrengthSubTwoMap);
                    pairStrengthSubThreeMap = new TreeMap<>();
                    pairStrengthSubTwoMap = new TreeMap<>();
                }
                pairStrengthSubMap.put(destinationFileId, pairSubTwoMapList);

            }
            localPairStrength.put(sourceFileId, pairStrengthSubMap);
        }
        return localPairStrength;
    }
}
