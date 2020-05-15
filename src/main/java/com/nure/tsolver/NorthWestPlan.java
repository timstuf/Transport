package com.nure.tsolver;

import com.nure.tsolver.model.*;

import java.util.Arrays;


public class NorthWestPlan {
    private DistributionCell[][] costMatrix;
    private DistributionParticipants participants;

    /**
     * Construct plan builder from cell matrix and participants.
     *
     * @param costMatrix   matrix with filled costs, its height and width must be equal
     *                     with participants consumer and supplier count.
     * @param participants not empty participants instance.
     */
    public NorthWestPlan(DistributionCell[][] costMatrix, DistributionParticipants participants) {
        this.costMatrix = Arrays.stream(costMatrix).map(DistributionCell[]::clone).toArray(e -> costMatrix.clone());
        this.participants = participants.clone();
    }

    /**
     * Find initial distribution plan using minimal element algorithm. Cells with cheapest distribution
     * cost will be filled first. Product quantity that will be set in cell depend on current demand and supply for
     * this cell.
     *
     * @return built {@code DistributionPlan} where empty cells filled by empty placeholder.
     */
    DistributionPlan findPlan() {
        DistributionParticipants originalParticipants = participants.clone();
        Consumer[] consumers = new Consumer[participants.consumersCount()];
        System.arraycopy(participants.getConsumers(), 0, consumers, 0,consumers.length);
        Supplier[] suppliers = new Supplier[participants.suppliersCount()];
        System.arraycopy(participants.getSuppliers(), 0, suppliers, 0,suppliers.length);
        for (int i = 0; i <costMatrix.length ; i++) {
            for (int j = 0; j < costMatrix[0].length; j++) {
                if(consumers[j].getDemand()==0) continue;
                int min = Math.min(consumers[j].getDemand(), suppliers[i].getSupply());
                costMatrix[i][j].setFullness(min);
                consumers[j].subDemand(min);
                suppliers[i].subSupply(min);
                if(suppliers[i].getSupply()==0) break;
            }
        }
        fillEmptyFullnessByPlaceholder();
        DistributionPlan plan = new DistributionPlan(costMatrix, originalParticipants);

        plan.printPlan();
        return plan;
    }

    /**
     * Fill cells with zero fullness of {@code DistributionCell} matrix by empty placeholder.
     */
    private void fillEmptyFullnessByPlaceholder() {
        for (DistributionCell[] matrix : costMatrix) {
            for (int j = 0; j < costMatrix[0].length; j++) {
                if (matrix[j].getFullness() == 0)
                    matrix[j].setFullnessEmpty();
            }
        }
    }


}
