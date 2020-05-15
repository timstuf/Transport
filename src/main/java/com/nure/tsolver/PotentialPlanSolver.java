package com.nure.tsolver;

import com.nure.tsolver.model.DistributionCell;
import com.nure.tsolver.model.DistributionParticipants;
import com.nure.tsolver.model.DistributionPlan;
import com.nure.tsolver.model.PotentialArray;

public class PotentialPlanSolver {
    private DistributionPlan distributionPlan;
    private DistributionParticipants participants;
    private CycleMover cycleMover;

    private PotentialArray uArray;
    private PotentialArray vArray;

    private int potentialErrorCounter = 0;

    /**
     * Construct plan solver from initial distribution plan created by {@code MinElementPlanSolver}
     *
     * @param firstDistributionPlan not empty distribution plan
     */
    public PotentialPlanSolver(DistributionPlan firstDistributionPlan) {
        this.distributionPlan = firstDistributionPlan;
        this.participants = firstDistributionPlan.getParticipants();
        this.cycleMover = new CycleMover(distributionPlan, participants);

        this.uArray = new PotentialArray(participants.consumersCount());
        this.vArray = new PotentialArray(participants.suppliersCount());
    }

    /**
     * Entry point of plan optimization. First of all algorithm checks is distribution plan
     * one dimensional, because one dimensional plan after {@code MinElementPlanSolver} will be always
     * optimal. Then it calculates potentials sums and takes bigger one. It must perform cycle moves of
     * products until the biggest sum be grater than zero. After that plan will be optimal.
     *
     * Warning: sometimes two equal initial plans after optimization will not be equal by cell distribution, but them
     * always equal by full plan cost.
     * @return optimized distribution plan. Empty cell fullness is filled by empty placeholder.
     */
    public DistributionPlan findOptimalPlan() {

        if(isStartPlanOneColumnOrOneRow())
            return distributionPlan;

        makePotentials();
        int iterations = 1;
        DistributionCell maxPotentialCell = findMaxPotentialSum();
        while (maxPotentialCell.getPotentialSum() > 0) {

//            System.out.println("Max potential before cycle:" + maxPotentialCell.getPotentialSum() +
//                    " on [" + maxPotentialCell.getX()+ "," + maxPotentialCell.getY()+"]");


            cycleMover.cycle(maxPotentialCell);

            makePotentials();
            maxPotentialCell = findMaxPotentialSum();

//            System.out.println("Max potential after cycle:" + maxPotentialCell.getPotentialSum() +
//                    " on [" + maxPotentialCell.getX()+ "," + maxPotentialCell.getY()+"]");

            System.out.println("Iteration "+iterations++);
            distributionPlan.printPlan(vArray, uArray);
            uArray.clear();
            vArray.clear();
        }

        distributionPlan.countMoney();
        return distributionPlan;
    }

    /**
     * Internal check for one-dimensional plan
     * @return true if plan one-dimensional.
     */
    private boolean isStartPlanOneColumnOrOneRow() {
        return distributionPlan.getHeight()==1 || distributionPlan.getWidth() == 1;
    }

    /**
     * Method that calculates potentials for each row and column. It fill potential arrays until there
     * is at least one empty cell or potential conflict occurred.
     */
    private void makePotentials() {
        uArray.set(0, 0);
        int iterations = 0;
        while (uArray.findIndexOfNull() > -1 || vArray.findIndexOfNull() > -1) {
            for (int i = 0; i < participants.suppliersCount(); i++) {
                for (int j = 0; j < participants.consumersCount(); j++) {
                    if (!distributionPlan.getCell(i,j).isFullnessNull()) {
                        if (!uArray.isNull(j) && vArray.isNull(i)) {
                            vArray.set(i, distributionPlan.getCell(i,j).getTariffCost() - uArray.get(j));
                        } else if (uArray.isNull(j) && !vArray.isNull(i)) {
                            uArray.set(j, distributionPlan.getCell(i,j).getTariffCost() - vArray.get(i));
                        }
                    }
                }
            }
            iterations++;
            if (iterations > 20_000) {
                potentialErrorCounter++;
                if (potentialErrorCounter > 5)
                    System.out.println("Potentials conflicting errors " + potentialErrorCounter);
                System.out.println("Potentials conflict detected, resolving...");
                int uElementNullIndex = uArray.findIndexOfNull();
                int vElementNullIndex = vArray.findIndexOfNull();
                if (uElementNullIndex > -1) {
                    uArray.set(uElementNullIndex, 0);
                } else if (vElementNullIndex > -1) {
                    vArray.set(vElementNullIndex, 0);
                }
                iterations = 0;
            }
        }
    }

    /**
     * Finds cell with maximum potential sum.
     * @return cell with max potential sum or null if all cells not empty.
     */
    private DistributionCell findMaxPotentialSum() {
        double maxElementSum = Integer.MIN_VALUE;

        DistributionCell maxCell = null;
        for (int i = 0; i < participants.suppliersCount(); i++) {
            for (int j = 0; j < participants.consumersCount(); j++) {
                if (distributionPlan.getCell(i,j).isFullnessNull()) {
                    distributionPlan.getCell(i,j).setPotentialSum(
                            uArray.get(j) + vArray.get(i) - distributionPlan.getCell(i,j).getTariffCost());
                    if (distributionPlan.getCell(i,j).getPotentialSum() > maxElementSum) {
                        maxElementSum = distributionPlan.getCell(i,j).getPotentialSum();
                        maxCell = distributionPlan.getCell(i,j);
                    }
                }
            }
        }
        return maxCell;
    }
}
