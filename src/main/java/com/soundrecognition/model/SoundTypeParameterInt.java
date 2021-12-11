package com.soundrecognition.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.sql.In;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class SoundTypeParameterInt implements Serializable {
    public SoundTypeParameterInt(List<Integer> parameterValues, List<Integer> parameterValuesCount, SoundTypeParameters.ParameterName name) {
        this.parameterValues = parameterValues;
        this.parameterValuesCount = parameterValuesCount;
        this.name = name;
    }

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    public SoundTypeParameters.ParameterName name;

    @ElementCollection
    @Column
    private List<Integer> parameterValues;

    public List<Integer> getParameterValuesRaw() {

        return parameterValues;
    }

    public List<Integer> getParameterValuesWeighted() {
        for(int i = 0; i < parameterValues.size(); i++) {
            parameterValues.set(i,parameterValues.get(i) / parameterValuesCount.get(i));
        }
        return parameterValues;
    }

    public void setParameterValues(List<Integer> parameterValues) {
        this.parameterValues = parameterValues;
    }

    @ElementCollection
    @Column
    public List<Integer> parameterValuesCount;

    public void calculateNewParamAverageAdd(List<Integer> parametersNew) {
        int minSize = Math.min(parametersNew.size(), parameterValues.size());
        for(int i = 0; i < minSize; i++) {
            parameterValues.set(i, (parameterValues.get(i) + parametersNew.get(i)));
            parameterValuesCount.set(i, parameterValuesCount.get(i)+1);
        }
        if(minSize == parameterValues.size()) {
            int maxSize = Math.max(parametersNew.size(), parameterValues.size());
            for(int i = minSize; i < maxSize;i++) {
                parameterValues.add(parametersNew.get(i));
                parameterValuesCount.add(1);
            }
        }
    }

    public void calculateNewParamAverageDelete(List<Integer> parametersSound) {
        int minSize = Math.min(parameterValues.size(), parametersSound.size());
        for(int i = 0; i < minSize; i++) {
            parameterValues.set(i, ((parameterValues.get(i)  - parametersSound.get(i) )));
            parameterValuesCount.set(i, parameterValuesCount.get(i)-1);
        }

        while(parameterValuesCount.get(parameterValues.size()-1) == 0) {
            parameterValues.remove(parameterValuesCount.size()-1);
            parameterValuesCount.remove(parameterValuesCount.size()-1);
        }
    }
}