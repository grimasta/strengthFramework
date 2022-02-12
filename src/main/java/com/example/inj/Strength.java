package com.example.inj;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.example.inj.global.ProjectNameContainer;


@SpringBootApplication
public class Strength {


    DataManipulateExcel dataManipulate;

    @Autowired
    public void setDataManipulate(DataManipulateExcel dataManipulate) {
        this.dataManipulate = dataManipulate;
    }

    public static void main(String[] args) throws ParseException, InvalidFormatException, IOException {
			
			ProjectNameContainer.PROJECT_NAME = "amarok";
	    	try {
	            ConfigurableApplicationContext ack = SpringApplication.run(Strength.class, args);
		        System.out.println(ProjectNameContainer.PROJECT_NAME);
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
