package com.example.inj;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.text.ParseException;


@SpringBootApplication
public class Strength {


    DataManipulateExcel dataManipulate;

    @Autowired
    public void setDataManipulate(DataManipulateExcel dataManipulate) {
        this.dataManipulate = dataManipulate;
    }

    public static void main(String[] args) throws ParseException, InvalidFormatException, IOException {
        try {
            ConfigurableApplicationContext ack = SpringApplication.run(Strength.class, args);

        long heapMaxSize = Runtime.getRuntime().maxMemory();
        // To print the JVM Heap Size
        System.out.println("Heap Size: " + heapMaxSize);
        //System.exit(0);
        DataManipulateExcel dataManipulate= ack.getBean("dataManipulateExcel", DataManipulateExcel.class);
        dataManipulate.dataToExcel();
        ack.close();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
    }

}
