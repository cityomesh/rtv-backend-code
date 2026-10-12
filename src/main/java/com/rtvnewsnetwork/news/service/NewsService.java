package com.rtvnewsnetwork.news.service;

import com.rtvnewsnetwork.news.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface NewsService {

    NewsModel createNews(NewsModel news,Boolean notify) throws Exception;

    Page<NewsModel> getAllNews(Pageable pageable);

    NewsModel getNewsById(String id);

    void deleteNews(String id);

//    Page<NewsModel> searchNews(String query, Pageable pageable);
 Page<NewsModel> searchNews(String query, Pageable pageable);

    NewsModel updateNews(String id, NewsModel updatedNews) throws Exception;

    NewsLiveTvDto getLiveTvUrl();

    Page<ArchivedNewsModel> getArchivedNews(Pageable pageable);

    List<CategoryResponse> getAllCategories();

    List<NewsModel> getAllNewsFiltered(List<String> districts, List<CategoryType> categories, Pageable pageable,String appVersion);
}
