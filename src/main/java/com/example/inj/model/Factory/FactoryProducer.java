package com.example.inj.model.Factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.inj.model.strength.pair.PairFactory;

@Component
public class FactoryProducer {

    PairFactory pairFactory;

    @Autowired
    public void setPairFactory(PairFactory pairFactory) {
        this.pairFactory = pairFactory;
    }

    public AbstractPairFactory getFactory(FactoryProducerEnum type)
    {
    	switch(type) {
    	case PAIR:
    		   return null;
    	
    	default:
    		System.out.println("ERROR creating PAIR FACTORY USING THE ABSTRACT FACTORY CREATOR ?????");
    		System.exit(1);
    		return null;
    	}
    }
}
