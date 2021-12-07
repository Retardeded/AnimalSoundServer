package com.soundrecognition.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class DataSoundParameters implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    public String typeName;

    @ElementCollection
    @Column
    public List<Integer> signalEnvelope;

    @ElementCollection
    @Column
    public List<Integer> rootMeanSquareEnergy;

    @ElementCollection
    @Column
    public List<Integer> powerSpectrum;

    public Integer zeroCrossingDensity;

    public DataSoundParameters(String type, List<Integer> signalEnvelope, List<Integer> rootMeanSquareEnergy, List<Integer> powerSpectrum, Integer zeroCrossingDensity) {
        this.typeName = type;
        this.signalEnvelope = signalEnvelope;
        this.rootMeanSquareEnergy = rootMeanSquareEnergy;
        this.powerSpectrum = powerSpectrum;
        this.zeroCrossingDensity = zeroCrossingDensity;
    }

    @Override
    public String toString() {
        return "DataSoundParameters{" +
                "signalEnvelope=" + signalEnvelope +
                ", rootMeanSquareEnergy=" + rootMeanSquareEnergy +
                ", zeroCrossingDensity=" + zeroCrossingDensity +
                '}';
    }

}