package com.example.inj.readingStrategy.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;

@Component
public class ReadingStrategyContext {

    @Autowired
    private ReadingStrategy readingStrategy;

    public void parsingStrategy() throws IOException, ParseException {

        readingStrategy.parseData();

    }
}
