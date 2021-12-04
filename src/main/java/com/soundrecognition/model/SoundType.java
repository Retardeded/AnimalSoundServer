package com.soundrecognition.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class SoundType {
    public SoundType(String name, List<DataSound> dataSounds) {
        this.name = name;
        this.dataSounds = dataSounds;
    }

    public SoundType(String name, List<DataSound> dataSounds, SoundTypeParameters soundTypeParameters) {
        this.name = name;
        this.dataSounds = dataSounds;
        this.soundTypeParameters = soundTypeParameters;
    }

    @Id
    @Column
    @GeneratedValue
    private Integer id;

    @NotBlank
    @Column
    private String name;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<DataSound> dataSounds;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    public SoundTypeParameters soundTypeParameters;
}
