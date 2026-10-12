package com.rtvnewsnetwork.news.controller;

import com.rtvnewsnetwork.news.model.*;
import com.rtvnewsnetwork.news.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;



    /**
     * Create a news item
     */
    @Operation(
            summary = "Create a new news item",
            description = "This endpoint is used to create a new news item from admin dashboard ",
            tags = { "News Management" }
    )
    @PostMapping
    public NewsModel createNews(@RequestBody NewsModel news,@RequestParam Boolean notify) throws Exception {
        return newsService.createNews(news,notify);
    }

    /**
     * Get all news with pagination
     */
    @Operation(
            summary = "Get all news with pagination",
            description = "This endpoint is used to get all news with pagination. ",
            tags = { "News Management" }
    )
    @GetMapping
    public List<NewsModel> getAllNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return newsService.getAllNews(pageable).toList();
    }


    @PostMapping("/all")
    public List<NewsModel> getAllNews(@RequestBody NewsFilterRequest request,
                                      @RequestHeader(value = "appVersion", required = false) String appVersion) {
        int page = (request.getPage() != null) ? request.getPage() : 0;
        int size = (request.getSize() != null) ? request.getSize() : 10; // default = 10

        Pageable pageable = PageRequest.of(page, size);
        if(appVersion == null) {
         appVersion="5.2.1";
        }
        return newsService
                .getAllNewsFiltered(request.getDistricts(), request.getCategories(), pageable,appVersion);
    }


    /**
     * Get a news item by ID
     */
    @Operation(
            summary = "Get a news item by ID",
            description = "This endpoint is used to get a news item by ID. ",
            tags = { "News Management" }
    )
    @GetMapping("/{id}")
    public NewsModel getNewsById(@PathVariable String id) {
        return newsService.getNewsById(id);
    }

    /**
     * Delete a news item by ID
     */
    @Operation(
            summary = "Delete a news item by ID",
            description = "This endpoint is used to delete a news item by ID. ",
            tags = { "News Management" }
    )
    @DeleteMapping("/{id}")
    public void deleteNews(@PathVariable String id) {
        newsService.deleteNews(id);
    }

    /**
     * Search news by title, description, or story card content
     */
//    @Operation(
//            summary = "Search news by title, description, or story card content",
//            description = "This endpoint is used to search news by title, description, or story card content. ",
//            tags = { "News Management" }
//    )
//    @GetMapping("/search")
//    public Page<NewsModel> searchNews(
//            @RequestParam String query,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//        return newsService.searchNews(query, pageable);
//    }

    @Operation(
            summary = "Search news by title, description, or story card content",
            description = "This endpoint is used to search news by title, description, or story card content. ",
            tags = { "News Management" }
    )
    @GetMapping("/search")
    public Page<NewsModel> searchNews(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return newsService.searchNews(query, pageable);
    }



    @Operation(summary = "Update a news item")
    @PutMapping("/{id}")
    public NewsModel updateNews(
            @PathVariable String id,
            @RequestBody NewsModel updatedNews
    ) throws Exception {
       return newsService.updateNews(id, updatedNews);
    }

    @GetMapping("/livetv")
    public NewsLiveTvDto getLiveTvUrl() {
        return newsService.getLiveTvUrl();
    }


    @GetMapping("/archive")
    public List<ArchivedNewsModel> getArchivedNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("archivedAt").descending());
        return newsService.getArchivedNews(pageable).toList();
    }


    @Operation(summary = "Get all available news categories")
    @GetMapping("/category")
    public List<CategoryResponse> getAllCategories() {
        return newsService.getAllCategories();
    }


}

