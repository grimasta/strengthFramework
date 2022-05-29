package com.example.inj.StrategyFactory.SegmentationStrategy;

import com.example.inj.model.storage.DataRepository;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ISegmentationStrategy {

    Map<String, Integer> calculateSegment(DataRepository dataRepository);
}
