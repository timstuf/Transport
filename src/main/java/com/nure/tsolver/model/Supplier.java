package com.nure.tsolver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Supplier {
    private int supply;
    private final MatrixPosition matrixPosition;

    public void subSupply(int supply) {
        this.supply -= supply;
    }
}
