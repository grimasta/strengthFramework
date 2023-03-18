package com.example.inj.Analysis;

import com.google.common.collect.Sets;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.HashSet;
import java.util.Set;

public class CoverageAnalysis {


    private static int combinationSize = 3;


    public static void main(String[] args) {
        for(int i=0; i < FileNameEnum.values().length; i++){
            Table table = Table.read().csv(FileNameEnum.values()[i].name()+".csv");
            System.out.println(table.summary());
            Table result = Table.create();
            Set<Integer> columnIndexSet = new HashSet<>();
            int BFCCount = table.rowCount();
            for(int j = 0; j<table.columnCount(); j++){
                columnIndexSet.add(j);
            }
            StringColumn sc = StringColumn.create("patternCombination");
            DoubleColumn dc =DoubleColumn.create("Coverage Percentage");
            for(Set<Integer> set: Sets.combinations(columnIndexSet, combinationSize)){

                String patternCombination="";

                Set<Integer> coverage = new HashSet<>();
                for(Integer columnIndex: set){
                    coverage.addAll(table.intColumn(columnIndex).asSet());
                    patternCombination = patternCombination + table.intColumn(columnIndex).name()+ "&";
                }
                sc.append(patternCombination);
                dc.append(coverage.size()/1.0/BFCCount);

            }
            result.addColumns(sc);
            result.addColumns(dc);
            result.write().csv(FileNameEnum.values()[i].name()+combinationSize+".csv");
        }
    }
}
