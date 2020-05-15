package com.nure;

import com.nure.tsolver.model.*;
import com.nure.tsolver.ProductDistributor;

public class App {
    public static void main(String[] args) {
        Consumer[] consumers = {
                new Consumer(8, new MatrixPosition(4, 0)),
                new Consumer(10, new MatrixPosition(4, 1)),
                new Consumer(12, new MatrixPosition(4, 2)),
                new Consumer(8, new MatrixPosition(4, 3)),
                new Consumer(12, new MatrixPosition(4, 4))
        };
        Supplier[] suppliers = {
                new Supplier(9, new MatrixPosition(0, 5)),
                new Supplier(11, new MatrixPosition(1, 5)),
                new Supplier(14, new MatrixPosition(2, 5)),
                new Supplier(16, new MatrixPosition(3, 5))
        };
        DistributionParticipants distributionParticipants = new DistributionParticipants(consumers, suppliers);
        int[][] matrix = {
                {5, 15, 3, 6, 10},
                {23, 8, 13, 27, 12, 11},
                {30, 1, 5, 24, 25},
                {8, 26, 7, 28, 9}
        };
        ProductDistributor productDistributor = new ProductDistributor(matrix, distributionParticipants);
        DistributionPlan plan = productDistributor.distribute();
        System.out.println(plan);
    }
}
