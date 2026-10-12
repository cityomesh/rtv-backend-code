package com.rtvnewsnetwork.news.model;

public enum CategoryType {
    SPORTS(1, "Sports","https://d3l8l5q21enszy.cloudfront.net/images/HkwnrsojHvMhFIymVKsFgWGpmTFiPXwM.svg"),
    POLITICS(2, "Politics","https://d3l8l5q21enszy.cloudfront.net/images/tHgkFjDWhIYFCDKxBojwtfbOnXEncSBo.svg"),
    ENTERTAINMENT(3, "Entertainment","https://d3l8l5q21enszy.cloudfront.net/images/YgyYIVgHbpPyExMmEhCEYknBPMqFupoS.svg"),
    ECONOMY(4, "Economy","https://d3l8l5q21enszy.cloudfront.net/images/zRLOlfKyNMWEdZxsZJYeKQgWLXFjfgTD.svg"),
    FASHION(5, "Fashion","https://d3l8l5q21enszy.cloudfront.net/images/WbYKHRKdGvKxOXCZytnNdcZJwUzHJrlf.svg"),
    TECHNOLOGY(6, "Technology","https://d3l8l5q21enszy.cloudfront.net/images/xYHGwgtQbKxLEMHKFbpoJfwfMFtPtkaW.svg"),
    _NA(7,"_NA","https://d3l8l5q21enszy.cloudfront.net/images/xYHGwgtQbKxLEMHKFbpoJfwfMFtPtkaW.svg");

    private final int id;
    private final String label;
    private final String iconUrl;

    CategoryType(int id, String label, String iconUrl) {
        this.id = id;
        this.label = label;
        this.iconUrl = iconUrl;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
    public String getIconUrl() {
        return iconUrl;
    }
}

