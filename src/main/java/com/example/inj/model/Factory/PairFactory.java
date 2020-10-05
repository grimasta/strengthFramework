package com.example.inj.model.Factory;

import com.example.inj.model.Strength.PairStrength;
import com.example.inj.model.Strength.PairStrengthIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class PairFactory extends AbstractPairFactory {

    PairStrength pairStrength;

    @Autowired
    public void setPairStrength(PairStrength pairStrength) {

        this.pairStrength = pairStrength;
    }

    @Override
    public PairStrengthIn getPairStrengthType(String Pairtype) {
        if(Pairtype.equalsIgnoreCase(FactoryEnum.pairStrength.toString())){
            //System.out.println("Inside the enusmmm " + FactoryEnum.pairStrength.toString());
            return this.pairStrength;
        }
        else {
            return null;
        }
    }
}
