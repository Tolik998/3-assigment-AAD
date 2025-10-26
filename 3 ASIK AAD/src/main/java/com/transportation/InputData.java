package com.transportation;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InputData {
    private List<GraphInput> graphs;

    public InputData() {}

    public InputData(List<GraphInput> graphs) {
        this.graphs = graphs;
    }

    @JsonProperty("graphs")
    public List<GraphInput> getGraphs() { return graphs; }
    public void setGraphs(List<GraphInput> graphs) { this.graphs = graphs; }
}