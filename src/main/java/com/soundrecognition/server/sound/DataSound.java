package com.soundrecognition.server.sound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
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

    public Long getPointsInGraphs() {
        return pointsInGraphs;
    }

    @NotBlank
    @Column
    private Long pointsInGraphs;

    public Long getNumOfGraphs() {
        return numOfGraphs;
    }

    @NotBlank
    @Column
    private Long numOfGraphs;


    public void setFreqDomainPoints(List<DataPoint> dataPoints) {
        this.freqDomainPoints = dataPoints;
    }

    @ElementCollection
    @Column
    private List<DataPoint> freqDomainPoints;

    public void setTimeDomainPoints(List<DataPoint> dataPoints) {
        this.timeDomainPoints = dataPoints;
    }

    @ElementCollection
    @Column
    private List<DataPoint> timeDomainPoints;
}