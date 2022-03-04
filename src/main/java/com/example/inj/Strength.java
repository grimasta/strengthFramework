package com.example.inj;

import java.io.IOException;
import java.text.ParseException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.example.inj.global.ProjectNameContainer;
import com.example.inj.model.strength.accumulators.StrengthAccumulatorFactory;
import com.example.inj.model.strength.accumulators.StrengthAccumulators;
import com.example.inj.model.strength.accumulators.strategies.StrengthAccumulatorStrategiesEnum;
import com.example.inj.model.strength.accumulators.strategies.StrengthAccumulatorStrategiesFactory;
import com.example.inj.model.strength.pair.PairCalculatorEnum;
import com.example.inj.model.strength.pair.PairFactory;
import com.example.inj.model.strength.pair.strategies.PairStrengthStrategyEnum;
import com.example.inj.model.strength.pair.strategies.PairStrengthStrategyFactory;
import com.example.inj.readingStrategy.strategy.ReadingStrategyEnumeration;
import com.example.inj.readingStrategy.strategy.ReadingStrategyFactory;

public class Strength {

	private DataManipulateExcel dataManipulate;

	public static void main(String[] args) throws ParseException, InvalidFormatException, IOException {

		ProjectNameContainer.PROJECT_NAME = "elisa";
//		try {
		
//	            ConfigurableApplicationContext ack = SpringApplication.run(Strength.class, args);
		System.out.println(ProjectNameContainer.PROJECT_NAME);
		long heapMaxSize = Runtime.getRuntime().maxMemory();
		// To print the JVM Heap Size
		System.out.println("Heap Size: " + heapMaxSize);
		// System.exit(0);

		Strength theStrengthCalculator = new Strength();
//	        DataManipulateExcel dataManipulate= ack.getBean("dataManipulateExcel", DataManipulateExcel.class);

		theStrengthCalculator.setupObjects();
		theStrengthCalculator.runSystem();

//	        ack.close();
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			System.out.println(e.getStackTrace());
//		}
	}

	private Strength() {

	}

	private void setupObjects() {
		dataManipulate = new DataManipulateExcel();
		dataManipulate.setStrengthAccumulator( // This is where you can change the overall
												// strength calculation Strategy
				StrengthAccumulatorFactory.create( // Look into the com.example.inj.model.strength.accumulators
													// package
						StrengthAccumulators.DEFAULT // For more details on how to implement your own version and
														// how to add it
				) // to the StrengthAccumulator types enumerations and the factory
		); //

		dataManipulate.getStrengthAccumulator().setStrategy(StrengthAccumulatorStrategiesFactory
				.createStrengthAccumulatorStrategy(StrengthAccumulatorStrategiesEnum.DEFAULT));

		dataManipulate.setPairStrength( // This is where you can set the Pairwise
										// Strength Calculator
				PairFactory.getPairStrengthType( // Look into the com.example.inj.model.strength.pair
													// package
						PairCalculatorEnum.RIA_S_PAIRWISE_CALCULATOR // for more details on how to implement your
				// own version and how to add it
				) // to the PairFactory types enumerations
		); // and the corresponding factory

//		Set Concrete pairStrength Calculation Strategy
		dataManipulate.getPairStrength()
				.setStrategy(PairStrengthStrategyFactory.createPairStrengthStrategy(PairStrengthStrategyEnum.RONGJI));

	}

	private void runSystem() {
		try {
			ReadingStrategyFactory.create(ReadingStrategyEnumeration.PARALLEL_READER).parseData();
			dataManipulate.dataToExcel();
		} catch (ParseException pe) {
			System.out.println("failure in ParseException: " + pe.getMessage());
		} catch (IOException ioe) {
			System.out.println("failure in IOException: " + ioe.getMessage());
		} catch (NullPointerException npe) {
			System.out.println("failure in NullPointerException: " + npe.getMessage());
		}
	}

}
