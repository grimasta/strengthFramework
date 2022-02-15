package com.example.inj.model.strength.pair;

import org.springframework.stereotype.Component;

@Component
public class PairFactory {

    PairStrength pairStrength;
//
//    @Autowired
//    public void setPairStrength(PairStrength pairStrength) {
//
//        this.pairStrength = pairStrength;
//    }

    public static ΙPairStrength getPairStrengthType(FactoryEnum type) {
        switch(type) {
        case RIA_S_PAIRWISE_CALCULATOR:
        	return new PairStrength();
        default:
        	System.err.println("ERROR CREATING PAIRWISE STRENGTH CALCULATOR");
        	System.exit(1);
        	return null;
        }
    }
}
