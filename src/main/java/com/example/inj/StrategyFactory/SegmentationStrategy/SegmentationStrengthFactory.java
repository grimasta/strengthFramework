package com.example.inj.StrategyFactory.SegmentationStrategy;

import com.example.inj.model.Analysis.SegmentationStrategyFixedNumber;
import com.example.inj.model.Analysis.SegmentationStrategyPercentage;

public class SegmentationStrengthFactory {

    public static ISegmentationStrategy createSegmentationStrategy(SegmentationStrategyEnum type){
        ISegmentationStrategy segmentationStrategy;
        switch (type){
            case Fixed_number:
                segmentationStrategy = new SegmentationStrategyFixedNumber();
                return segmentationStrategy;
            case Percentage:
                segmentationStrategy = new SegmentationStrategyPercentage();
                return segmentationStrategy;
            default:
                System.err.println("ERROR CREATING SEGMENTATION STRATEGY");
                System.exit(1);
                return null;
        }

    }
}
