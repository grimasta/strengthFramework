package com.example.inj.model.Factory;

import org.springframework.stereotype.Component;

import com.example.inj.model.strength.pair.PairStrength;
import com.example.inj.model.strength.pair.ΙPairStrength;

@Component
public abstract class AbstractPairFactory {

    public abstract ΙPairStrength getPairStrengthType(String Pairtype);
}
