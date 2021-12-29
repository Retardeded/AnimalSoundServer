package com.soundrecognition.model.entities.typeparameters;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class SoundTypeParameterInt implements Serializable, SoundTypeParameter {
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

        var list = new ArrayList<Integer>();
        for(int i = 0; i < parameterValues.size(); i++) {
            list.add(i,parameterValues.get(i) / parameterValuesCount.get(i));
        }
        return list;
    }

    public void setParameterToWeightedValues() {
        this.parameterValues = getParameterValuesWeighted();
    }

    @ElementCollection
    @Column
    public List<Integer> parameterValuesCount;

    public void updateParameterValueAdd(List<?> parametersNew) {
        int minSize = Math.min(parametersNew.size(), parameterValues.size());
        var parametersNewInt = (List<Integer>) parametersNew;
        for(int i = 0; i < minSize; i++) {
            parameterValues.set(i, (parameterValues.get(i) + parametersNewInt.get(i)));
            parameterValuesCount.set(i, parameterValuesCount.get(i)+1);
        }
        if(minSize == parameterValues.size()) {
            int maxSize = Math.max(parametersNew.size(), parameterValues.size());
            for(int i = minSize; i < maxSize;i++) {
                parameterValues.add(parametersNewInt.get(i));
                parameterValuesCount.add(1);
            }
        }
    }

    public void updateParameterValueDelete(List<?> parametersToRemove) {
        int minSize = Math.min(parameterValues.size(), parametersToRemove.size());
        var parametersToRemoveInt = (List<Integer>) parametersToRemove;
        for(int i = 0; i < minSize; i++) {
            parameterValues.set(i, ((parameterValues.get(i)  - parametersToRemoveInt.get(i) )));
            parameterValuesCount.set(i, parameterValuesCount.get(i)-1);
        }

        while(parameterValuesCount.size() > 0 && parameterValuesCount.get(parameterValuesCount.size()-1) == 0) {
            parameterValues.remove(parameterValuesCount.size()-1);
            parameterValuesCount.remove(parameterValuesCount.size()-1);
        }
    }

    public Integer getParameterValuesCount() {
        return parameterValuesCount.size();
    }
}