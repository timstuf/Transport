package com.nure.tsolver.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class DistributionParticipants {
    @Getter
    private final Consumer[] consumers;
    @Getter
    private final Supplier[] suppliers;

    public int consumersCount(){
        return consumers.length;
    }

    public int suppliersCount(){
        return suppliers.length;
    }

    public DistributionParticipants clone(){
        return new DistributionParticipants(
                Arrays.stream(consumers).map(c->new Consumer(c.getDemand(),c.getMatrixPosition())).toArray(Consumer[]::new),
                Arrays.stream(suppliers).map(s->new Supplier(s.getSupply(),s.getMatrixPosition())).toArray(Supplier[]::new)
        );
    }
    public int getConsumerDemand(int index){
        checkConsumerIndex(index);
        return consumers[index].getDemand();
    }

    public int getSupplierSupply(int index){
        checkSupplierIndex(index);
        return suppliers[index].getSupply();
    }
    public void setConsumerDemand(int index, int demand){
        checkConsumerIndex(index);
        consumers[index].setDemand(demand);
    }

    public void setSupplierSupply(int index, int supply){
        checkSupplierIndex(index);
        suppliers[index].setSupply(supply);
    }
    private void checkSupplierIndex(int index){
        if(index<0 || index>=suppliersCount())
            throw new IllegalArgumentException("Supplier array index must be positive integer and be less than count.");
    }

    private void checkConsumerIndex(int index){
        if(index<0 || index>=consumersCount())
            throw new IllegalArgumentException("Consumer array index must be positive integer and be less than count.");
    }
}
