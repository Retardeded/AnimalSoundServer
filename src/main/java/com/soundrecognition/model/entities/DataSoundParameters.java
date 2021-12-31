package com.soundrecognition.model.entities;

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
    public List<Double> spectralCentroids;

    @ElementCollection
    @Column
    public List<Integer> spectralFluxes;

    @ElementCollection
    @Column
    public List<Double> spectralRollOffPoints;
    @ElementCollection
    @Column
    public List<Integer> zeroCrossingDensity;

    @Override
    public String toString() {
        return "DataSoundParameters{" +
                "signalEnvelope=" + signalEnvelope +
                ", rootMeanSquareEnergy=" + rootMeanSquareEnergy +
                ", zeroCrossingDensity=" + zeroCrossingDensity +
                ", spectralCentroids=" + spectralCentroids +
                ", spectralFluxes=" + spectralFluxes +
                ", spectralRolloffPoints=" + spectralRollOffPoints +
                '}';
    }

}