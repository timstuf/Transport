package com.nure.tsolver.model;

import lombok.*;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class DistributionCell {
    static final int EMPTY_FULLNESS_PLACEHOLDER = -1;

    private final MatrixPosition position;
    @Getter
    private final int tariffCost;
    @Getter
    @Setter
    private int fullness;
    @Getter
    @Setter
    private int potentialSum;



    public boolean isFullnessNull(){
        return fullness == EMPTY_FULLNESS_PLACEHOLDER;
    }

    public void setFullnessEmpty(){
        fullness = EMPTY_FULLNESS_PLACEHOLDER;
    }

    public int getX(){
        return position.getX();
    }

    public int getY(){
        return position.getY();
    }

}
