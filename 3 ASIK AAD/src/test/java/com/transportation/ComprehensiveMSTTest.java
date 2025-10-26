package com.transportation;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Comprehensive tests for MST algorithms including operation count validation
 */
public class ComprehensiveMSTTest {
    private PrimMST prim;
    private KruskalMST kruskal;

    @BeforeEach
    void setUp() {
        prim = new PrimMST();
        kruskal = new KruskalMST();
    }

    @Test
    @DisplayName("Operation counts should be proportional to graph size")
    void testOperationCountScaling() {
        // Test small graph
        Graph smallGraph = createGraph(10, 0.3);
        MSTResult smallPrim = prim.findMST(smallGraph);
        MSTResult smallKruskal = kruskal.findMST(smallGraph);

        // Test large graph
        Graph largeGraph = createGraph(100, 0.3);
        MSTResult largePrim = prim.findMST(largeGraph);
        MSTResult largeKruskal = kruskal.findMST(largeGraph);

        // Operations should increase with graph size
        assertTrue(largePrim.getOperationsCount() > smallPrim.getOperationsCount(),
                "Prim operations should increase with graph size");
        assertTrue(largeKruskal.getOperationsCount() > smallKruskal.getOperationsCount(),
                "Kruskal operations should increase with graph size");

        // But not exponentially (should follow O(E log V) / O(E log E))
        double primRatio = (double) largePrim.getOperationsCount() / smallPrim.getOperationsCount();
        double kruskalRatio = (double) largeKruskal.getOperationsCount() / smallKruskal.getOperationsCount();

        // For 10x more vertices, operations should increase but reasonably
        assertTrue(primRatio < 1000, "Prim operations should not grow exponentially");
        assertTrue(kruskalRatio < 1000, "Kruskal operations should not grow exponentially");
    }

    @Test
    @DisplayName("Operation counts should be reasonable for graph complexity")
    void testReasonableOperationCounts() {
        Graph graph = createGraph(50, 0.4); // 50 vertices, ~500 edges

        MSTResult primResult = prim.findMST(graph);
        MSTResult kruskalResult = kruskal.findMST(graph);

        // For 50 vertices and ~500 edges, operations should be in thousands, not millions
        assertTrue(primResult.getOperationsCount() < 1000000,
                "Prim operations should be reasonable for graph size");
        assertTrue(kruskalResult.getOperationsCount() < 1000000,
                "Kruskal operations should be reasonable for graph size");

        System.out.printf("Reasonable operations - Prim: %d, Kruskal: %d for graph V=%d, E=%d%n",
                primResult.getOperationsCount(), kruskalResult.getOperationsCount(),
                graph.getVertexCount(), graph.getEdgeCount());
    }

    private Graph createGraph(int size, double density) {
        Graph graph = new Graph();
        Random rand = new Random(42);

        for (int i = 0; i < size; i++) {
            graph.addVertex("V" + i);
        }

        // Ensure connectivity
        List<String> vertices = graph.getVertices();
        for (int i = 1; i < vertices.size(); i++) {
            int fromIndex = rand.nextInt(i);
            graph.addEdge(vertices.get(fromIndex), vertices.get(i), rand.nextInt(100) + 1);
        }

        // Add additional edges
        int maxEdges = size * (size - 1) / 2;
        int targetEdges = Math.max((int)(maxEdges * density), size - 1);

        while (graph.getEdgeCount() < targetEdges && graph.getEdgeCount() < maxEdges) {
            int from = rand.nextInt(size);
            int to = rand.nextInt(size);
            if (from != to) {
                graph.addEdge("V" + from, "V" + to, rand.nextInt(100) + 1);
            }
        }

        return graph;
    }
}