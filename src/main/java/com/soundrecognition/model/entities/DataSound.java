package com.soundrecognition.model.entities;

import com.soundrecognition.model.DataPoint;
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
public class DataSound {

    @Id
    @Column
    @GeneratedValue
    private Integer id;

    public DataSound(Integer id, String title, String type, Long durationMillis, Long pointsInGraphs, Long numOfGraphs, DataSoundParameters dataSoundParameters) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.durationMillis = durationMillis;
        this.pointsInGraphs = pointsInGraphs;
        this.numOfGraphs = numOfGraphs;
        this.dataSoundParameters = dataSoundParameters;
    }

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
}