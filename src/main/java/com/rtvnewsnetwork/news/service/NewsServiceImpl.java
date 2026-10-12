package com.rtvnewsnetwork.news.service;

import com.rtvnewsnetwork.common.service.ThumbnailExtractorService;
import com.rtvnewsnetwork.config.kafka.KafkaTopicConfig;
import com.rtvnewsnetwork.district.service.DistrictService;
import com.rtvnewsnetwork.event.model.EventType;
import com.rtvnewsnetwork.event.service.EventPublisher;
import com.rtvnewsnetwork.event.service.EventService;
import com.rtvnewsnetwork.news.model.*;
import com.rtvnewsnetwork.news.repository.ArchivedNewsRepository;
import com.rtvnewsnetwork.news.repository.NewsRepository;
import com.rtvnewsnetwork.post.model.ExternalFile;
import com.rtvnewsnetwork.post.model.Video;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.rtvnewsnetwork.reel.service.ReelServiceImpl.getYouTubeThumbnail;

@Service
public class NewsServiceImpl implements NewsService , EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(NewsServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;
    private final NewsRepository newsRepository;
    private final EventService eventService;
    @Value("${cloudfront.url}")
    private String cloudfrontUrl;
    @Value("${rtv.livetv.url}")
    private String rtvLiveTvUrl;
    @Value("${rtv.youtube.icon.url}")
    private String youtubeIconUrl;
    private final ArchivedNewsRepository archivedNewsRepository;
    private final DistrictService districtService;
    private final ThumbnailExtractorService thumbnailExtractorService;

    public NewsServiceImpl(NewsRepository newsRepository, EventService eventService, ArchivedNewsRepository archivedNewsRepository, DistrictService districtService, ThumbnailExtractorService thumbnailExtractorService) {
        this.newsRepository = newsRepository;
        this.eventService = eventService;
        this.archivedNewsRepository = archivedNewsRepository;
        this.districtService = districtService;
        this.thumbnailExtractorService = thumbnailExtractorService;
    }

    @Override
    public NewsModel createNews(NewsModel news, Boolean notify) throws Exception {
        news.setCreatedAt(Instant.now());

        if (news.getDistrict() == null || news.getDistrict().isBlank()) {
            news.setDistrict("_NA");
        }

        if (news.getCategoryType() == null) {
            news.setCategoryType(CategoryType._NA);
        }

        if (news.getTags() != null && !news.getTags().isEmpty()) {
            List<String> cleanTags = news.getTags().stream()
                    .filter(tag -> tag != null && !tag.isBlank())
                    .map(String::trim)
                    .map(String::toLowerCase) // normalize for consistent searching
                    .distinct()
                    .toList();
            news.setTags(cleanTags);
        } else {
            news.setTags(List.of());
        }

        NewsModel savedNews = newsRepository.save(news);
        String shareUrl = generateShareUrl(savedNews.getId());
                savedNews.setShareUrl(shareUrl);
                savedNews = newsRepository.save(savedNews);

        if (savedNews.getVideo() != null) {
            if (savedNews.getVideo().getExternalFile() != null) {
                String platform = thumbnailExtractorService.platformFromUrl(savedNews.getVideo().getExternalFile().getUrl());
                 System.out.println("platform"+platform);
                if (platform != null && platform.equalsIgnoreCase("youtube")) {
                    savedNews.getVideo().getExternalFile().setThumbnailUrl(getYouTubeThumbnail(savedNews.getVideo().getExternalFile().getUrl()));
                } else if (platform != null && platform.equalsIgnoreCase("facebook")) {
                    savedNews.getVideo().getExternalFile().setThumbnailUrl(thumbnailExtractorService.extractFacebookThumbnail(savedNews.getVideo().getExternalFile().getUrl()));
                } else if (platform != null && platform.equalsIgnoreCase("twitter")) {
                    String thumbnailUrl = thumbnailExtractorService.extractTwitterThumbnail(savedNews.getVideo().getExternalFile().getUrl());
                    if (thumbnailUrl != null) {
                        savedNews.getVideo().getExternalFile().setThumbnailUrl(thumbnailUrl);
                    } else {
                        savedNews.getVideo().getExternalFile().setThumbnailUrl("https://rtvnews-media.s3.ap-south-1.amazonaws.com/images/twitter_thumbnail.png");
                    }
                } else {
                    savedNews.getVideo().getExternalFile().setThumbnailUrl("https://rtvnews-media.s3.ap-south-1.amazonaws.com/images/twitter_thumbnail.png");
                }
            }
        }
        log.info("🔔 [createNews] notify param value: {}", notify);
        if (notify) {
            log.info("🔔 [createNews] notify=true. Preparing Kafka notification event for newsId={}, title={}",
                    savedNews.getId(), news.getTitle());
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("title", "Latest News");
            if (news.getBannerImage() != null) {
                eventData.put("description", news.getTitle());
                eventData.put("imageUrl", cloudfrontUrl + "/" + news.getBannerImage().getPath());
                log.info("🔔 [createNews] Using bannerImage for notification. newsId={}", savedNews.getId());
            } else if (news.getVideo() != null) {
                eventData.put("description", news.getTitle());
                if (news.getVideo().getInternalFile() != null) {
                    eventData.put("imageUrl", cloudfrontUrl + "/" + news.getVideo().getInternalFile().getThumbnail().getPath());
                    log.info("🔔 [createNews] Using internal video thumbnail for notification. newsId={}", savedNews.getId());
                } else {
                    eventData.put("imageUrl", youtubeIconUrl);
                    log.info("🔔 [createNews] Using YouTube icon for notification. newsId={}", savedNews.getId());
                }
            } else {
                StoryCard storyCard = news.getStoryCards().get(0);
                eventData.put("description", storyCard.getDescription());
                eventData.put("imageUrl", cloudfrontUrl + "/" + storyCard.getBannerImage().getPath());
                log.info("🔔 [createNews] Using storyCard banner for notification. newsId={}", savedNews.getId());
            }

            eventData.put("path", "/news/" + savedNews.getId());
            log.info("🔔 [createNews] About to produce event to Kafka topic='{}', eventType=NEW_POST, newsId={}, path={}",
                    KafkaTopicConfig.NOTIFICATIONS_CHANNEL, savedNews.getId(), eventData.get("path"));
            produceEventToKafka(EventType.NEW_POST, null, KafkaTopicConfig.NOTIFICATIONS_CHANNEL, eventData);
            log.info("✅ [createNews] Kafka event produced successfully for newsId={}, path={}",
                    savedNews.getId(), eventData.get("path"));
        } else {
            log.info("⏭️ [createNews] notify=false. Skipping notification for newsId={}", savedNews.getId());
        }
        log.info("✅ [createNews] News saved to DB. newsId={}, title={}", savedNews.getId(), news.getTitle());
        return newsRepository.save(savedNews);
    }

    String generateShareUrl(String id) {
        return "https://app.rtvlive.com/news/" + id;
    }

    @Override
    public Page<NewsModel> getAllNews(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }


//    @Override
//    public Page<NewsModel> getAllNewsFiltered(List<String> districts, List<CategoryType> categories, Pageable pageable) {
//        if ((districts == null || districts.isEmpty()) && (categories == null || categories.isEmpty())) {
//            return newsRepository.findAll(pageable);
//        }
//        if (categories == null || categories.isEmpty()) {
//            return newsRepository.findByDistrictIn(districts, pageable);
//        }
//        if (districts == null || districts.isEmpty()) {
//            return newsRepository.findByCategoryTypeIn(categories, pageable);
//        }
//        return newsRepository.findAllWithFilters(districts, categories, pageable);
//    }


//    @Override
//    public Page<NewsModel> getAllNewsFiltered(
//            List<String> districts,
//            List<CategoryType> categories,
//            Pageable pageable
//    ) {
//        boolean includeNullDistrict = districts != null && districts.contains(null);
//        boolean includeNullCategory = categories != null && categories.contains(null);
//
//        // Remove actual nulls before passing to repository
//        if (districts != null) {
//            districts = districts.stream()
//                    .filter(Objects::nonNull)
//                    .toList();
//        }
//
//        if (categories != null) {
//            categories = categories.stream()
//                    .filter(Objects::nonNull)
//                    .toList();
//        }
//
//        // CASE 1️⃣: No filters → return all
//        if ((districts == null || districts.isEmpty()) && (categories == null || categories.isEmpty())) {
//            return newsRepository.findAll(pageable);
//        }
//
//        // CASE 2️⃣: Both include null
//        if (includeNullDistrict && includeNullCategory) {
//            return newsRepository.findWithNullableDistrictAndCategory(districts, categories, pageable);
//        }
//
//        // CASE 3️⃣: Only district includes null
//        if (includeNullDistrict) {
//            return newsRepository.findWithNullableDistrictAndCategory(districts, categories, pageable);
//        }
//
//        // CASE 4️⃣: Only category includes null
//        if (includeNullCategory) {
//            return newsRepository.findWithNullableDistrictAndCategory(districts, categories, pageable);
//        }
//
//        // CASE 5️⃣: Normal filters
//        if (districts != null && !districts.isEmpty() && categories != null && !categories.isEmpty()) {
//            return newsRepository.findAllWithFilters(districts, categories, pageable);
//        }
//
//        // CASE 6️⃣: Only district
//        if (categories == null || categories.isEmpty()) {
//            return includeNullDistrict
//                    ? newsRepository.findByDistrictInOrDistrictIsNull(districts, pageable)
//                    : newsRepository.findByDistrictIn(districts, pageable);
//        }
//
//        // CASE 7️⃣: Only category
//        if (districts == null || districts.isEmpty()) {
//            return includeNullCategory
//                    ? newsRepository.findByCategoryTypeInOrCategoryTypeIsNull(categories, pageable)
//                    : newsRepository.findByCategoryTypeIn(categories, pageable);
//        }
//
//        return Page.empty(pageable);
//    }

    @Override
    public List<NewsModel> getAllNewsFiltered(
            List<String> districts,
            List<CategoryType> categories,
            Pageable pageable,
            String appVersion
    ) {
        // Normalize null inputs
        if (districts == null) districts = new ArrayList<>();
        if (categories == null) categories = new ArrayList<>();

        districts = districts.stream().filter(Objects::nonNull).toList();
        categories = categories.stream().filter(Objects::nonNull).toList();

        // Always sort by latest updatedAt
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        // ✔ CASE 1: No filters → Return all with SORT applied
        if (districts.isEmpty() && categories.isEmpty()) {
            return newsRepository.findAll(sortedPageable).toList();
        }

        // ✔ CASE 2: Filters present → Build query
        List<Criteria> criteriaList = new ArrayList<>();

        if (!districts.isEmpty()) {
            criteriaList.add(Criteria.where("district").in(districts));
        }

        if (!categories.isEmpty()) {
            criteriaList.add(Criteria.where("categoryType").in(categories));
        }

        Criteria finalCriteria = criteriaList.isEmpty()
                ? new Criteria()
                : new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));

        Query query = new Query(finalCriteria).with(sortedPageable);

        List<NewsModel> mainContent = mongoTemplate.find(query, NewsModel.class);


        if (mainContent.size() >= 10) {
            return mainContent;
        }

        if ( !isVersionEqualOrLessThan(appVersion, "5.5.5")) {
            return mainContent;
        }

        // Use Set to avoid duplicates
        Set<NewsModel> combined = new LinkedHashSet<>(mainContent);

        // Case 1: Only category filter applied → fetch rest categories
        if (districts.isEmpty() && !categories.isEmpty()) {
            List<CategoryType> restCategories = getRemainingCategories(categories);

            if (!restCategories.isEmpty()) {
                Query extraQuery = new Query(Criteria.where("categoryType").in(restCategories))
                        .with(sort)
                        .limit(100);

                combined.addAll(mongoTemplate.find(extraQuery, NewsModel.class));
            }
        }

        // Case 2: Only district filter applied → fetch rest districts
        else if (!districts.isEmpty() && categories.isEmpty()) {

            List<String> restDistricts = districtService.getRemainingDistricts(districts); // FIXED

            if (!restDistricts.isEmpty()) {
                Query extraQuery = new Query(Criteria.where("district").in(restDistricts))
                        .with(sort)
                        .limit(100);

                combined.addAll(mongoTemplate.find(extraQuery, NewsModel.class));
            }
        }

        // Case 3: Both filters applied → fetch remaining both
        else {
            List<String> restDistricts = districtService.getRemainingDistricts(districts);
            List<CategoryType> restCategories = getRemainingCategories(categories);

            List<Criteria> extraCriteria = new ArrayList<>();
            extraCriteria.add(Criteria.where("district").in(restDistricts));
            extraCriteria.add(Criteria.where("categoryType").in(restCategories));

            Query extraQuery = new Query(
                    new Criteria().andOperator(extraCriteria.toArray(new Criteria[0]))
            ).with(sort).limit(100);

            combined.addAll(mongoTemplate.find(extraQuery, NewsModel.class));
        }

        // Return final list (no pagination)
        return new ArrayList<>(combined);
    }






    @Override
    public NewsModel getNewsById(String id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
    }

    @Override
    public void deleteNews(String id) {
        newsRepository.deleteById(id);
    }

//    @Override
//    public Page<NewsModel> searchNews(String query, Pageable pageable) {
//        return newsRepository.searchByTitleDescriptionOrStoryCards(query, pageable);
//    }

    @Override
    public Page<NewsModel> searchNews(String query, Pageable pageable) {

        boolean hasQuery = query != null && !query.isBlank();

        if (!hasQuery) {
            return newsRepository.findAll(pageable);
        }

        return newsRepository.searchEverywhere(query, pageable);
    }


    @Override
    public NewsModel updateNews(String id, NewsModel updatedNews) throws Exception {
        NewsModel existing = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with id " + id));

        // Update top-level fields
        if (updatedNews.getTitle() != null) existing.setTitle(updatedNews.getTitle());
        if (updatedNews.getDescription() != null) existing.setDescription(updatedNews.getDescription());
        if(updatedNews.getReadMoreUrl()!=null) existing.setReadMoreUrl(updatedNews.getReadMoreUrl());
        if (updatedNews.getBannerImage() != null) {
            existing.setBannerImage(updatedNews.getBannerImage());
            existing.setVideo(null);
            existing.setStoryCards(null);
        }

//        if (updatedNews.getVideo() != null){
////            existing.setVideo(updatedNews.getVideo());
//
//            existing.setBannerImage(null);
//            existing.setStoryCards(null);
//        }
        if (updatedNews.getVideo() != null) {
            Video incomingVideo = updatedNews.getVideo();
            Video newVideo = new Video();

            if (incomingVideo.getExternalFile() != null) {
                ExternalFile ef = incomingVideo.getExternalFile();
                newVideo.setExternalFile(ef);

                String platform = thumbnailExtractorService.platformFromUrl(ef.getUrl());
//
//                String platform = thumbnailExtractorService.platformFromUrl(incomingVideo.getExternalFile().getUrl());
//                if (platform != null && platform.equalsIgnoreCase("youtube")) {
//                    newVideo.getExternalFile().setThumbnailUrl(getYouTubeThumbnail(incomingVideo.getExternalFile().getUrl()));
//                } else if (platform != null && platform.equalsIgnoreCase("facebook")) {
//                    newVideo.getExternalFile().setThumbnailUrl(thumbnailExtractorService.extractFacebookThumbnail(incomingVideo.getExternalFile().getUrl()));
//                } else if (platform != null && platform.equalsIgnoreCase("twitter")) {
//                    String thumbnailUrl = thumbnailExtractorService.extractTwitterThumbnail(incomingVideo.getExternalFile().getUrl());
//                    if (thumbnailUrl != null) {
//                        newVideo.getExternalFile().setThumbnailUrl(thumbnailUrl);
//                    } else {
//                        newVideo.getExternalFile().setThumbnailUrl("https://rtvnews-media.s3.ap-south-1.amazonaws.com/images/twitter_thumbnail.png");
//                    }
//                }


                if ("youtube".equalsIgnoreCase(platform)) {
                    ef.setThumbnailUrl(getYouTubeThumbnail(ef.getUrl()));
                } else if ("facebook".equalsIgnoreCase(platform)) {
                    ef.setThumbnailUrl(thumbnailExtractorService.extractFacebookThumbnail(ef.getUrl()));
                } else if ("twitter".equalsIgnoreCase(platform)) {
                    String thumbnailUrl = thumbnailExtractorService.extractTwitterThumbnail(ef.getUrl());
                    ef.setThumbnailUrl(thumbnailUrl != null
                            ? thumbnailUrl
                            : "https://rtvnews-media.s3.ap-south-1.amazonaws.com/images/twitter_thumbnail.png"
                    );
                }

//                newVideo.setExternalFile(incomingVideo.getExternalFile());

                newVideo.setInternalFile(null);
                existing.setBannerImage(null);
                existing.setStoryCards(null);
            } else if (incomingVideo.getInternalFile() != null) {
                // Case 3: Internal video
                newVideo.setInternalFile(incomingVideo.getInternalFile());
                newVideo.setExternalFile(null);
                existing.setBannerImage(null);
                existing.setStoryCards(null);
            }
            existing.setVideo(newVideo);
        }

        // New fields
        if (updatedNews.getDistrict() != null)
            existing.setDistrict(updatedNews.getDistrict());

        if (updatedNews.getCategoryType() != null)
            existing.setCategoryType(updatedNews.getCategoryType());
        if (updatedNews.getTags() != null) {
            existing.setTags(updatedNews.getTags());
        }

        if (updatedNews.getShareUrl() != null) existing.setShareUrl(updatedNews.getShareUrl());

        if (updatedNews.getStoryCards() != null) {
            List<StoryCard> finalCards = new ArrayList<>();

            for (StoryCard card : updatedNews.getStoryCards()) {
                if (card.getId() == null) {
                    // 🔹 New card → assign id + timestamps
                    card.setId(new ObjectId().toString());
                    card.setCreatedAt(Instant.now());
                    card.setUpdatedAt(Instant.now());
                    finalCards.add(card);
                } else {
                    // 🔹 Existing card → find in DB
                    StoryCard existingCard = existing.getStoryCards() != null
                            ? existing.getStoryCards().stream()
                            .filter(c -> c.getId().equals(card.getId()))
                            .findFirst()
                            .orElse(null)
                            : null;

                    if (existingCard != null) {
                        // Preserve old createdAt, update fields & updatedAt
                        card.setCreatedAt(existingCard.getCreatedAt());
                        card.setUpdatedAt(Instant.now());
                    } else {
                        // Edge case: ID sent but not found → treat like new
                        card.setCreatedAt(Instant.now());
                        card.setUpdatedAt(Instant.now());
                    }
                    finalCards.add(card);
                }
            }

            existing.setStoryCards(finalCards);
            existing.setVideo(null);
            existing.setBannerImage(null);
        }

        // Save updated entity
        return newsRepository.save(existing);
    }

    @Override
    public NewsLiveTvDto getLiveTvUrl() {
        String url = "https://english.rtvlive.com/wp-json/wl/v1/rtv-settings-api";
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String rawUrl = response.getBody();

                // remove surrounding quotes if present
                String cleanUrl = rawUrl.replaceAll("^\"|\"$", "");
                String fixedUrl = cleanUrl.replace("\\/", "/").replace("\\", "");
                return new NewsLiveTvDto(fixedUrl);
            } else {
                System.err.println("Failed to fetch Live TV URL, status: " + response.getStatusCode());
                return new NewsLiveTvDto(null);
            }
        } catch (Exception e) {
            System.err.println("Error while fetching Live TV URL: " + e.getMessage());
            return new NewsLiveTvDto(null);
        }
    }

    @Override
    public Page<ArchivedNewsModel> getArchivedNews(Pageable pageable) {
        return archivedNewsRepository.findAll(pageable);
    }


    @Override
    public EventService getEventService() {
        return eventService;
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return Arrays.stream(CategoryType.values())
                .filter(cat -> cat != CategoryType._NA) // ✅ exclude NA
                .map(cat -> new CategoryResponse(cat.getId(), cat.getLabel(), cat.getIconUrl()))
                .collect(Collectors.toList());
    }

    public  List<CategoryType> getRemainingCategories(List<CategoryType> excludeList) {
        return Arrays.stream(CategoryType.values())
                .filter(category -> !excludeList.contains(category))
                .collect(Collectors.toList());
    }

    public boolean isVersionEqualOrLessThan(String currentVersion, String targetVersion) {
        String[] c = currentVersion.split("\\.");
        String[] t = targetVersion.split("\\.");

        int max = Math.max(c.length, t.length);

        for (int i = 0; i < max; i++) {
            int cv = i < c.length ? Integer.parseInt(c[i]) : 0;
            int tv = i < t.length ? Integer.parseInt(t[i]) : 0;

            if (cv < tv) return true;      // current < target
            if (cv > tv) return false;     // current > target
        }

        return true; // equal
    }


}

