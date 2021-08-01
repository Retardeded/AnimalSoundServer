package com.quiz.quizapp.sound;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataGraph implements Serializable {

    @ElementCollection
    @Column
    private List<DataPoint> dataPoints;
}