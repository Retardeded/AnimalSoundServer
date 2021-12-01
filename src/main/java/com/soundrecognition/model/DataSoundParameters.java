package com.soundrecognition.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class DataSoundParameters implements Serializable {

    @Id
    @Column
    @GeneratedValue
    private Integer id;

    @Column
    public String typeName;

    @ElementCollection
    @Column
    public List<Integer> signalEnvelope;

    @ElementCollection
    @Column
    public List<Integer> rootMeanSquareEnergy;

    public Integer zeroCrossingDensity;

    public DataSoundParameters(String type, List<Integer> signalEnvelope, List<Integer> rootMeanSquareEnergy, Integer zeroCrossingDensity) {
        this.typeName = type;
        this.signalEnvelope = signalEnvelope;
        this.rootMeanSquareEnergy = rootMeanSquareEnergy;
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