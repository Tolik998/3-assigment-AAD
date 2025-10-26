package com.transportation;

import java.util.*;

/**
 * Implementation of Prim's algorithm for finding Minimum Spanning Trees.
 * Uses a priority queue (min-heap) to efficiently select the next edge.
 */
public class PrimMST {

    /**
     * Finds the Minimum Spanning Tree using Prim's algorithm
     * @param graph the input graph
     * @return MST result containing edges, cost, and performance metrics
     */
    public MSTResult findMST(Graph graph) {
        long startTime = System.nanoTime();
        long operations = 0;

        List<Edge> mstEdges = new ArrayList<>();
        int totalCost = 0;

        // Handle empty graph and single vertex graph
        if (graph.getVertexCount() <= 1) {
            long executionTime = (System.nanoTime() - startTime) / 1_000_000;
            return new MSTResult(mstEdges, totalCost, operations, executionTime);
        }

        Set<String> visited = new HashSet<>();
        PriorityQueue<Edge> minHeap = new PriorityQueue<>();

        List<String> vertices = graph.getVertices();
        String startVertex = vertices.get(0);
        visited.add(startVertex);

        // Add initial edges - count only heap operations
        for (Edge edge : graph.getEdges()) {
            if (edge.getFrom().equals(startVertex) || edge.getTo().equals(startVertex)) {
                minHeap.add(edge);
                operations++; // heap insertion O(log E)
            }
        }

        // Main loop - count only heap operations
        while (!minHeap.isEmpty() && visited.size() < graph.getVertexCount()) {
            Edge currentEdge = minHeap.poll();
            operations++; // heap extraction O(log E)

            String nextVertex = null;
            if (visited.contains(currentEdge.getFrom()) && !visited.contains(currentEdge.getTo())) {
                nextVertex = currentEdge.getTo();
            } else if (visited.contains(currentEdge.getTo()) && !visited.contains(currentEdge.getFrom())) {
                nextVertex = currentEdge.getFrom();
            }

            if (nextVertex != null) {
                visited.add(nextVertex);
                mstEdges.add(currentEdge);
                totalCost += currentEdge.getWeight();

                // Add edges from new vertex - count only heap operations
                for (Edge edge : graph.getEdges()) {
                    if ((edge.getFrom().equals(nextVertex) && !visited.contains(edge.getTo())) ||
                            (edge.getTo().equals(nextVertex) && !visited.contains(edge.getFrom()))) {
                        minHeap.add(edge);
                        operations++; // heap insertion O(log E)
                    }
                }
            }
        }

        long executionTime = (System.nanoTime() - startTime) / 1_000_000;

        if (visited.size() != graph.getVertexCount()) {
            System.out.println("Info: Graph is not connected. MST covers " +
                    visited.size() + " out of " + graph.getVertexCount() + " vertices.");
        }

        return new MSTResult(mstEdges, totalCost, operations, executionTime);
    }
}