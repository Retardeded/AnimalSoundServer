package com.soundrecognition.model.coefficients;

public class PowerSpectrumCoefficient implements CorrelationCoefficient {

    public double powerSpectrumCoefficient;

    public PowerSpectrumCoefficient(double powerSpectrumCoefficient) {
        this.powerSpectrumCoefficient = powerSpectrumCoefficient;
    }

    @Override
    public String toString() {
        return "SoundsFreqCoefficients{" +
                "powerSpectrumCoefficient=" + powerSpectrumCoefficient +
                ", mergedCoefficient=" + getMergedCoefficient() +
                '}';
    }

    @Override
    public double getMergedCoefficient() {
        return powerSpectrumCoefficient;
    }
}
