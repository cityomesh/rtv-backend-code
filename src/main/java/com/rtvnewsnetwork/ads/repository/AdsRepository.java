package com.rtvnewsnetwork.ads.repository;

import com.rtvnewsnetwork.ads.model.AdsModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdsRepository extends MongoRepository<AdsModel, String> {
    Optional<AdsModel> findById(String id);

}
