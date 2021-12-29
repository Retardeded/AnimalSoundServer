package com.soundrecognition.model.entities.typeparameters;

import java.util.List;

public interface SoundTypeParameter {
    List<?> getParameterValuesRaw();
    List<?> getParameterValuesWeighted();
    void setParameterToWeightedValues();
    void updateParameterValueAdd(List<?> parametersNew);
    void updateParameterValueDelete(List<?> parametersToRemove);
    Integer getParameterValuesCount();
}
