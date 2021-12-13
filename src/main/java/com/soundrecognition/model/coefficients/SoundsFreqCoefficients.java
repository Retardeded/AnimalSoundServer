package com.soundrecognition.model.coefficients;

public class SoundsFreqCoefficients implements CorrelationCoefficient {
    public double centroidsCoefficient;
    public double fluxesCoefficient;
    public double rollOffPointsCoeficient;

    public SoundsFreqCoefficients(double centroidsCoefficient, double fluxesCoefficient, double rollOffPointsCoeficient) {
        this.centroidsCoefficient = centroidsCoefficient;
        this.fluxesCoefficient = fluxesCoefficient;
        this.rollOffPointsCoeficient = rollOffPointsCoeficient;
    }

    @Override
    public String toString() {
        return "SoundsCoefficients{" +
                "centroidsCoefficient=" + centroidsCoefficient +
                ", fluxesCoefficient=" + fluxesCoefficient +
                ", rollOffPointsCoefficient=" + rollOffPointsCoeficient +
                ", mergedCoefficient=" + getMergedCoefficient() +
                '}';
    }

    @Override
    public double getMergedCoefficient() {
        return centroidsCoefficient * 0.4 + fluxesCoefficient * 0.4 + rollOffPointsCoeficient * 0.2;
    }
}
