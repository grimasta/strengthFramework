package com.example.inj.readingStrategy.strategy;


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

    Table<String, String, Map<Integer, List<Object>>> getReadableMappingSameNI();


    Table<String, String, Map<Integer, List<Object>>> getReadableMappingFinalI();

    HashMap<String, String> getDictionaryStringI();

    HashMap<Integer, String> getDictionaryI();

    Map<String, Map<String, Boolean>> getReadableBugFixingI();

    Map<String, List<String>> getFileCommitsI();

    Map<String,String> getDictionaryTimeI();




}
