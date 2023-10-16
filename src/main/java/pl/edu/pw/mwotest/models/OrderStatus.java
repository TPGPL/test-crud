package pl.edu.pw.mwotest.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderStatus {
    @JsonProperty("New")
    New,
    @JsonProperty("InProgress")
    InProgress,
    @JsonProperty("Completed")
    Completed,
    @JsonProperty("Cancelled")
    Cancelled
}
