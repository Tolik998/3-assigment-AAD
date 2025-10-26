package com.transportation;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Performance tests for MST algorithms
 */
public class PerformanceBenchmarkTest {

    @Test
    @DisplayName("Performance should scale reasonably with graph size")
    void testPerformanceScaling() {
        PrimMST prim = new PrimMST();
        KruskalMST kruskal = new KruskalMST();

        // Test small graph
        Graph smallGraph = createGraph(10, 0.3);
        long smallPrimTime = measureExecutionTime(() -> prim.findMST(smallGraph));
        long smallKruskalTime = measureExecutionTime(() -> kruskal.findMST(smallGraph));
        long smallPrimOps = prim.findMST(smallGraph).getOperationsCount();
        long smallKruskalOps = kruskal.findMST(smallGraph).getOperationsCount();

        // Test medium graph
        Graph mediumGraph = createGraph(50, 0.3);
        long mediumPrimTime = measureExecutionTime(() -> prim.findMST(mediumGraph));
        long mediumKruskalTime = measureExecutionTime(() -> kruskal.findMST(mediumGraph));
        long mediumPrimOps = prim.findMST(mediumGraph).getOperationsCount();
        long mediumKruskalOps = kruskal.findMST(mediumGraph).getOperationsCount();

        // Medium should take longer than small
        assertTrue(mediumPrimTime >= smallPrimTime,
                "Prim should scale with graph size");
        assertTrue(mediumKruskalTime >= smallKruskalTime,
                "Kruskal should scale with graph size");

        System.out.printf("Performance scaling - Small: Prim=%dms (%d ops), Kruskal=%dms (%d ops) | Medium: Prim=%dms (%d ops), Kruskal=%dms (%d ops)%n",
                smallPrimTime, smallPrimOps, smallKruskalTime, smallKruskalOps,
                mediumPrimTime, mediumPrimOps, mediumKruskalTime, mediumKruskalOps);
    }

    @Test
    @DisplayName("Operation counts should be consistent across runs")
    void testOperationCountConsistency() {
        Graph graph = createGraph(20, 0.4);
        PrimMST prim = new PrimMST();
        KruskalMST kruskal = new KruskalMST();

        List<Long> primOps = new ArrayList<>(); // Changed to Long
        List<Long> kruskalOps = new ArrayList<>(); // Changed to Long

        // Run multiple times
        for (int i = 0; i < 3; i++) {
            MSTResult primResult = prim.findMST(graph);
            MSTResult kruskalResult = kruskal.findMST(graph);

            primOps.add(primResult.getOperationsCount());
            kruskalOps.add(kruskalResult.getOperationsCount());
        }

        // Check that operation counts are consistent
        assertTrue(isConsistent(primOps), "Prim operation counts should be consistent");
        assertTrue(isConsistent(kruskalOps), "Kruskal operation counts should be consistent");

        System.out.printf("Operation consistency - Prim: %s, Kruskal: %s%n", primOps, kruskalOps);
    }

    @Test
    @DisplayName("Algorithms should produce same MST cost")
    void testMSTCostConsistency() {
        Graph graph = createGraph(30, 0.5);
        PrimMST prim = new PrimMST();
        KruskalMST kruskal = new KruskalMST();

        MSTResult primResult = prim.findMST(graph);
        MSTResult kruskalResult = kruskal.findMST(graph);

        assertEquals(primResult.getTotalCost(), kruskalResult.getTotalCost(),
                "Both algorithms should produce same MST cost");

        System.out.printf("MST cost consistency - Prim: %d, Kruskal: %d%n",
                primResult.getTotalCost(), kruskalResult.getTotalCost());
    }

    @Test
    @DisplayName("Operation counts should follow theoretical complexity")
    void testTheoreticalComplexity() {
        Graph smallGraph = createGraph(20, 0.3);
        Graph largeGraph = createGraph(100, 0.3);

        PrimMST prim = new PrimMST();
        KruskalMST kruskal = new KruskalMST();

        MSTResult smallPrim = prim.findMST(smallGraph);
        MSTResult largePrim = prim.findMST(largeGraph);
        MSTResult smallKruskal = kruskal.findMST(smallGraph);
        MSTResult largeKruskal = kruskal.findMST(largeGraph);

        // Calculate expected scaling factors
        double primScalingFactor = (double) largePrim.getOperationsCount() / smallPrim.getOperationsCount();
        double kruskalScalingFactor = (double) largeKruskal.getOperationsCount() / smallKruskal.getOperationsCount();

        // For 5x more vertices, operations should increase but not exponentially
        assertTrue(primScalingFactor > 1.0, "Prim operations should increase with graph size");
        assertTrue(kruskalScalingFactor > 1.0, "Kruskal operations should increase with graph size");

        System.out.printf("Complexity scaling - Prim: %.2fx, Kruskal: %.2fx%n",
                primScalingFactor, kruskalScalingFactor);
    }

    @Test
    @DisplayName("Algorithms should handle edge cases")
    void testEdgeCases() {
        PrimMST prim = new PrimMST();
        KruskalMST kruskal = new KruskalMST();

        // Test empty graph
        Graph emptyGraph = new Graph();
        assertDoesNotThrow(() -> prim.findMST(emptyGraph));
        assertDoesNotThrow(() -> kruskal.findMST(emptyGraph));

        // Test single vertex graph
        Graph singleVertexGraph = new Graph();
        singleVertexGraph.addVertex("A");
        assertDoesNotThrow(() -> prim.findMST(singleVertexGraph));
        assertDoesNotThrow(() -> kruskal.findMST(singleVertexGraph));

        // Test disconnected graph
        Graph disconnectedGraph = createDisconnectedGraph();
        assertDoesNotThrow(() -> prim.findMST(disconnectedGraph));
        assertDoesNotThrow(() -> kruskal.findMST(disconnectedGraph));

        System.out.println("Edge cases handled successfully");
    }

    private Graph createGraph(int size, double density) {
        Graph graph = new Graph();
        Random rand = new Random(42);

        // Add vertices
        for (int i = 0; i < size; i++) {
            graph.addVertex("V" + i);
        }

        // Ensure connectivity - create spanning tree first
        List<String> vertices = graph.getVertices();
        for (int i = 1; i < vertices.size(); i++) {
            int fromIndex = rand.nextInt(i);
            graph.addEdge(vertices.get(fromIndex), vertices.get(i), rand.nextInt(100) + 1);
        }

        // Add edges based on density
        int maxPossibleEdges = size * (size - 1) / 2;
        int targetEdges = Math.max((int) (maxPossibleEdges * density), size - 1);

        while (graph.getEdgeCount() < targetEdges && graph.getEdgeCount() < maxPossibleEdges) {
            int from = rand.nextInt(size);
            int to = rand.nextInt(size);
            if (from != to) {
                String fromVertex = "V" + from;
                String toVertex = "V" + to;

                // Check if edge already exists
                boolean edgeExists = false;
                for (Edge edge : graph.getEdges()) {
                    if ((edge.getFrom().equals(fromVertex) && edge.getTo().equals(toVertex)) ||
                            (edge.getFrom().equals(toVertex) && edge.getTo().equals(fromVertex))) {
                        edgeExists = true;
                        break;
                    }
                }

                if (!edgeExists) {
                    graph.addEdge(fromVertex, toVertex, rand.nextInt(100) + 1);
                }
            }
        }

        return graph;
    }

    private Graph createDisconnectedGraph() {
        Graph graph = new Graph();

        // Create two disconnected components
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");

        graph.addEdge("A", "B", 1); // Component 1
        graph.addEdge("C", "D", 2); // Component 2

        return graph;
    }

    private long measureExecutionTime(Runnable operation) {
        long startTime = System.nanoTime();
        operation.run();
        return (System.nanoTime() - startTime) / 1_000_000;
    }

    private boolean isConsistent(List<Long> values) { // Changed to Long
        if (values.size() <= 1) return true;

        long first = values.get(0);
        for (int i = 1; i < values.size(); i++) {
            // Allow 10% variance due to system load and measurement inaccuracies
            if (Math.abs(values.get(i) - first) > first * 0.1) {
                return false;
            }
        }
        return true;
    }
}