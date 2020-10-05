package com.example.inj.model.Factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FactoryProducer {

    PairFactory pairFactory;

    @Autowired
    public void setPairFactory(PairFactory pairFactory) {
        this.pairFactory = pairFactory;
    }

    public AbstractPairFactory getFactory(String type)
    {
        if(type.equalsIgnoreCase(FactoryProducerEnum.pair.toString()))
        {
            //System.out.println("FactoryProducerEnum.pair.toString() " + FactoryProducerEnum.pair.toString());
            return pairFactory;
        }
        return null;
    }
}
