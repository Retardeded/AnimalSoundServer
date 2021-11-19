package com.soundrecognition.model;

public class SoundsCoefficients {
    public double envelopeCoefficient;
    public double energyCoefficient;
    public double zeroCrossingCoefficient;

    public double mergedCoefficient;

    public SoundsCoefficients(double envelopeCoefficient, double energyCoefficient, double zeroCrossingCoefficient, double mergedCoefficient) {
        this.envelopeCoefficient = envelopeCoefficient;
        this.energyCoefficient = energyCoefficient;
        this.zeroCrossingCoefficient = zeroCrossingCoefficient;
        this.mergedCoefficient = mergedCoefficient;
    }

    @Override
    public String toString() {
        return "SoundsCoefficients{" +
                "envelopeCoefficient=" + envelopeCoefficient +
                ", energyCoefficient=" + energyCoefficient +
                ", zeroCrossingCoefficient=" + zeroCrossingCoefficient +
                ", mergedCoefficient=" + mergedCoefficient +
                '}';
    }
}
