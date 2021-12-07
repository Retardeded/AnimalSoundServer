package com.soundrecognition.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Comparator;
import java.util.List;


@Table
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DataSound implements Comparator<DataPoint> {

    @Id
    @Column
    @GeneratedValue
    private Integer id;

    @Column
    private String title;

    @Column
    private String type;

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

    //@JoinColumn()
    //@OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    //private DataSoundParameters dataSoundParameters;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DataSoundParameters dataSoundParameters;

    public void setTimeDomainPoints(List<DataPoint> dataPoints) {
        this.timeDomainPoints = dataPoints;
    }


    public List<DataPoint> getTimeDomainPoints() {
        return timeDomainPoints;
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