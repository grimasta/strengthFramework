package com.example.inj.model.strength.pair;

import org.springframework.stereotype.Component;

@Component
public class PairFactory {

    
//
//    @Autowired
//    public void setPairStrength(PairStrength pairStrength) {
//
//        this.pairStrength = pairStrength;
//    }

    public static IPairStrength getPairStrengthType(PairCalculatorEnum type) {
        IPairStrength pairStrength;
        switch(type) {
            case RIA_S_PAIRWISE_CALCULATOR:
        	    pairStrength = new PairStrength();
        	    return pairStrength;
            /*case RONGJI_S_PAIRWISE_CALCULATOR:
                pairStrength = new PairStrength();
                return pairStrength;*/
        default:
        	System.err.println("ERROR CREATING PAIRWISE STRENGTH CALCULATOR");
        	System.exit(1);
        	return null;
        }
    }
}
