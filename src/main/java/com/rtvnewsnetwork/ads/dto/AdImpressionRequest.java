package com.rtvnewsnetwork.ads.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class AdImpressionRequest {

    private String adId;
    private String deviceId;
    private Integer viewFrequency;
    private Instant lastViewedAt=null;

}
