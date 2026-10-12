package com.rtvnewsnetwork.ads.dto;

import com.rtvnewsnetwork.ads.model.AdsModel;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AdImpressionResponse {

    private String adId;
    private String deviceId;
    private Integer viewFrequency;
    private Instant lastViewedAt;

    private AdsResponse ad;
}
