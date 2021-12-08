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

    @ElementCollection
    @Column
    public List<Integer> spectralCentroids;

    @ElementCollection
    @Column
    public List<Integer> spectralFluxes;

    @ElementCollection
    @Column
    public List<Double> spectralRollOffPoints;

    public Integer zeroCrossingDensity;

    @Override
    public String toString() {
        return "DataSoundParameters{" +
                "signalEnvelope=" + signalEnvelope +
                ", rootMeanSquareEnergy=" + rootMeanSquareEnergy +
                ", zeroCrossingDensity=" + zeroCrossingDensity +
                ", spectralCentroids=" + spectralCentroids +
                ", spectralFluxs=" + spectralFluxes +
                ", spectralRolloffPoints=" + spectralRollOffPoints +
                '}';
    }

}