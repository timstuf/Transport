package com.nure.tsolver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Consumer {
    private int demand;
    private final MatrixPosition matrixPosition;
    public void subDemand(int demand){
        this.demand-=demand;
    }
}
