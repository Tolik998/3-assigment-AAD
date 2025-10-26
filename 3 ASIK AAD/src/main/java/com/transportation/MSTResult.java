package com.transportation;

import java.util.List;

/**
 * Represents the result of a Minimum Spanning Tree computation.
 */
public class MSTResult {
    private List<Edge> mstEdges;
    private int totalCost;
    private long operationsCount;
    private long executionTime;

    public MSTResult(List<Edge> mstEdges, int totalCost, long operationsCount, long executionTime) {
        this.mstEdges = mstEdges;
        this.totalCost = totalCost;
        this.operationsCount = operationsCount;
        this.executionTime = executionTime;
    }

    // Getters
    public List<Edge> getMstEdges() { return mstEdges; }
    public int getTotalCost() { return totalCost; }
    public long getOperationsCount() { return operationsCount; }
    public long getExecutionTime() { return executionTime; }
}