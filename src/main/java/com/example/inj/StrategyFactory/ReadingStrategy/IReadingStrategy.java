package com.example.inj.StrategyFactory.ReadingStrategy;


import com.example.inj.attributes.AttributesField;
import com.google.common.collect.Table;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public interface IReadingStrategy {

    void parseData() throws IOException, ParseException;

    ArrayList<String> convertJson(List<AttributesField> attf) throws IOException;

    Table<String, String, Map<Integer, List<Object>>> getReadableMappingSameN();

    Table<String, String, Map<Integer, List<Object>>> getReadableMappingFinal();

    HashMap<String, String> getDictionaryString();

    HashMap<Integer, String> getDictionary();

    Map<String, Map<String, Boolean>> getReadableBugFixing();

    Map<String, List<String>> getFileCommits();

    Map<String,String> getDictionaryTime();




}
