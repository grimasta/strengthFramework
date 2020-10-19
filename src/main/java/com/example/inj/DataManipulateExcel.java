package com.example.inj;

import com.example.inj.model.Factory.AbstractPairFactory;
import com.example.inj.model.Factory.FactoryProducer;
import com.example.inj.model.Factory.PairFactory;
import com.example.inj.model.Strength.AccumulatedStrength;
import com.example.inj.model.Strength.PairStrengthIn;
import com.example.inj.model.Strength.SingleFileStrength;
import com.example.inj.model.decays.DecayImplementation;
import com.example.inj.model.decays.GlobalDecay;
import com.example.inj.model.sampling.CreateSample;
import com.example.inj.model.sampling.CreateVector;
import com.example.inj.model.sampling.CreateWidth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

//Bug 001: Committed as part of the file that is committed alone.
//Bug 002: Committed as part of commitID to be added in the sample data
//Bug 003: Explicity using garbage Collector
@Component("dataManipulateExcel")
public class DataManipulateExcel {

    AccumulatedStrength accumulatedStrength;

    @Autowired
    public void setAccumulatedStrength(AccumulatedStrength accumulatedStrength) {
        this.accumulatedStrength = accumulatedStrength;
    }

    PairFactory pairFactory;

    @Autowired
    public void setPairFactory(PairFactory pairFactory) {
        this.pairFactory = pairFactory;
    }

    FactoryProducer factoryProducer;

    @Autowired
    public void setFactoryProducer(FactoryProducer factoryProducer) {
        this.factoryProducer = factoryProducer;
    }

    AbstractPairFactory abstractPairFactory;

    @Autowired
    public void setAbstractPairFactory(AbstractPairFactory abstractPairFactory) {
        this.abstractPairFactory = abstractPairFactory;
    }
    /*  PairStrength pairStrength;

    @Autowired
    public void setPairStrength(PairStrength pairStrength) {
        this.pairStrength = pairStrength;
    }*/

    GlobalDecay decay;
    @Autowired
    public void setDecay(GlobalDecay decay) {
        this.decay = decay;
    }

    DecayImplementation decayImplementation;

    @Autowired
    public void setDecayImplementation(DecayImplementation decayImplementation) {
        this.decayImplementation = decayImplementation;
    }

    SingleFileStrength singleFileStrength;

    @Autowired
    public void setSingleFileStrength(SingleFileStrength singleFileStrength) {
        this.singleFileStrength = singleFileStrength;
    }

    CreateWidth createWidth;

    @Autowired
    public void setCreateWidth(CreateWidth createWidth) {
        this.createWidth = createWidth;
    }

    CreateVector createVectors;

    CreateSample createSample;

    @Autowired
    public void setCreateVectors(CreateVector createVectors) {
        this.createVectors = createVectors;
    }

    @Autowired
    public void setCreateSample(CreateSample createSample) {
        this.createSample = createSample;
    }



    /*
           Case-1 Number of times the file A&B are co-committed
           Case-2 Number of time (A&B) are co-committed/ Number of time A is committed globally
           Case-3 Time difference when A&B are co-commited in a consecutive commit/ Count Difference
           Case-4 Number of lines of A has modified/Total number of lines in the commit has modified, excluding A&B
           Case-5 Number of lines of B has modified/Number of lines of the commit(except A&B)
            */
    public void dataToExcel() throws IOException, ParseException {


        Map<String, Map<String, Float>> overallStrength;
        //Start: Modified as part of Factory Changes
        abstractPairFactory= factoryProducer.getFactory("pair");
        PairStrengthIn pairStrength=abstractPairFactory.getPairStrengthType("PairStrength");
        System.out.println("Successful Calling the Pair Strength " + pairStrength.toString());
        //End: Modified as part of Factory Changes
        //It will calculate the pairStrength based on the cases
        pairStrength.calculatePairStrength();


        System.out.println("Pair Strength Map In");
        //pairStrength.getPairStrengthMapIn().entrySet().forEach(e-> System.out.print(e));

        //Todo: I am using the instance to get the values here, any better way of achieving this
        accumulatedStrength.calculateAccumulatedStrength(pairStrength.getPairStrengthMapIn(), pairStrength.getExcelYearMapsIn());
        overallStrength = accumulatedStrength.getAccumulatedStrength();

        List<String> commitDatesSchedule = commitSchedule(overallStrength);

        decay.calculateGlobalDecay(commitDatesSchedule, overallStrength);
        Map<String, Map<String, Float>> globalDecay=decay.getGlobalDecay();

        decayImplementation.implementDecayInStrengthSecond(globalDecay, overallStrength);

        singleFileStrength.finalStrengthSingleFile(); // Includes Pair as well Single file that are committed alone

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


}




