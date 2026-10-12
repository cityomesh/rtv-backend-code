package com.rtvnewsnetwork.ads.dto;

import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class AdsRequest {
    private List<RelativePath> bannerImages;
    private String link;
    private Instant expiryDate;
    private Integer slotFrequency;
    private Integer viewFrequency;
}
