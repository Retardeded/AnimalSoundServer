package com.soundrecognition.model;

import lombok.*;
import org.springframework.data.relational.core.sql.In;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

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
    public List<Integer> signalEnvelope;

    @ElementCollection
    @Column
    public List<Integer> rootMeanSquareEnergy;

    public Integer zeroCrossingDensity;

    public SoundTypeParameters(String type, List<Integer> signalEnvelope, List<Integer> rootMeanSquareEnergy, Integer zeroCrossingDensity) {
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