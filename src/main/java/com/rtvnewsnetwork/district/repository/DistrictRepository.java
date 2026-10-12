package com.rtvnewsnetwork.district.repository;

import com.rtvnewsnetwork.district.model.DistrictModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DistrictRepository extends MongoRepository<DistrictModel, String> {
    List<DistrictModel> findByNameNot(String name);
}
