package com.quiz.quizapp.sound;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class DataSound {
    @Id
    @Column
    @GeneratedValue
    private Integer id;

    @NotBlank
    @Column
    private String title;

    @NotBlank
    @Column
    private Long durationMillis;

    @Column
    @ElementCollection
    private List<DataGraph> dataGraphs;
}