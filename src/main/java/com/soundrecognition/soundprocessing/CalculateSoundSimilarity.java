package com.soundrecognition.soundprocessing;

import com.soundrecognition.model.DataPoint;
import com.soundrecognition.model.DataSound;
import com.soundrecognition.model.DataSoundParameters;
import com.soundrecognition.model.SoundsCoefficients;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CalculateSoundSimilarity {

    public static Double[] signalEnvelope(DataSound sound) {
        int n = Math.toIntExact(sound.getNumOfGraphs());
        int m = Math.toIntExact(sound.getPointsInGraphs());
        Double[] signalEnvelopeArray = new Double[n];

        for(int i = 0; i < n; i++) {
            var singleGraph = sound.getTimeDomainPoints().subList(i*m, (i+1)*m);
            double max = 0;
            for(var point:singleGraph) {
                if(Math.abs(point.getY()) > max)
                    max = Math.abs(point.getY());
            }
            signalEnvelopeArray[i] = max;
        }

        return signalEnvelopeArray;
    }

    public static Double[] rootMeanSquareEnergy(DataSound sound) {
        int n = Math.toIntExact(sound.getNumOfGraphs());
        int m = Math.toIntExact(sound.getPointsInGraphs());
        Double[] signalEnvelopeArray = new Double[n];

        for(int i = 0; i < n; i++) {
            var singleGraph = sound.getTimeDomainPoints().subList(i*m, (i+1)*m);
            var rms = rmsValue(singleGraph, singleGraph.size());
            signalEnvelopeArray[i] = rms;
        }

        return signalEnvelopeArray;
    }

    static double rmsValue(List<DataPoint> arr, int n)
    {
        double square = 0;
        double mean = 0;
        double root = 0;

        // Calculate square.
        for(int i = 0; i < n; i++)
        {
            square += Math.pow(arr.get(i).y, 2);
        }

        // Calculate Mean.
        mean = (square / (n));

        // Calculate Root.
        root = Math.sqrt(mean);

        return root;
    }

    public static double zeroCrossingDensity(DataSound sound)
    {
        var timePoints = sound.getTimeDomainPoints();

        int numCrossing = 0;
        for (int i = 0; i < timePoints.size()-1; i++)
        {
            if ((timePoints.get(i).y > 0 && timePoints.get(i+1).y <= 0) ||
                    (timePoints.get(i).y < 0 && timePoints.get(i+1).y >= 0))
            {
                numCrossing++;
            }
        }
        return (double) numCrossing / sound.getNumOfGraphs();
    }

    public static SoundsCoefficients correlationParamsCoefficient(DataSoundParameters newSound, DataSoundParameters compareSound)
    {
        double envelopeCoefficient = calculateCoefficient(newSound.signalEnvelope, compareSound.signalEnvelope,Math.min(newSound.signalEnvelope.size(),compareSound.signalEnvelope.size()));
        double energyCoefficient = calculateCoefficient(newSound.rootMeanSquareEnergy, compareSound.rootMeanSquareEnergy,Math.min(newSound.rootMeanSquareEnergy.size(),compareSound.rootMeanSquareEnergy.size()));
        var X = newSound.zeroCrossingDensity;
        var Y = compareSound.zeroCrossingDensity;
        double zeroCrossingCoefficient = X < Y ? X/Y : Y/X;
        double coefficient = envelopeCoefficient * 0.4 + energyCoefficient * 0.4 + zeroCrossingCoefficient * 0.2;

        return new SoundsCoefficients(envelopeCoefficient, energyCoefficient, zeroCrossingCoefficient, coefficient);
    }

    private static double calculateCoefficient(List<Double> newSoundData, List<Double> compareSoundData, int n) {
        double sum_X = 0, sum_Y = 0, sum_XY = 0;
        double squareSum_X = 0, squareSum_Y = 0;

        for (int i = 0; i < n; i++)
        {
            // sum of elements of array X.
            double X = newSoundData.get(i);
            double Y = compareSoundData.get(i);
            sum_X = sum_X + X;

            // sum of elements of array Y.
            sum_Y = sum_Y + Y;

            // sum of X[i] * Y[i].
            sum_XY = sum_XY + X * Y;

            // sum of square of array elements.
            squareSum_X = squareSum_X + X * X;
            squareSum_Y = squareSum_Y + Y * Y;
        }

        // use formula for calculating correlation coefficient.
        double corr = (n * sum_XY - sum_X * sum_Y)
                / Math.sqrt((n * squareSum_X - sum_X * sum_X)
                * (n * squareSum_Y - sum_Y * sum_Y));
        return corr;
    }

    public static double correlationCoefficient(DataSound newSound, DataSound compareSound)
    {
        var newSoundData = newSound.getFreqDomainPoints();
        var compareSoundData = compareSound.getFreqDomainPoints();
        int n = Math.min(newSoundData.size(),compareSoundData.size());

        int sum_X = 0, sum_Y = 0, sum_XY = 0;
        int squareSum_X = 0, squareSum_Y = 0;

        for (int i = 0; i < n; i++)
        {
            // sum of elements of array X.
            int X = (int)newSoundData.get(i).getY();
            int Y = (int)compareSoundData.get(i).getY();
            sum_X = sum_X + X;

            // sum of elements of array Y.
            sum_Y = sum_Y + Y;

            // sum of X[i] * Y[i].
            sum_XY = sum_XY + X * Y;

            // sum of square of array elements.
            squareSum_X = squareSum_X + X * X;
            squareSum_Y = squareSum_Y + Y * Y;
        }

        // use formula for calculating correlation coefficient.
        double corr = (n * sum_XY - sum_X * sum_Y)
                / Math.sqrt((n * squareSum_X - sum_X * sum_X)
                * (n * squareSum_Y - sum_Y * sum_Y));

        return corr;
    }
}
