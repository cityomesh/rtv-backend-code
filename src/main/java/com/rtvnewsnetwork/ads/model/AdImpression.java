package com.rtvnewsnetwork.ads.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Document(collection = "ad_impressions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdImpression {

    @Id
    private String id=null;

    private String adId;

    private String deviceId;

    private Integer viewFrequency;

    private Instant lastViewedAt=null;
}
