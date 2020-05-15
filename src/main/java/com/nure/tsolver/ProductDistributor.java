package com.nure.tsolver;

import com.nure.tsolver.model.DistributionCell;
import com.nure.tsolver.model.DistributionParticipants;
import com.nure.tsolver.model.DistributionPlan;
import com.nure.tsolver.model.MatrixPosition;

public class ProductDistributor {
    private final DistributionCell[][] distributionCells;
    private final DistributionParticipants distributionParticipants;


    /**
     * Constructs distributor from double cost matrix and build cell matrix inside
     * @param costArray double matrix, its height and width must be equal with participants consumer and supplier count.
     * @param distributionParticipants participants of distribution.
     */
    public ProductDistributor(int[][] costArray, DistributionParticipants distributionParticipants) {
        this.distributionCells = new DistributionCell[distributionParticipants.suppliersCount()][distributionParticipants.consumersCount()];
        for (int i = 0; i < distributionParticipants.suppliersCount(); i++) {
            for (int j = 0; j < distributionParticipants.consumersCount(); j++) {
                distributionCells[i][j] = new DistributionCell(new MatrixPosition(i,j),costArray[i][j]);
            }
        }
        this.distributionParticipants = distributionParticipants;
    }

    /**
     * Entry point of transportation problem solver. First of all minimal element algorithm builds initial plan of distribution.
     * Then potential algorithm improves initial plan by cyclic moves.
     * @return optimal {@code DistributionPlan}.
     */
    public DistributionPlan distribute()  {
        //NorthWestPlan nortWestPlan = new NorthWestPlan(distributionCells, distributionParticipants);
        MinElementPlanSolver minElementPlanSolver = new MinElementPlanSolver(distributionCells, distributionParticipants);
        DistributionPlan firstDistributionPlan = minElementPlanSolver.findPlan();
        //DistributionPlan firstDistributionPlan = nortWestPlan.findPlan();
        PotentialPlanSolver potentialPlanSolver = new PotentialPlanSolver(firstDistributionPlan);
        DistributionPlan optimalPlan = potentialPlanSolver.findOptimalPlan();
        optimalPlan.clearEmptyFullness();
        return optimalPlan;
    }
}
