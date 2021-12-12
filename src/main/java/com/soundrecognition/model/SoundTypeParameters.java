package com.soundrecognition.model;

import lombok.*;
import org.hibernate.mapping.Map;
import org.springframework.data.relational.core.sql.In;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class SoundTypeParameters implements Serializable {

    public enum ParameterName {
        SignalEnvelope,
        RootMeanSquareEnergy,
        PowerSpectrum,
        SpectralCentroids,
        SpectralFluxes,
        SpectralRollOffPoints
    }

    @Id
    @Column
    @GeneratedValue
    private Integer id;

    @Column
    public String typeName;

    @OneToMany(cascade = {CascadeType.ALL})
    public List<SoundTypeParameterInt> parametersListInt = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL})
    public List<SoundTypeParameterDouble> parametersListDouble = new ArrayList<>();

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

    public SoundTypeParameters(String type, List<Integer> signalEnvelope, List<Integer> rootMeanSquareEnergy, Integer zeroCrossingDensity, List<Integer> powerSpectrum, List<Double> spectralCentroids, List<Integer> spectralFluxes, List<Double> spectralRolloffPoints) {
        this.typeName = type;
        this.parametersListInt.add(createParameterInt(signalEnvelope, ParameterName.SignalEnvelope));
        this.parametersListInt.add(createParameterInt(rootMeanSquareEnergy, ParameterName.RootMeanSquareEnergy));
        this.zeroCrossingDensityCount = 1;
        this.zeroCrossingDensity = zeroCrossingDensity;
        this.parametersListInt.add(createParameterInt(powerSpectrum, ParameterName.PowerSpectrum));
        this.parametersListDouble.add(createParameterDouble(spectralCentroids, ParameterName.SpectralCentroids));
        this.parametersListInt.add(createParameterInt(spectralFluxes, ParameterName.SpectralFluxes));
        this.parametersListDouble.add(createParameterDouble(spectralRolloffPoints, ParameterName.SpectralRollOffPoints));
    }

    private SoundTypeParameterInt createParameterInt(List<Integer> valuesList, ParameterName name) {
        return new SoundTypeParameterInt(valuesList, new ArrayList<Integer>(Collections.nCopies(valuesList.size(), 1)), name);
    }
    private SoundTypeParameterDouble createParameterDouble(List<Double> valuesList, ParameterName name) {
        return new SoundTypeParameterDouble(valuesList, new ArrayList<Integer>(Collections.nCopies(valuesList.size(), 1)), name);
    }

    public SoundTypeParameterInt getParamIntByName(ParameterName name) {
        for (var param:parametersListInt
        ) {
            if(param.name.equals(name))
                return param;
        }
        return null;
    }

    public SoundTypeParameterDouble getParamDoubleByName(ParameterName name) {
        for (var param:parametersListDouble
        ) {
            if(param.name.equals(name))
                return param;
        }
        return null;
    }
    public void setParameterInt(ParameterName name) {
        var param = getParamIntByName(name);
        param.setParameterValues(param.getParameterValuesWeighted());
    }

    public void updateIntParameterValueAdd(ParameterName name) {
        var param = getParamIntByName(name);
        param.calculateNewParamAverageAdd(param.getParameterValuesRaw());
    }

    public void updateIntParameterValueDelete(ParameterName name) {
        var param = getParamIntByName(name);
        param.calculateNewParamAverageDelete(param.getParameterValuesRaw());
    }

    public List<Integer> getParameterIntWeighted(ParameterName name) {
        var param = getParamIntByName(name);
        return param.getParameterValuesWeighted();
    }

    public Integer getParameterIntSize(ParameterName name) {
        var param = getParamIntByName(name);
        return param.getParameterValuesCount().size();
    }

    public void setParameterDouble(ParameterName name) {
        var param = getParamDoubleByName(name);
        param.setParameterValues(param.getParameterValuesWeighted());
    }

    public void updateDoubleParameterValueAdd(ParameterName name) {
        var param = getParamDoubleByName(name);
        param.calculateNewParamAverageAdd(param.getParameterValuesRaw());
    }

    public void updateDoubleParameterValueDelete(ParameterName name) {
        var param = getParamDoubleByName(name);
        param.calculateNewParamAverageDelete(param.getParameterValuesRaw());
    }

    public List<Double> getParameterDoubleWeighted(ParameterName name) {
        var param = getParamDoubleByName(name);
        return param.getParameterValuesWeighted();
    }
    public Integer getParameterDoubleSize(ParameterName name) {
        var param = getParamDoubleByName(name);
        return param.getParameterValuesCount().size();
    }
}