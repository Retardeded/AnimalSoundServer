package com.soundrecognition.model.coefficients;

public class SoundsFreqCoefficients implements CorrelationCoefficient {
    public double centroidsCoefficient;
    public double fluxesCoefficient;
    public double rollOffPointsCoefficient;

    public SoundsFreqCoefficients(double centroidsCoefficient, double fluxesCoefficient, double rollOffPointsCoefficient) {
        this.centroidsCoefficient = centroidsCoefficient;
        this.fluxesCoefficient = fluxesCoefficient;
        this.rollOffPointsCoefficient = rollOffPointsCoefficient;
    }

    @Override
    public String toString() {
        return "SoundsCoefficients{" +
                "centroidsCoefficient=" + centroidsCoefficient +
                ", fluxesCoefficient=" + fluxesCoefficient +
                ", rollOffPointsCoefficient=" + rollOffPointsCoefficient +
                ", mergedCoefficient=" + getMergedCoefficient() +
                '}';
    }

    @Override
    public double getMergedCoefficient() {
        return centroidsCoefficient * 0.4 + fluxesCoefficient * 0.4 + rollOffPointsCoefficient * 0.2;
    }
}
