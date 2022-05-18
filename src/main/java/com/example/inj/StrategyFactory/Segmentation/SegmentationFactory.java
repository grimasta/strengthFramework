package com.example.inj.StrategyFactory.Segmentation;

import com.example.inj.StrategyFactory.SegmentationStrategy.ISegmentationStrategy;
import com.example.inj.model.Analysis.Segmentation;

public class SegmentationFactory {
    public static ISegmentation creat(SegmentationEnum type){
        switch (type){
            case DEFAULT:
                return new Segmentation();
            default:
                System.out.println("ERROR WHEN CREATING THE SEGMENTATION OBJECT");
                System.exit(1);
                return null;
        }
    }
}
