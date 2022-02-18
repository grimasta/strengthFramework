package com.example.inj.automate;


import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.inj.global.ProjectNameContainer;
import com.example.inj.model.storage.DataRepository;

public class Chi2Automate {

    static int count1UBugFixing = 0;
    static int cont1UNonBugFixing = 0;
    static int count1DBugFixing = 0;
    static int count1DNonBugFixing = 0;
    static int count1UNonBugFixing = 0;
    static int count1UNonDevelopmentBugFixing = 0;
    static int count1DNonDevelopmentBugFixing = 0;
    static int count1UDevelopmentBugFixing = 0;
    static int count1DDevelopmentBugFixing = 0;
    static int count1UActualBugFixing = 0;
    static int count1DActualBugFixing = 0;
    static int count2UUBugFixing = 0;
    static int count2UUNonBugFixing = 0;
    static int count2UUDevelopmentBugFixing = 0;
    static int count2UUNonDevelopmentBugFixing = 0;
    static int count2UUActualBugFixing = 0;
    static int coun2UULastSegmentIsBuggy = 0;
    static int count2UUSecondLastSegmentIsBuggy = 0;
    static int count2UDBugFixing = 0;
    static int count2UDNonBugFixing = 0;
    static int count2UDDevelopmentBugFixing = 0;
    static int count2UDNonDevelopmentBugFixing = 0;
    static int count2UDActualBugFixing = 0;
    static int coun2UDLastSegmentIsBuggy = 0;
    static int count2UDSecondLastSegmentIsBuggy = 0;
    static int count2DUBugFixing = 0;
    static int count2DUNonBugFixing = 0;
    static int count2DUDevelopmentBugFixing = 0;
    static int count2DUNonDevelopmentBugFixing = 0;
    static int count2DUActualBugFixing = 0;
    static int coun2DULastSegmentIsBuggy = 0;
    static int count2DUSecondLastSegmentIsBuggy = 0;
    static int count2DDBugFixing = 0;
    static int count2DDNonBugFixing = 0;
    static int count2DDDevelopmentBugFixing = 0;
    static int count2DDNonDevelopmentBugFixing = 0;
    static int count2DDActualBugFixing = 0;
    static int coun2DDLastSegmentIsBuggy = 0;
    static int count2DDSecondLastSegmentIsBuggy = 0;
    //UUU,UDD,DDU,DUD,UUD,DUU,UDU,DDD
    static int count3UUUBugFixing = 0;
    static int count3UUUNonBugFixing = 0;
    static int count3UUUDevelopmentBugFixing = 0;
    static int count3UUUActualBugFixing = 0;
    static int count3UUUNonDevelopmentBugFixing = 0;
    static int coun3UUULastSegmentIsBuggy = 0;
    static int count3UUUSecondLastSegmentIsBuggy = 0;
    static int count3UUULastSecondLastSegmentIsBuggy = 0;
    static int count3UDDBugFixing = 0;
    static int count3UDDNonBugFixing = 0;
    static int count3UDDDevelopmentBugFixing = 0;
    static int count3UDDNonDevelopmentBugFixing = 0;
    static int count3UDDActualBugFixing = 0;
    static int coun3UDDLastSegmentIsBuggy = 0;
    static int count3UDDSecondLastSegmentIsBuggy = 0;
    static int count3UDDLastSecondLastSegmentIsBuggy = 0;
    static int count3DDUBugFixing = 0;
    static int count3DDUNonBugFixing = 0;
    static int count3DDUDevelopmentBugFixing = 0;
    static int count3DDUNonDevelopmentBugFixing = 0;
    static int count3DDUActualBugFixing = 0;
    static int coun3DDULastSegmentIsBuggy = 0;
    static int count3DDUSecondLastSegmentIsBuggy = 0;
    static int count3DDULastSecondLastSegmentIsBuggy = 0;
    static int count3DUDBugFixing = 0;
    static int count3DUDNonBugFixing = 0;
    static int count3DUDDevelopmentBugFixing = 0;
    static int count3DUDNonDevelopmentBugFixing = 0;
    static int count3DUDActualBugFixing = 0;
    static int coun3DUDLastSegmentIsBuggy = 0;
    static int count3DUDSecondLastSegmentIsBuggy = 0;
    static int count3DUDLastSecondLastSegmentIsBuggy = 0;
    static int count3UUDBugFixing = 0;
    static int count3UUDNonBugFixing = 0;
    static int count3UUDDevelopmentBugFixing = 0;
    static int count3UUDNonDevelopmentBugFixing = 0;
    static int count3UUDActualBugFixing = 0;
    static int coun3UUDLastSegmentIsBuggy = 0;
    static int count3UUDSecondLastSegmentIsBuggy = 0;
    static int count3UUDLastSecondLastSegmentIsBuggy = 0;
    static int count3DUUBugFixing = 0;
    static int count3DUUNonBugFixing = 0;
    static int count3DUUDevelopmentBugFixing = 0;
    static int count3DUUNonDevelopmentBugFixing = 0;
    static int count3DUUActualBugFixing = 0;
    static int coun3DUULastSegmentIsBuggy = 0;
    static int count3DUUSecondLastSegmentIsBuggy = 0;
    static int count3DUULastSecondLastSegmentIsBuggy = 0;
    static int count3UDUBugFixing = 0;
    static int count3UDUNonBugFixing = 0;
    static int count3UDUDevelopmentBugFixing = 0;
    static int count3UDUNonDevelopmentBugFixing = 0;
    static int count3UDUActualBugFixing = 0;
    static int coun3UDULastSegmentIsBuggy = 0;
    static int count3UDUSecondLastSegmentIsBuggy = 0;
    static int count3UDULastSecondLastSegmentIsBuggy = 0;
    static int count3DDDBugFixing = 0;
    static int count3DDDNonBugFixing = 0;
    static int count3DDDDevelopmentBugFixing = 0;
    static int count3DDDNonDevelopmentBugFixing = 0;
    static int count3DDDActualBugFixing = 0;
    static int coun3DDDLastSegmentIsBuggy = 0;
    static int count3DDDSecondLastSegmentIsBuggy = 0;
    static int count3DDDLastSecondLastSegmentIsBuggy = 0;
    //UUUU,DUUU,UDUU, UUDU,UUUD,DDUU,UDDU,UUDD, DUUD, UDUD,DUDU, UDDD,DUDD,DDUD, DDDU, DDDD
    static int count4UUUUBugFixing = 0;
    static int count4UUUUNonBugFixing = 0;
    static int count4UUUUDevelopmentBugFixing = 0;
    static int count4UUUUNonDevelopmentBugFixing = 0;
    static int count4UUUUActualBugFixing = 0;
    static int count4UUUULastSegmentIsBuggy = 0;
    static int count4UUUUSecondLastSegmentIsBuggy = 0;
    static int count4UUUULastSecondLastSegmentIsBuggy = 0;
    static int count4DUUUBugFixing = 0;
    static int count4DUUUNonBugFixing = 0;
    static int count4DUUUDevelopmentBugFixing = 0;
    static int count4DUUUNonDevelopmentBugFixing = 0;
    static int count4DUUUActualBugFixing = 0;
    static int count4DUUULastSegmentIsBuggy = 0;
    static int count4DUUUSecondLastSegmentIsBuggy = 0;
    static int count4DUUULastSecondLastSegmentIsBuggy = 0;
    static int count4UDUUBugFixing = 0;
    static int count4UDUUNonBugFixing = 0;
    static int count4UDUUDevelopmentBugFixing = 0;
    static int count4UDUUNonDevelopmentBugFixing = 0;
    static int count4UDUUActualBugFixing = 0;
    static int count4UDUULastSegmentIsBuggy = 0;
    static int count4UDUUSecondLastSegmentIsBuggy = 0;
    static int count4UDUULastSecondLastSegmentIsBuggy = 0;
    static int count4UUDUBugFixing = 0;
    static int count4UUDUNonBugFixing = 0;
    static int count4UUDUDevelopmentBugFixing = 0;
    static int count4UUDUNonDevelopmentBugFixing = 0;
    static int count4UUDUActualBugFixing = 0;
    static int count4UUDULastSegmentIsBuggy = 0;
    static int count4UUDUSecondLastSegmentIsBuggy = 0;
    static int count4UUDULastSecondLastSegmentIsBuggy = 0;
    static int count4UUUDBugFixing = 0;
    static int count4UUUDNonBugFixing = 0;
    static int count4UUUDDevelopmentBugFixing = 0;
    static int count4UUUDNonDevelopmentBugFixing = 0;
    static int count4UUUDActualBugFixing = 0;
    static int count4UUUDLastSegmentIsBuggy = 0;
    static int count4UUUDSecondLastSegmentIsBuggy = 0;
    static int count4UUUDLastSecondLastSegmentIsBuggy = 0;
    static int count4DDUUBugFixing = 0;
    static int count4DDUUNonBugFixing = 0;
    static int count4DDUUDevelopmentBugFixing = 0;
    static int count4DDUUNonDevelopmentBugFixing = 0;
    static int count4DDUUActualBugFixing = 0;
    static int count4DDUULastSegmentIsBuggy = 0;
    static int count4DDUUSecondLastSegmentIsBuggy = 0;
    static int count4DDUULastSecondLastSegmentIsBuggy = 0;
    static int count4UDDUBugFixing = 0;
    static int count4UDDUNonBugFixing = 0;
    static int count4UDDUDevelopmentBugFixing = 0;
    static int count4UDDUNonDevelopmentBugFixing = 0;
    static int count4UDDUActualBugFixing = 0;
    static int count4UDDULastSegmentIsBuggy = 0;
    static int count4UDDUSecondLastSegmentIsBuggy = 0;
    static int count4UDDULastSecondLastSegmentIsBuggy = 0;
    static int count4UUDDBugFixing = 0;
    static int count4UUDDNonBugFixing = 0;
    static int count4UUDDDevelopmentBugFixing = 0;
    static int count4UUDDNonDevelopmentBugFixing = 0;
    static int count4UUDDActualBugFixing = 0;
    static int count4UUDDLastSegmentIsBuggy = 0;
    static int count4UUDDSecondLastSegmentIsBuggy = 0;
    static int count4UUDDLastSecondLastSegmentIsBuggy = 0;
    static int count4DUUDBugFixing = 0;
    static int count4DUUDNonBugFixing = 0;
    static int count4DUUDDevelopmentBugFixing = 0;
    static int count4DUUDNonDevelopmentBugFixing = 0;
    static int count4DUUDActualBugFixing = 0;
    static int count4DUUDLastSegmentIsBuggy = 0;
    static int count4DUUDSecondLastSegmentIsBuggy = 0;
    static int count4DUUDLastSecondLastSegmentIsBuggy = 0;
    static int count4UDUDBugFixing = 0;
    static int count4UDUDNonBugFixing = 0;
    static int count4UDUDDevelopmentBugFixing = 0;
    static int count4UDUDNonDevelopmentBugFixing = 0;
    static int count4UDUDActualBugFixing = 0;
    static int count4UDUDLastSegmentIsBuggy = 0;
    static int count4UDUDSecondLastSegmentIsBuggy = 0;
    static int count4UDUDLastSecondLastSegmentIsBuggy = 0;
    static int count4DUDUBugFixing = 0;
    static int count4DUDUNonBugFixing = 0;
    static int count4DUDUDevelopmentBugFixing = 0;
    static int count4DUDUNonDevelopmentBugFixing = 0;
    static int count4DUDUActualBugFixing = 0;
    static int count4DUDULastSegmentIsBuggy = 0;
    static int count4DUDUSecondLastSegmentIsBuggy = 0;
    static int count4DUDULastSecondLastSegmentIsBuggy = 0;
    static int count4UDDDBugFixing = 0;
    static int count4UDDDNonBugFixing = 0;
    static int count4UDDDDevelopmentBugFixing = 0;
    static int count4UDDDNonDevelopmentBugFixing = 0;
    static int count4UDDDActualBugFixing = 0;
    static int count4UDDDLastSegmentIsBuggy = 0;
    static int count4UDDDSecondLastSegmentIsBuggy = 0;
    static int count4UDDDLastSecondLastSegmentIsBuggy = 0;
    static int count4DUDDBugFixing = 0;
    static int count4DUDDNonBugFixing = 0;
    static int count4DUDDDevelopmentBugFixing = 0;
    static int count4DUDDNonDevelopmentBugFixing = 0;
    static int count4DUDDActualBugFixing = 0;
    static int count4DUDDLastSegmentIsBuggy = 0;
    static int count4DUDDSecondLastSegmentIsBuggy = 0;
    static int count4DUDDLastSecondLastSegmentIsBuggy = 0;
    static int count4DDUDBugFixing = 0;
    static int count4DDUDNonBugFixing = 0;
    static int count4DDUDDevelopmentBugFixing = 0;
    static int count4DDUDNonDevelopmentBugFixing = 0;
    static int count4DDUDActualBugFixing = 0;
    static int count4DDUDLastSegmentIsBuggy = 0;
    static int count4DDUDSecondLastSegmentIsBuggy = 0;
    static int count4DDUDLastSecondLastSegmentIsBuggy = 0;
    static int count4DDDUBugFixing = 0;
    static int count4DDDUNonBugFixing = 0;
    static int count4DDDUDevelopmentBugFixing = 0;
    static int count4DDDUNonDevelopmentBugFixing = 0;
    static int count4DDDUActualBugFixing = 0;
    static int count4DDDULastSegmentIsBuggy = 0;
    static int count4DDDUSecondLastSegmentIsBuggy = 0;
    static int count4DDDULastSecondLastSegmentIsBuggy = 0;
    static int count4DDDDBugFixing = 0;
    static int count4DDDDNonBugFixing = 0;
    static int count4DDDDDevelopmentBugFixing = 0;
    static int count4DDDDNonDevelopmentBugFixing = 0;
    static int count4DDDDActualBugFixing = 0;
    static int count4DDDDLastSegmentIsBuggy = 0;
    static int count4DDDDSecondLastSegmentIsBuggy = 0;
    static int count4DDDDLastSecondLastSegmentIsBuggy = 0;
    
    private Logger logger = LoggerFactory.getLogger(Chi2Automate.class);
    private boolean logging = false;
    private DataRepository dataRepository;
    		
    public Chi2Automate(Boolean logging) {
    	this.logging = logging;
    	dataRepository = DataRepository.getInstance();
    }
    
    public void getDetailsOfChi2() {

        Map<String, List<List<Object>>> sampleVectors = dataRepository.getVectorsForExcel();
        for (String key : sampleVectors.keySet()) {
            List<List<Object>> vectorList = sampleVectors.get(key);
            int slope = 0;
            List<Integer> listSlope = new ArrayList<>();
            List<Boolean> bugFixing = new ArrayList<>();
            List<Boolean> developmentFix = new ArrayList<>();

            for (List<Object> objLis : vectorList) {
                int count = 0;
                if (logging) {
                System.out.println("Here is list of objects");
                System.out.println(objLis.toString());
                }
                if (objLis.get(2).toString().equalsIgnoreCase("U")) {
                    slope = -1;
                } else {
                    slope = 1;
                }
                listSlope.add(slope);

                bugFixing.add((Boolean) objLis.get(6));
                if (logging) {
                logger.info("Inside the chi2 method");
                }
                //logger.info(objLis.get(4).toString());
                List<Boolean> abc = new ArrayList<>();
                abc.addAll((Collection<? extends Boolean>) objLis.get(4));

                for (Boolean b : (Collection<? extends Boolean>) objLis.get(4)) {
                    //System.out.println("Boolean " + b);
                    if (b) {
                        //System.out.println("Inside Boolean " + b);
                        count++;
                    }
                }
                if (count > 0) {
                    developmentFix.add(false);
                } else {
                    developmentFix.add(true);
                }


            }
            if (logging) 
            logger.info(" Development Fix " + developmentFix.toString());
            //System.exit(0);

            //Start: Chi-1 Results
            for (int i = 0, j = 0, k = 0; i < listSlope.size() && j < bugFixing.size() && k < developmentFix.size(); i++, j++, k++) {
                if (listSlope.get(i) == -1) {
                    if (developmentFix.get(k)) {
                        count1UDevelopmentBugFixing++;
                    } else {
                        if (bugFixing.get(j)) {
                            count1UActualBugFixing++;

                        }
                        count1UNonDevelopmentBugFixing++;
                    }
                    if (bugFixing.get(j)) {
                        count1UBugFixing++;
                    } else {
                        count1UNonBugFixing++;
                    }
                }

                if (listSlope.get(i) == 1) {
                    if (developmentFix.get(k)) {
                        count1DDevelopmentBugFixing++;
                    } else {
                        if (bugFixing.get(j)) {
                            count1DActualBugFixing++;

                        }
                        count1DNonDevelopmentBugFixing++;
                    }
                    if (bugFixing.get(j)) {
                        count1DBugFixing++;
                    } else {
                        count1DNonBugFixing++;
                    }
                }

            }

            //End: Chi-1 Results
            //Chi-2 for the results
            //UU

            for (int i = 0, j = 0, k = 0; i < listSlope.size() && j < bugFixing.size() && k < developmentFix.size(); i++, j++, k++) {
                int first = i;
                int second = ++i;
                //System.out.print("Second " + second + " listSlope.size() " + listSlope.size());
                if (second < listSlope.size() - 1) {

                    if (listSlope.get(first) == -1 && listSlope.get(second) == -1) {
                        // System.out.println("Inside First " + i);

                        if (++j < bugFixing.size() && bugFixing.get(j)) {
                            count2UUBugFixing++;
                            if (!developmentFix.get(k)) {
                                count2UUSecondLastSegmentIsBuggy++;
                            }
                            k++;
                            if (!developmentFix.get(k)) {
                                coun2UULastSegmentIsBuggy++;
                            }
                            k--;

                        } else {
                            count2UUNonBugFixing++;
                        }

                        if (developmentFix.get(k) && developmentFix.get(++k)) {
                            count2UUDevelopmentBugFixing++;
                        } else {
                            if (bugFixing.get(j)) {
                                count2UUActualBugFixing++;
                            }

                            count2UUNonDevelopmentBugFixing++;
                        }
                    }
                    //UD
                    else if (listSlope.get(first) == -1 && listSlope.get(second) == 1) {
                        //  System.out.println(" Inside Second " + i);
                        if (++j < bugFixing.size() && bugFixing.get(j)) {
                            count2UDBugFixing++;
                            if (!developmentFix.get(k)) {
                                count2UDSecondLastSegmentIsBuggy++;
                            }
                            k++;
                            if (!developmentFix.get(k)) {
                                coun2UDLastSegmentIsBuggy++;
                            }
                            k--;
                        } else {
                            count2UDNonBugFixing++;
                        }
                        if (developmentFix.get(k) && developmentFix.get(++k)) {
                            count2UDDevelopmentBugFixing++;
                        } else {
                            if (bugFixing.get(j)) {
                                count2UDActualBugFixing++;

                            }
                            count2UDNonDevelopmentBugFixing++;
                        }

                    }
                    //DU
                    else if (listSlope.get(first) == 1 && listSlope.get(second) == -1) {
                        if (++j < bugFixing.size() && bugFixing.get(j)) {
                            count2DUBugFixing++;
                            if (!developmentFix.get(k)) {
                                count2DUSecondLastSegmentIsBuggy++;
                            }
                            k++;
                            if (!developmentFix.get(k)) {
                                coun2DULastSegmentIsBuggy++;
                            }
                            k--;
                        } else {
                            count2DUNonBugFixing++;
                        }

                        if (developmentFix.get(k) && developmentFix.get(++k)) {
                            count2DUDevelopmentBugFixing++;
                        } else {
                            if (bugFixing.get(j)) {
                                count2DUActualBugFixing++;

                            }
                            count2DUNonDevelopmentBugFixing++;
                        }

                    }
                    //DD
                    else if (listSlope.get(first) == 1 && listSlope.get(second) == 1) {
                        //System.out.println(" Inside Fourth " + i);
                        if (++j < bugFixing.size() && bugFixing.get(j)) {
                            count2DDBugFixing++;
                            if (!developmentFix.get(k)) {
                                count2DDSecondLastSegmentIsBuggy++;
                            }
                            k++;
                            if (!developmentFix.get(k)) {
                                coun2DDLastSegmentIsBuggy++;
                            }
                            k--;
                        } else {
                            count2DDNonBugFixing++;
                        }

                        if (developmentFix.get(k) && developmentFix.get(++k)) {
                            count2DDDevelopmentBugFixing++;
                        } else {
                            if (bugFixing.get(j)) {
                                count2DDActualBugFixing++;
                            }
                            count2DDNonDevelopmentBugFixing++;
                        }

                    }
                } else {
                    break;
                }
            }
            if (logging) {
            logger.info("Chi-2 Results");
            logger.info("  BugFixing UU  " + count2UUBugFixing + "\n" + "  NonBugFixing UU " + count2UUNonBugFixing + "  " + "count2UUDevelopmentBugFixing" + count2UUDevelopmentBugFixing);
            logger.info(" BugFixing UD " + count2UDBugFixing + "\n" + " NonBugFixing UD " + count2UDNonBugFixing + " " + "count2UDDevelopmentBugFixing" + count2UDDevelopmentBugFixing);
            logger.info(" BugFixing DU " + count2DUBugFixing + "\n" + " NonBugFixing DU " + count2DUNonBugFixing + "  " + "count2DUDevelopmentBugFixing" + count2DUDevelopmentBugFixing);
            logger.info(" BugFixing DD " + count2DDBugFixing + "\n" + " NonBugFixing DDU " + count2DDNonBugFixing + "  " + "count2DDDevelopmentBugFixing" + count2DDDevelopmentBugFixing);
            }
            //System.exit(0);
            //System.out.println(" Chi-2 After");


            //Chi-3
            for (int i = 0, j = 0, k = 0; i < listSlope.size() && j < bugFixing.size() && k < bugFixing.size(); i++, j++, k++) {
                //UUU
                int first = i;
                int second = ++i;
                int third = ++i;
                ++j;

                //System.out.print("Second " + second + " third " + third + " listSlope.size() " + listSlope.size());
                if (second < listSlope.size() - 1 && third < listSlope.size() - 1) {
                    if (third < listSlope.size() && listSlope.get(first) == -1 && listSlope.get(second) == -1 && listSlope.get(third) == -1) {
                        if (++j < bugFixing.size() && bugFixing.get(j)) {
                            count3UUUBugFixing++;
                            k++;
                            if (k < developmentFix.size() && !developmentFix.get(k)) {
                                count3UUUSecondLastSegmentIsBuggy++;
                            }
                            k++;
                            if (k < developmentFix.size() && !developmentFix.get(k)) {
                                coun3UUULastSegmentIsBuggy++;
                            }

                            k--;
                            k--;
                        } else {
                            count3UUUNonBugFixing++;
                        }


                        int a = k + 1;
                        int b = a + 1;

                        if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                            count3UUULastSecondLastSegmentIsBuggy++;
                        }

                        int m = k;
                        int n = ++k;
                        int p = ++k;

                        if (p < developmentFix.size() && developmentFix.get(m) && developmentFix.get(n) && developmentFix.get(p)) {
                            count3UUUDevelopmentBugFixing++;
                        } else {
                            count3UUUNonDevelopmentBugFixing++;
                            if (j < bugFixing.size() && bugFixing.get(j)) {

                                count3UUUActualBugFixing++;
                            }
                        }


                    }
                    //UDD
                    if (listSlope.get(first) == -1 && listSlope.get(second) == 1 && listSlope.get(third) == 1) {

                        if (++j < bugFixing.size() && bugFixing.get(j)) {
                            count3UDDBugFixing++;
                            k++; //1
                            if (k < developmentFix.size() && !developmentFix.get(k)) {
                                count3UDDSecondLastSegmentIsBuggy++;
                            }
                            k++; //2
                            if (k < developmentFix.size() && !developmentFix.get(k)) {
                                coun3UDDLastSegmentIsBuggy++;
                            }
                            k--; //1
                            k--; //0
                        } else {
                            count3UDDNonBugFixing++;
                        }



                        int a = k + 1;
                        int b = a + 1;

                        if (a < developmentFix.size() && b < developmentFix.size()) {
                            if (!developmentFix.get(a) && !developmentFix.get(b)) {
                                count3UDDLastSecondLastSegmentIsBuggy++;
                            }
                        }

                        int m = k;
                        int n = ++k;
                        int p = ++k;

                        if (p < developmentFix.size() && developmentFix.get(m) && developmentFix.get(n) && developmentFix.get(p)) {
                            count3UDDDevelopmentBugFixing++;

                        } else {

                            if (j < bugFixing.size() && bugFixing.get(j)) {
                                count3UDDActualBugFixing++;
                            }

                            count3UDDNonDevelopmentBugFixing++;
                        }


                    }


                    //DDU
                    if (listSlope.get(first) == 1 && listSlope.get(second) == 1 && listSlope.get(third) == -1) {

                        if (++j < bugFixing.size() && bugFixing.get(j)) {
                            count3DDUBugFixing++;
                            k++;
                            if (k < developmentFix.size() && !developmentFix.get(k)) {
                                count3DDUSecondLastSegmentIsBuggy++;
                            }
                            k++;
                            if (k < developmentFix.size() && !developmentFix.get(k)) {
                                coun3DDULastSegmentIsBuggy++;
                            }
                            k--;
                            k--;
                        } else {
                            count3DDUNonBugFixing++;
                        }




                        int a = k + 1;
                        int b = a + 1;

                        if (a < developmentFix.size() && b < developmentFix.size()) {
                            if (!developmentFix.get(a) && !developmentFix.get(b)) {
                                count3DDULastSecondLastSegmentIsBuggy++;

                            }
                        }

                        int m = k;
                        int n = ++k;
                        int p = ++k;


                        if (p < developmentFix.size() && developmentFix.get(m) && developmentFix.get(n) && developmentFix.get(p)) {
                            count3DDUDevelopmentBugFixing++;

                        } else {

                            if (j < bugFixing.size() && bugFixing.get(j)) {

                                count3DDUActualBugFixing++;
                            }

                            count3DDUNonDevelopmentBugFixing++;
                        }

                    }

                    //DUD
                    if (listSlope.get(first) == 1 && listSlope.get(second) == -1 && listSlope.get(third) == 1) {


                        if (++j < bugFixing.size() && bugFixing.get(j)) {
                            count3DUDBugFixing++;
                            k++;
                            if (k < developmentFix.size() && !developmentFix.get(k)) {
                                count3DUDSecondLastSegmentIsBuggy++;
                            }
                            k++;
                            if (k < developmentFix.size() && !developmentFix.get(k)) {
                                coun3DUDLastSegmentIsBuggy++;
                            }
                            k--;
                            k--;
                        } else {
                            count3DUDNonBugFixing++;
                        }


                        int a = k + 1;
                        int b = a + 1;

                        if (a < developmentFix.size() && b < developmentFix.size()) {
                            if (!developmentFix.get(a) && !developmentFix.get(b)) {
                                count3DUDLastSecondLastSegmentIsBuggy++;

                            }
                        }


                        int m = k;
                        int n = ++k;
                        int p = ++k;

                        if (p < developmentFix.size() && developmentFix.get(m) && developmentFix.get(n) && developmentFix.get(p)) {
                            count3DUDDevelopmentBugFixing++;

                        } else {
                            j = j + 1;
                            if (j < bugFixing.size() && bugFixing.get(j)) {

                                count3DUDActualBugFixing++;
                            }
                            j = j - 1;
                            count3DUDNonDevelopmentBugFixing++;
                        }


                    }
                    //UUD
                    if (listSlope.get(first) == -1 && listSlope.get(second) == -1 && listSlope.get(third) == 1) {


                        if (++j < bugFixing.size() && bugFixing.get(j)) {
                            count3UUDBugFixing++;
                            k++;
                            if (k < developmentFix.size() && !developmentFix.get(k)) {
                                count3UUDSecondLastSegmentIsBuggy++;
                            }
                            k++;
                            if (k < developmentFix.size() && !developmentFix.get(k)) {
                                coun3UUDLastSegmentIsBuggy++;
                            }
                            k--;
                            k--;
                        } else {
                            count3UUDNonBugFixing++;
                        }


                        int a = k + 1;
                        int b = a + 1;
                        if (a < developmentFix.size() && b < developmentFix.size()) {
                            if (!developmentFix.get(a) && !developmentFix.get(b)) {
                                count3UUDLastSecondLastSegmentIsBuggy++;

                            }
                        }

                        int m = k;
                        int n = ++k;
                        int p = ++k;


                        if (p < developmentFix.size() && developmentFix.get(m) && developmentFix.get(n) && developmentFix.get(p)) {
                            count3UUDDevelopmentBugFixing++;

                        } else {
                            if (j < bugFixing.size() && bugFixing.get(j)) {

                                count3UUDActualBugFixing++;
                            }

                            count3UUDNonDevelopmentBugFixing++;
                        }


                    }

                    //DUU
                    if (listSlope.get(first) == 1 && listSlope.get(second) == -1 && listSlope.get(third) == -1) {


                        if (++j < bugFixing.size() && bugFixing.get(j)) {
                            count3DUUBugFixing++;
                            k++;
                            if (k < developmentFix.size() && !developmentFix.get(k)) {
                                count3DUUSecondLastSegmentIsBuggy++;
                            }
                            k++;
                            if (k < developmentFix.size() && !developmentFix.get(k)) {
                                coun3DUULastSegmentIsBuggy++;
                            }
                            k--;
                            k--;
                        } else {
                            count3DUUNonBugFixing++;
                        }


                        int a = k + 1;
                        int b = a + 1;

                        if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                            count3DUULastSecondLastSegmentIsBuggy++;
                        }
                        int m = k;
                        int n = ++k;
                        int p = ++k;


                        if (p < developmentFix.size() && developmentFix.get(m) && developmentFix.get(n) && developmentFix.get(p)) {
                            count3DUUDevelopmentBugFixing++;
                            /*j=j+1;
                            if(j<bugFixing.size() && bugFixing.get(j))
                            {

                                count3DUUActualBugFixing++;
                            }
                            j=j-1;*/
                        } else {

                            if (j < bugFixing.size() && bugFixing.get(j)) {

                                count3DUUActualBugFixing++;
                            }


                            count3DUUNonDevelopmentBugFixing++;
                        }


                    }

                    //UDU
                    if (listSlope.get(first) == -1 && listSlope.get(second) == 1 && listSlope.get(third) == -1) {

                        if (++j < bugFixing.size() && bugFixing.get(j)) {
                            count3UDUBugFixing++;
                            k++;
                            if (k < developmentFix.size() && !developmentFix.get(k)) {
                                count3UDUSecondLastSegmentIsBuggy++;
                            }
                            k++;
                            if (k < developmentFix.size() && !developmentFix.get(k)) {
                                coun3UDULastSegmentIsBuggy++;
                            }
                            k--;
                            k--;
                        } else {
                            count3UDUNonBugFixing++;
                        }


                        int a = k + 1;
                        int b = a + 1;
                        if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                            count3UDULastSecondLastSegmentIsBuggy++;
                        }


                        int m = k;
                        int n = ++k;
                        int p = ++k;


                        if (p < developmentFix.size() && developmentFix.get(m) && developmentFix.get(n) && developmentFix.get(p)) {
                            count3UDUDevelopmentBugFixing++;
                            /*j=j+1;
                            if(j<bugFixing.size() && bugFixing.get(j))
                            {

                                count3UDUActualBugFixing++;
                            }
                            j=j-1;*/
                        } else {
                            if (j < bugFixing.size() && bugFixing.get(j)) {

                                count3UDUActualBugFixing++;
                            }
                            count3UDUNonDevelopmentBugFixing++;
                        }


                    }

                    //DDD
                    if (listSlope.get(first) == 1 && listSlope.get(second) == 1 && listSlope.get(third) == 1) {
                        if (++j < bugFixing.size() && bugFixing.get(j)) {
                            count3DDDBugFixing++;
                            k++;
                            if (k < developmentFix.size() && !developmentFix.get(k)) {
                                count3DDDSecondLastSegmentIsBuggy++;
                            }
                            k++;
                            if (k < developmentFix.size() && !developmentFix.get(k)) {
                                coun3DDDLastSegmentIsBuggy++;
                            }
                            k--;
                            k--;
                        } else {
                            count3DDDNonBugFixing++;
                        }


                        int a = k + 1;
                        int b = a + 1;
                        if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                            count3DDDLastSecondLastSegmentIsBuggy++;
                        }

                        int m = k;
                        int n = ++k;
                        int p = ++k;


                        if (p < developmentFix.size() && developmentFix.get(m) && developmentFix.get(n) && developmentFix.get(p)) {
                            count3DDDDevelopmentBugFixing++;
                        } else {
                            if (j < bugFixing.size() && bugFixing.get(j)) {

                                count3DDDActualBugFixing++;
                            }
                            count3DDDNonDevelopmentBugFixing++;
                        }

                    }
                }
            }

//            System.out.println(" Chi-3 After");
            //Chi-4
            //    static int count4UUUUBugFixing=0;
            //    static int count4UUUUNonBugFixing=0;
            for (int i = 0, j = 0, k = 0; i < listSlope.size() - 3 && j < bugFixing.size(); i++, j++, k++) {
                int first = i;
                int second = ++i;
                int third = ++i;
                int fourth = ++i;
                j = j + 2;
                //UUUU
                if (listSlope.get(first) == -1 && listSlope.get(second) == -1 && listSlope.get(third) == -1 && listSlope.get(fourth) == -1) {

                    if (++j < bugFixing.size() && bugFixing.get(j)) {
                        count4UUUUBugFixing++;
                        k++; //1
                        k++; //2
                        if (k < developmentFix.size() && !developmentFix.get(k)) //second last
                        {
                            count4UUUUSecondLastSegmentIsBuggy++;
                        }
                        k++; //3
                        if (k < developmentFix.size() && !developmentFix.get(k)) { //last
                            count4UUUULastSegmentIsBuggy++;
                        }
                        k--; //2
                    } else {
                        count4UUUUNonBugFixing++;
                        k++;
                        k++;
                    }



                    int a = k;//2
                    int b = a + 1;//3
                    if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                        count4UUUULastSecondLastSegmentIsBuggy++;
                    }
                    k--; //1
                    k--; //0

                    int p = k;
                    int q = ++k;
                    int r = ++k;
                    int s = ++k;
                    if (s < developmentFix.size() && developmentFix.get(p) && developmentFix.get(q) && developmentFix.get(r) && developmentFix.get(s)) {
                        count4UUUUDevelopmentBugFixing++;
                    } else {

                        if (j < bugFixing.size() && bugFixing.get(j)) {

                            count4UUUUActualBugFixing++;
                        }
                        count4UUUUNonDevelopmentBugFixing++;
                    }


                }
                //DUUU
                if (listSlope.get(first) == 1 && listSlope.get(second) == -1 && listSlope.get(third) == -1 && listSlope.get(fourth) == -1) {

                    if (++j < bugFixing.size() && bugFixing.get(j)) {
                        count4DUUUBugFixing++;
                        k++;
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) //second last
                        {
                            count4DUUUSecondLastSegmentIsBuggy++;
                        }
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) { //last
                            count4DUUULastSegmentIsBuggy++;
                        }
                        k--;
                    } else {
                        count4DUUUNonBugFixing++;
                        k++;
                        k++;
                    }



                    int a = k;//2
                    int b = a + 1;//3
                    if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                        count4DUUULastSecondLastSegmentIsBuggy++;
                    }
                    k--;
                    k--;

                    int p = k;
                    int q = ++k;
                    int r = ++k;
                    int s = ++k;
                    if (s < developmentFix.size() && developmentFix.get(p) && developmentFix.get(q) && developmentFix.get(r) && developmentFix.get(s)) {
                        count4DUUUDevelopmentBugFixing++;

                    } else {
                        if (j < bugFixing.size() && bugFixing.get(j)) {

                            count4DUUUActualBugFixing++;
                        }
                        count4DUUUNonDevelopmentBugFixing++;
                    }


                }
                //UDUU
                if (listSlope.get(first) == -1 && listSlope.get(second) == 1 && listSlope.get(third) == -1 && listSlope.get(fourth) == -1) {

                    if (++j < bugFixing.size() && bugFixing.get(j)) {
                        count4UDUUBugFixing++;
                        k++;
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) //second last
                        {
                            count4UDUUSecondLastSegmentIsBuggy++;
                        }
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) { //last
                            count4UDUULastSegmentIsBuggy++;
                        }
                        k--;
                    } else {
                        count4UDUUNonBugFixing++;
                        k++;
                        k++;
                    }



                    int a = k;//2
                    int b = a + 1;//3
                    if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                        count4UDUULastSecondLastSegmentIsBuggy++;
                    }
                    k--;
                    k--;

                    int p = k;
                    int q = ++k;
                    int r = ++k;
                    int s = ++k;
                    if (s < developmentFix.size() && developmentFix.get(p) && developmentFix.get(q) && developmentFix.get(r) && developmentFix.get(s)) {
                        count4UDUUDevelopmentBugFixing++;
                    } else {
                        if (j < bugFixing.size() && bugFixing.get(j)) {

                            count4UDUUActualBugFixing++;
                        }
                        count4UDUUNonDevelopmentBugFixing++;
                    }


                }

                //UUDU
                if (listSlope.get(first) == -1 && listSlope.get(second) == -1 && listSlope.get(third) == 1 && listSlope.get(fourth) == -1) {

                    if (++j < bugFixing.size() && bugFixing.get(j)) {
                        count4UUDUBugFixing++;
                        k++;
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) //second last
                        {
                            count4UUDUSecondLastSegmentIsBuggy++;
                        }
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) { //last
                            count4UUDULastSegmentIsBuggy++;
                        }
                        k--;
                    } else {
                        count4UUDUNonBugFixing++;
                        k++;
                        k++;
                    }



                    int a = k;//2
                    int b = a + 1;//3
                    if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                        count4UUDULastSecondLastSegmentIsBuggy++;
                    }
                    k--;
                    k--;

                    int p = k;
                    int q = ++k;
                    int r = ++k;
                    int s = ++k;
                    if (s < developmentFix.size() && developmentFix.get(p) && developmentFix.get(q) && developmentFix.get(r) && developmentFix.get(s)) {
                        count4UUDUDevelopmentBugFixing++;

                    } else {
                        if (j < bugFixing.size() && bugFixing.get(j)) {

                            count4UUDUActualBugFixing++;
                        }
                        count4UUDUNonDevelopmentBugFixing++;
                    }


                }

                //UUUD
                if (listSlope.get(first) == -1 && listSlope.get(second) == -1 && listSlope.get(third) == -1 && listSlope.get(fourth) == 1) {


                    if (++j < bugFixing.size() && bugFixing.get(j)) {
                        count4UUUDBugFixing++;
                        k++;
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) //second last
                        {
                            count4UUUDSecondLastSegmentIsBuggy++;
                        }
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) { //last
                            count4UUUDLastSegmentIsBuggy++;
                        }
                        k--;
                    } else {
                        count4UUUDNonBugFixing++;
                        k++;
                        k++;
                    }



                    int a = k;//2
                    int b = a + 1;//3
                    if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                        count4UUUDLastSecondLastSegmentIsBuggy++;
                    }
                    k--;
                    k--;

                    int p = k;
                    int q = ++k;
                    int r = ++k;
                    int s = ++k;
                    if (s < developmentFix.size() && developmentFix.get(p) && developmentFix.get(q) && developmentFix.get(r) && developmentFix.get(s)) {
                        count4UUUDDevelopmentBugFixing++;

                    } else {
                        if (j < bugFixing.size() && bugFixing.get(j)) {

                            count4UUUDActualBugFixing++;
                        }
                        count4UUUDNonDevelopmentBugFixing++;
                    }


                }

                //DDUU
                if (listSlope.get(first) == 1 && listSlope.get(second) == 1 && listSlope.get(third) == -1 && listSlope.get(fourth) == -1) {

                    if (++j < bugFixing.size() && bugFixing.get(j)) {
                        count4DDUUBugFixing++;
                        k++;
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) //second last
                        {
                            count4DDUUSecondLastSegmentIsBuggy++;
                        }
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) { //last
                            count4DDUULastSegmentIsBuggy++;
                        }
                        k--;
                    } else {
                        count4DDUUNonBugFixing++;
                        k++;
                        k++;
                    }


                    int a = k;//2
                    int b = a + 1;//3
                    if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                        count4DDUULastSecondLastSegmentIsBuggy++;
                    }
                    k--;
                    k--;

                    int p = k;
                    int q = ++k;
                    int r = ++k;
                    int s = ++k;
                    if (s < developmentFix.size() && developmentFix.get(p) && developmentFix.get(q) && developmentFix.get(r) && developmentFix.get(s)) {
                        count4DDUUDevelopmentBugFixing++;

                    } else {
                        if (j < bugFixing.size() && bugFixing.get(j)) {

                            count4DDUUActualBugFixing++;
                        }
                        count4DDUUNonDevelopmentBugFixing++;
                    }


                }

                //UDDU
                if (listSlope.get(first) == -1 && listSlope.get(second) == 1 && listSlope.get(third) == 1 && listSlope.get(fourth) == -1) {

                    if (++j < bugFixing.size() && bugFixing.get(j)) {
                        count4UDDUBugFixing++;
                        k++;
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) //second last
                        {
                            count4UDDUSecondLastSegmentIsBuggy++;
                        }
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) { //last
                            count4UDDULastSegmentIsBuggy++;
                        }
                        k--;
                    } else {
                        count4UDDUNonBugFixing++;
                        k++;
                        k++;
                    }



                    int a = k;//2
                    int b = a + 1;//3
                    if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                        count4UDDULastSecondLastSegmentIsBuggy++;
                    }
                    k--;
                    k--;

                    int p = k;
                    int q = ++k;
                    int r = ++k;
                    int s = ++k;
                    if (s < developmentFix.size() && developmentFix.get(p) && developmentFix.get(q) && developmentFix.get(r) && developmentFix.get(s)) {
                        count4UDDUDevelopmentBugFixing++;

                    } else {
                        if (j < bugFixing.size() && bugFixing.get(j)) {

                            count4UDDUActualBugFixing++;
                        }
                        count4UDDUNonDevelopmentBugFixing++;
                    }


                }

                //UUDD
                if (listSlope.get(first) == -1 && listSlope.get(second) == -1 && listSlope.get(third) == 1 && listSlope.get(fourth) == 1) {

                    if (++j < bugFixing.size() && bugFixing.get(j)) {
                        count4UUDDBugFixing++;
                        k++;
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) //second last
                        {
                            count4UUDDSecondLastSegmentIsBuggy++;
                        }
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) { //last
                            count4UUDDLastSegmentIsBuggy++;
                        }
                        k--;
                    } else {
                        count4UUDDNonBugFixing++;
                        k++;
                        k++;
                    }



                    int a = k;//2
                    int b = a + 1;//3
                    if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                        count4UUDDLastSecondLastSegmentIsBuggy++;
                    }
                    k--;
                    k--;

                    int p = k;
                    int q = ++k;
                    int r = ++k;
                    int s = ++k;
                    if (s < developmentFix.size() && developmentFix.get(p) && developmentFix.get(q) && developmentFix.get(r) && developmentFix.get(s)) {
                        count4UUDDDevelopmentBugFixing++;
                        /*j=j+1;
                        if(j<bugFixing.size() &&  bugFixing.get(j))
                        {

                            count4UUDDActualBugFixing++;
                        }
                        j=j-1;*/
                    } else {
                        if (j < bugFixing.size() && bugFixing.get(j)) {

                            count4UUDDActualBugFixing++;
                        }
                        count4UUDDNonDevelopmentBugFixing++;
                    }


                }

                //DUUD
                if (listSlope.get(first) == 1 && listSlope.get(second) == -1 && listSlope.get(third) == -1 && listSlope.get(fourth) == 1) {


                    if (++j < bugFixing.size() && bugFixing.get(j)) {
                        count4DUUDBugFixing++;
                        k++;
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) //second last
                        {
                            count4DUUDSecondLastSegmentIsBuggy++;
                        }
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) { //last
                            count4DUUDLastSegmentIsBuggy++;
                        }
                        k--;

                    } else {
                        count4DUUDNonBugFixing++;
                        k++;
                        k++;
                    }



                    int a = k;//2
                    int b = a + 1;//3
                    if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                        count4DUUDLastSecondLastSegmentIsBuggy++;
                    }
                    k--;
                    k--;

                    int p = k;
                    int q = ++k;
                    int r = ++k;
                    int s = ++k;
                    if (s < developmentFix.size() && developmentFix.get(p) && developmentFix.get(q) && developmentFix.get(r) && developmentFix.get(s)) {
                        count4DUUDDevelopmentBugFixing++;
                    } else {
                        if (j < bugFixing.size() && bugFixing.get(j)) {

                            count4DUUDActualBugFixing++;
                        }
                        count4DUUDNonDevelopmentBugFixing++;
                    }

                }

                //UDUD,

                if (listSlope.get(first) == -1 && listSlope.get(second) == 1 && listSlope.get(third) == -1 && listSlope.get(fourth) == 1) {

                    if (++j < bugFixing.size() && bugFixing.get(j)) {
                        count4UDUDBugFixing++;
                        k++;
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) //second last
                        {
                            count4UDUDSecondLastSegmentIsBuggy++;
                        }
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) { //last
                            count4UDUDLastSegmentIsBuggy++;
                        }
                        k--;
                    } else {
                        count4UDUDNonBugFixing++;
                        k++;
                        k++;
                    }





                    int a = k;//2
                    int b = a + 1;//3
                    if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                        count4UDUDLastSecondLastSegmentIsBuggy++;
                    }
                    k--;
                    k--;

                    int p = k;
                    int q = ++k;
                    int r = ++k;
                    int s = ++k;
                    if (s < developmentFix.size() && developmentFix.get(p) && developmentFix.get(q) && developmentFix.get(r) && developmentFix.get(s)) {
                        count4UDUDDevelopmentBugFixing++;
                        /*j=j+1;
                        if(j<bugFixing.size() &&  bugFixing.get(j))
                        {
                            count4UDUDActualBugFixing++;
                        }
                        j=j-1;*/
                    } else {
                        if (j < bugFixing.size() && bugFixing.get(j)) {
                            count4UDUDActualBugFixing++;
                        }
                        count4UDUDNonDevelopmentBugFixing++;
                    }


                }
                //DUDU,
                if (listSlope.get(first) == 1 && listSlope.get(second) == -1 && listSlope.get(third) == 1 && listSlope.get(fourth) == -1) {

                    if (++j < bugFixing.size() && bugFixing.get(j)) {
                        count4DUDUBugFixing++;
                        k++;
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) //second last
                        {
                            count4DUDUSecondLastSegmentIsBuggy++;
                        }
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) { //last
                            count4DUDULastSegmentIsBuggy++;
                        }
                        k--;
                    } else {
                        count4DUDUNonBugFixing++;
                        k++;
                        k++;
                    }



                    int a = k;//2
                    int b = a + 1;//3
                    if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                        count4DUDULastSecondLastSegmentIsBuggy++;
                    }
                    k--;
                    k--;

                    int p = k;
                    int q = ++k;
                    int r = ++k;
                    int s = ++k;
                    if (s < developmentFix.size() && developmentFix.get(p) && developmentFix.get(q) && developmentFix.get(r) && developmentFix.get(s)) {
                        count4DUDUDevelopmentBugFixing++;
                    } else {
                        if (j < bugFixing.size() && bugFixing.get(j)) {

                            count4DUDUActualBugFixing++;
                        }
                        count4DUDUNonDevelopmentBugFixing++;
                    }


                }

                //UDDD
                if (listSlope.get(first) == -1 && listSlope.get(second) == 1 && listSlope.get(third) == 1 && listSlope.get(fourth) == 1) {


                    if (++j < bugFixing.size() && bugFixing.get(j)) {
                        count4UDDDBugFixing++;
                        k++;
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) //second last
                        {
                            count4UDDDSecondLastSegmentIsBuggy++;
                        }
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) { //last
                            count4UDDDLastSegmentIsBuggy++;
                        }
                        k--;
                    } else {
                        count4UDDDNonBugFixing++;
                        k++;
                        k++;
                    }


                    int a = k;//2
                    int b = a + 1;//3
                    if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                        count4UDDDLastSecondLastSegmentIsBuggy++;
                    }
                    k--;
                    k--;

                    int p = k;
                    int q = ++k;
                    int r = ++k;
                    int s = ++k;
                    if (s < developmentFix.size() && developmentFix.get(p) && developmentFix.get(q) && developmentFix.get(r) && developmentFix.get(s)) {
                        count4UDDDDevelopmentBugFixing++;
                    } else {
                        if (j < bugFixing.size() && bugFixing.get(j)) {

                            count4UDDDActualBugFixing++;
                        }
                        count4UDDDNonDevelopmentBugFixing++;
                    }


                }
                //,DUDD
                if (listSlope.get(first) == 1 && listSlope.get(second) == -1 && listSlope.get(third) == 1 && listSlope.get(fourth) == 1) {

                    if (++j < bugFixing.size() && bugFixing.get(j)) {
                        count4DUDDBugFixing++;
                        k++;
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) //second last
                        {
                            count4DUDDSecondLastSegmentIsBuggy++;
                        }
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) { //last
                            count4DUDDLastSegmentIsBuggy++;
                        }
                        k--;
                    } else {
                        count4DUDDNonBugFixing++;
                        k++;
                        k++;
                    }



                    int a = k;//2
                    int b = a + 1;//3
                    if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                        count4DUDDLastSecondLastSegmentIsBuggy++;
                    }
                    k--;
                    k--;

                    int p = k;
                    int q = ++k;
                    int r = ++k;
                    int s = ++k;
                    if (s < developmentFix.size() && developmentFix.get(p) && developmentFix.get(q) && developmentFix.get(r) && developmentFix.get(s)) {
                        count4DUDDDevelopmentBugFixing++;

                    } else {
                        if (j < bugFixing.size() && bugFixing.get(j)) {

                            count4DUDDActualBugFixing++;
                        }
                        count4DUDDNonDevelopmentBugFixing++;
                    }


                }

                //DDUD
                if (listSlope.get(first) == 1 && listSlope.get(second) == 1 && listSlope.get(third) == -1 && listSlope.get(fourth) == 1) {

                    if (++j < bugFixing.size() && bugFixing.get(j)) {
                        count4DDUDBugFixing++;
                        k++;
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) //second last
                        {
                            count4DDUDSecondLastSegmentIsBuggy++;
                        }
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) { //last
                            count4DDUDLastSegmentIsBuggy++;
                        }
                        k--;
                    } else {
                        count4DDUDNonBugFixing++;
                        k++;
                        k++;
                    }



                    int a = k;//2
                    int b = a + 1;//3
                    if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                        count4DDUDLastSecondLastSegmentIsBuggy++;
                    }
                    k--;
                    k--;

                    int p = k;
                    int q = ++k;
                    int r = ++k;
                    int s = ++k;
                    if (s < developmentFix.size() && developmentFix.get(p) && developmentFix.get(q) && developmentFix.get(r) && developmentFix.get(s)) {
                        count4DDUDDevelopmentBugFixing++;

                    } else {

                        if (j < bugFixing.size() && bugFixing.get(j)) {
                            count4DDUDActualBugFixing++;
                        }

                        count4DDUDNonDevelopmentBugFixing++;
                    }


                }

                //DDDU
                if (listSlope.get(first) == 1 && listSlope.get(second) == 1 && listSlope.get(third) == 1 && listSlope.get(fourth) == -1) {


                    if (++j < bugFixing.size() && bugFixing.get(j)) {
                        count4DDDUBugFixing++;
                        k++;
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) //second last
                        {
                            count4DDDUSecondLastSegmentIsBuggy++;
                        }
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) { //last
                            count4DDDULastSegmentIsBuggy++;
                        }
                        k--;
                    } else {
                        count4DDDUNonBugFixing++;
                        k++;
                        k++;
                    }

                    int a = k;//2
                    int b = a + 1;//3
                    if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                        count4DDDULastSecondLastSegmentIsBuggy++;
                    }
                    k--;
                    k--;

                    int p = k;
                    int q = ++k;
                    int r = ++k;
                    int s = ++k;
                    if (s < developmentFix.size() && developmentFix.get(p) && developmentFix.get(q) && developmentFix.get(r) && developmentFix.get(s)) {
                        count4DDDUDevelopmentBugFixing++;

                    } else {

                        if (j < bugFixing.size() && bugFixing.get(j)) {
                            count4DDDUActualBugFixing++;
                        }

                        count4DDDUNonDevelopmentBugFixing++;
                    }


                }

                //DDDD
                if (listSlope.get(first) == 1 && listSlope.get(second) == 1 && listSlope.get(third) == 1 && listSlope.get(fourth) == 1) {

                    if (++j < bugFixing.size() && bugFixing.get(j)) {
                        count4DDDDBugFixing++;
                        k++;
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) //second last
                        {
                            count4DDDDSecondLastSegmentIsBuggy++;
                        }
                        k++;
                        if (k < developmentFix.size() && !developmentFix.get(k)) { //last
                            count4DDDDLastSegmentIsBuggy++;
                        }
                        k--;

                    } else {
                        count4DDDDNonBugFixing++;
                        k++;
                        k++;
                    }


                    int a = k;//2
                    int b = a + 1;//3
                    if (b < developmentFix.size() && !developmentFix.get(a) && !developmentFix.get(b)) {
                        count4DDDDLastSecondLastSegmentIsBuggy++;
                    }
                    k--;
                    k--;

                    int p = k;
                    int q = ++k;
                    int r = ++k;
                    int s = ++k;
                    if (s < developmentFix.size() && developmentFix.get(p) && developmentFix.get(q) && developmentFix.get(r) && developmentFix.get(s)) {
                        count4DDDDDevelopmentBugFixing++;

                    } else {
                        if (j < bugFixing.size() && bugFixing.get(j)) {

                            count4DDDDActualBugFixing++;
                        }
                        count4DDDDNonDevelopmentBugFixing++;
                    }


                }
            }

        }
        //System.out.println(" Chi-4 After");

        getInsertIntoExcel();
        if (logging) {
        logger.info("Chi-2 Results");
        logger.info("  BugFixing UU  " + count2UUBugFixing + "\n" + "  NonBugFixing UU " + count2UUNonBugFixing);
        logger.info(" BugFixing UD " + count2UDBugFixing + "\n" + " NonBugFixing UD " + count2UDNonBugFixing);
        logger.info(" BugFixing DU " + count2DUBugFixing + "\n" + " NonBugFixing DU " + count2DUNonBugFixing);
        logger.info(" BugFixing DD " + count2DDBugFixing + "\n" + " NonBugFixing DDU " + count2DDNonBugFixing);


        logger.info("Chi-3 Results");
        logger.info("  BugFixing UUU  " + count3UUUBugFixing + "\n" + "  NonBugFixing UUU " + count3UUUNonBugFixing);
        logger.info(" BugFixing UDD " + count3UDDBugFixing + "\n" + " NonBugFixing UDD " + count3UDDNonBugFixing);
        logger.info(" BugFixing DDU " + count3DDUBugFixing + "\n" + " NonBugFixing DDU " + count3DDUNonBugFixing);
        logger.info(" BugFixing DUD " + count3DUDBugFixing + "\n" + " NonBugFixing DUD " + count3DUDNonBugFixing);
        logger.info(" BugFixing UUD " + count3UUDBugFixing + "\n" + " NonBugFixing UUD " + count3UUDNonBugFixing);
        logger.info(" BugFixing DUU " + count3DUUBugFixing + "\n" + " NonBugFixing DUU " + count3DUUNonBugFixing);
        logger.info(" BugFixing UDU " + count3UDUBugFixing + "\n" + " NonBugFixing UDU " + count3UDUNonBugFixing);
        logger.info(" BugFixing DDD " + count3DDDBugFixing + "\n" + " NonBugFixing DDD " + count3DDDNonBugFixing);


        logger.info("Chi-4 Results");
        logger.info("  BugFixing UUUU  " + count4UUUUBugFixing + "\n" + "  NonBugFixing UUUU " + count4UUUUNonBugFixing);
        logger.info(" BugFixing DUUU " + count4DUUUBugFixing + "\n" + " NonBugFixing DUUU " + count4DUUUNonBugFixing);
        logger.info(" BugFixing UDUU " + count4UDUUBugFixing + "\n" + " NonBugFixing UDUU " + count4UDUUNonBugFixing);
        logger.info(" BugFixing UUDU " + count4UUDUBugFixing + "\n" + " NonBugFixing UUDU " + count4UUDUNonBugFixing);
        logger.info(" BugFixing UUUD " + count4UUUDBugFixing + "\n" + " NonBugFixing UUUD " + count4UUUDNonBugFixing);
        logger.info(" BugFixing DDUU " + count4DDUUBugFixing + "\n" + " NonBugFixing DDUU " + count4DDUUNonBugFixing);
        logger.info(" BugFixing UDDU " + count4UDDUBugFixing + "\n" + " NonBugFixing UDDU " + count4UDDUNonBugFixing);
        logger.info(" BugFixing UUDD " + count4UUDDBugFixing + "\n" + " NonBugFixing UUDD " + count4UUDDNonBugFixing);
        logger.info(" BugFixing DUUD " + count4DUUDBugFixing + "\n" + " NonBugFixing DUUD " + count4DUUDNonBugFixing);
        logger.info(" BugFixing UDUD " + count4UDUDBugFixing + "\n" + " NonBugFixing UDUD " + count4UDUDNonBugFixing);
        logger.info(" BugFixing DUDU " + count4DUDUBugFixing + "\n" + " NonBugFixing DUDU " + count4DUDUNonBugFixing);
        logger.info(" BugFixing UDDD " + count4UDDDBugFixing + "\n" + " NonBugFixing UDDD " + count4UDDDNonBugFixing);
        logger.info(" BugFixing DUDD " + count4DUDDBugFixing + "\n" + " NonBugFixing DUDD " + count4DUDDNonBugFixing);
        logger.info(" BugFixing DDUD " + count4DDUDBugFixing + "\n" + " NonBugFixing DDUD " + count4DDUDNonBugFixing);
        logger.info(" BugFixing DDDU " + count4DDDUBugFixing + "\n" + " NonBugFixing DDDU " + count4DDDUNonBugFixing);
        logger.info(" BugFixing DDDDB " + count4DDDDBugFixing + "\n" + " NonBugFixing DDDDB " + count4DDDDNonBugFixing);
        }

    }

    public void getInsertIntoExcel() {
        Workbook workbook = null;
        workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Chi-2");

        //Start: Chi-1 New Addition
        Sheet sheet5 = workbook.createSheet("Chi-1");
        Row header5 = sheet5.createRow(0);
        header5.createCell(0).setCellValue("U");
        header5.createCell(1).setCellValue("D");
        Row rowD5 = sheet5.createRow(1);
        rowD5.createCell(0).setCellValue(count1UBugFixing);
        rowD5.createCell(1).setCellValue(count1DBugFixing);
        rowD5 = sheet5.createRow(2);
        rowD5.createCell(0).setCellValue(count1UNonBugFixing);
        rowD5.createCell(1).setCellValue(count1DNonBugFixing);
        rowD5 = sheet5.createRow(3);
        rowD5.createCell(0).setCellValue(count1UDevelopmentBugFixing);
        rowD5.createCell(1).setCellValue(count1DDevelopmentBugFixing);
        rowD5 = sheet5.createRow(4);
        rowD5.createCell(0).setCellValue(count1UNonDevelopmentBugFixing);
        rowD5.createCell(1).setCellValue(count1DNonDevelopmentBugFixing);
        rowD5 = sheet5.createRow(5);
        rowD5.createCell(0).setCellValue(count1UActualBugFixing);
        rowD5.createCell(1).setCellValue(count1DActualBugFixing);
        //End: Chi-1 New Addition

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("UU");
        header.createCell(1).setCellValue("UD");
        header.createCell(2).setCellValue("DU");
        header.createCell(3).setCellValue("DD");
        if (logging) {
        logger.info("  BugFixing UU  " + count2UUBugFixing + "\n" + "  NonBugFixing UU " + count2UUNonBugFixing);
        logger.info(" BugFixing UD " + count2UDBugFixing + "\n" + " NonBugFixing UD " + count2UDNonBugFixing);
        logger.info(" BugFixing DU " + count2DUBugFixing + "\n" + " NonBugFixing DU " + count2DUNonBugFixing);
        logger.info(" BugFixing DD " + count2DDBugFixing + "\n" + " NonBugFixing DDU " + count2DDNonBugFixing);
        }

        Row row = sheet.createRow(1);

        row.createCell(0).setCellValue(count2UUBugFixing);
        row.createCell(1).setCellValue(count2UDBugFixing);
        row.createCell(2).setCellValue(count2DUBugFixing);
        row.createCell(3).setCellValue(count2DDBugFixing);

        row = sheet.createRow(2);

        row.createCell(0).setCellValue(count2UUNonBugFixing);
        row.createCell(1).setCellValue(count2UDNonBugFixing);
        row.createCell(2).setCellValue(count2DUNonBugFixing);
        row.createCell(3).setCellValue(count2DDNonBugFixing);

        row = sheet.createRow(3);

        row.createCell(0).setCellValue(count2UUDevelopmentBugFixing);
        row.createCell(1).setCellValue(count2UDDevelopmentBugFixing);
        row.createCell(2).setCellValue(count2DUDevelopmentBugFixing);
        row.createCell(3).setCellValue(count2DDDevelopmentBugFixing);

        row = sheet.createRow(4);

        row.createCell(0).setCellValue(count2UUNonDevelopmentBugFixing);
        row.createCell(1).setCellValue(count2UDNonDevelopmentBugFixing);
        row.createCell(2).setCellValue(count2DUNonDevelopmentBugFixing);
        row.createCell(3).setCellValue(count2DDNonDevelopmentBugFixing);

        row = sheet.createRow(5);
        row.createCell(0).setCellValue(count2UUActualBugFixing);
        row.createCell(1).setCellValue(count2UDActualBugFixing);
        row.createCell(2).setCellValue(count2DUActualBugFixing);
        row.createCell(3).setCellValue(count2DDActualBugFixing);

        row = sheet.createRow(6);
        row.createCell(0).setCellValue(coun2UULastSegmentIsBuggy);
        row.createCell(1).setCellValue(coun2UDLastSegmentIsBuggy);
        row.createCell(2).setCellValue(coun2DULastSegmentIsBuggy);
        row.createCell(3).setCellValue(coun2DDLastSegmentIsBuggy);
        row = sheet.createRow(7);
        row.createCell(0).setCellValue(count2UUSecondLastSegmentIsBuggy);
        row.createCell(1).setCellValue(count2UDSecondLastSegmentIsBuggy);
        row.createCell(2).setCellValue(count2DUSecondLastSegmentIsBuggy);
        row.createCell(3).setCellValue(count2DDSecondLastSegmentIsBuggy);


        Sheet sheet2 = workbook.createSheet("Chi-3");
        Row header2 = sheet2.createRow(0);

        header2.createCell(0).setCellValue("UUU");
        header2.createCell(1).setCellValue("UDD");
        header2.createCell(2).setCellValue("DDU");
        header2.createCell(3).setCellValue("DUD");
        header2.createCell(4).setCellValue("UUD");
        header2.createCell(5).setCellValue("DUU");
        header2.createCell(6).setCellValue("UDU");
        header2.createCell(7).setCellValue("DDD");

        row = sheet2.createRow(1);

        row.createCell(0).setCellValue(count3UUUBugFixing);
        row.createCell(1).setCellValue(count3UDDBugFixing);
        row.createCell(2).setCellValue(count3DDUBugFixing);
        row.createCell(3).setCellValue(count3DUDBugFixing);
        row.createCell(4).setCellValue(count3UUDBugFixing);
        row.createCell(5).setCellValue(count3DUUBugFixing);
        row.createCell(6).setCellValue(count3UDUBugFixing);
        row.createCell(7).setCellValue(count3DDDBugFixing);

        row = sheet2.createRow(2);

        row.createCell(0).setCellValue(count3UUUNonBugFixing);
        row.createCell(1).setCellValue(count3UDDNonBugFixing);
        row.createCell(2).setCellValue(count3DDUNonBugFixing);
        row.createCell(3).setCellValue(count3DUDNonBugFixing);
        row.createCell(4).setCellValue(count3UUDNonBugFixing);
        row.createCell(5).setCellValue(count3DUUNonBugFixing);
        row.createCell(6).setCellValue(count3UDUNonBugFixing);
        row.createCell(7).setCellValue(count3DDDNonBugFixing);

        row = sheet2.createRow(3);

        row.createCell(0).setCellValue(count3UUUDevelopmentBugFixing);
        row.createCell(1).setCellValue(count3UDDDevelopmentBugFixing);
        row.createCell(2).setCellValue(count3DDUDevelopmentBugFixing);
        row.createCell(3).setCellValue(count3DUDDevelopmentBugFixing);
        row.createCell(4).setCellValue(count3UUDDevelopmentBugFixing);
        row.createCell(5).setCellValue(count3DUUDevelopmentBugFixing);
        row.createCell(6).setCellValue(count3UDUDevelopmentBugFixing);
        row.createCell(7).setCellValue(count3DDDDevelopmentBugFixing);

        row = sheet2.createRow(4);

        row.createCell(0).setCellValue(count3UUUNonDevelopmentBugFixing);
        row.createCell(1).setCellValue(count3UDDNonDevelopmentBugFixing);
        row.createCell(2).setCellValue(count3DDUNonDevelopmentBugFixing);
        row.createCell(3).setCellValue(count3DUDNonDevelopmentBugFixing);
        row.createCell(4).setCellValue(count3UUDNonDevelopmentBugFixing);
        row.createCell(5).setCellValue(count3DUUNonDevelopmentBugFixing);
        row.createCell(6).setCellValue(count3UDUNonDevelopmentBugFixing);
        row.createCell(7).setCellValue(count3DDDNonDevelopmentBugFixing);

        row = sheet2.createRow(5);

        row.createCell(0).setCellValue(count3UUUActualBugFixing);
        row.createCell(1).setCellValue(count3UDDActualBugFixing);
        row.createCell(2).setCellValue(count3DDUActualBugFixing);
        row.createCell(3).setCellValue(count3DUDActualBugFixing);
        row.createCell(4).setCellValue(count3UUDActualBugFixing);
        row.createCell(5).setCellValue(count3DUUActualBugFixing);
        row.createCell(6).setCellValue(count3UDUActualBugFixing);
        row.createCell(7).setCellValue(count3DDDActualBugFixing);

        row = sheet2.createRow(6);
        row.createCell(0).setCellValue(coun3UUULastSegmentIsBuggy);
        row.createCell(1).setCellValue(coun3UDDLastSegmentIsBuggy);
        row.createCell(2).setCellValue(coun3DDULastSegmentIsBuggy);
        row.createCell(3).setCellValue(coun3DUDLastSegmentIsBuggy);
        row.createCell(4).setCellValue(coun3UUDLastSegmentIsBuggy);
        row.createCell(5).setCellValue(coun3DUULastSegmentIsBuggy);
        row.createCell(6).setCellValue(coun3UDULastSegmentIsBuggy);
        row.createCell(7).setCellValue(coun3DDDLastSegmentIsBuggy);

        row = sheet2.createRow(7);
        row.createCell(0).setCellValue(count3UUUSecondLastSegmentIsBuggy);
        row.createCell(1).setCellValue(count3UDDSecondLastSegmentIsBuggy);
        row.createCell(2).setCellValue(count3DDUSecondLastSegmentIsBuggy);
        row.createCell(3).setCellValue(count3DUDSecondLastSegmentIsBuggy);
        row.createCell(4).setCellValue(count3UUDSecondLastSegmentIsBuggy);
        row.createCell(5).setCellValue(count3DUUSecondLastSegmentIsBuggy);
        row.createCell(6).setCellValue(count3UDUSecondLastSegmentIsBuggy);
        row.createCell(7).setCellValue(count3DDDSecondLastSegmentIsBuggy);


        row = sheet2.createRow(8);
        row.createCell(0).setCellValue(count3UUULastSecondLastSegmentIsBuggy);
        row.createCell(1).setCellValue(count3UDDLastSecondLastSegmentIsBuggy);
        row.createCell(2).setCellValue(count3DDULastSecondLastSegmentIsBuggy);
        row.createCell(3).setCellValue(count3DUDLastSecondLastSegmentIsBuggy);
        row.createCell(4).setCellValue(count3UUDLastSecondLastSegmentIsBuggy);
        row.createCell(5).setCellValue(count3DUULastSecondLastSegmentIsBuggy);
        row.createCell(6).setCellValue(count3UDULastSecondLastSegmentIsBuggy);
        row.createCell(7).setCellValue(count3DDDLastSecondLastSegmentIsBuggy);


        Sheet sheet3 = workbook.createSheet("Chi-4");
        Row header3 = sheet3.createRow(0);
        header3.createCell(0).setCellValue("UUUU");
        header3.createCell(1).setCellValue("DUUU");
        header3.createCell(2).setCellValue("UDUU");
        header3.createCell(3).setCellValue("UUDU");
        header3.createCell(4).setCellValue("UUUD");
        header3.createCell(5).setCellValue("DDUU");
        header3.createCell(6).setCellValue("UDDU");
        header3.createCell(7).setCellValue("UUDD");
        header3.createCell(8).setCellValue("DUUD");
        header3.createCell(9).setCellValue("UDUD");
        header3.createCell(10).setCellValue("DUDU");
        header3.createCell(11).setCellValue("UDDD");
        header3.createCell(12).setCellValue("DUDD");
        header3.createCell(13).setCellValue("DDUD");
        header3.createCell(14).setCellValue("DDDU");
        header3.createCell(15).setCellValue("DDDD");


        row = sheet3.createRow(1);

        row.createCell(0).setCellValue(count4UUUUBugFixing);
        row.createCell(1).setCellValue(count4DUUUBugFixing);
        row.createCell(2).setCellValue(count4UDUUBugFixing);
        row.createCell(3).setCellValue(count4UUDUBugFixing);
        row.createCell(4).setCellValue(count4UUUDBugFixing);
        row.createCell(5).setCellValue(count4DDUUBugFixing);
        row.createCell(6).setCellValue(count4UDDUBugFixing);
        row.createCell(7).setCellValue(count4UUDDBugFixing);
        row.createCell(8).setCellValue(count4DUUDBugFixing);
        row.createCell(9).setCellValue(count4UDUDBugFixing);
        row.createCell(10).setCellValue(count4DUDUBugFixing);
        row.createCell(11).setCellValue(count4UDDDBugFixing);
        row.createCell(12).setCellValue(count4DUDDBugFixing);
        row.createCell(13).setCellValue(count4DDUDBugFixing);
        row.createCell(14).setCellValue(count4DDDUBugFixing);
        row.createCell(15).setCellValue(count4DDDDBugFixing);


        row = sheet3.createRow(2);

        row.createCell(0).setCellValue(count4UUUUNonBugFixing);
        row.createCell(1).setCellValue(count4DUUUNonBugFixing);
        row.createCell(2).setCellValue(count4UDUUNonBugFixing);
        row.createCell(3).setCellValue(count4UUDUNonBugFixing);
        row.createCell(4).setCellValue(count4UUUDNonBugFixing);
        row.createCell(5).setCellValue(count4DDUUNonBugFixing);
        row.createCell(6).setCellValue(count4UDDUNonBugFixing);
        row.createCell(7).setCellValue(count4UUDDNonBugFixing);
        row.createCell(8).setCellValue(count4DUUDNonBugFixing);
        row.createCell(9).setCellValue(count4UDUDNonBugFixing);
        row.createCell(10).setCellValue(count4DUDUNonBugFixing);
        row.createCell(11).setCellValue(count4UDDDNonBugFixing);
        row.createCell(12).setCellValue(count4DUDDNonBugFixing);
        row.createCell(13).setCellValue(count4DDUDNonBugFixing);
        row.createCell(14).setCellValue(count4DDDUNonBugFixing);
        row.createCell(15).setCellValue(count4DDDDNonBugFixing);

        row = sheet3.createRow(3);


        row.createCell(0).setCellValue(count4UUUUDevelopmentBugFixing);
        row.createCell(1).setCellValue(count4DUUUDevelopmentBugFixing);
        row.createCell(2).setCellValue(count4UDUUDevelopmentBugFixing);
        row.createCell(3).setCellValue(count4UUDUDevelopmentBugFixing);
        row.createCell(4).setCellValue(count4UUUDDevelopmentBugFixing);
        row.createCell(5).setCellValue(count4DDUUDevelopmentBugFixing);
        row.createCell(6).setCellValue(count4UDDUDevelopmentBugFixing);
        row.createCell(7).setCellValue(count4UUDDDevelopmentBugFixing);
        row.createCell(8).setCellValue(count4DUUDDevelopmentBugFixing);
        row.createCell(9).setCellValue(count4UDUDDevelopmentBugFixing);
        row.createCell(10).setCellValue(count4DUDUDevelopmentBugFixing);
        row.createCell(11).setCellValue(count4UDDDDevelopmentBugFixing);
        row.createCell(12).setCellValue(count4DUDDDevelopmentBugFixing);
        row.createCell(13).setCellValue(count4DDUDDevelopmentBugFixing);
        row.createCell(14).setCellValue(count4DDDUDevelopmentBugFixing);
        row.createCell(15).setCellValue(count4DDDDDevelopmentBugFixing);


        row = sheet3.createRow(4);


        row.createCell(0).setCellValue(count4UUUUNonDevelopmentBugFixing);
        row.createCell(1).setCellValue(count4DUUUNonDevelopmentBugFixing);
        row.createCell(2).setCellValue(count4UDUUNonDevelopmentBugFixing);
        row.createCell(3).setCellValue(count4UUDUNonDevelopmentBugFixing);
        row.createCell(4).setCellValue(count4UUUDNonDevelopmentBugFixing);
        row.createCell(5).setCellValue(count4DDUUNonDevelopmentBugFixing);
        row.createCell(6).setCellValue(count4UDDUNonDevelopmentBugFixing);
        row.createCell(7).setCellValue(count4UUDDNonDevelopmentBugFixing);
        row.createCell(8).setCellValue(count4DUUDNonDevelopmentBugFixing);
        row.createCell(9).setCellValue(count4UDUDNonDevelopmentBugFixing);
        row.createCell(10).setCellValue(count4DUDUNonDevelopmentBugFixing);
        row.createCell(11).setCellValue(count4UDDDNonDevelopmentBugFixing);
        row.createCell(12).setCellValue(count4DUDDNonDevelopmentBugFixing);
        row.createCell(13).setCellValue(count4DDUDNonDevelopmentBugFixing);
        row.createCell(14).setCellValue(count4DDDUNonDevelopmentBugFixing);
        row.createCell(15).setCellValue(count4DDDDNonDevelopmentBugFixing);

        row = sheet3.createRow(5);

        row.createCell(0).setCellValue(count4UUUUActualBugFixing);
        row.createCell(1).setCellValue(count4DUUUActualBugFixing);
        row.createCell(2).setCellValue(count4UDUUActualBugFixing);
        row.createCell(3).setCellValue(count4UUDUActualBugFixing);
        row.createCell(4).setCellValue(count4UUUDActualBugFixing);
        row.createCell(5).setCellValue(count4DDUUActualBugFixing);
        row.createCell(6).setCellValue(count4UDDUActualBugFixing);
        row.createCell(7).setCellValue(count4UUDDActualBugFixing);
        row.createCell(8).setCellValue(count4DUUDActualBugFixing);
        row.createCell(9).setCellValue(count4UDUDActualBugFixing);
        row.createCell(10).setCellValue(count4DUDUActualBugFixing);
        row.createCell(11).setCellValue(count4UDDDActualBugFixing);
        row.createCell(12).setCellValue(count4DUDDActualBugFixing);
        row.createCell(13).setCellValue(count4DDUDActualBugFixing);
        row.createCell(14).setCellValue(count4DDDUActualBugFixing);
        row.createCell(15).setCellValue(count4DDDDActualBugFixing);

        row = sheet3.createRow(6);

        row.createCell(0).setCellValue(count4UUUULastSegmentIsBuggy);
        row.createCell(1).setCellValue(count4DUUULastSegmentIsBuggy);
        row.createCell(2).setCellValue(count4UDUULastSegmentIsBuggy);
        row.createCell(3).setCellValue(count4UUDULastSegmentIsBuggy);
        row.createCell(4).setCellValue(count4UUUDLastSegmentIsBuggy);
        row.createCell(5).setCellValue(count4DDUULastSegmentIsBuggy);
        row.createCell(6).setCellValue(count4UDDULastSegmentIsBuggy);
        row.createCell(7).setCellValue(count4UUDDLastSegmentIsBuggy);
        row.createCell(8).setCellValue(count4DUUDLastSegmentIsBuggy);
        row.createCell(9).setCellValue(count4UDUDLastSegmentIsBuggy);
        row.createCell(10).setCellValue(count4DUDULastSegmentIsBuggy);
        row.createCell(11).setCellValue(count4UDDDLastSegmentIsBuggy);
        row.createCell(12).setCellValue(count4DUDDLastSegmentIsBuggy);
        row.createCell(13).setCellValue(count4DDUDLastSegmentIsBuggy);
        row.createCell(14).setCellValue(count4DDDULastSegmentIsBuggy);
        row.createCell(15).setCellValue(count4DDDDLastSegmentIsBuggy);

        row = sheet3.createRow(7);

        row.createCell(0).setCellValue(count4UUUUSecondLastSegmentIsBuggy);
        row.createCell(1).setCellValue(count4DUUUSecondLastSegmentIsBuggy);
        row.createCell(2).setCellValue(count4UDUUSecondLastSegmentIsBuggy);
        row.createCell(3).setCellValue(count4UUDUSecondLastSegmentIsBuggy);
        row.createCell(4).setCellValue(count4UUUDSecondLastSegmentIsBuggy);
        row.createCell(5).setCellValue(count4DDUUSecondLastSegmentIsBuggy);
        row.createCell(6).setCellValue(count4UDDUSecondLastSegmentIsBuggy);
        row.createCell(7).setCellValue(count4UUDDSecondLastSegmentIsBuggy);
        row.createCell(8).setCellValue(count4DUUDSecondLastSegmentIsBuggy);
        row.createCell(9).setCellValue(count4UDUDSecondLastSegmentIsBuggy);
        row.createCell(10).setCellValue(count4DUDUSecondLastSegmentIsBuggy);
        row.createCell(11).setCellValue(count4UDDDSecondLastSegmentIsBuggy);
        row.createCell(12).setCellValue(count4DUDDSecondLastSegmentIsBuggy);
        row.createCell(13).setCellValue(count4DDUDSecondLastSegmentIsBuggy);
        row.createCell(14).setCellValue(count4DDDUSecondLastSegmentIsBuggy);
        row.createCell(15).setCellValue(count4DDDDSecondLastSegmentIsBuggy);

        row = sheet3.createRow(8);

        row.createCell(0).setCellValue(count4UUUULastSecondLastSegmentIsBuggy);
        row.createCell(1).setCellValue(count4DUUULastSecondLastSegmentIsBuggy);
        row.createCell(2).setCellValue(count4UDUULastSecondLastSegmentIsBuggy);
        row.createCell(3).setCellValue(count4UUDULastSecondLastSegmentIsBuggy);
        row.createCell(4).setCellValue(count4UUUDLastSecondLastSegmentIsBuggy);
        row.createCell(5).setCellValue(count4DDUULastSecondLastSegmentIsBuggy);
        row.createCell(6).setCellValue(count4UDDULastSecondLastSegmentIsBuggy);
        row.createCell(7).setCellValue(count4UUDDLastSecondLastSegmentIsBuggy);
        row.createCell(8).setCellValue(count4DUUDLastSecondLastSegmentIsBuggy);
        row.createCell(9).setCellValue(count4UDUDLastSecondLastSegmentIsBuggy);
        row.createCell(10).setCellValue(count4DUDULastSecondLastSegmentIsBuggy);
        row.createCell(11).setCellValue(count4UDDDLastSecondLastSegmentIsBuggy);
        row.createCell(12).setCellValue(count4DUDDLastSecondLastSegmentIsBuggy);
        row.createCell(13).setCellValue(count4DDUDLastSecondLastSegmentIsBuggy);
        row.createCell(14).setCellValue(count4DDDULastSecondLastSegmentIsBuggy);
        row.createCell(15).setCellValue(count4DDDDLastSecondLastSegmentIsBuggy);


        try {
            FileOutputStream fileOut = new FileOutputStream("results\\" + ProjectNameContainer.PROJECT_NAME + "Chi-Results.xlsx");
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Get Message");
        }

    }

}


