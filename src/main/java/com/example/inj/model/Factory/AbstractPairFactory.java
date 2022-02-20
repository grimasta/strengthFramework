package com.example.inj.model.Factory;

import com.example.inj.model.strength.pair.IPairStrength;

public abstract class AbstractPairFactory {

    public abstract IPairStrength getPairStrengthType(String Pairtype);
}
