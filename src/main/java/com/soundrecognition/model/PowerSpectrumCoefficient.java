package com.soundrecognition.model;

public class PowerSpectrumCoefficient {

    public double powerSpectrumCoefficient;

    public double mergedCoefficient;

    public PowerSpectrumCoefficient(double powerSpectrumCoefficient) {
        this.powerSpectrumCoefficient = powerSpectrumCoefficient;
        this.mergedCoefficient = powerSpectrumCoefficient;
    }

    @Override
    public String toString() {
        return "SoundsFreqCoefficients{" +
                "powerSpectrumCoefficient=" + powerSpectrumCoefficient +
                ", mergedCoefficient=" + mergedCoefficient +
                '}';
    }
}
