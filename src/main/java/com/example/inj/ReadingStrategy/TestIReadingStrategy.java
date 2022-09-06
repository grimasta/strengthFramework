package com.example.inj.ReadingStrategy;

import java.io.IOException;
import java.text.ParseException;

public interface TestIReadingStrategy {
    //void parseData() throws IOException, ParseException;
    void parseData(String fileName) throws IOException, ParseException;
}
