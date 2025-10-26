package com.transportation;

import java.util.*;
import java.io.File;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class GraphGenerator {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static void main(String[] args) throws Exception {
        // Create directories
        new File("data/input").mkdirs();

        generateSmallGraphs();
        generateMediumGraphs();
        generateLargeGraphs();
        generateExtraLargeGraphs();
        generateCombinedDataset();
        System.out.println("All test datasets generated successfully!");
    }

    public static void generateSmallGraphs() throws Exception {
        List<GraphInput> graphs = new ArrayList<>();
        Random random = new Random(42);

        // Small: 5 graphs with varied sizes up to 50
        int[] smallSizes = {10, 20, 30, 40, 50};
        for (int i = 0; i < 5; i++) {
            int vertices = smallSizes[i];
            double density = 0.3 + (random.nextDouble() * 0.4); // 30-70% density
            graphs.add(generateGraph(i + 1, vertices, density, random));
        }

        InputData data = new InputData(graphs);
        objectMapper.writeValue(new File("data/input/small_graphs.json"), data);
        System.out.println("Generated small graphs: 5 graphs with sizes: " + Arrays.toString(smallSizes));
    }

    public static void generateMediumGraphs() throws Exception {
        List<GraphInput> graphs = new ArrayList<>();
        Random random = new Random(42);

        // Medium: 10 graphs with varied sizes 50-300
        int[] mediumSizes = {50, 75, 100, 125, 150, 175, 200, 225, 250, 300};
        for (int i = 0; i < 10; i++) {
            int vertices = mediumSizes[i];
            double density = 0.2 + (random.nextDouble() * 0.3); // 20-50% density
            graphs.add(generateGraph(i + 1, vertices, density, random));
        }

        InputData data = new InputData(graphs);
        objectMapper.writeValue(new File("data/input/medium_graphs.json"), data);
        System.out.println("Generated medium graphs: 10 graphs with sizes: " + Arrays.toString(mediumSizes));
    }

    public static void generateLargeGraphs() throws Exception {
        List<GraphInput> graphs = new ArrayList<>();
        Random random = new Random(42);

        // Large: 10 graphs with varied sizes 300-1000
        int[] largeSizes = {300, 400, 500, 600, 700, 800, 900, 1000, 350, 450};
        for (int i = 0; i < 10; i++) {
            int vertices = largeSizes[i];
            double density = 0.15 + (random.nextDouble() * 0.25); // 15-40% density
            graphs.add(generateGraph(i + 1, vertices, density, random));
        }

        InputData data = new InputData(graphs);
        objectMapper.writeValue(new File("data/input/large_graphs.json"), data);
        System.out.println("Generated large graphs: 10 graphs with sizes: " + Arrays.toString(largeSizes));
    }

    public static void generateExtraLargeGraphs() throws Exception {
        List<GraphInput> graphs = new ArrayList<>();
        Random random = new Random(42);

        // Extra Large: 5 graphs with varied sizes 1000-3000
        int[] extraLargeSizes = {1000, 1500, 2000, 2500, 3000};
        for (int i = 0; i < 5; i++) {
            int vertices = extraLargeSizes[i];
            double density = 0.1 + (random.nextDouble() * 0.2); // 10-30% density
            graphs.add(generateGraph(i + 1, vertices, density, random));
        }

        InputData data = new InputData(graphs);
        objectMapper.writeValue(new File("data/input/extra_large_graphs.json"), data);
        System.out.println("Generated extra large graphs: 5 graphs with sizes: " + Arrays.toString(extraLargeSizes));
    }

    public static void generateCombinedDataset() throws Exception {
        List<GraphInput> allGraphs = new ArrayList<>();

        // Combine all graphs into one dataset
        InputData small = objectMapper.readValue(new File("data/input/small_graphs.json"), InputData.class);
        InputData medium = objectMapper.readValue(new File("data/input/medium_graphs.json"), InputData.class);
        InputData large = objectMapper.readValue(new File("data/input/large_graphs.json"), InputData.class);
        InputData extra = objectMapper.readValue(new File("data/input/extra_large_graphs.json"), InputData.class);

        allGraphs.addAll(small.getGraphs());
        allGraphs.addAll(medium.getGraphs());
        allGraphs.addAll(large.getGraphs());
        allGraphs.addAll(extra.getGraphs());

        // Reindex all graphs
        for (int i = 0; i < allGraphs.size(); i++) {
            allGraphs.get(i).setId(i + 1);
        }

        InputData combinedData = new InputData(allGraphs);
        objectMapper.writeValue(new File("data/input.json"), combinedData);
        System.out.println("Generated combined dataset: " + allGraphs.size() + " graphs total");
    }

    private static GraphInput generateGraph(int id, int vertexCount, double density, Random random) {
        List<String> nodes = new ArrayList<>();
        List<EdgeInput> edges = new ArrayList<>();

        // Generate node names
        for (int i = 0; i < vertexCount; i++) {
            nodes.add("N" + i);
        }

        // Generate edges with controlled density
        int maxEdges = vertexCount * (vertexCount - 1) / 2;
        int targetEdges = (int) (maxEdges * density);
        targetEdges = Math.max(targetEdges, vertexCount - 1); // Ensure connectivity
        targetEdges = Math.min(targetEdges, maxEdges); // Don't exceed maximum

        Set<String> addedEdges = new HashSet<>();

        // First, ensure graph is connected (create spanning tree)
        List<Integer> connected = new ArrayList<>();
        connected.add(0);
        List<Integer> unconnected = new ArrayList<>();
        for (int i = 1; i < vertexCount; i++) {
            unconnected.add(i);
        }

        while (!unconnected.isEmpty()) {
            int fromIndex = random.nextInt(connected.size());
            int toIndex = random.nextInt(unconnected.size());
            int from = connected.get(fromIndex);
            int to = unconnected.get(toIndex);

            int weight = 1 + random.nextInt(100);
            String edgeKey = Math.min(from, to) + "-" + Math.max(from, to);

            edges.add(new EdgeInput("N" + from, "N" + to, weight));
            addedEdges.add(edgeKey);

            connected.add(to);
            unconnected.remove(toIndex);
        }

        // Add additional random edges
        while (edges.size() < targetEdges && edges.size() < maxEdges) {
            int from = random.nextInt(vertexCount);
            int to = random.nextInt(vertexCount);

            if (from != to) {
                String edgeKey = Math.min(from, to) + "-" + Math.max(from, to);

                if (!addedEdges.contains(edgeKey)) {
                    int weight = 1 + random.nextInt(100);
                    edges.add(new EdgeInput("N" + from, "N" + to, weight));
                    addedEdges.add(edgeKey);
                }
            }
        }

        double actualDensity = (edges.size() * 100.0) / maxEdges;
        System.out.printf("Graph %d: %d vertices, %d edges (density: %.1f%%)%n",
                id, vertexCount, edges.size(), actualDensity);

        return new GraphInput(id, nodes, edges);
    }
}