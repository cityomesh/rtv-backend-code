package com.rtvnewsnetwork.news.model;

import java.util.List;

public  class TagFilterRequest {
    private List<String> tags;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
