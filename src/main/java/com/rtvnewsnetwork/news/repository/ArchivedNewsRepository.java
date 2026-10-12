package com.rtvnewsnetwork.news.repository;

import com.rtvnewsnetwork.news.model.ArchivedNewsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchivedNewsRepository extends MongoRepository<ArchivedNewsModel, String> {
    Page<ArchivedNewsModel> findAll(Pageable pageable);
}

