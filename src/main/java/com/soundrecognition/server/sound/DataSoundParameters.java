package com.soundrecognition.server.sound;

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

    @ElementCollection
    @Column
    List<Double> signalEnvelope;


}