package com.example.inj.readingStrategy.strategy;

public class ReadingStrategyFactory {
	
	public static IReadingStrategy create(ReadingStrategyEnumeration type) {
		switch(type) {
		case DEFAULT:
			return new DefaultReadingStrategy();
		default:
			System.err.println("ERROR TRYING TO INSTANTIATE IReadingStrategy in ReadingStrategyFactory");
			System.exit(1);
			return null;
		}
	}
}
