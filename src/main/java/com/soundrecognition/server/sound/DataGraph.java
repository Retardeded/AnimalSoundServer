package com.soundrecognition.server.sound;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataGraph implements Serializable {

    private List<DataPoint> dataPoints;
}