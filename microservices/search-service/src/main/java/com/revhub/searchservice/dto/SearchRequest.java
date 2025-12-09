package com.revhub.searchservice.dto;

import jakarta.validation.constraints.NotBlank;

public class SearchRequest {
    @NotBlank
    private String query;
    
    private String type = "ALL"; // ALL, POSTS, USERS
    private String sortBy = "relevance"; // relevance, date, popularity
    private Integer page = 0;
    private Integer size = 20;

    // Getters and Setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
}