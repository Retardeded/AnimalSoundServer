package com.soundrecognition.soundprocessing;

import ca.uol.aig.fftpack.RealDoubleFFT;
import com.soundrecognition.model.*;

import java.util.ArrayList;
import java.util.List;

public class CalculateSoundSimilarity {

    public static Integer[] signalEnvelope(DataSound sound) {
        int n = Math.toIntExact(sound.getNumOfGraphs());
        int m = Math.toIntExact(sound.getPointsInGraphs());
        Integer[] signalEnvelopeArray = new Integer[n];

        for(int i = 0; i < n; i++) {
            var singleGraph = sound.getTimeDomainPoints().subList(i*m, (i+1)*m);
            int max = 0;
            for(var point:singleGraph) {
                if(Math.abs(point.getY()) > max)
                    max = Math.abs((int)point.getY());
            }
            signalEnvelopeArray[i] = max;
        }

        return signalEnvelopeArray;
    }

    public static Integer[] rootMeanSquareEnergy(DataSound sound) {
        int n = Math.toIntExact(sound.getNumOfGraphs());
        int m = Math.toIntExact(sound.getPointsInGraphs());
        Integer[] signalEnvelopeArray = new Integer[n];

        for(int i = 0; i < n; i++) {
            var singleGraph = sound.getTimeDomainPoints().subList(i*m, (i+1)*m);
            var rms = rmsValue(singleGraph, singleGraph.size());
            signalEnvelopeArray[i] = rms;
        }

        return signalEnvelopeArray;
    }

    static int rmsValue(List<DataPoint> arr, int n)
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

        return (int) root;
    }

    public static int zeroCrossingDensity(DataSound sound)
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
        return (int) (numCrossing / sound.getNumOfGraphs());
    }

    public static List<double[]> calculatePowerSpectres(DataSound dataSound) {
        int n = Math.toIntExact(dataSound.getPointsInGraphs());
        int m = Math.toIntExact(dataSound.getNumOfGraphs());
        var audioData = dataSound.getTimeDomainPoints();
        var powerSpectres = new ArrayList<double[]>();
        //var magnitudeSpectrum = new double[n/2];
        var transformer = new RealDoubleFFT(n);
        double[] toTransform = new double[n];
        for(int j = 0; j < m; j++) {
            var powerSpectrum = new double[n/2];
            for (int i = 0; i < n; i++) {
                toTransform[i] = audioData.get(i+j*n).getY() / n;
            }
            transformer.ft(toTransform);
            for (int i = 0; i < n/2; i++) {
                powerSpectrum[i] = toTransform[i*2+1] * toTransform[i*2+1] + toTransform[i*2] * toTransform[i*2];
            }

            powerSpectres.add(powerSpectrum);

        }

        return powerSpectres;
    }

    public static ArrayList<Integer> calculateFullSignalFrequencyDomain(List<double[]> powerSpectres, int n, int m) {
        var dataAmplitudeFullSignal = new double[n/2];

        for(int j = 0; j < m; j++) {


            for (int i = 0; i < n/2; i++) {
                dataAmplitudeFullSignal[i] += powerSpectres.get(j)[i];
                //dataAmplitudeFullSignal[i] += toTransform[i*2+1] * toTransform[i*2+1] + toTransform[i*2] * toTransform[i*2];
                //dataFreqDomain.add(new DataPoint(i, toTransform[i*2+1] * toTransform[i*2+1] + toTransform[i*2] * toTransform[i*2]));
            }
        }
        var powerSpectrumInt = new ArrayList<Integer>();
        for(Double d : dataAmplitudeFullSignal) {
            powerSpectrumInt.add(d.intValue()/m);
        }

        return powerSpectrumInt;
    }

    public static ArrayList<Integer> spectralCentroids(List<double[]> powerSpectres, int n, int m) {
        var spectralCentroids = new double[m];
        for(int j = 0; j < m; j++) {
            double total = 0.0;
            double weighted_total = 0.0;
            for (int bin = 0; bin < powerSpectres.get(j).length; bin++)
            {
                weighted_total += bin * powerSpectres.get(j)[bin];
                total += powerSpectres.get(j)[bin];
                //dataFreqDomain.add(new DataPoint(i, toTransform[i*2+1] * toTransform[i*2+1] + toTransform[i*2] * toTransform[i*2]));
            }
            System.out.println("total:" + total);
            System.out.println("weighted:" + total);
            spectralCentroids[j] = weighted_total / total;
        }
        var spectralCentroidsInt = new ArrayList<Integer>();
        for(Double d : spectralCentroids) {
            spectralCentroidsInt.add(d.intValue());
            System.out.println("centroid:" + d.intValue());
        }
        return spectralCentroidsInt;
    }

    public static ArrayList<Integer> spectralFluxes(List<double[]> powerSpectres, int n, int m) {
        var spectralFluxes = new double[m];
        for(int j = 1; j < m; j++) {
            double sum = 0.0;
            for (int bin = 0; bin < powerSpectres.get(j).length; bin++)
            {
                double difference = Math.sqrt(powerSpectres.get(j)[bin])
                        - Math.sqrt(powerSpectres.get(j-1)[bin]);
                double differences_squared = difference * difference;
                sum += differences_squared;
            }
            spectralFluxes[j] = sum;
        }
        var spectralFluxesInt = new ArrayList<Integer>();
        for(Double d : spectralFluxes) {
            spectralFluxesInt.add(d.intValue());
        }
        return spectralFluxesInt;
    }

    public static ArrayList<Double> spectralRollOffPoints(List<double[]> powerSpectres, int n, int m) {
        var spectralRollOffPoints = new double[m];
        double cutoff = 0.85;

        for(int j = 0; j < m; j++) {
            double total = 0.0;
            for (int bin = 0; bin < powerSpectres.get(j).length; bin++)
                total += powerSpectres.get(j)[bin];
            double threshold = total * cutoff;
            total = 0.0;
            int point = 0;
            for (int bin = 0; bin < powerSpectres.get(j).length; bin++) {
                total += powerSpectres.get(j)[bin];
                if (total >= threshold) {
                    point = bin;
                    bin = powerSpectres.get(j).length;
                }
            }
            spectralRollOffPoints[j] =  (double) point /(double) powerSpectres.get(j).length;
        }
        var spectralCentroidsList = new ArrayList<Double>();
        for(Double d : spectralRollOffPoints) {
            spectralCentroidsList.add(d);
        }
        return spectralCentroidsList;
    }

    public static SoundsTimeCoefficients correlationTimeParamsCoefficient(SoundTypeParameters soundTypeParameters, DataSoundParameters newSound)
    {
        double envelopeCoefficient = calculateCoefficient(newSound.signalEnvelope, soundTypeParameters.getSignalEnvelopeWeighted(),Math.min(newSound.signalEnvelope.size(),soundTypeParameters.signalEnvelopeCount.size()));
        double energyCoefficient = calculateCoefficient(newSound.rootMeanSquareEnergy, soundTypeParameters.getRootMeanSquareEnergyWeighted(),Math.min(newSound.rootMeanSquareEnergy.size(),soundTypeParameters.rootMeanSquareEnergyCount.size()));
        var X = newSound.zeroCrossingDensity;
        var Y = soundTypeParameters.getZeroCrossingDensityWeighted();
        double zeroCrossingCoefficient = X < Y ? X/Y : Y/X;

        return new SoundsTimeCoefficients(envelopeCoefficient, energyCoefficient, zeroCrossingCoefficient);
    }

    public static PowerSpectrumCoefficient correlationPowerSpectrumCoefficient(SoundTypeParameters soundTypeParameters, DataSoundParameters newSound)
    {
        double powerSpectrumCoefficient = calculateCoefficient(newSound.powerSpectrum, soundTypeParameters.getPowerSpectrumWeighted(),Math.min(newSound.powerSpectrum.size(),soundTypeParameters.powerSpectrumCount.size()));
        double coefficient = powerSpectrumCoefficient;

        return new PowerSpectrumCoefficient(coefficient);
    }

    public static SoundsFreqCoefficients correlationFreqParamsCoefficient(SoundTypeParameters soundTypeParameters, DataSoundParameters newSound)
    {
        double centroidsCoefficient = calculateCoefficient(newSound.spectralCentroids, soundTypeParameters.getSpectralCentroidsWeighted(),Math.min(newSound.spectralCentroids.size(),soundTypeParameters.spectralCentroidsCount.size()));
        double fluxesCoefficient = calculateCoefficient(newSound.spectralFluxes, soundTypeParameters.getSpectralFluxesRaw(),Math.min(newSound.spectralFluxes.size(),soundTypeParameters.spectralFluxesCount.size()));
        double rollOffCoefficient = calculateCoefficientDouble(newSound.spectralRollOffPoints, soundTypeParameters.getSpectralRollOffPointsRaw(),Math.min(newSound.spectralRollOffPoints.size(),soundTypeParameters.spectralRollOffPointsCount.size()));

        return new SoundsFreqCoefficients(centroidsCoefficient, fluxesCoefficient , rollOffCoefficient);
    }

    public static double calculateCoefficientDouble(List<Double> soundTypeData,List<Double> newSoundData, int n) {
        double sum_X = 0, sum_Y = 0, sum_XY = 0;
        double squareSum_X = 0, squareSum_Y = 0;

        for (int i = 0; i < n; i++)
        {
            // sum of elements of array X.
            double X = newSoundData.get(i);
            double Y = soundTypeData.get(i);
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


    public static double calculateCoefficient(List<Integer> soundTypeData,List<Integer> newSoundData, int n) {
        double sum_X = 0, sum_Y = 0, sum_XY = 0;
        double squareSum_X = 0, squareSum_Y = 0;

        for (int i = 0; i < n; i++)
        {
            // sum of elements of array X.
            double X = newSoundData.get(i);
            double Y = soundTypeData.get(i);
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
