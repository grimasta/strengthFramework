package com.example.inj.model.strength.accumulators.strategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DefaultStrengthAccumulatorStrategy implements IStrengthAccumulatorStrategy {

	@Override
	public Map<String, Map<String, Float>> calculate(Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMap,
			Map<String, Map<Integer, Map<String, Integer>>> excelYearMap,
			Map<String, Map<String, Map<String, Double>>> globalDecay) {
		
		Map<String, Map<String, List<Map<Integer, Map<String, Float>>>>> pairStrengthMapSecond = new LinkedHashMap<>();
        pairStrengthMapSecond.putAll(pairStrengthMap);
        Double decay = 0.0;
        List<String> acceptString = null;
        int index = 0;
        Map<String, Map<String, Float>> overallStrengthDateMap = new HashMap<>();
        Map<String, List<Map<Integer, Map<String, Float>>>> columnPairs;
        Map<String, Integer> countColumn;
        Map<String, Integer> countExistenceColumn;
        List<String> datesMap;
		for (String row : excelYearMap.keySet()) {
			columnPairs = pairStrengthMap.get(row);
			countColumn = new LinkedHashMap<>();
			countExistenceColumn = new LinkedHashMap<>();
			for (String cPair : columnPairs.keySet()) {
				countColumn.put(cPair, 0);
				countExistenceColumn.put(cPair, 0);
			}
			for (String rowPair : pairStrengthMap.keySet()) {

				if (rowPair.equals(row)) {
					datesMap = new ArrayList<>();
					Map<Integer, Map<String, Integer>> yearColumn = excelYearMap.get(row);
					for (int key : yearColumn.keySet()) {
						Map<String, Integer> yearSubColumn = yearColumn.get(key);
						datesMap.addAll(yearSubColumn.keySet());
					}
					Collections.sort(datesMap);
					Map<String, List<Map<Integer, Map<String, Float>>>> columnPair = pairStrengthMap.get(rowPair);
					List<String> listColumns = new ArrayList<>();
					listColumns.addAll(columnPair.keySet());

					ListIterator<String> datesMapIterator = datesMap.listIterator();
					Map<String, Float> overallStrengthMap = new LinkedHashMap<>();
					String firstDate = null;
					float overallStrength = 0.0f;
					while (datesMapIterator.hasNext()) {
						overallStrength = 0.0f;
						firstDate = datesMapIterator.next();
						acceptString = new LinkedList<>();
						for (String columnKey : columnPair.keySet()) {
							{

								List<Map<Integer, Map<String, Float>>> columnPairList = columnPair.get(columnKey);
								ListIterator<Map<Integer, Map<String, Float>>> columnPairListIterator = columnPairList
										.listIterator();

								while (columnPairListIterator.hasNext()) {
									Map<Integer, Map<String, Float>> columnMapPair = (Map<Integer, Map<String, Float>>) columnPairListIterator
											.next();
									for (int keys : columnMapPair.keySet()) {
										Map<String, Float> pairStrength = columnMapPair.get(keys);
										if (pairStrength.containsKey(firstDate)) {
											float strength = 0.0f;
											strength = pairStrength.get(firstDate);
											countColumn.put(columnKey, 1);

											acceptString.add(columnKey);
											overallStrength += strength;
										}

									}
								}
								columnPairListIterator = null; // Bug 003: Explicity using garbage Collector
							}
						}

						List<String> validValue = new LinkedList<>();
						if (!acceptString.isEmpty()) {
							{

								for (String rowCount : countColumn.keySet()) {
									if (!(acceptString.contains(rowCount)) && (countColumn.get(rowCount) == 1)) {
										validValue.add(rowCount);
									}
								}

							}
							Iterator<String> validValueIterator = validValue.listIterator();
							index = datesMap.indexOf(firstDate);
							if (index > 0) {

								while (validValueIterator.hasNext()) {
									String val = (String) validValueIterator.next();
									float decaySt = 0;
									float st = 0;
									Iterator<Map<Integer, Map<String, Float>>> pairIteratorTry = pairStrengthMap
											.get(row).get(val).iterator();
									Map<Integer, Map<String, Float>> pairMapping;
									Set<String> pairTry = new TreeSet<>();
									while (pairIteratorTry.hasNext()) {
										pairMapping = (Map<Integer, Map<String, Float>>) pairIteratorTry.next();
										for (int i : pairMapping.keySet()) {
											Map<String, Float> pairs = pairMapping.get(i);
											for (String is : pairs.keySet()) {
												pairTry.add(is);

											}
										}
									}
									pairIteratorTry = null; // Bug 003: Explicity using garbage Collector
									String prev = ((TreeSet<String>) pairTry).floor(firstDate);
									if (prev != null) {
										Iterator<Map<Integer, Map<String, Float>>> pairIterator = pairStrengthMap
												.get(row).get(val).iterator();
										Map<Integer, Map<String, Float>> pairMappings;
										while (pairIterator.hasNext()) {
											pairMappings = (Map<Integer, Map<String, Float>>) pairIterator.next();
											for (int i : pairMappings.keySet()) {
												Map<String, Float> pairs = pairMappings.get(i);
												if (pairs.containsKey(prev)) {
													st = pairs.get(prev);
												}
											}
										}
										pairIterator = null;// Bug 003: Explicity using garbage Collector
									}

									decay = globalDecay.get(row).get(val).get(firstDate);
									decaySt = (float) (st * decay);
									overallStrength += decaySt;

								}
								validValueIterator = null; // Bug 003: Explicity using garbage Collector

							}
						}

						overallStrengthMap.put(firstDate, overallStrength);

					}

					datesMapIterator = null; // Bug 003: Explicity using garbage Collector

					overallStrengthDateMap.put(row, overallStrengthMap);
				}

			}

		}
		return overallStrengthDateMap;
	}

}
