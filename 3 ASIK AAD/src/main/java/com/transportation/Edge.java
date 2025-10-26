package com.transportation;

/**
 * Represents a weighted edge between two vertices in the transportation network.
 */
public class Edge implements Comparable<Edge> {
    private String from;
    private String to;
    private int weight;

    public Edge(String from, String to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public String getFrom() { return from; }
    public String getTo() { return to; }
    public int getWeight() { return weight; }

    /**
     * Compares edges by weight for sorting
     */
    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Edge edge = (Edge) obj;
        return weight == edge.weight &&
                ((from.equals(edge.from) && to.equals(edge.to)) ||
                        (from.equals(edge.to) && to.equals(edge.from)));
    }

    @Override
    public int hashCode() {
        return from.hashCode() + to.hashCode() + weight;
    }

    @Override
    public String toString() {
        return String.format("%s-%s(%d)", from, to, weight);
    }
}