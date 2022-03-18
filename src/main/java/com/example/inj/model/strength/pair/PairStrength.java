package com.example.inj.model.strength.pair;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.inj.model.cases.CoCommittedExcel;
import com.example.inj.model.cases.CoCommittedFiles;
import com.example.inj.model.cases.LinesModified;
import com.example.inj.model.cases.TimeDifference;
import com.example.inj.model.cases.coupled.ParseCoupledCSV;
import com.example.inj.model.cases.prime.CoCommittedPrime;
import com.example.inj.model.cases.prime.CommittedSoFar;
import com.example.inj.model.cases.prime.LinesModifiedPrime;
import com.example.inj.model.cases.prime.WithoutCommitPrime;
import com.example.inj.model.storage.DataRepository;
import com.example.inj.model.strength.accumulators.IStrengthAccumulator;
import com.example.inj.model.strength.pair.strategies.IPairStrengthStrategy;
import com.google.common.collect.Table;

import javafx.util.Pair;

public class PairStrength implements IPairStrength{

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
    private DataRepository dataRepository;
    Logger logger = LoggerFactory.getLogger(PairStrength.class);
    private IPairStrengthStrategy theStrategy = null;

    public PairStrength() {
    	dataRepository = DataRepository.getInstance();
    }
    
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
    	committedSoFar = new CommittedSoFar();
    	coCommittedPrime = new CoCommittedPrime();
    	withoutCommitPrime = new WithoutCommitPrime();
    	linesModifiedPrime = new LinesModifiedPrime();
    	parseCoupledCSV = new ParseCoupledCSV();
    	coCommittedFiles = new CoCommittedFiles();
    }
    
    public void setStrategy(IPairStrengthStrategy newStrategy) {
    	this.theStrategy = newStrategy;
    }
    
    public void calculatePairStrength(){
//    	TODO may be possible to be completely cleaned up of external dependencies
    	setUpObjects();
        logger.info("inside calculate Pair Strength");
        System.out.println("inside calculate Pair Strength ");
        //Populate Committed So Far
        logger.info("Before Committed So Far");
        committedSoFar.committedSoFar();
        logger.info("After Committed So Far");
        //Case 1'' Number of times the file A&B are co-committed * 2/  Number of times A has been committed so far + Number of times B has been committed so far
        logger.info("Before coCommittedPrime");
        coCommittedPrime.getCoCommittedFiles();
        logger.info("After coCommittedPrime");
        //Case 2: Number of time (A&B) are co-committed/ Number of time A is committed so far
        logger.info("After Case 2");
        System.out.println("After Case 2");
        //How many times the File A has been committed without File B/ Number of time A is committed so far
        logger.info("coCommitTogether");
        withoutCommitPrime.getTimeDifference();
        logger.info("After Case 3");
        //Case 6: ’: Number of calls between A to B/ Average number of calls from A to all other co-committed files. (Ignore the self calls
        //parseCoupledCSV.parseData();
        logger.info("After Case 6");
        System.out.println("After Case 6");

        linesModifiedPrime.getLinesModified();
        System.out.println("After Case-4");
        //case-5
//        TODO REVIEW THE ACTUAL STRENGTH CALCULATION STRATEGY
        
        dataRepository.setPairStrengthMap(theStrategy.calculate(dataRepository));
//       
//		why the hell do we need this??? call (coCommitABCD)
//        looks like we need the value of the pair later on in StrengthAccumulator
        coCommittedFiles.coCommitABCD();
//        Pair<Map<String, Map<String, Map<Integer, Map<String, Integer>>>>, Map<String, Map<Integer, Map<String, Integer>>>> pairMaps = dataRepository.getPairMaps();
//        Map<String, TreeSet<String>> fileIds2CommitDates = dataRepository.getFileId2CommitDates();
//        Map<String, Map<Integer, Map<String, Integer>>> excelYearMap = pairMaps.getValue();
//        dataRepository.setExcelYearMaps(excelYearMap);
//        setPairStrengthMap(localPairStrength);
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

}
