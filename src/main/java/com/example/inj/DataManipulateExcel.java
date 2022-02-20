package com.example.inj;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.example.inj.automate.Chi2Automate;
import com.example.inj.model.cases.TimeDifference;
import com.example.inj.model.cases.prime.CoCommittedPrime;
import com.example.inj.model.cases.prime.CommittedSoFar;
import com.example.inj.model.cases.prime.WithoutCommitPrime;
import com.example.inj.model.decays.DecayImplementation;
import com.example.inj.model.decays.GlobalDecay;
import com.example.inj.model.decays.PairLevelDecay;
import com.example.inj.model.sampling.CreateSample;
import com.example.inj.model.sampling.CreateVector;
import com.example.inj.model.sampling.CreateWidth;
import com.example.inj.model.sampling.InsertExcel;
import com.example.inj.model.storage.DataRepository;
import com.example.inj.model.strength.accumulators.IStrengthAccumulator;
import com.example.inj.model.strength.pair.IPairStrength;
import com.example.inj.model.strength.singlefile.ISingleFileStrength;
import com.example.inj.model.strength.singlefile.SingleFileStrength;


public class DataManipulateExcel {

	private TimeDifference timeDifference;
	private IStrengthAccumulator strengthAccumulator;
	private CoCommittedPrime coCommittedPrime;
	private WithoutCommitPrime withoutCommitPrime;
	private PairLevelDecay pairLevelDecay;
	private CommittedSoFar committedSoFar;
	private GlobalDecay decay;
	private DecayImplementation decayImplementation;
	private ISingleFileStrength singleFileStrength;
	private InsertExcel insertExcel;
	private CreateWidth createWidth;
	private CreateVector createVectors;
	private CreateSample createSample;
	private Chi2Automate chi2Automate;
	private IPairStrength pairStrength;
	private DataRepository dataRepository;

	public DataManipulateExcel() {
		dataRepository = DataRepository.getInstance();
		timeDifference = new TimeDifference();
		coCommittedPrime = new CoCommittedPrime();
		withoutCommitPrime = new WithoutCommitPrime();
		pairLevelDecay = new PairLevelDecay();
		committedSoFar = new CommittedSoFar();
		decay = new GlobalDecay();
		decayImplementation = new DecayImplementation();
		singleFileStrength = new SingleFileStrength();
		insertExcel = new InsertExcel();
		createWidth = new CreateWidth();
		createVectors = new CreateVector();
		createSample = new CreateSample();
		chi2Automate = new Chi2Automate(false);
	}

	/*
	 * Case-1 Number of times the file A&B are co-committed Case-2 Number of time
	 * (A&B) are co-committed/ Number of time A is committed globally Case-3 Time
	 * difference when A&B are co-commited in a consecutive commit/ Count Difference
	 * Case-4 Number of lines of A has modified/Total number of lines in the commit
	 * has modified, excluding A&B Case-5 Number of lines of B has modified/Number
	 * of lines of the commit(except A&B)
	 */
	public void dataToExcel() throws IOException, ParseException {
		/*
		 * committedSoFar.committedSoFar(); coCommittedPrime.getCoCommittedFiles();
		 * withoutCommitPrime.getTimeDifference(); System.exit(0);
		 */
		// Start: Modified as part of Factory Changes
//        abstractPairFactory= factoryProducer.getFactory("pair");
//        ΙPairStrength pairStrength=abstractPairFactory.getPairStrengthType("PairStrength");
		System.out.println("Successful Calling the Pair Strength "); // + pairStrength.toString());
		// End: Modified as part of Factory Changes
		// It will calculate the pairStrength based on the cases
		pairStrength.calculatePairStrength();

		System.out.println("Pair Strength Map In");
		// pairStrength.getPairStrengthMapIn().entrySet().forEach(e->
		// System.out.print(e));

		// Todo: I am using the instance to get the values here, any better way of
		// achieving this\
		pairLevelDecay.globalDecay();
		strengthAccumulator.calculateAccumulatedStrength(dataRepository);
//        overallStrength is calculated and available through the dataRepository dataRepository.getAccumulatedStrength();

		List<String> commitDatesSchedule = commitSchedule(dataRepository.getAccumulatedStrength());

		decay.calculateGlobalDecay(commitDatesSchedule, dataRepository.getAccumulatedStrength());

		decayImplementation.implementDecayInStrengthSecond(dataRepository.getGlobalDecay(),
				dataRepository.getAccumulatedStrength());
		singleFileStrength.finalStrengthSingleFile(); // Includes Pair as well Single file that are committed alone

		insertExcel.insertOverallStrength();
//      TODO move to a setup method

//        createWidth.setSingleFileStrength(singleFileStrength);
//        createWidth.setPairLevelDecay(strengthAccumulator.getPairLevelDecay());
		createWidth.createSegmentWidth();

		createVectors.createVector();
		
		createSample.createRandomSample();
		
		System.out.println("Before excel");
		insertExcel.insertDataExcel();
//        System.exit(0);
		System.out.println("Before ChiSquare");

		chi2Automate.getDetailsOfChi2();

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

		/* Start: Bug 003: Explicity using garbage Collector */
		accumulatedStrength = null;
		accumulatedStrengthCol = null;
		/* End: Bug 003: Explicity using garbage Collector */
		// System.out.println("Commit Schedule");
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
