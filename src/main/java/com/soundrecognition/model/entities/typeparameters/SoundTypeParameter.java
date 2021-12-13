package com.soundrecognition.model.entities.typeparameters;

import java.util.List;

public interface SoundTypeParameter {
    List<?> getParameterValuesRaw();
    List<?> getParameterValuesWeighted();
    void setParameterValues(List<?> parameterValues);
    void calculateNewParamAverageAdd(List<?> parametersNew);
    void calculateNewParamAverageDelete(List<?> parametersToRemove);
    Integer getParameterValuesCount();
}
