package com.example.inj.StrategyFactory.ReadingStrategy;

import com.example.inj.readingStrategy.strategy.DefaultReadingStrategy;
import com.example.inj.readingStrategy.strategy.ParallelReadingStrategy;

public class ReadingStrategyFactory {
	
	public static IReadingStrategy create(ReadingStrategyEnumeration type) {
		switch(type) {
		case DEFAULT:
			return new DefaultReadingStrategy();
		case PARALLEL_READER:
			return new ParallelReadingStrategy();
		default:
			System.err.println("ERROR TRYING TO INSTANTIATE IReadingStrategy in ReadingStrategyFactory");
			System.exit(1);
			return null;
		}
	}
}
