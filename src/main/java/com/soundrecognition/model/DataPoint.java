package com.soundrecognition.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DataPoint implements Serializable {
    private static final long serialVersionUID = 1428263322645L;
    public double x;
    public double y;

}