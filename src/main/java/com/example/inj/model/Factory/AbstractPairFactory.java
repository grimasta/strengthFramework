package com.example.inj.model.Factory;

import com.example.inj.model.Strength.PairStrength;
import com.example.inj.model.Strength.PairStrengthIn;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractPairFactory {

    public abstract PairStrengthIn getPairStrengthType(String Pairtype);
}
