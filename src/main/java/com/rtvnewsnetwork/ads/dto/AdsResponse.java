package com.rtvnewsnetwork.ads.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class AdsResponse {

    private String id;
    private List<String> bannerImages;   // STRING LIST
    private String link;
    private Instant expiryDate;
    private Integer slotFrequency;
    private Integer viewFrequency;
    private Instant createdAt;
    private Instant updatedAt;
}
