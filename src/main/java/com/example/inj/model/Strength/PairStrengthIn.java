package com.example.inj.model.Strength;

import com.example.inj.model.cases.CoCommittedExcel;
import com.example.inj.readingStrategy.strategy.ReadingStrategyImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface PairStrengthIn {

    Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> getPairStrengthMapIn();
    Map<String, Map<Integer, Map<String, Integer>>> getExcelYearMapsIn();
    void calculatePairStrength();

}
