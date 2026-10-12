package com.rtvnewsnetwork.ads.repository;

import com.rtvnewsnetwork.ads.model.AdImpression;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AdImpressionRepository extends MongoRepository<AdImpression, String> {

    Optional<AdImpression> findByAdIdAndDeviceId(String adId, String deviceId);
    List<AdImpression> findByDeviceId(String deviceId);



}
