package com.example.inj.model.sampling;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.example.inj.model.decays.PairLevelDecay;
import com.example.inj.model.strength.singlefile.ISingleFileStrength;

public class CreateWidth {

	private PairLevelDecay pairLevelDecay;
    private ISingleFileStrength singleFileStrength;
    private Map<String, Integer> segmentWidth = new LinkedHashMap<>();
    
    
    /*CreateSegmentWidth() function is created to estimate the width of segment based on the mean of
    file is committed between the intervals, like t1, t4, t8, t12. Whereas, global clock tick from t1,
    t2, t3, t4, t5, t6, t7... t12.
    TD1: t4-t1
    TD2: t8-t4
    TD3: t12-t8
    Mean of (TD1, TD2, TD3) will be the segment width.
    */
    public void createSegmentWidth() throws ParseException {
        Map<String, List<String>> yearMapPairSame = pairLevelDecay.getYearMapPair();
        Map<String, List<String>> yearMapAloneSame = singleFileStrength.getYearMapAloneSame();
        List<String> commitYear = new LinkedList<>();
        Map<String, List<String>> commitYearMap = new LinkedHashMap<>();

        for (String row : yearMapPairSame.keySet()) {
            for (String col : yearMapAloneSame.keySet()) {
                if (row.equals(col)) {
                    commitYear.addAll(yearMapAloneSame.get(row));
                }

            }

            commitYear.addAll(yearMapPairSame.get(row));
            Collections.sort(commitYear);
            commitYearMap.put(row, commitYear);
            commitYear = new LinkedList<>();

        }


        String prev = "";
        String next = "";
        List<Long> meanString = new LinkedList<>();
        Map<String, Integer> meanMap = new LinkedHashMap<>();
        Date prevDate = null;
        Date nextDate = null;
        long difference_In_Time = 0;
        long difference_In_Hours = 0;
        Double avg = 0.0;


        for (String commitKey : commitYearMap.keySet()) {
            commitYear.addAll(commitYearMap.get(commitKey));
            if (commitYear.size() > 1) {
                for (int i = 0; i < commitYear.size(); i++) {
                    prev = commitYear.get(i);
                    i++;
                    if (i < commitYear.size()) {
                        next = commitYear.get(i);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        prevDate = sdf.parse(prev);
                        nextDate = sdf.parse(next);

                        //System.out.println("prevDate "+ prevDate + " nextDate " + nextDate+  " nextDate.getTime()  " + nextDate.getTime() + " prevDate.getTime() " + prevDate.getTime() );



                        difference_In_Time = nextDate.getTime() - prevDate.getTime();
                        difference_In_Hours = (difference_In_Time
                                / (1000 * 60 * 60))
                                % 24;
                       /* System.out.println(" row " + commitKey +" prev " + prevDate + " next " + nextDate  + "difference_In_Hours " + difference_In_Hours);
                        System.exit(0);*/

                    }
                    i--;
                    meanString.add(difference_In_Hours);
                    difference_In_Hours = 0;

                }
                avg = meanString.stream().mapToLong(i -> i).average().getAsDouble();

            }

            meanMap.put(commitKey, (int) Math.round(avg));
            commitYear = new LinkedList<>();
            meanString = new LinkedList<>();
        }


        System.out.println(" Mean Map ");

        setSegmentWidth(meanMap);




    }


	public PairLevelDecay getPairLevelDecay() {
		return pairLevelDecay;
	}


	public void setPairLevelDecay(PairLevelDecay pairLevelDecay) {
		this.pairLevelDecay = pairLevelDecay;
	}


	public ISingleFileStrength getSingleFileStrength() {
		return singleFileStrength;
	}


	public void setSingleFileStrength(ISingleFileStrength singleFileStrength) {
		this.singleFileStrength = singleFileStrength;
	}


	public Map<String, Integer> getSegmentWidth() {
		return segmentWidth;
	}


	public void setSegmentWidth(Map<String, Integer> segmentWidth) {
		this.segmentWidth = segmentWidth;
	}
}
