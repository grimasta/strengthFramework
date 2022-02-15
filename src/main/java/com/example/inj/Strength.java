package com.example.inj;

import java.io.IOException;
import java.text.ParseException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.inj.automate.Chi2Automate;
import com.example.inj.global.ProjectNameContainer;
import com.example.inj.model.cases.CoCommittedExcel;
import com.example.inj.model.cases.CoCommittedFiles;
import com.example.inj.model.cases.TimeDifference;
import com.example.inj.model.cases.prime.CoCommittedPrime;
import com.example.inj.model.cases.prime.CommittedSoFar;
import com.example.inj.model.cases.prime.WithoutCommitPrime;
import com.example.inj.model.decays.DecayImplementation;
import com.example.inj.model.decays.GlobalDecay;
import com.example.inj.model.sampling.CreateSample;
import com.example.inj.model.sampling.CreateVector;
import com.example.inj.model.sampling.CreateWidth;
import com.example.inj.model.sampling.InsertExcel;
import com.example.inj.model.strength.accumulators.StrengthAccumulatorFactory;
import com.example.inj.model.strength.accumulators.StrengthAccumulators;
import com.example.inj.model.strength.pair.FactoryEnum;
import com.example.inj.model.strength.pair.PairFactory;
import com.example.inj.model.strength.singlefile.SingleFileStrength;
import com.example.inj.readingStrategy.strategy.ReadingStrategyEnumeration;
import com.example.inj.readingStrategy.strategy.ReadingStrategyFactory;

@SpringBootApplication
public class Strength {

	private DataManipulateExcel dataManipulate;

	public static void main(String[] args) throws ParseException, InvalidFormatException, IOException {

		ProjectNameContainer.PROJECT_NAME = "digikam_small";
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
			theStrengthCalculator.setupDecays();
			theStrengthCalculator.linkObjects();
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
		dataManipulate.setStrengthAccumulator( 	// This is where you can change the overall
											   	// strength calculation Strategy
			StrengthAccumulatorFactory.create( 	// Look into the com.example.inj.model.strength.accumulators
											   	// package
				StrengthAccumulators.DEFAULT   	// For more details on how to implement your own version and
											   	// how to add it
				)								// to the StrengthAccumulator types enumerations and the factory
		); 										//
		
		dataManipulate.setPairStrength( 				// This is where you can set the Pairwise
														// Strength Calculator
			PairFactory.getPairStrengthType(			// Look into the com.example.inj.model.strength.pair
														// package
				FactoryEnum.RIA_S_PAIRWISE_CALCULATOR 	// for more details on how to implement your
														// own version and how to add it
			) 											// to the PairFactory types enumerations 
		);							 					// and the corresponding factory
		
		dataManipulate.getPairStrength(). 				// That's an important step if you adhere to the
														// current mode of operation
			setReadingStrategy( 						// for a pairStrength calculator which 
														// is responsible to get the date from a
				ReadingStrategyFactory.create( 			// ReadingStrategy
					ReadingStrategyEnumeration.DEFAULT 	//
				) 										//
			);
		dataManipulate.getStrengthAccumulator()
		.setReadingStrategy(dataManipulate.getPairStrength().getReadingStrategy());//
		dataManipulate.getStrengthAccumulator().setUpObjects();
		dataManipulate.setSingleFileStrength(new SingleFileStrength());
		dataManipulate.getSingleFileStrength()
		.setAccumulatedStrength(dataManipulate.getStrengthAccumulator());
		dataManipulate.getSingleFileStrength()
		.setReadingStrategy(dataManipulate.getPairStrength().getReadingStrategy());
		dataManipulate.setCoCommittedPrime(new CoCommittedPrime());
		dataManipulate.setCommittedSoFar(new CommittedSoFar());
		
	}
	
	private void setupDecays() {
		dataManipulate.setDecay(new GlobalDecay());
		dataManipulate.getDecay().setReadingStrategy(dataManipulate.getPairStrength().getReadingStrategy());
		dataManipulate.setDecayImplementation(new DecayImplementation());
		dataManipulate.setCreateWidth(new CreateWidth());
		dataManipulate.getCreateWidth().setSingleFileStrength(dataManipulate.getSingleFileStrength());
		dataManipulate.setCreateVectors(new CreateVector());
		dataManipulate.setCreateSample(new CreateSample());
	}
	
	private void linkObjects() {
		dataManipulate.getSingleFileStrength().setDecayImplementation(dataManipulate.getDecayImplementation());
		dataManipulate.getSingleFileStrength().setInsertExcel(new InsertExcel());
		dataManipulate.getSingleFileStrength().getInsertExcel().setCreateSample(dataManipulate.getCreateSample());
		dataManipulate.getSingleFileStrength().getInsertExcel().setCreateWidth(dataManipulate.getCreateWidth());
		dataManipulate.getSingleFileStrength().getInsertExcel().setReadingStrategy(dataManipulate.getPairStrength().getReadingStrategy());
		dataManipulate.getSingleFileStrength().getInsertExcel().setSingleFileStrength(dataManipulate.getSingleFileStrength());
		dataManipulate.getCreateVectors().setCreateWidth(dataManipulate.getCreateWidth());
		dataManipulate.getCreateVectors().setReadingStrategy(dataManipulate.getPairStrength().getReadingStrategy());
		dataManipulate.getCreateVectors().setSingleFileStrength(dataManipulate.getSingleFileStrength());
		dataManipulate.getCreateVectors().setDecayImplementation(dataManipulate.getDecayImplementation());
		dataManipulate.getCreateSample().setCreateVector(dataManipulate.getCreateVectors());
		dataManipulate.getCreateSample().setInsertExcel(dataManipulate.getSingleFileStrength().getInsertExcel());
		dataManipulate.getCreateSample().setChi2Automate(new Chi2Automate(false));
		dataManipulate.getCreateSample().getChi2Automate().setCreateSample(dataManipulate.getCreateSample());
		dataManipulate.getCoCommittedPrime().setReadingStrategy(dataManipulate.getPairStrength().getReadingStrategy());
		dataManipulate.getCoCommittedPrime().setCommittedSoFar(dataManipulate.getCommittedSoFar());
		dataManipulate.getCommittedSoFar().setReadingStrategy(dataManipulate.getPairStrength().getReadingStrategy());
		dataManipulate.setTimeDifference(new TimeDifference());
		dataManipulate.getPairStrength().setCommittedSoFar(dataManipulate.getCommittedSoFar());
		dataManipulate.getPairStrength().setTimeDifferences(dataManipulate.getTimeDifference());
		dataManipulate.getPairStrength().setAccumulatedStrength(dataManipulate.getStrengthAccumulator());
		dataManipulate.getPairStrength().setCoCommittedExcel(new CoCommittedExcel());
		dataManipulate.getPairStrength().getCoCommittedExcel().setReadingStrategy(dataManipulate.getPairStrength().getReadingStrategy());
		dataManipulate.getPairStrength().setCoCommittedFiles(new CoCommittedFiles());
		dataManipulate.getPairStrength().setCoCommittedPrime(dataManipulate.getCoCommittedPrime());
		dataManipulate.setWithoutCommitPrime(new WithoutCommitPrime());
		dataManipulate.getPairStrength().setWithoutCommitPrime(dataManipulate.getWithoutCommitPrime());
		dataManipulate.getPairStrength().setCoCommittedPrime(dataManipulate.getCoCommittedPrime());
		
	}
	
	private void runSystem() {
		try {
			dataManipulate.getPairStrength().getReadingStrategy().parseData();
			dataManipulate.dataToExcel();
		} catch (ParseException pe) {
			System.out.println("failure in ParseException " + pe.getMessage());
		} catch (IOException ioe) {
			System.out.println("failure in IOException " + ioe.getMessage());
		} catch (NullPointerException npe) {
			System.out.println("failure in NullPointerException " + npe.getMessage());
		}
	}

}
