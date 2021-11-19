package com.soundrecognition.model;

import com.soundrecognition.model.DataPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.Comparator;
import java.util.List;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class DataSound implements Comparator<DataPoint> {
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

    @Override
    public int compare(DataPoint a, DataPoint b) {
        var aY = Math.abs(a.y);
        var bY = Math.abs(b.y);
        if (aY > bY)
            return -1; // highest value first
        if (aY == bY)
            return 0;
        return 1;
    }
}