package com.rtvnewsnetwork.news.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "news_archive")
public class ArchivedNewsModel extends NewsModel {

    private Instant archivedAt = Instant.now();

    // Copy constructor
    public ArchivedNewsModel(NewsModel news) {
        super(
                null,
                news.getTitle(),
                news.getDescription(),
                news.getBannerImage(),
                news.getVideo(),
                news.getStoryCards(),
                news.getShareUrl(),
                news.getCreatedAt(),
                news.getUpdatedAt(),
                news.getDistrict(),
                news.getCategoryType(),
                news.getTags(),
                news.getReadMoreUrl()
        );
    }
}
