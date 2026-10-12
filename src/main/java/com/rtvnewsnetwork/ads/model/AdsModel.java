package com.rtvnewsnetwork.ads.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class AdsModel {

    private String id = null;

    @Schema(description = "List of banner images")
    private List<RelativePath> bannerImages;

    @Schema(description = "Redirect link associated with the banner")
    private String link = null;

    @Schema(description = "Expiry date of the banner")
    private Instant expiryDate = null;

    @Schema(description = "Slot frequency for banner placement")
    private Integer slotFrequency = null;

    @Schema(description = "View frequency limit for the banner")
    private Integer viewFrequency = null;

    @CreatedDate
    private Instant createdAt=null;

    @LastModifiedDate
    private Instant updatedAt=null;
}
