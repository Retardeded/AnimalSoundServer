package com.soundrecognition.model.coefficients;

public class SoundsTimeCoefficients implements CorrelationCoefficient {
    public double envelopeCoefficient;
    public double energyCoefficient;
    public double zeroCrossingCoefficient;

    public SoundsTimeCoefficients(double envelopeCoefficient, double energyCoefficient, double zeroCrossingCoefficient) {
        this.envelopeCoefficient = envelopeCoefficient;
        this.energyCoefficient = energyCoefficient;
        this.zeroCrossingCoefficient = zeroCrossingCoefficient;
    }

    @Override
    public String toString() {
        return "SoundsCoefficients{" +
                "envelopeCoefficient=" + envelopeCoefficient +
                ", energyCoefficient=" + energyCoefficient +
                ", zeroCrossingCoefficient=" + zeroCrossingCoefficient +
                ", mergedCoefficient=" + getMergedCoefficient() +
                '}';
    }

    @Override
    public double getMergedCoefficient() {
        return envelopeCoefficient * 0.4 + energyCoefficient * 0.4 + zeroCrossingCoefficient * 0.2;
    }
}
