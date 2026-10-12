package com.rtvnewsnetwork.news.model;


import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;
import com.rtvnewsnetwork.post.model.Video;
import com.rtvnewsnetwork.post.model.VideoContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class NewsModel {
    private String id=null;
    private String title=null;
    private String description=null;
    @Schema(implementation = String.class, description = " image path url")
    private RelativePath bannerImage=null;
    private Video video=null;
    private List<StoryCard> storyCards;
    private String shareUrl;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    @Schema(description = "District associated with the news")
    private String district=null;
    @Schema(description = "Category type of the news", example = "POLITICS")
    private CategoryType categoryType=null;
    @Schema(description = "List of tags associated with the news", example = "[\"modi\", \"politics\", \"breaking\"]")
    private List<String> tags=null;
    private String readMoreUrl;
}
