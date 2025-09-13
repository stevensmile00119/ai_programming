package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Model representing the response from TWSE API STOCK_DAY endpoint
 */
public class TwseApiResponse {
    
    @JsonProperty("stat")
    private String stat;
    
    @JsonProperty("date")
    private String date;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("fields")
    private List<String> fields;
    
    @JsonProperty("data")
    private List<List<String>> data;
    
    @JsonProperty("notes")
    private List<String> notes;

    // Default constructor
    public TwseApiResponse() {}

    // Getters and Setters
    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }
}