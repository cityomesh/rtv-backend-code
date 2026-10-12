package com.rtvnewsnetwork.reel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;
import com.rtvnewsnetwork.post.model.Video;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ReelModel {

    @Id
    private String id;
    private String userId=null;
    private String title;
  //  @Schema(implementation = String.class, description = "Profile image path")
//    private RelativePath thumbnail;
//    @Schema(implementation = String.class, description = "Profile image path")
//    private RelativePath videoURL;
    private Video video;
    private ReelInsightModel insights;
    private Boolean isApproved=false;
    private String UploadedBy;
    private ReviewStatus status;
    private String rejectionReason;
    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
    @Transient
    @JsonProperty("liked")
    private boolean isLiked = false;

}
