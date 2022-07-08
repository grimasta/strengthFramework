package com.example.inj.Analysis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VectorNode {
    private double occurrence;
    private double intermediateProbability;
    private double overallProbability;

    public VectorNode(double occurrence, double intermediateProbability, double overallProbability) {
        this.occurrence = occurrence;
        this.intermediateProbability = intermediateProbability;
        this.overallProbability = overallProbability;
    }
}
