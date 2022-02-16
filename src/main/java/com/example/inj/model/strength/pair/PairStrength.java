package com.example.inj.model.strength.pair;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.inj.model.cases.CoCommittedExcel;
import com.example.inj.model.cases.CoCommittedFiles;
import com.example.inj.model.cases.LinesModified;
import com.example.inj.model.cases.TimeDifference;
import com.example.inj.model.cases.coupled.ParseCoupledCSV;
import com.example.inj.model.cases.prime.CoCommittedPrime;
import com.example.inj.model.cases.prime.CommittedSoFar;
import com.example.inj.model.cases.prime.LinesModifiedPrime;
import com.example.inj.model.cases.prime.WithoutCommitPrime;
import com.example.inj.model.strength.accumulators.IStrengthAccumulator;
import com.example.inj.readingStrategy.strategy.IReadingStrategy;
import com.google.common.collect.Table;

import javafx.util.Pair;

@Component
public class PairStrength implements ΙPairStrength{

	private LinesModifiedPrime linesModifiedPrime;
    private CommittedSoFar committedSoFar;
    private CoCommittedPrime coCommittedPrime;
    private WithoutCommitPrime withoutCommitPrime;
    private ParseCoupledCSV parseCoupledCSV;
    private CoCommittedExcel coCommittedExcel;
    private CoCommittedFiles coCommittedFiles;
    private LinesModified linesModified;
    private TimeDifference timeDifferences;
    private IStrengthAccumulator accumulatedStrength;
    private Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMap;
    private Map<String, Map<Integer, Map<String, Integer>>> excelYearMaps;
    private IReadingStrategy readingStrategy;
    Logger logger = LoggerFactory.getLogger(PairStrength.class);

 

    



    public void calculatePairStrength2(){
    	
//        HashMap<Integer, String> dictionary = readingStrategy.getDictionaryI();

        Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> localPairStrength= new LinkedHashMap<>();

        //Case-1
        coCommittedFiles.coCommitABCD();
        Pair<Map<String, Map<String, Map<Integer, Map<String, Integer>>>>, Map<String, Map<Integer, Map<String, Integer>>>> pairMaps = coCommittedFiles.getPairMaps();
        System.out.println("After Case-1");
        //Case-2
        coCommittedExcel.coCommitted();
        Table<String, String, Map<Integer, Map<String, List<Float>>>> tableCommits = coCommittedExcel.getTableCommits();
        System.out.println("After Case-2");
        //Case-3 //Todo Check better way of doing this
        timeDifferences.timeDifferenceExcel(pairMaps);
        Map<String, Map<String, Map<Integer, Map<String, Integer>>>> timeDifference = timeDifferences.getTimeDifference();
        timeDifference.entrySet().forEach(e->System.out.print(e));
        System.out.println("After Case-3");
        //System.exit(0);
        //Case-4
        linesModified.numberOflinesModified();
        System.out.println("After Case-4");
        Map<String, Map<String, Map<Integer, Map<String, Float>>>> caseFourList = linesModified.getLinesModifyA();
        //case-5
        Map<String, Map<String, Map<Integer, Map<String, Float>>>> caseFourListB = linesModified.getLinesModifyB();
        System.out.println("After Case-5");
        //case-6
        parseCoupledCSV.parseData();
        System.out.println("Here is case 6");
        //System.exit(0);
        Map<String, List<Map<Integer, Map<String, Float>>>> pairStrengthSubMap = new LinkedHashMap<>();
        Map<Integer, Map<String, Float>> pairStrengthSubTwoMap = new LinkedHashMap<>();
        List<Map<Integer, Map<String, Float>>> pairSubTwoMapList = new LinkedList<>();
        Map<String, Float> pairStrengthSubThreeMap = new LinkedHashMap<>();

        int i = 0;

        Map<String, Map<Integer, Map<String, Integer>>> excelYearMap = pairMaps.getValue();
        setExcelYearMaps(excelYearMap);
        Map<String, Map<String, Map<Integer, Map<String, Integer>>>> excelCoCommitMap = pairMaps.getKey();
        for (String row : excelYearMap.keySet()) {
            Map<String, Map<Integer, Map<String, Integer>>> excelCoCommitColumnMap = excelCoCommitMap.get(row);
            for (String column : excelCoCommitColumnMap.keySet()) {
                Map<Integer, Map<String, Integer>> excelCommitID = excelCoCommitColumnMap.get(column);
                for (int commitKey : excelCommitID.keySet()) {
                    Map<String, Integer> excelCoCommitvalue = excelCommitID.get(commitKey);
                    for (String key : excelCoCommitvalue.keySet()) {
                        i = i + 1;
                        //Case-1 Number of times A&B are co-committed together
                        int case1 = excelCoCommitvalue.get(key);
                        //Case-2  Number of time (A&B) are co-committed/ Number of time A is committed globally
                        float case2 = 0.0f;
                        if (tableCommits.contains(row, column)) {
                            if (tableCommits.get(row, column).containsKey(commitKey)) {
                                if (tableCommits.get(row, column).get(commitKey).containsKey(key)) {
                                    case2 = tableCommits.get(row, column).get(commitKey).get(key).get(2);
                                }
                            }
                        }
                        //Case-3 Time difference when A&B are co-commited in a consecutive commit/ Count Difference
                        int case3 = 0;
                        if (!timeDifference.isEmpty() && timeDifference.containsKey(row)) {
                            if (timeDifference.get(row).containsKey(column)) {
                                if (timeDifference.get(row).get(column).containsKey(commitKey)) {
                                    if (timeDifference.get(row).get(column).get(commitKey).containsKey(key)) {
                                        case3 = timeDifference.get(row).get(column).get(commitKey).get(key);
                                    }
                                }
                            }
                        }
                        //Case-4 Number of lines of A has modified/Total number of lines in the commit has modified, excluding A&B
                        float case4 = 0.0f;
                        if (caseFourList.containsKey(row) && caseFourList.get(row).containsKey(column) && caseFourList.get(row).get(column).containsKey(commitKey) && caseFourList.get(row).get(column).get(commitKey).containsKey(key)) {
                            case4 = caseFourList.get(row).get(column).get(commitKey).get(key);
                        }
                        // Case-5 Number of lines of B has modified/Number of lines of the commit(except A&B)
                        float case5 = 0.0f;
                        if (caseFourListB.containsKey(row) && caseFourListB.get(row).containsKey(column) && caseFourListB.get(row).get(column).containsKey(commitKey) && caseFourListB.get(row).get(column).get(commitKey).containsKey(key)) {
                            case5 = caseFourListB.get(row).get(column).get(commitKey).get(key);
                        }
                        //Case-6-//Case6:(Number of calls between A to B/ Maximum number of calls of A to all other co-committed files)*(modified lines in File A + modified lines in File B)
//                        HashMap<String,HashMap<String,HashMap<String,Float>>> callingMap=parseCoupledCSV.getFinalCallsValue();
//                        String dict = "";
//                        if (!dictionary.isEmpty()) {
//                            dict = dictionary.get(commitKey);
//                        }
//                        float case6=0.0f;
//                        //System.out.println(" Out case 6");
//                        //Start: Creating a HashMap for PairWise Strength
//                        if(callingMap.containsKey(row) && callingMap.get(row).containsKey(column) && callingMap.get(row).get(column).containsKey(dict))
//                        {
//                             case6= callingMap.get(row).get(column).get(dict);
//                             System.out.println(" IN case 6");
//                        }
                        //Summation
                        float sum = (float) case1 +  case2 + (float) case3 + case4 + case5;


                        //System.out.println("SUMMATION" + sum);

                        {
                            pairStrengthSubThreeMap.put(key, sum); //year
                            pairStrengthSubTwoMap.put(commitKey, pairStrengthSubThreeMap); //integer
                            pairSubTwoMapList.add(pairStrengthSubTwoMap);
                            pairStrengthSubThreeMap = new LinkedHashMap<>();
                            pairStrengthSubTwoMap = new LinkedHashMap<>();
                        }
                        //End: Creating a HashMap for PairWise Strength

                    }

                }
                pairStrengthSubMap.put(column, pairSubTwoMapList);
                pairSubTwoMapList = new LinkedList<>();
            }
            localPairStrength.put(row, pairStrengthSubMap);
            pairStrengthSubMap = new LinkedHashMap<>();
            i++;

        }
        setPairStrengthMap(localPairStrength);
        logger.info("Pair Strength");
        logger.info(localPairStrength.toString());


    }
    
    public void setUpObjects() {
//    	committedSoFar = new CommittedSoFar();
    	committedSoFar.setReadingStrategy(readingStrategy);
//    	coCommittedPrime = new CoCommittedPrime();
    	coCommittedPrime.setCommittedSoFar(committedSoFar);
    	coCommittedPrime.setReadingStrategy(readingStrategy);
//    	withoutCommitPrime = new WithoutCommitPrime();
    	withoutCommitPrime.setCommittedSoFar(committedSoFar);
    	withoutCommitPrime.setReadingStrategy(readingStrategy);
    	linesModifiedPrime = new LinesModifiedPrime();
    	linesModifiedPrime.setReadingStrategy(readingStrategy);
    	parseCoupledCSV = new ParseCoupledCSV();
    	parseCoupledCSV.setReadingStrategy(readingStrategy);
//    	coCommittedFiles = new CoCommittedFiles();
    	coCommittedFiles.setReadingStrategy(readingStrategy);
    }
    
    @Override
    public void calculatePairStrength(){
    	setUpObjects();
        logger.info("inside calculate Pair Strength");
        System.out.println("inside calculate Pair Strength ");
        Map<String,Map<String, Map<Integer,List<Object>>>> readMap= readingStrategy.getReadableMappingFinalI().rowMap();
        Map<Integer,String> dictionaryKey= readingStrategy.getDictionaryI();
        Map<String,String> dictionaryStringDate=readingStrategy.getDictionaryTimeI();
        Map<String,Map<String,Boolean>> bugFixingMap= readingStrategy.getReadableBugFixingI();
        //Populate Committed So Far
        logger.info("Before Committed So Far");
        committedSoFar.committedSoFar();
        logger.info("After Committed So Far");
        //Case 1'' Number of times the file A&B are co-committed * 2/  Number of times A has been committed so far + Number of times B has been committed so far
        logger.info("Before coCommittedPrime");
        coCommittedPrime.getCoCommittedFiles();
        logger.info("After coCommittedPrime");
        Map<String,Map<String,Map<String, Float>>> coCommit=coCommittedPrime.getCommittedPrimeValue();
        //Case 2: Number of time (A&B) are co-committed/ Number of time A is committed so far
        logger.info("After Case 2");
        System.out.println("After Case 2");
        Map<String,Map<String,Map<String, Float>>> coCommitTogether=coCommittedPrime.getCommittedTogetherValue();
        //How many times the File A has been committed without File B/ Number of time A is committed so far
        logger.info("coCommitTogether");
        withoutCommitPrime.getTimeDifference();
        logger.info("After Case 3");
        Map<String,Map<String,Map<String, Float>>> committedNotTogether=withoutCommitPrime.getCoTimeDifference(); //Todo Need to check at that particular point when files are committed together
        //Case 6: ’: Number of calls between A to B/ Average number of calls from A to all other co-committed files. (Ignore the self calls
        //parseCoupledCSV.parseData();
        logger.info("After Case 6");
        System.out.println("After Case 6");

        linesModifiedPrime.getLinesModified();
        System.out.println("After Case-4");
        Map<String, Map<String, Map<String, Float>>> sourceLinesModified = linesModifiedPrime.getLinesModifiedSource();
        //case-5
        Map<String, Map<String, Map<String, Float>>> destinationLinesModified = linesModifiedPrime.getLinesModifiedDestination();
//        HashMap<String,HashMap<String,HashMap<String,Float>>> callsMap=parseCoupledCSV.getFinalCallsValue();
        float pairStrength=0.0f;
        Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> localPairStrength = new HashMap<>();
        for(String source: readMap.keySet())
        {
            Map<String, List<Map<Integer, Map<String, Float>>>> pairStrengthSubMap = new LinkedHashMap<>();
            for(String destination: readMap.get(source).keySet())
            {
                Map<String, Float> pairStrengthSubThreeMap = new TreeMap<>();
                Map<Integer, Map<String, Float>> pairStrengthSubTwoMap = new TreeMap<>();
                List<Map<Integer, Map<String, Float>>> pairSubTwoMapList = new LinkedList<>();
                for(int commitKey: readMap.get(source).get(destination).keySet())
                {
                    String commitTime=dictionaryStringDate.get(dictionaryKey.get(commitKey));

                    float coCommitValue=coCommit.get(source).get(destination).get(commitTime); //Case 1''
                    float coCommitTogetherValue=coCommitTogether.get(source).get(destination).get(commitTime); //Case 2
//                    float callsValue=0.0f;
                    /*if(callsMap.containsKey(source) && callsMap.get(source).containsKey(destination) && callsMap.get(source).get(destination).containsKey(commitTime))
                    {
                        callsValue=callsMap.get(source).get(destination).get(commitTime);
                    }*/
                    float commitNotTog=0.0f;
                    if(committedNotTogether.containsKey(source) && committedNotTogether.get(source).containsKey(destination) && committedNotTogether.get(source).get(destination).containsKey(commitTime))
                    {
                        commitNotTog=committedNotTogether.get(source).get(destination).get(commitTime);
                    }

                    float sourceLinesModify=0.0f;
                    if(sourceLinesModified.containsKey(source) && sourceLinesModified.get(source).containsKey(destination) && sourceLinesModified.get(source).get(destination).containsKey(commitTime))
                    {
                        //logger.info("Inside 1");
                        sourceLinesModify=sourceLinesModified.get(source).get(destination).get(commitTime);
                    }
                    float destinationLinesModify=0.0f;
                    if(destinationLinesModified.containsKey(source) && destinationLinesModified.get(source).containsKey(destination) && destinationLinesModified.get(source).get(destination).containsKey(commitTime))
                    {
                        //logger.info("Inside 2");
                        destinationLinesModify=destinationLinesModified.get(source).get(destination).get(commitTime);
                    }
                    //replce- with+
                    if((bugFixingMap.containsKey(source) && bugFixingMap.get(source).containsKey(commitTime) && bugFixingMap.get(source).get(commitTime)) && (bugFixingMap.containsKey(source) && bugFixingMap.get(destination).containsKey(commitTime) && bugFixingMap.get(destination).get(commitTime)))
                    {
                        pairStrength =1.5f*(coCommitValue +coCommitTogetherValue + sourceLinesModify + destinationLinesModify) - commitNotTog;
                    }
                    else {
                        //pairStrength=coCommitValue+coCommitTogetherValue+ callsValue  + sourceLinesModify + destinationLinesModify - commitNotTog;
                        //0.50  //0.20
                        pairStrength =  0.20f*(coCommitValue + coCommitTogetherValue + sourceLinesModify + destinationLinesModify - commitNotTog);
                    }
                    //logger.info(" callsValue "+ callsValue + " coCommitValue " + coCommitValue + " coCommitTogetherValue " + coCommitTogetherValue +  " commitNotTog " + commitNotTog + " sourceLinesModify " + sourceLinesModify + " destinationLinesModify " + destinationLinesModify);

                    pairStrengthSubThreeMap.put(commitTime,pairStrength);
                    pairStrengthSubTwoMap.put(commitKey, pairStrengthSubThreeMap);
                    pairSubTwoMapList.add(pairStrengthSubTwoMap);
                    pairStrengthSubThreeMap=new TreeMap<>();
                    pairStrengthSubTwoMap= new TreeMap<>();
                }
                pairStrengthSubMap.put(destination, pairSubTwoMapList);

            }
            localPairStrength.put(source, pairStrengthSubMap);
        }
        //System.exit(0);

        coCommittedFiles.coCommitABCD();
        Pair<Map<String, Map<String, Map<Integer, Map<String, Integer>>>>, Map<String, Map<Integer, Map<String, Integer>>>> pairMaps = coCommittedFiles.getPairMaps();
        Map<String, Map<Integer, Map<String, Integer>>> excelYearMap = pairMaps.getValue();
        setExcelYearMaps(excelYearMap);
        setPairStrengthMap(localPairStrength);
        //logger.info("Pair Strength");
        //logger.info(localPairStrength.toString());

    }

	public LinesModifiedPrime getLinesModifiedPrime() {
		return linesModifiedPrime;
	}

	public void setLinesModifiedPrime(LinesModifiedPrime linesModifiedPrime) {
		this.linesModifiedPrime = linesModifiedPrime;
	}

	public CommittedSoFar getCommittedSoFar() {
		return committedSoFar;
	}

	public void setCommittedSoFar(CommittedSoFar committedSoFar) {
		this.committedSoFar = committedSoFar;
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

	public ParseCoupledCSV getParseCoupledCSV() {
		return parseCoupledCSV;
	}

	public void setParseCoupledCSV(ParseCoupledCSV parseCoupledCSV) {
		this.parseCoupledCSV = parseCoupledCSV;
	}

	public CoCommittedExcel getCoCommittedExcel() {
		return coCommittedExcel;
	}

	public void setCoCommittedExcel(CoCommittedExcel coCommittedExcel) {
		this.coCommittedExcel = coCommittedExcel;
	}

	public CoCommittedFiles getCoCommittedFiles() {
		return coCommittedFiles;
	}

	public void setCoCommittedFiles(CoCommittedFiles coCommittedFiles) {
		this.coCommittedFiles = coCommittedFiles;
	}

	public LinesModified getLinesModified() {
		return linesModified;
	}

	public void setLinesModified(LinesModified linesModified) {
		this.linesModified = linesModified;
	}

	public TimeDifference getTimeDifferences() {
		return timeDifferences;
	}

	public void setTimeDifferences(TimeDifference timeDifferences) {
		this.timeDifferences = timeDifferences;
	}

	public IStrengthAccumulator getAccumulatedStrength() {
		return accumulatedStrength;
	}

	public void setAccumulatedStrength(IStrengthAccumulator accumulatedStrength) {
		this.accumulatedStrength = accumulatedStrength;
	}

	public Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> getPairStrengthMap() {
		return pairStrengthMap;
	}

	public void setPairStrengthMap(Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMap) {
		this.pairStrengthMap = pairStrengthMap;
	}

	public Map<String, Map<Integer, Map<String, Integer>>> getExcelYearMaps() {
		return excelYearMaps;
	}

	public void setExcelYearMaps(Map<String, Map<Integer, Map<String, Integer>>> excelYearMaps) {
		this.excelYearMaps = excelYearMaps;
	}

	public IReadingStrategy getReadingStrategy() {
		return readingStrategy;
	}

	public void setReadingStrategy(IReadingStrategy readingStrategy) {
		this.readingStrategy = readingStrategy;
	}
	
	public Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> getPairStrengthMapIn() {
        return getPairStrengthMap();
    }

    public Map<String, Map<Integer, Map<String, Integer>>> getExcelYearMapsIn() {
        return getExcelYearMaps();
    }


}
