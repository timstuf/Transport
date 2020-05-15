package com.nure.tsolver.model;

import java.util.Arrays;

public class PotentialArray {
    private int[] array;
    public static final int NULL_PLACEHOLDER = -Integer.MAX_VALUE;

    public PotentialArray(int size) {
        this.array = new int[size];
        Arrays.fill(this.array, NULL_PLACEHOLDER);
    }

    public int get(int index) {
        indexCheck(index);
        return array[index];
    }

    public void set(int index, int value) {
        indexCheck(index);
        array[index] = value;
    }

    public boolean isNull(int index) {
        indexCheck(index);
        return array[index] == NULL_PLACEHOLDER;
    }

    private void indexCheck(int index) {
        if (index < 0)
            throw new IllegalArgumentException("Array index must be positive integer");
    }

    public void clear() {
        Arrays.fill(array, NULL_PLACEHOLDER);
    }

    public int findIndexOfNull() {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == NULL_PLACEHOLDER)
                return i;
        }
        return -1;
    }
}
