package com.nure.tsolver;

import com.nure.tsolver.model.DistributionCell;
import com.nure.tsolver.model.DistributionParticipants;
import com.nure.tsolver.model.DistributionPlan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CycleMover {
    private DistributionPlan potentialPlan;
    private DistributionParticipants participants;

    /**
     * Constructs mover for specified potential plan with participants.
     *
     * @param potentialPlan distribution plan where cycle will be build.
     * @param participants  distribution participants.
     */
    public CycleMover(DistributionPlan potentialPlan, DistributionParticipants participants) {
        this.potentialPlan = potentialPlan;
        this.participants = participants;
    }

    /**
     * Start cycle building from specified cell using 2 ways: by row and by column. If all ways is empty,
     * because build cycle with current cycle basis is impossible, algorithm add new random cell to basis and
     * try to build cycle again.
     * After that it moves products by built cycle.
     * @param cell max potential cell from distribution plan.
     */
    void cycle(DistributionCell cell) {
        List<DistributionCell> usedPositions = new ArrayList<>();
        Optional<List<DistributionCell>> firstWay = Optional.empty();
        Optional<List<DistributionCell>> secondWay = Optional.empty();

        if (checkRowOnFilling(cell.getX()) > 0) {
            firstWay = makeCycle(cell, usedPositions, true);
        }
        cell.setFullnessEmpty();
        usedPositions = new ArrayList<>();
        if (checkColumnOnFilling(cell.getY()) > 0) {
            secondWay = makeCycle(cell, usedPositions, false);
        } else
            System.out.println("Can't build cycle");
        cell.setFullnessEmpty();

        if (!firstWay.isPresent() && !secondWay.isPresent()) {
            System.out.println("Can't build cycle");
            //firstWay = addBasisCellAndRebuildCycle(usedPositions, cell);
        }


        if ((!firstWay.isPresent() && secondWay.isPresent()))
            firstWay = secondWay;
        else if(firstWay.isPresent() && secondWay.isPresent() && firstWay.get().size() > secondWay.get().size())
            firstWay = secondWay;

        firstWay.ifPresent(way-> moveProductsUsingCycle(way, cell));

    }

    /**
     * Recursive cycle path finder, that can go by row and column, depend on {@code isRow} parameter.
     * @param position starting distribution basis cell.
     * @param used list of used cells in cycle path, used for recursion.
     * @param isRow next move direction parameter.
     * @return optional of corner distribution cells used in path.
     */
    private Optional<List<DistributionCell>> makeCycle(DistributionCell position, List<DistributionCell> used, boolean isRow) {
        used.add(position);
        if (used.size() == 1)
            used.get(0).setFullness(1);

        if (used.size() > 3 && used.get(used.size() - 1) == used.get(0))
            return Optional.of(used);
        if (isRow) {
            for (int i = 0; i < participants.consumersCount(); i++) {
                if (!potentialPlan.getCell(position.getX(), i).isFullnessNull()
                        && potentialPlan.getCell(position.getX(), i) != position) {
                    if (used.size() > 3 && used.get(0) == potentialPlan.getCell(position.getX(), i)) {
                        used.add(potentialPlan.getCell(position.getX(), i));
                        return Optional.of(used);
                    } else {
                        if (!used.subList(1, used.size()).contains(potentialPlan.getCell(position.getX(), i))
                                && checkColumnOnFilling(i) > 0) {

                            List<DistributionCell> newArray = new ArrayList<>(used);
                            Optional<List<DistributionCell>> recursiveRes =
                                    makeCycle(potentialPlan.getCell(position.getX(), i), newArray, false);
                            if (recursiveRes.isPresent()) {
                                return recursiveRes;
                            }
                        }
                    }

                }
            }
        } else {
            for (int i = 0; i < participants.suppliersCount(); i++) {
                if (!potentialPlan.getCell(i, position.getY()).isFullnessNull()
                        && potentialPlan.getCell(i, position.getY()) != position) {
                    if (used.size() > 3 && potentialPlan.getCell(i, position.getY()) == used.get(0)) {
                        used.add(potentialPlan.getCell(i, position.getY()));
                        return Optional.of(used);
                    } else {
                        if (!used.subList(1, used.size()).contains(potentialPlan.getCell(i, position.getY()))
                                && checkRowOnFilling(i) > 0) {

                            List<DistributionCell> newArray = new ArrayList<>(used);
                            Optional<List<DistributionCell>> recursiveRes =
                                    makeCycle(potentialPlan.getCell(i, position.getY()), newArray, true);
                            if (recursiveRes.isPresent()) {
                                return recursiveRes;
                            }
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Method that adds to basis random empty distribution cell using shuffle.
     * @param usedPositions list of used cells in cycle path, used for down {@code makeCycle} recursion.
     * @param cell starting distribution cell.
     * @return optional of corner distribution cells used in path.
     */

    /**
     * Method that moves product using found cycle way.
     * @param way list of corner of distribution cycle way.
     * @param cell starting distribution basis cell.
     */
    private void moveProductsUsingCycle(List<DistributionCell> way, DistributionCell cell) {
        DistributionCell minCost = way.get(1);
        for (int i = 2; i < way.size(); i++) {
            if (i % 2 != 0 && way.get(i).getFullness() < minCost.getFullness())
                minCost = way.get(i);
        }
        cell.setFullness(0);
        int minCostValue = minCost.getFullness();
        for (int i = 0; i < way.size() - 1; i++) {
            if (i % 2 == 0) {
                way.get(i).setFullness(way.get(i).getFullness() + minCostValue);
            } else {
                way.get(i).setFullness(way.get(i).getFullness() - minCostValue);
                if (way.get(i).getFullness() == 0)
                    way.get(i).setFullnessEmpty();
            }
        }
    }

    /**
     * Used cells in row counter for cycle mover.
     * @param row row index, starting from 0.
     * @return count of cycle corners in row.
     */
    private int checkRowOnFilling(int row) {
        int count = 0;
        for (int i = 0; i < potentialPlan.getWidth(); i++) {
            if (!potentialPlan.getCell(row, i).isFullnessNull()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Used cells in column counter for cycle mover.
     * @param column column index, starting from 0.
     * @return count of cycle corners in column.
     */
    private int checkColumnOnFilling(int column) {
        int count = 0;
        for (int i = 0; i < potentialPlan.getHeight(); i++) {
            if (!potentialPlan.getCell(i, column).isFullnessNull())
                count++;
        }
        return count;
    }

    /**
     * Find all cells with empty fullness in distribution plan, except {@code forbiddenCell}
     * @param minForbiddenCell exception cell.
     * @return list of all empty cells in plan.
     */
    private List<DistributionCell> findEmptyCells(DistributionCell minForbiddenCell) {
        List<DistributionCell> resultList = new ArrayList<>();
        for (int i = 0; i < participants.suppliersCount(); i++) {
            for (int j = 0; j < participants.consumersCount(); j++) {
                if (potentialPlan.getCell(i, j) != minForbiddenCell && potentialPlan.getCell(i, j).isFullnessNull())
                    resultList.add(potentialPlan.getCell(i, j));
            }
        }
        return resultList;
    }
}
