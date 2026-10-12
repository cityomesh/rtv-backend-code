package com.rtvnewsnetwork.news.repository;

import com.rtvnewsnetwork.news.model.CategoryType;
import com.rtvnewsnetwork.news.model.NewsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface NewsRepository extends MongoRepository<NewsModel, String> {

    @Query("{ '$or': [ " +
            "{ 'title': { $regex: ?0, $options: 'i' } }, " +
            "{ 'description': { $regex: ?0, $options: 'i' } }, " +
            "{ 'storyCards.title': { $regex: ?0, $options: 'i' } }, " +
            "{ 'storyCards.description': { $regex: ?0, $options: 'i' } } " +
            "] }")
    Page<NewsModel> searchByTitleDescriptionOrStoryCards(String query, Pageable pageable);

    @Query("""
{
  "$or": [
    { "title":            { "$regex": ?0, "$options": "i" } },
    { "description":      { "$regex": ?0, "$options": "i" } },
    { "storyCards.title": { "$regex": ?0, "$options": "i" } },
    { "storyCards.description": { "$regex": ?0, "$options": "i" } },
    { "tags":            { "$regex": ?0, "$options": "i" } }
  ]
}
""")
    Page<NewsModel> searchEverywhere(String query, Pageable pageable);


    @Query("{ 'tags': { $in: ?0 } }")
    Page<NewsModel> searchByTags(List<String> tags, Pageable pageable);


    @Query("{ '$and': [ " +
            "  { 'district': { $in: ?0 } }, " +
            "  { 'categoryType': { $in: ?1 } } " +
            "] }")
    Page<NewsModel> findAllWithFilters(List<String> districts, List<CategoryType> categories, Pageable pageable);

    Page<NewsModel> findByDistrictIn(List<String> districts, Pageable pageable);

    Page<NewsModel> findByCategoryTypeIn(List<CategoryType> categories, Pageable pageable);


    Page<NewsModel> findByDistrictInOrDistrictIsNull(
            List<String> districts, Pageable pageable);


    Page<NewsModel> findByCategoryTypeInOrCategoryTypeIsNull(
            List<CategoryType> categories, Pageable pageable);

    @Query("{ '$and': [ " +
            "  { '$or': [ " +
            "      { 'district': { '$in': ?0 } }, " +
            "      { 'district': null } " +
            "  ] }, " +
            "  { '$or': [ " +
            "      { 'categoryType': { '$in': ?1 } }, " +
            "      { 'categoryType': null } " +
            "  ] } " +
            "] }")
    Page<NewsModel> findWithNullableDistrictAndCategory(
            List<String> districts,
            List<CategoryType> categories,
            Pageable pageable);






}
