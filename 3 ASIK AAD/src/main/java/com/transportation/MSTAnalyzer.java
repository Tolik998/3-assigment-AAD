package com.transportation;

import java.io.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Main analysis engine that coordinates MST computation and result reporting.
 */
public class MSTAnalyzer {
    private PrimMST prim = new PrimMST();
    private KruskalMST kruskal = new KruskalMST();
    private ObjectMapper objectMapper = new ObjectMapper();

    public void analyzeAndSaveResults() {
        try {
            List<Graph> graphs = readInputGraphs("data/input.json");
            System.out.println("📁 Loaded " + graphs.size() + " graphs for analysis");

            List<AnalysisResult> results = new ArrayList<>();
            for (int i = 0; i < graphs.size(); i++) {
                Graph graph = graphs.get(i);
                System.out.println("🔍 Analyzing graph " + (i + 1) + "/" + graphs.size() +
                        " (V=" + graph.getVertexCount() + ", E=" + graph.getEdgeCount() + ")...");
                results.add(analyzeGraph(i + 1, graph));
            }

            saveResultsToJson(results, "data/output.json");
            printSummary(results);

        } catch (IOException e) {
            throw new RuntimeException("Error during analysis: " + e.getMessage(), e);
        }
    }

    private AnalysisResult analyzeGraph(int graphId, Graph graph) {
        try {
            MSTResult primResult = prim.findMST(graph);
            MSTResult kruskalResult = kruskal.findMST(graph);

            // Calculate theoretical complexities for comparison
            long primTheoretical = calculatePrimTheoretical(graph);
            long kruskalTheoretical = calculateKruskalTheoretical(graph);

            System.out.println("  Prim: " + primResult.getOperationsCount() + " ops (theoretical: " + primTheoretical + ")");
            System.out.println("  Kruskal: " + kruskalResult.getOperationsCount() + " ops (theoretical: " + kruskalTheoretical + ")");

            return new AnalysisResult(graphId, graph, primResult, kruskalResult);

        } catch (Exception e) {
            System.err.println("Error analyzing graph " + graphId + ": " + e.getMessage());
            return new AnalysisResult(graphId, graph, null, null);
        }
    }

    private long calculatePrimTheoretical(Graph graph) {
        // Prim: O(E log V) operations
        if (graph.getEdgeCount() == 0) return 0;
        return (long)(graph.getEdgeCount() * Math.log(graph.getVertexCount()) / Math.log(2));
    }

    private long calculateKruskalTheoretical(Graph graph) {
        // Kruskal: O(E log E) operations for sorting + O(E) for Union-Find
        if (graph.getEdgeCount() == 0) return 0;
        return (long)(graph.getEdgeCount() * Math.log(graph.getEdgeCount()) / Math.log(2)) + graph.getEdgeCount();
    }

    private List<Graph> readInputGraphs(String filename) throws IOException {
        List<Graph> graphs = new ArrayList<>();
        InputData inputData = objectMapper.readValue(new File(filename), InputData.class);

        for (GraphInput graphInput : inputData.getGraphs()) {
            Graph graph = convertToGraph(graphInput);
            graphs.add(graph);
        }

        return graphs;
    }

    private Graph convertToGraph(GraphInput graphInput) {
        Graph graph = new Graph();

        for (String node : graphInput.getNodes()) {
            graph.addVertex(node);
        }

        for (EdgeInput edgeInput : graphInput.getEdges()) {
            graph.addEdge(edgeInput.getFrom(), edgeInput.getTo(), edgeInput.getWeight());
        }

        return graph;
    }

    private void saveResultsToJson(List<AnalysisResult> results, String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write("{\n  \"results\": [\n");

        for (int i = 0; i < results.size(); i++) {
            AnalysisResult result = results.get(i);
            writer.write(result.toJson());
            if (i < results.size() - 1) writer.write(",");
            writer.write("\n");
        }

        writer.write("  ]\n}");
        writer.close();
    }

    private void printSummary(List<AnalysisResult> results) {
        System.out.println("\n📈 PERFORMANCE SUMMARY");
        System.out.println("========================================================================");
        System.out.println("Graph ID | Vertices | Edges | Prim Time | Kruskal Time | Prim Ops | Kruskal Ops | Cost");
        System.out.println("--------|----------|-------|-----------|--------------|----------|-------------|-----");

        int successful = 0;
        for (AnalysisResult result : results) {
            if (result.primResult != null && result.kruskalResult != null) {
                System.out.printf("%8d|%10d|%7d|%11d|%14d|%10d|%13d|%5d\n",
                        result.graphId,
                        result.graph.getVertexCount(),
                        result.graph.getEdgeCount(),
                        result.primResult.getExecutionTime(),
                        result.kruskalResult.getExecutionTime(),
                        result.primResult.getOperationsCount(),
                        result.kruskalResult.getOperationsCount(),
                        result.primResult.getTotalCost());
                successful++;
            }
        }

        System.out.println("========================================================================");
        System.out.println("✅ Successfully analyzed: " + successful + "/" + results.size() + " graphs");

        // Print complexity analysis
        printComplexityAnalysis(results);
    }

    private void printComplexityAnalysis(List<AnalysisResult> results) {
        System.out.println("\n🔬 COMPLEXITY ANALYSIS");
        System.out.println("Theoretical complexities:");
        System.out.println("- Prim: O(E log V) operations");
        System.out.println("- Kruskal: O(E log E) operations");
        System.out.println("Note: Operations should be comparable for same graph size");
    }

    private static class AnalysisResult {
        int graphId;
        Graph graph;
        MSTResult primResult;
        MSTResult kruskalResult;

        AnalysisResult(int graphId, Graph graph, MSTResult primResult, MSTResult kruskalResult) {
            this.graphId = graphId;
            this.graph = graph;
            this.primResult = primResult;
            this.kruskalResult = kruskalResult;
        }

        String toJson() {
            if (primResult == null || kruskalResult == null) {
                return String.format("    {\"graph_id\": %d, \"error\": \"Analysis failed\"}", graphId);
            }

            StringBuilder json = new StringBuilder();
            json.append("    {\n");
            json.append("      \"graph_id\": ").append(graphId).append(",\n");
            json.append("      \"input_stats\": {\n");
            json.append("        \"vertices\": ").append(graph.getVertexCount()).append(",\n");
            json.append("        \"edges\": ").append(graph.getEdgeCount()).append("\n");
            json.append("      },\n");

            json.append("      \"prim\": ").append(resultToJson(primResult)).append(",\n");
            json.append("      \"kruskal\": ").append(resultToJson(kruskalResult)).append("\n");
            json.append("    }");
            return json.toString();
        }

        private String resultToJson(MSTResult result) {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("        \"mst_edges\": [\n");

            List<Edge> edges = result.getMstEdges();
            for (int i = 0; i < edges.size(); i++) {
                Edge edge = edges.get(i);
                json.append("          {\"from\": \"").append(edge.getFrom())
                        .append("\", \"to\": \"").append(edge.getTo())
                        .append("\", \"weight\": ").append(edge.getWeight()).append("}");
                if (i < edges.size() - 1) json.append(",");
                json.append("\n");
            }

            json.append("        ],\n");
            json.append("        \"total_cost\": ").append(result.getTotalCost()).append(",\n");
            json.append("        \"operations_count\": ").append(result.getOperationsCount()).append(",\n");
            json.append("        \"execution_time_ms\": ").append(result.getExecutionTime()).append("\n");
            json.append("      }");

            return json.toString();
        }
    }
}