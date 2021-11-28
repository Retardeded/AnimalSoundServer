package com.soundrecognition.model;

public class SoundsFreqCoefficients {

    public double powerSpectrumCoefficient;

    public double mergedCoefficient;

    public SoundsFreqCoefficients(double powerSpectrumCoefficient) {
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