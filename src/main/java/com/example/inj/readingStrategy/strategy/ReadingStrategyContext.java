package com.example.inj.readingStrategy.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;

@Component
public class ReadingStrategyContext {


    private ReadingStrategy readingStrategy;

    @Autowired
    public void setReadingStrategy(ReadingStrategyImp readingStrategy) {

        this.readingStrategy = ReadingStrategyImp.getInstance();
    }

    public void parsingStrategy() throws IOException, ParseException {

        readingStrategy.parseData();

    }
}
