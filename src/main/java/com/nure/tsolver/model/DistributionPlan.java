package com.nure.tsolver.model;

import lombok.Getter;
import lombok.ToString;

@ToString
public class DistributionPlan {
    @Getter
    private int height;
    @Getter
    private int width;

    private DistributionCell[][] plan;

    private DistributionParticipants participants;

    public DistributionParticipants getParticipants() {
        return participants;
    }


    /**
     * Constructs new instance from pre-defined distribution cell matrix and participants.
     *
     * @param cells        its height and width must be equal
     *                     with participants consumer and supplier count.
     * @param participants participants of distribution.
     */
    public DistributionPlan(DistributionCell[][] cells, DistributionParticipants participants) {
        this.plan = cells;
        this.height = participants.suppliersCount();
        this.width = participants.consumersCount();
        this.participants = participants;
    }


    /**
     * Gets cell by matrix coordinates. May throw {@code IllegalArgumentException} if i or j out of bounds.
     *
     * @param i x (height) coordinate of matrix.
     * @param j y (width) coordinate of matrix.
     * @return the cell the at the specified position in this plan
     */
    public DistributionCell getCell(int i, int j) {
        if (i < 0 || j < 0 || i >= height || j >= width)
            throw new IllegalArgumentException("Array index must be positive integer and be in bounds of height and width of plan");
        return plan[i][j];
    }

    /**
     * Clears fullness with empty placeholder to zero.
     */
    public void clearEmptyFullness() {
        for (DistributionCell[] distributionCells : plan) {
            for (int j = 0; j < plan[0].length; j++) {
                if (distributionCells[j].getFullness() == DistributionCell.EMPTY_FULLNESS_PLACEHOLDER)
                    distributionCells[j].setFullness(0);
            }
        }
    }

    public void countMoney() {
        int sum = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (plan[i][j].isFullnessNull()) continue;
                System.out.print(plan[i][j].getTariffCost() + " * " + plan[i][j].getFullness() + " + ");
                sum += plan[i][j].getTariffCost() * plan[i][j].getFullness();
            }
        }
        System.out.println("= "+sum);
    }

    public void printPlan() {
        System.out.println("Опорный план, построенный методом северно-западного угла");
        for (int i = 0; i < height; i++) {
            System.out.println("----------------------------------------------------------");
            for (int j = 0; j < width; j++) {
                System.out.print(String.format("%4s", "-"));
                System.out.print(",");
                System.out.print(String.format("%4s", plan[i][j].getTariffCost()));
                System.out.print("|");
            }
            System.out.printf("%d", participants.getSuppliers()[i].getSupply());
            System.out.println();
            for (int j = 0; j < width; j++) {
                System.out.print(String.format("%4s", plan[i][j].getFullness() == DistributionCell.EMPTY_FULLNESS_PLACEHOLDER ? 0 : plan[i][j].getFullness()));
                System.out.print(",");
                System.out.print(String.format("%4s", "-"));
                System.out.print("|");
            }
            System.out.println();
        }
        for (int i = 0; i < width; i++) {
            System.out.print(String.format("%-11s", participants.getConsumers()[i].getDemand()));
        }
        System.out.println();
        System.out.println("----------------------------------------------------------");

    }

    public void printPlan(PotentialArray v, PotentialArray u) {
        for (int i = 0; i < height; i++) {
            System.out.println("----------------------------------------------------------");
            for (int j = 0; j < width; j++) {
                System.out.print(String.format("%4s", u.get(j) + v.get(i)));
                System.out.print(",");
                System.out.print(String.format("%4s", plan[i][j].getTariffCost()));
                System.out.print("|");
            }
            System.out.println(v.get(i));
            for (int j = 0; j < width; j++) {
                System.out.print(String.format("%4s", plan[i][j].getFullness() == DistributionCell.EMPTY_FULLNESS_PLACEHOLDER ? 0 : plan[i][j].getFullness()));
                System.out.print(",");
                System.out.print(String.format("%4s", plan[i][j].getTariffCost() - u.get(j) - v.get(i)));
                System.out.print("|");
            }
            System.out.println();
        }
        for (int i = 0; i < width; i++) {
            System.out.print(String.format("%-12s", u.get(i)));
        }
        System.out.println();
        System.out.println("----------------------------------------------------------");
    }
}
