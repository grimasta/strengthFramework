package com.example.inj.StrategyFactory.Segmentation;

import com.example.inj.StrategyFactory.SegmentationStrategy.ISegmentationStrategy;
import com.example.inj.model.storage.DataRepository;

public interface ISegmentation {
    void setStrategy(ISegmentationStrategy theStrategy);
    void calculateSegment(DataRepository dataRepository);
}
