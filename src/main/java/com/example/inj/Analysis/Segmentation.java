package com.example.inj.Analysis;

import com.example.inj.StrategyFactory.Segmentation.ISegmentation;
import com.example.inj.StrategyFactory.SegmentationStrategy.ISegmentationStrategy;
import com.example.inj.model.storage.DataRepository;
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
