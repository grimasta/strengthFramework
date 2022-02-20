package com.example.inj.model.storage;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.example.inj.readingStrategy.strategy.ReadingStrategyContext;

public class CreateTableMapping extends ReadingStrategyContext implements ApplicationListener<ContextRefreshedEvent> {

    ReadingStrategyContext readingStrategyContext;

    public void setReadingStrategyContext(ReadingStrategyContext readingStrategyContext) {
        this.readingStrategyContext = readingStrategyContext;
    }

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        loadData();
    }

    public void loadData() {
        try {
            readingStrategyContext.parsingStrategy();
            System.out.println("Inside Parsing");
        }
        catch(Exception e)
        {
            System.out.println("Exception while parsing the table mapping" + e.getMessage());
        }
        System.out.println("After Parse Data");
    }



}
