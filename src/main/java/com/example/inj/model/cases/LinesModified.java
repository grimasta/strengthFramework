package com.example.inj.model.cases;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.inj.model.storage.DataRepository;
//Donot Use for now
public class LinesModified {

    private DataRepository dataRepository;
    
    public LinesModified() {
    	dataRepository = DataRepository.getInstance();
    }

    Map<String, Map<String, Map<Integer, Map<String, Float>>>> linesModifyA;
    Map<String, Map<String, Map<Integer, Map<String, Float>>>> linesModifyB;

    public Map<String, Map<String, Map<Integer, Map<String, Float>>>> getLinesModifyA() {
        return linesModifyA;
    }

    public void setLinesModifyA(Map<String, Map<String, Map<Integer, Map<String, Float>>>> linesModifyA) {
        this.linesModifyA = linesModifyA;
    }

    public Map<String, Map<String, Map<Integer, Map<String, Float>>>> getLinesModifyB() {
        return linesModifyB;
    }

    public void setLinesModifyB(Map<String, Map<String, Map<Integer, Map<String, Float>>>> linesModifyB) {
        this.linesModifyB = linesModifyB;
    }

    //Fourth And Fifth Case
     /*Case-4 Start- Number of Lines of A is modified to the number of lines are modified in The COMMIT excluding A&B
     OR Number of lines of A is modified/ Total number of lines are modified in the commit excluding A&B
     And,
     Case-5 Number of lines of B is modified/ Total number of lines are modified in the commit excluding A&B
     */

    public void numberOflinesModified() {

        List<Object> xyz;
        Map<Integer, List<Object>> hm;
        int modifiedLinesA = 0; //Number of lines of A has modified(not globally in a particular commit)
        int modifiedLinesB = 0; //Number of lines of B has modified(not globally in a particular commit) when A & B are committed together
        int modifiedA = 0;
        int modifiedB = 0;
        int cAddition = 0;
        float calA = 0;
        float calB = 0;
        String dates = "";
        /* Map<String, Float> linesModifiedA = new LinkedHashMap<>();*/
        Map<Integer, Map<String, Float>> linesModifiedAAP = new LinkedHashMap<>(); //For Excel
        Map<String, Map<Integer, Map<String, Float>>> linesAAP = new LinkedHashMap<>(); //For Excel
        Map<String, Map<String, Map<Integer, Map<String, Float>>>> linesAAAP = new LinkedHashMap<>(); //For Excel

        Map<Integer, Map<String, Float>> linesModifiedBBP = new LinkedHashMap<>(); //For Excel
        Map<String, Map<Integer, Map<String, Float>>> linesBBP = new LinkedHashMap<>(); //For Excel
        Map<String, Map<String, Map<Integer, Map<String, Float>>>> linesBBBP = new LinkedHashMap<>(); //For Excel

        /*Map<String, Float> linesModifiedB = new LinkedHashMap<>();*/
        Map<String, Map<String, Map<Integer, List<Object>>>> pqr = dataRepository.getReadableMappingFinal().rowMap();

        for (String row : pqr.keySet()) {
            Map<String, Map<Integer, List<Object>>> column = pqr.get(row);

            for (String c : column.keySet()) {
                hm = column.get(c);
                Iterator<Map.Entry<Integer, List<Object>>> hmIterator = hm.entrySet().iterator();
                modifiedLinesA = 0;
                modifiedLinesB = 0;
                cAddition = 0;
                while (hmIterator.hasNext()) {    //for( int key: hm.keySet())
                    //cAddition = 0;  //NEED TO CONFIRM
                    Map.Entry<Integer, List<Object>> mapElement = (Map.Entry<Integer, List<Object>>) hmIterator.next();
                    Map<String, Float> linesModifiedAA = new LinkedHashMap<>(); //For Excel
                    Map<String, Float> linesModifiedBB = new LinkedHashMap<>(); //For Excel
                    xyz = (List<Object>) mapElement.getValue(); //xyz= hm.get(key);
                    int key = (int) mapElement.getKey();
                    modifiedA = (Integer) xyz.get(2);
                    modifiedB = (Integer) xyz.get(3);
                    modifiedLinesA = modifiedLinesA + modifiedA;
                    modifiedLinesB = modifiedLinesB + modifiedB;
                    // CAddition- Total number of lines are committed in a global clock excluding the modfied lines of A &B
                    //xyz.get(14)- It is added for the bug

                    cAddition = cAddition + (Integer) xyz.get(13) + (Integer)xyz.get(14) - modifiedA - modifiedB;

                    //
                    dates = (String) xyz.get(10);

                    if (cAddition != 0) {
                        calA = ((float) modifiedLinesA / (float) cAddition);
                        calB = ((float) modifiedLinesB / (float) cAddition);
                        linesModifiedAA.put(dates, calA); //For Excel
                        linesModifiedAAP.put(key, linesModifiedAA); //For Excel
                        linesModifiedBB.put(dates, calB); //For Excel
                        linesModifiedBBP.put(key, linesModifiedBB); //For Excel
                    }
                    else if(cAddition==0)
                    {
                        if(cAddition == 0 )
                        {
                            System.out.println("cAddition " + cAddition + " modifiedLinesA  " + modifiedLinesA + " modifiedLinesB " + modifiedLinesB + "(Integer) xyz.get(13) " + (Integer) xyz.get(13));
                        }
                        //Need to fix
                        calA = (float) modifiedLinesA;
                        calB = (float) modifiedLinesB;
                        linesModifiedAA.put(dates, calA); //For Excel
                        linesModifiedAAP.put(key, linesModifiedAA); //For Excel
                        linesModifiedBB.put(dates, calB); //For Excel
                        linesModifiedBBP.put(key, linesModifiedBB); //For Excel
                    }


                }

                hmIterator = null; //Bug 003: Explicity using garbage Collector
                linesAAP.put(c, linesModifiedAAP); //For Excel
                linesBBP.put(c, linesModifiedBBP); //For Excel


                linesModifiedAAP = new LinkedHashMap<>(); //For Excel
                linesModifiedBBP = new LinkedHashMap<>(); //For Excel
            }

            linesAAAP.put(row, linesAAP); //For Excel
            linesBBBP.put(row, linesBBP); //For Excel

            linesAAP = new LinkedHashMap<>(); //For excel;
            linesBBP = new LinkedHashMap<>(); //For Excel
        }



        System.out.println("lines Modified AAP");
        dataRepository.setLinesModifyA(linesAAAP);
        dataRepository.setLinesModifyB(linesBBBP);

        System.out.println("Lines Modified in A");
        //linesAAAP.get("caa84917-1ed0-11eb-99c6-482ae32cf5b4").entrySet().forEach(e-> System.out.print(e));


        System.out.println("Lines Modified in B");
        //linesBBBP.get("caa84917-1ed0-11eb-99c6-482ae32cf5b4").entrySet().forEach(e-> System.out.print(e));
    }


}
