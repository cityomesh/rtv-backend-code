package com.rtvnewsnetwork.notification.repository;

import com.mongodb.client.result.UpdateResult;
import com.rtvnewsnetwork.notification.model.FcmModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public class FcmRepositoryImpl implements FcmRepositoryCustom {

    private static final Logger log = LoggerFactory.getLogger(FcmRepositoryImpl.class);
    private final MongoTemplate mongoTemplate;

    @Autowired
    public FcmRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void updateSuccessCount(String token) {
        try {
            String successEntry = String.format("SuccessAt=%s", Instant.now());

            Query query = new Query(Criteria.where("token").is(token));
            Update update = new Update()
                    .inc("successCount", 1)
                    .set("successLastUpdatedAt", Instant.now());

            UpdateResult result = mongoTemplate.updateFirst(query, update, FcmModel.class);
            log.info("Success count incremented for token {} (matched={}, modified={})",
                    token, result.getMatchedCount(), result.getModifiedCount());

        } catch (Exception e) {
            log.error("Error incrementing success count for token {}: {}", token, e.getMessage(), e);
        }
    }



    @Override
    public void updateFailureCount(String token) {
        try {


            Query query = new Query(Criteria.where("token").is(token));
            Update update = new Update()
                    .inc("failureCounter", 1)                 // <- keep only this
                     .set("lastFailureAt", Instant.now());

            mongoTemplate.updateFirst(query, update, FcmModel.class);
            log.warn("Failure count incremented for token {}", token);

        } catch (Exception e) {
            log.error("Error updating failure for token {}: {}", token, e.getMessage(), e);
        }
    }

}
