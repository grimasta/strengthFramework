package com.example.inj;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.inj.model.Factory.AbstractPairFactory;
import com.example.inj.model.Factory.FactoryProducer;
import com.example.inj.model.cases.TimeDifference;
import com.example.inj.model.cases.prime.CoCommittedPrime;
import com.example.inj.model.cases.prime.CommittedSoFar;
import com.example.inj.model.cases.prime.WithoutCommitPrime;
import com.example.inj.model.decays.DecayImplementation;
import com.example.inj.model.decays.GlobalDecay;
import com.example.inj.model.sampling.CreateSample;
import com.example.inj.model.sampling.CreateVector;
import com.example.inj.model.sampling.CreateWidth;
import com.example.inj.model.strength.accumulators.IStrengthAccumulator;
import com.example.inj.model.strength.pair.PairFactory;
import com.example.inj.model.strength.pair.IPairStrength;
import com.example.inj.model.strength.singlefile.ISingleFileStrength;

//Bug 001: Committed as part of the file that is committed alone.
//Bug 002: Committed as part of commitID to be added in the sample data
//Bug 003: Explicity using garbage Collector
@Component("dataManipulateExcel")
public class DataManipulateExcel {


    private TimeDifference timeDifference;
    private IStrengthAccumulator strengthAccumulator;
    private CoCommittedPrime coCommittedPrime;
    private WithoutCommitPrime withoutCommitPrime;    
    private PairFactory pairFactory;
    private FactoryProducer factoryProducer;
    private AbstractPairFactory abstractPairFactory;
    private CommittedSoFar committedSoFar;
    private GlobalDecay decay;
    private DecayImplementation decayImplementation;
    private ISingleFileStrength singleFileStrength;
    private CreateWidth createWidth;
    private CreateVector createVectors;
    private CreateSample createSample;
    private IPairStrength pairStrength;
  
	/*
           Case-1 Number of times the file A&B are co-committed
           Case-2 Number of time (A&B) are co-committed/ Number of time A is committed globally
           Case-3 Time difference when A&B are co-commited in a consecutive commit/ Count Difference
           Case-4 Number of lines of A has modified/Total number of lines in the commit has modified, excluding A&B
           Case-5 Number of lines of B has modified/Number of lines of the commit(except A&B)
            */
    public void dataToExcel() throws IOException, ParseException {
        /*committedSoFar.committedSoFar();
        coCommittedPrime.getCoCommittedFiles();
        withoutCommitPrime.getTimeDifference();
        System.exit(0);*/
        Map<String, Map<String, Float>> overallStrength;
        //Start: Modified as part of Factory Changes
//        abstractPairFactory= factoryProducer.getFactory("pair");
//        ΙPairStrength pairStrength=abstractPairFactory.getPairStrengthType("PairStrength");
        System.out.println("Successful Calling the Pair Strength "); // + pairStrength.toString());
        //End: Modified as part of Factory Changes
        //It will calculate the pairStrength based on the cases
        pairStrength.calculatePairStrength();


        System.out.println("Pair Strength Map In");
        //pairStrength.getPairStrengthMapIn().entrySet().forEach(e-> System.out.print(e));

        //Todo: I am using the instance to get the values here, any better way of achieving this
        strengthAccumulator.calculateAccumulatedStrength(pairStrength.getPairStrengthMapIn(), pairStrength.getExcelYearMapsIn());
        overallStrength = strengthAccumulator.getAccumulatedStrength();

        List<String> commitDatesSchedule = commitSchedule(overallStrength);

        decay.calculateGlobalDecay(commitDatesSchedule, overallStrength);
        Map<String, Map<String, Float>> globalDecay=decay.getGlobalDecay();

        decayImplementation.implementDecayInStrengthSecond(globalDecay, overallStrength);

        singleFileStrength.finalStrengthSingleFile(); // Includes Pair as well Single file that are committed alone
        
//      TODO move to a setup method
        createWidth.setSingleFileStrength(singleFileStrength);
        createWidth.setPairLevelDecay(strengthAccumulator.getPairLevelDecay());
        createWidth.createSegmentWidth();
        createVectors.createVector();
        createSample.createRandomSample();

        System.out.println("END OF PROGRAM");

    }

    public List<String> commitSchedule(Map<String, Map<String, Float>> accumulatedStrength) {
        List<String> finalCommitDates = new LinkedList<>();
        Map<String, Float> accumulatedStrengthCol = new LinkedHashMap<>();
        for (String row : accumulatedStrength.keySet()) {
            accumulatedStrengthCol = accumulatedStrength.get(row);
            for (String column : accumulatedStrengthCol.keySet()) {
                if (!finalCommitDates.contains(column)) {
                    finalCommitDates.add(column);
                }
            }
        }
        Collections.sort(finalCommitDates);

        /*Start:  Bug 003: Explicity using garbage Collector */
        accumulatedStrength = null;
        accumulatedStrengthCol = null;
        /*End:  Bug 003: Explicity using garbage Collector */
        //System.out.println("Commit Schedule");
        return finalCommitDates;
    }

	public IPairStrength getPairStrength() {
		// TODO Auto-generated method stub
		return pairStrength;
	}

	public TimeDifference getTimeDifference() {
		return timeDifference;
	}

	public void setTimeDifference(TimeDifference timeDifference) {
		this.timeDifference = timeDifference;
	}

	public IStrengthAccumulator getStrengthAccumulator() {
		return strengthAccumulator;
	}

	public void setStrengthAccumulator(IStrengthAccumulator strengthAccumulator) {
		this.strengthAccumulator = strengthAccumulator;
	}

	public CoCommittedPrime getCoCommittedPrime() {
		return coCommittedPrime;
	}

	public void setCoCommittedPrime(CoCommittedPrime coCommittedPrime) {
		this.coCommittedPrime = coCommittedPrime;
	}

	public WithoutCommitPrime getWithoutCommitPrime() {
		return withoutCommitPrime;
	}

	public void setWithoutCommitPrime(WithoutCommitPrime withoutCommitPrime) {
		this.withoutCommitPrime = withoutCommitPrime;
	}

	public PairFactory getPairFactory() {
		return pairFactory;
	}

	public void setPairFactory(PairFactory pairFactory) {
		this.pairFactory = pairFactory;
	}

	public FactoryProducer getFactoryProducer() {
		return factoryProducer;
	}

	public void setFactoryProducer(FactoryProducer factoryProducer) {
		this.factoryProducer = factoryProducer;
	}

	public AbstractPairFactory getAbstractPairFactory() {
		return abstractPairFactory;
	}

	public void setAbstractPairFactory(AbstractPairFactory abstractPairFactory) {
		this.abstractPairFactory = abstractPairFactory;
	}

	public CommittedSoFar getCommittedSoFar() {
		return committedSoFar;
	}

	public void setCommittedSoFar(CommittedSoFar committedSoFar) {
		this.committedSoFar = committedSoFar;
	}

	public GlobalDecay getDecay() {
		return decay;
	}

	public void setDecay(GlobalDecay decay) {
		this.decay = decay;
	}

	public DecayImplementation getDecayImplementation() {
		return decayImplementation;
	}

	public void setDecayImplementation(DecayImplementation decayImplementation) {
		this.decayImplementation = decayImplementation;
	}

	public ISingleFileStrength getSingleFileStrength() {
		return singleFileStrength;
	}

	public void setSingleFileStrength(ISingleFileStrength singleFileStrength) {
		this.singleFileStrength = singleFileStrength;
	}

	public CreateWidth getCreateWidth() {
		return createWidth;
	}

	public void setCreateWidth(CreateWidth createWidth) {
		this.createWidth = createWidth;
	}

	public CreateVector getCreateVectors() {
		return createVectors;
	}

	public void setCreateVectors(CreateVector createVectors) {
		this.createVectors = createVectors;
	}

	public CreateSample getCreateSample() {
		return createSample;
	}

	public void setCreateSample(CreateSample createSample) {
		this.createSample = createSample;
	}

	public void setPairStrength(IPairStrength pairStrength) {
		this.pairStrength = pairStrength;
	}
	

}




