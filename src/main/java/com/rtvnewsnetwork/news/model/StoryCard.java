package com.rtvnewsnetwork.news.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryCard {
    private String id= new ObjectId().toString();
    @Schema(implementation = String.class, description = "story card image")
    private RelativePath bannerImage;
    private String description;
    private String shareUrl;

    @CreatedDate
    private Instant createdAt=Instant.now();
    @LastModifiedDate
    private Instant updatedAt=Instant.now();
}