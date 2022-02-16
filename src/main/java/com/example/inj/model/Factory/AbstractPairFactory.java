package com.example.inj.model.Factory;

import org.springframework.stereotype.Component;

import com.example.inj.model.strength.pair.PairStrength;
import com.example.inj.model.strength.pair.IPairStrength;

@Component
public abstract class AbstractPairFactory {

    public abstract IPairStrength getPairStrengthType(String Pairtype);
}
