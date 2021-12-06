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

    public SoundTypeParameters(String type, List<Integer> signalEnvelope, List<Integer> rootMeanSquareEnergy, Integer zeroCrossingDensity) {
        this.typeName = type;
        this.signalEnvelope = signalEnvelope;
        this.signalEnvelopeCount = new ArrayList<Integer>(Collections.nCopies(signalEnvelope.size(), 1));
        this.rootMeanSquareEnergy = rootMeanSquareEnergy;
        this.rootMeanSquareEnergyCount = new ArrayList<Integer>(Collections.nCopies(rootMeanSquareEnergy.size(), 1));
        this.zeroCrossingDensityCount = 1;
        this.zeroCrossingDensity = zeroCrossingDensity;
    }

    @Override
    public String toString() {
        return "DataSoundParameters{" +
                "signalEnvelope=" + getSignalEnvelopeWeighted() +
                ", rootMeanSquareEnergy=" + getRootMeanSquareEnergyWeighted() +
                ", zeroCrossingDensity=" + zeroCrossingDensity +
                '}';
    }

    public void calculateNewParamAverageAdd(List<Integer> parametersPresent, List<Integer> parametersNew, List<Integer> parametersCount) {
        int minSize = Math.min(parametersNew.size(), parametersPresent.size());
        for(int i = 0; i < minSize; i++) {
            //int n = parametersCount.get(i);
            //parametersPresent.set(i,(int) (parametersPresent.get(i) * (n / (n + 1)) + parametersNew.get(i) / (n + 1)));
            parametersPresent.set(i,(int) (parametersPresent.get(i) + parametersNew.get(i)));
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
            //parametersType.set(i,(int) ((parametersType.get(i) * n  - parametersSound.get(i) ) / (n + 1)));
            parametersType.set(i,(int) ((parametersType.get(i)  - parametersSound.get(i) )));
            parametersCount.set(i, parametersCount.get(i)-1);
        }

        while(parametersCount.get(parametersType.size()-1) == 0) {
            parametersType.remove(parametersCount.size()-1);
            parametersCount.remove(parametersCount.size()-1);
            //parametersType.set(i, (parametersType.get(i) * n  - parametersSound.get(i) ) / (n + 1));
        }
    }


}