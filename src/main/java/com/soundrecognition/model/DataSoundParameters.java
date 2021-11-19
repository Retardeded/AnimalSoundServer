package com.soundrecognition.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Comparator;
import java.util.List;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class DataSoundParameters {
    @Id
    @Column
    @GeneratedValue
    private Integer id;

    public DataSoundParameters(List<Double> signalEnvelope) {
        this.signalEnvelope = signalEnvelope;
    }

    public DataSoundParameters(List<Double> signalEnvelope, List<Double> rootMeanSquareEnergy, Double zeroCrossingDensity) {
        this.signalEnvelope = signalEnvelope;
        this.rootMeanSquareEnergy = rootMeanSquareEnergy;
        this.zeroCrossingDensity = zeroCrossingDensity;
    }

    @ElementCollection
    @Column
    public List<Double> signalEnvelope;

    @ElementCollection
    @Column
    public List<Double> rootMeanSquareEnergy;

    @Column
    public Double zeroCrossingDensity;

    @Override
    public String toString() {
        return "DataSoundParameters{" +
                "signalEnvelope=" + signalEnvelope +
                ", rootMeanSquareEnergy=" + rootMeanSquareEnergy +
                ", zeroCrossingDensity=" + zeroCrossingDensity +
                '}';
    }
}