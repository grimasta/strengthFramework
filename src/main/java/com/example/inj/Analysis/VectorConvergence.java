package com.example.inj.Analysis;

import com.example.inj.model.sampling.CreateVector;
import com.example.inj.model.storage.DataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VectorConvergence {
    Logger logger = LoggerFactory.getLogger(CreateVector.class);
    private DataRepository dataRepository;

    VectorConvergence(){
        dataRepository = DataRepository.getInstance();
    }


}

