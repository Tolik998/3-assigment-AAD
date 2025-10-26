package com.transportation;

import java.util.*;

/**
 * Represents a weighted undirected graph for city transportation networks.
 * Vertices represent city districts, edges represent potential roads with construction costs.
 */
public class Graph {
    private List<String> vertices;
    private List<Edge> edges;

    public Graph() {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    /**
     * Adds a vertex (city district) to the graph
     * @param vertex the vertex to add
     */
    public void addVertex(String vertex) {
        if (!vertices.contains(vertex)) {
            vertices.add(vertex);
        }
    }

    /**
     * Adds an edge (potential road) to the graph
     * @param from the starting vertex
     * @param to the ending vertex
     * @param weight the construction cost
     */
    public void addEdge(String from, String to, int weight) {
        edges.add(new Edge(from, to, weight));
    }

    // Getters
    public List<String> getVertices() {
        return new ArrayList<>(vertices);
    }

    public List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }

    public int getVertexCount() {
        return vertices.size();
    }

    public int getEdgeCount() {
        return edges.size();
    }

    @Override
    public String toString() {
        return String.format("Graph{vertices=%d, edges=%d}", getVertexCount(), getEdgeCount());
    }
}