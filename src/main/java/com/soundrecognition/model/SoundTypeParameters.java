package com.soundrecognition.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class SoundTypeParameters implements Serializable {

    @Id
    @Column
    @GeneratedValue
    private Integer id;

    @Column
    public String typeName;

    @ElementCollection
    @Column
    private List<Integer> signalEnvelope;

    public List<Integer> getSignalEnvelopeRaw() {

      return signalEnvelope;
    }

    public List<Integer> getSignalEnvelopeWeighted() {

        var result = IntStream.range(0, signalEnvelope.size())
                .map(i -> signalEnvelope.get(i) / signalEnvelopeCount.get(i))
                .boxed()
                .collect(Collectors.toList());

        return result;
    }

    public void setSignalEnvelope(List<Integer> signalEnvelope) {
        this.signalEnvelope = signalEnvelope;
    }

    public List<Integer> getRootMeanSquareEnergyRaw() {
        return rootMeanSquareEnergy;
    }

    public List<Integer> getRootMeanSquareEnergyWeighted() {
        var result = IntStream.range(0, rootMeanSquareEnergy.size())
                .map(i -> rootMeanSquareEnergy.get(i) / rootMeanSquareEnergyCount.get(i))
                .boxed()
                .collect(Collectors.toList());

        return result;
    }

    public void setRootMeanSquareEnergy(List<Integer> rootMeanSquareEnergy) {
        this.rootMeanSquareEnergy = rootMeanSquareEnergy;
    }

    @ElementCollection
    @Column
    public List<Integer> signalEnvelopeCount;

    @ElementCollection
    @Column
    private List<Integer> rootMeanSquareEnergy;

    @ElementCollection
    @Column
    public List<Integer> rootMeanSquareEnergyCount;

    @ElementCollection
    @Column
    private List<Integer> powerSpectrum;

    public List<Integer> getPowerSpectrumRaw() {
        return powerSpectrum;
    }

    public List<Integer> getPowerSpectrumWeighted() {
        var result = IntStream.range(0, powerSpectrum.size())
                .map(i -> powerSpectrum.get(i) / powerSpectrumCount.get(i))
                .boxed()
                .collect(Collectors.toList());

        return result;
    }

    public void setPowerSpectrum(List<Integer> powerSpectrum) {
        this.powerSpectrum = powerSpectrum;
    }

    @ElementCollection
    @Column
    public List<Integer> powerSpectrumCount;


    @ElementCollection
    @Column
    private List<Integer> spectralCentroids;

    public List<Integer> getSpectralCentroidsRaw() {
        return spectralCentroids;
    }

    public List<Integer> getSpectralCentroidsWeighted() {
        var result = IntStream.range(0, spectralCentroids.size())
                .map(i -> spectralCentroids.get(i) / spectralCentroidsCount.get(i))
                .boxed()
                .collect(Collectors.toList());

        return result;
    }

    public void setSpectralCentroids(List<Integer> spectralCentroids) {
        this.spectralCentroids = spectralCentroids;
    }

    @ElementCollection
    @Column
    public List<Integer> spectralCentroidsCount;

    @ElementCollection
    @Column
    private List<Integer> spectralFluxes;

    public List<Integer> getSpectralFluxesRaw() {
        return spectralFluxes;
    }

    public List<Integer> getSpectralFluxesWeighted() {
        var result = IntStream.range(0, spectralFluxes.size())
                .map(i -> spectralFluxes.get(i) / spectralFluxesCount.get(i))
                .boxed()
                .collect(Collectors.toList());

        return result;
    }

    public void setSpectralFluxes(List<Integer> spectralCentroids) {
        this.spectralFluxes = spectralFluxes;
    }

    @ElementCollection
    @Column
    public List<Integer> spectralFluxesCount;


    @ElementCollection
    @Column
    private List<Double> spectralRolloffPoints;

    public List<Double> getSpectralRollOffPointsRaw() {
        return spectralRolloffPoints;
    }

    public List<Double> getSpectralRolloffPointsWeighted() {

        var weighted = new ArrayList<Double>();
        for (int i = 0; i< spectralRollOffPointsCount.size(); i++) {
            weighted.add(spectralRolloffPoints.get(i) / spectralRollOffPointsCount.get(i));
        }

        return weighted;
    }

    public void setSpectralRolloffPoints(List<Double> spectralRolloffPoints) {
        this.spectralRolloffPoints = spectralRolloffPoints;
    }

    @ElementCollection
    @Column
    public List<Integer> spectralRollOffPointsCount;



    private Integer zeroCrossingDensity;

    public Integer getZeroCrossingDensityWeighted() {
        return zeroCrossingDensity / zeroCrossingDensityCount;
    }
    public Integer getZeroCrossingDensityRaw() {
        return zeroCrossingDensity;
    }

    public void UpdateZeroCrossingDensity(Integer zeroCrossingDensity, Integer newValue, Boolean adding) {
        if (adding) {
            zeroCrossingDensityCount++;
            this.zeroCrossingDensity = zeroCrossingDensity + newValue;
        } else {
            zeroCrossingDensityCount--;
            this.zeroCrossingDensity = zeroCrossingDensity - newValue;
        }
    }

    public Integer zeroCrossingDensityCount;

    public SoundTypeParameters(String type, List<Integer> signalEnvelope, List<Integer> rootMeanSquareEnergy, Integer zeroCrossingDensity, List<Integer> powerSpectrum, List<Integer> spectralCentroids, List<Integer> spectralFluxes, List<Double> spectralRolloffPoints) {
        this.typeName = type;
        this.signalEnvelope = signalEnvelope;
        this.signalEnvelopeCount = new ArrayList<Integer>(Collections.nCopies(signalEnvelope.size(), 1));
        this.rootMeanSquareEnergy = rootMeanSquareEnergy;
        this.rootMeanSquareEnergyCount = new ArrayList<Integer>(Collections.nCopies(rootMeanSquareEnergy.size(), 1));
        this.zeroCrossingDensityCount = 1;
        this.zeroCrossingDensity = zeroCrossingDensity;
        this.powerSpectrum = powerSpectrum;
        this.powerSpectrumCount = new ArrayList<Integer>(Collections.nCopies(powerSpectrum.size(), 1));
        this.spectralCentroids = spectralCentroids;
        this.spectralCentroidsCount = new ArrayList<Integer>(Collections.nCopies(spectralCentroids.size(), 1));
        this.spectralFluxes = spectralFluxes;
        this.spectralFluxesCount = new ArrayList<Integer>(Collections.nCopies(spectralFluxes.size(), 1));
        this.spectralRolloffPoints = spectralRolloffPoints;
        this.spectralRollOffPointsCount = new ArrayList<Integer>(Collections.nCopies(spectralRolloffPoints.size(), 1));

    }

    @Override
    public String toString() {
        return "DataSoundParameters{" +
                "signalEnvelope=" + getSignalEnvelopeWeighted() +
                ", rootMeanSquareEnergy=" + getRootMeanSquareEnergyWeighted() +
                ", powerSpectrum=" + getPowerSpectrumWeighted() +
                ", zeroCrossingDensity=" + zeroCrossingDensity +
                '}';
    }

    public void calculateNewParamAverageAdd(List<Integer> parametersPresent, List<Integer> parametersNew, List<Integer> parametersCount) {
        int minSize = Math.min(parametersNew.size(), parametersPresent.size());
        for(int i = 0; i < minSize; i++) {
            parametersPresent.set(i, (parametersPresent.get(i) + parametersNew.get(i)));
            parametersCount.set(i, parametersCount.get(i)+1);
        }
        if(minSize == parametersPresent.size()) {
            int maxSize = Math.max(parametersNew.size(), parametersPresent.size());
            for(int i = minSize; i < maxSize;i++) {
                parametersPresent.add(parametersNew.get(i));
                parametersCount.add(1);
            }
        }
    }

    public void calculateNewParamAverageAddDouble(List<Double> parametersPresent, List<Double> parametersNew, List<Integer> parametersCount) {
        int minSize = Math.min(parametersNew.size(), parametersPresent.size());
        for(int i = 0; i < minSize; i++) {
            parametersPresent.set(i, (parametersPresent.get(i) + parametersNew.get(i)));
            parametersCount.set(i, parametersCount.get(i)+1);
        }
        if(minSize == parametersPresent.size()) {
            int maxSize = Math.max(parametersNew.size(), parametersPresent.size());
            for(int i = minSize; i < maxSize;i++) {
                parametersPresent.add(parametersNew.get(i));
                parametersCount.add(1);
            }
        }
    }


    public void calculateNewParamAverageDelete(List<Integer> parametersType, List<Integer> parametersSound, List<Integer> parametersCount) {
        int minSize = Math.min(parametersType.size(), parametersSound.size());
        for(int i = 0; i < minSize; i++) {
            parametersType.set(i, ((parametersType.get(i)  - parametersSound.get(i) )));
            parametersCount.set(i, parametersCount.get(i)-1);
        }

        while(parametersCount.get(parametersType.size()-1) == 0) {
            parametersType.remove(parametersCount.size()-1);
            parametersCount.remove(parametersCount.size()-1);
        }
    }

    public void calculateNewParamAverageDeleteDouble(List<Double> parametersType, List<Double> parametersSound, List<Integer> parametersCount) {
        int minSize = Math.min(parametersType.size(), parametersSound.size());
        for(int i = 0; i < minSize; i++) {
            parametersType.set(i, ((parametersType.get(i)  - parametersSound.get(i) )));
            parametersCount.set(i, parametersCount.get(i)-1);
        }

        while(parametersCount.get(parametersType.size()-1) == 0) {
            parametersType.remove(parametersCount.size()-1);
            parametersCount.remove(parametersCount.size()-1);
        }
    }


}