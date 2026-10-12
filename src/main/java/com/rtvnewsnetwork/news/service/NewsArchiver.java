package com.rtvnewsnetwork.news.service;

import com.rtvnewsnetwork.news.model.ArchivedNewsModel;
import com.rtvnewsnetwork.news.model.NewsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Component
public class NewsArchiver {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${archive.news.time.duration.in.hours:48}") // default 48 hours if not set
    private Integer archivedTimeDurationInHours;

    // Runs every 5 minutes (5 * 60 * 1000 ms)
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void archiveOldNews() {

        // Calculate cutoff time: current time minus configured hours (e.g. 48)
        Instant cutoff = Instant.now().minus(archivedTimeDurationInHours, ChronoUnit.HOURS);

        // Find all news created before cutoff
        Query query = new Query(Criteria.where("createdAt").lt(cutoff));
        List<NewsModel> expiredNews = mongoTemplate.find(query, NewsModel.class);

        if (expiredNews.isEmpty()) {
            System.out.println(" No news to archive at " + Instant.now());
            return;
        }

        // Archive news
        for (NewsModel news : expiredNews) {
            mongoTemplate.insert(new ArchivedNewsModel(news), "news_archive");
            mongoTemplate.remove(news);
        }

        System.out.println("🗄️ Archived " + expiredNews.size() + " news items at " + Instant.now());
    }
}

