package com.example.inj.ReadingStrategy;

import com.example.inj.StrategyFactory.ReadingStrategy.IReadingStrategy;

import java.io.IOException;
import java.text.ParseException;

public class ReadingStrategyContext {


    private IReadingStrategy readingStrategy;

    public void setReadingStrategy(DefaultReadingStrategy readingStrategy) {

        this.readingStrategy = new DefaultReadingStrategy();
    }

    public void parsingStrategy() throws IOException, ParseException {

        readingStrategy.parseData();

    }
}
