package com.transportation;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== City Transportation Network Optimization ===");
        System.out.println("Finding Minimum Spanning Trees using Prim's and Kruskal's algorithms");

        try {
            // Always generate new test data first
            System.out.println("📁 Generating test data...");
            GraphGenerator.main(new String[]{});

            MSTAnalyzer analyzer = new MSTAnalyzer();

            // Run analysis
            System.out.println("🔍 Analyzing transportation networks...");
            analyzer.analyzeAndSaveResults();

            System.out.println("✅ Analysis completed successfully!");
            System.out.println("📊 Results saved to: data/output.json");

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}