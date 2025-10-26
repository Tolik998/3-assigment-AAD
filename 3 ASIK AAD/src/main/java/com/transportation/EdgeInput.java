package com.transportation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EdgeInput {
    private String from;
    private String to;
    private int weight;

    public EdgeInput() {}

    public EdgeInput(String from, String to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    @JsonProperty("from")
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    @JsonProperty("to")
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    @JsonProperty("weight")
    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }
}
