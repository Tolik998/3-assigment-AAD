package com.transportation;

import java.util.*;

/**
 * Implementation of Kruskal's algorithm for finding Minimum Spanning Trees.
 * Uses union-find data structure to efficiently detect cycles.
 */
public class KruskalMST {

    /**
     * Finds the Minimum Spanning Tree using Kruskal's algorithm
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

        // Sort edges - estimate sorting operations: E * log(E)
        List<Edge> sortedEdges = new ArrayList<>(graph.getEdges());
        long sortOperations = sortedEdges.size() * (long)(Math.log(sortedEdges.size()) / Math.log(2));
        Collections.sort(sortedEdges);
        operations += sortOperations;

        UnionFind uf = new UnionFind(graph.getVertices());

        // Process each edge
        for (Edge edge : sortedEdges) {
            if (mstEdges.size() == graph.getVertexCount() - 1) break;

            // Count find operations (2 per edge)
            operations += 2;
            String root1 = uf.find(edge.getFrom());
            String root2 = uf.find(edge.getTo());

            if (!root1.equals(root2)) {
                mstEdges.add(edge);
                totalCost += edge.getWeight();

                // Count union operation
                operations += 1;
                uf.union(edge.getFrom(), edge.getTo());
            }
        }

        long executionTime = (System.nanoTime() - startTime) / 1_000_000;

        if (mstEdges.size() != graph.getVertexCount() - 1) {
            System.out.println("Info: Graph is not connected. MST covers " +
                    (mstEdges.size() + 1) + " out of " + graph.getVertexCount() + " vertices.");
        }

        return new MSTResult(mstEdges, totalCost, operations, executionTime);
    }

    /**
     * Union-Find (Disjoint Set Union) data structure for cycle detection
     */
    private static class UnionFind {
        private Map<String, String> parent;
        private Map<String, Integer> rank;

        public UnionFind(List<String> vertices) {
            parent = new HashMap<>();
            rank = new HashMap<>();

            for (String vertex : vertices) {
                parent.put(vertex, vertex);
                rank.put(vertex, 0);
            }
        }

        public String find(String x) {
            if (!parent.get(x).equals(x)) {
                parent.put(x, find(parent.get(x))); // Path compression
            }
            return parent.get(x);
        }

        public void union(String x, String y) {
            String rootX = find(x);
            String rootY = find(y);

            if (!rootX.equals(rootY)) {
                // Union by rank
                if (rank.get(rootX) < rank.get(rootY)) {
                    parent.put(rootX, rootY);
                } else if (rank.get(rootX) > rank.get(rootY)) {
                    parent.put(rootY, rootX);
                } else {
                    parent.put(rootY, rootX);
                    rank.put(rootX, rank.get(rootX) + 1);
                }
            }
        }
    }
}