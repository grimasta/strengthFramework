package com.example.inj.model.storage;

import com.example.inj.readingStrategy.strategy.IReadingStrategy;
import com.example.inj.readingStrategy.strategy.ReadingStrategyContext;
import com.example.inj.readingStrategy.strategy.DefaultReadingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class CreateTableMapping extends ReadingStrategyContext implements ApplicationListener<ContextRefreshedEvent> {

    ReadingStrategyContext readingStrategyContext;

    @Autowired
    public void setReadingStrategyContext(ReadingStrategyContext readingStrategyContext) {
        this.readingStrategyContext = readingStrategyContext;
    }

    @Override
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
