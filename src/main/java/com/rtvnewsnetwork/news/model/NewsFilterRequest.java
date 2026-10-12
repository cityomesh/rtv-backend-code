package com.rtvnewsnetwork.news.model;

import java.util.List;

public class NewsFilterRequest {

    private Integer page;        // Current page number
    private Integer size;        // Items per page
    private List<String> districts;
    private List<CategoryType> categories;

    // --- Getters and Setters ---
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<String> getDistricts() {
        return districts;
    }

    public void setDistricts(List<String> districts) {
        this.districts = districts;
    }

    public List<CategoryType> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryType> categories) {
        this.categories = categories;
    }
}
