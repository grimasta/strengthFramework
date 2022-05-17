package com.example.inj.model.sampling;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.example.inj.model.storage.DataRepository;

public class CreateWidth {

    private DataRepository dataRepository;
    
    public CreateWidth() {
    	dataRepository = DataRepository.getInstance();
    }

    // Let's say file A participates in commits: C1, C4, C9, C10(file A may not appear in every commit).
    // The time(in terms of hours) elapsed between every two commits is denoted as: t1, t2, t3(t1 for time elapsed between
    // C1 and C4, similarly for t2 and t3).
    // t1, t2, and t3 are normalized by taking the reminder after divided by 24(# of hour in a day).
    // then the mean of (t1, t2, t3) will be the segment width.

    public void createSegmentWidth() throws ParseException {
        Map<String, List<String>> yearMapPairSame = dataRepository.getYearMapPair();
        Map<String, List<String>> yearMapAloneSame = dataRepository.getYearMapAloneSame();
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

        dataRepository.setSegmentWidth(meanMap);

    }

}
