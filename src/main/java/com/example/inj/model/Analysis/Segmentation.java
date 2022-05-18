package com.example.inj.model.Analysis;

import com.example.inj.StrategyFactory.Segmentation.ISegmentation;
import com.example.inj.StrategyFactory.SegmentationStrategy.ISegmentationStrategy;
import com.example.inj.StrategyFactory.StrengthAccumulatorStrategy.IStrengthAccumulatorStrategy;
import com.example.inj.model.storage.DataRepository;
import com.example.inj.model.strength.AccumulatedStrength;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Segmentation implements ISegmentation {
    Logger logger = LoggerFactory.getLogger(Segmentation.class);
    private ISegmentationStrategy theStrategy = null;
    @Override
    public void setStrategy(ISegmentationStrategy theStrategy) {
        this.theStrategy= theStrategy;
    }

    @Override
    public void calculateSegment(DataRepository dataRepository) {
        dataRepository.setSegment(theStrategy.calculateSegment(dataRepository));
    }
}
