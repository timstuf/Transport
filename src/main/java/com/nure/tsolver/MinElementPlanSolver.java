package com.nure.tsolver;

import com.nure.tsolver.model.DistributionCell;
import com.nure.tsolver.model.DistributionParticipants;
import com.nure.tsolver.model.DistributionPlan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class that contain logic for building initial distribution plan using minimal element algorithm
 */
public class MinElementPlanSolver {
    private DistributionCell[][] costMatrix;
    private DistributionParticipants participants;

    /**
     * Construct plan builder from cell matrix and participants.
     *
     * @param costMatrix   matrix with filled costs, its height and width must be equal
     *                     with participants consumer and supplier count.
     * @param participants not empty participants instance.
     */
    public MinElementPlanSolver(DistributionCell[][] costMatrix, DistributionParticipants participants) {
        this.costMatrix = Arrays.stream(costMatrix).map(DistributionCell[]::clone).toArray(e -> costMatrix.clone());
        this.participants = participants.clone();
    }

    /**
     * Find initial distribution plan using minimal element algorithm. Cells with cheapest distribution
     * cost will be filled first. Product quantity that will be set in cell depend on current demand and supply for
     * this cell.
     * @return built {@code DistributionPlan} where empty cells filled by empty placeholder.
     */
    public DistributionPlan findPlan() {
        DistributionParticipants originalParticipants = participants.clone();
        List<DistributionCell> cells = new ArrayList<>();
        for (int i = 0; i < costMatrix.length; i++) {
            cells.addAll(Arrays.asList(costMatrix[i]));
        }

        while (cells.size() > 0) {
            DistributionCell cell = findLowCostCell(cells);
            if (participants.getSupplierSupply(cell.getX()) > 0 && participants.getConsumerDemand(cell.getY()) > 0) {
                if (participants.getConsumerDemand(cell.getY()) < participants.getSupplierSupply(cell.getX())) {
                    subtractAllConsume(cell);
                } else {
                    subtractAllSupply(cell);
                }
            }
            cells.remove(cell);
        }

        fillEmptyFullnessByPlaceholder();

        return new DistributionPlan(costMatrix, originalParticipants);
    }

    /**
     * Subtracts all available participant demand, set it as zero and updates participant supply.
     *
     * @param cell
     */
    private void subtractAllConsume(DistributionCell cell) {
        cell.setFullness(participants.getConsumerDemand(cell.getY()));
        participants.setSupplierSupply(cell.getX(),
                participants.getSupplierSupply(cell.getX()) - participants.getConsumerDemand(cell.getY()));
        participants.setConsumerDemand(cell.getY(), 0);
    }

    /**
     * Subtracts all available participant supply, set it as zero and updates participant demand.
     *
     * @param cell
     */
    private void subtractAllSupply(DistributionCell cell) {
        cell.setFullness(participants.getSupplierSupply(cell.getX()));
        participants.setConsumerDemand(cell.getY(),
                participants.getConsumerDemand(cell.getY()) - participants.getSupplierSupply(cell.getX()));
        participants.setSupplierSupply(cell.getX(), 0);
    }

    /**
     * Find cell in list with lowest distribution price.
     * @param list not empty list with cells.
     * @return cell with lowest distribution price.
     */
    private DistributionCell findLowCostCell(List<DistributionCell> list) {
        DistributionCell minCell = list.get(0);
        for (DistributionCell distributionCell : list) {
            if (distributionCell.getTariffCost() < minCell.getTariffCost()) {
                minCell = distributionCell;
            }
        }
        return minCell;
    }

    /**
     * Fill cells with zero fullness of {@code DistributionCell} matrix by empty placeholder.
     */
    private void fillEmptyFullnessByPlaceholder() {
        for (int i = 0; i < costMatrix.length; i++) {
            for (int j = 0; j < costMatrix[0].length; j++) {
                if (costMatrix[i][j].getFullness() == 0)
                    costMatrix[i][j].setFullnessEmpty();
            }
        }
    }
}

