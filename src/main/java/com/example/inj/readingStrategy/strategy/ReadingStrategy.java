package com.example.inj.readingStrategy.strategy;


import com.example.inj.attributes.AttributesField;
import com.google.common.collect.Table;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public interface ReadingStrategy {

    Table<String, String, Map<Integer, List<Object>>> parseData() throws IOException, ParseException;

    ArrayList<String> convertJson(List<AttributesField> attf) throws IOException;

}
