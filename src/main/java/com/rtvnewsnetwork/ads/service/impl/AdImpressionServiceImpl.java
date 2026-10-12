package com.rtvnewsnetwork.ads.service.impl;


import com.rtvnewsnetwork.ads.dto.AdImpressionRequest;
import com.rtvnewsnetwork.ads.dto.AdImpressionResponse;
import com.rtvnewsnetwork.ads.dto.AdsResponse;
import com.rtvnewsnetwork.ads.model.AdImpression;
import com.rtvnewsnetwork.ads.model.AdsModel;
import com.rtvnewsnetwork.ads.repository.AdImpressionRepository;
import com.rtvnewsnetwork.ads.repository.AdsRepository;
import com.rtvnewsnetwork.ads.service.AdImpressionService;
import com.rtvnewsnetwork.config.model.UploadedFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdImpressionServiceImpl implements AdImpressionService {

    private final AdImpressionRepository repository;
    private final AdsRepository adsRepository;




//        @Override
//        public List<AdImpressionResponse> getByDeviceIdWithAds(String deviceId) {
//
//            System.out.println("Fetching ads for deviceId: " + deviceId);
//
//            // 1. Fetch impressions
//            List<AdImpression> impressions =
//                    repository.findByDeviceId(deviceId);
//
//            System.out.println("Impressions found: " + impressions.size());
//
//            Map<String, AdImpression> impressionMap = impressions.stream()
//                    .collect(Collectors.toMap(
//                            AdImpression::getAdId,
//                            Function.identity()
//                    ));
//
//            // 2. Decide ads source
//            List<AdsModel> adsToProcess;
//
//            if (impressions.isEmpty()) {
//                adsToProcess = adsRepository.findAll();
//                System.out.println("No impressions -> returning ALL ads");
//            } else {
//                adsToProcess = adsRepository.findAllById(impressionMap.keySet());
//                System.out.println("Impressions exist -> returning ONLY impression ads");
//            }
//
//            Instant now = Instant.now();
//
//            // 3. Apply filters
//            return adsToProcess.stream()
//                    .map(ad -> {
//
//                        System.out.println("Processing adId: " + ad.getId());
//
//                        // Expiry check
//                        if (ad.getExpiryDate() != null &&
//                                !ad.getExpiryDate().isAfter(now)) {
//                            System.out.println("Filtered: expired");
//                            return null;
//                        }
//
//                        AdImpression impression =
//                                impressionMap.get(ad.getId());
//
//                        // View limit check
//                        if (impression != null &&
//                                ad.getViewFrequency() != null &&
//                                impression.getViewFrequency() != null &&
//                                impression.getViewFrequency() >= ad.getViewFrequency()) {
//
//                            System.out.println(
//                                    "Filtered: view limit reached | current="
//                                            + impression.getViewFrequency()
//                                            + " limit=" + ad.getViewFrequency()
//                            );
//                            return null;
//                        }
//
//                        return AdImpressionResponse.builder()
//                                .adId(ad.getId())
//                                .deviceId(deviceId)
//                                .viewFrequency(
//                                        impression != null
//                                                ? impression.getViewFrequency()
//                                                : 0
//                                )
//                                .lastViewedAt(
//                                        impression != null
//                                                ? impression.getLastViewedAt()
//                                                : null
//                                )
//                                .ad(ad)
//                                .build();
//                    })
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.toList());
//        }

//    @Override
//    public List<AdImpressionResponse> getByDeviceIdWithAds(String deviceId) {
//
//        List<AdImpression> impressions =
//                repository.findByDeviceId(deviceId);
//
//        if (impressions.isEmpty()) {
//            return List.of();
//        }
//
//        // Fetch all ads in ONE DB call
//        Map<String, AdsModel> adsMap = adsRepository
//                .findAllById(
//                        impressions.stream()
//                                .map(AdImpression::getAdId)
//                                .collect(Collectors.toSet())
//                )
//                .stream()
//                .collect(Collectors.toMap(AdsModel::getId, Function.identity()));
//
//        Instant now = Instant.now();
//
//        return impressions.stream()
//                .map(impression -> {
//                    AdsModel ad = adsMap.get(impression.getAdId());
//                    if (ad == null) {
//                        return null;
//                    }
//
//                    if (ad.getExpiryDate() != null &&
//                            !ad.getExpiryDate().isAfter(now)) {
//                        return null;
//                    }
//
//                    if (ad.getViewFrequency() != null &&
//                            impression.getViewFrequency() >= ad.getViewFrequency()) {
//                        return null;
//                    }
//
//                    return AdImpressionResponse.builder()
//                            .adId(impression.getAdId())
//                            .deviceId(impression.getDeviceId())
//                            .viewFrequency(impression.getViewFrequency())
//                            .lastViewedAt(impression.getLastViewedAt())
//                            .ad(ad)
//                            .build();
//                })
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//    }

        @Override
        public List<AdImpressionResponse> getByDeviceIdWithAds(String deviceId) {

            System.out.println("Fetching ads for deviceId: " + deviceId);

            // 1. Fetch impressions
            List<AdImpression> impressions =
                    repository.findByDeviceId(deviceId);

            System.out.println("Impressions found: " + impressions.size());

            Map<String, AdImpression> impressionMap = impressions.stream()
                    .collect(Collectors.toMap(
                            AdImpression::getAdId,
                            Function.identity()
                    ));

            // 2. Decide ads source
            List<AdsModel> adsToProcess;

            if (impressions.isEmpty()) {
                adsToProcess = adsRepository.findAll();
                System.out.println("No impressions -> returning ALL ads");
            } else {
                adsToProcess = adsRepository.findAllById(impressionMap.keySet());
                System.out.println("Impressions exist -> returning ONLY impression ads");
            }

            Instant now = Instant.now();

            // 3. Apply filters
            return adsToProcess.stream()
                    .map(ad -> {

                        System.out.println("Processing adId: " + ad.getId());

                        // Expiry check
                        if (ad.getExpiryDate() != null &&
                                !ad.getExpiryDate().isAfter(now)) {
                            System.out.println("Filtered: expired");
                            return null;
                        }

                        AdImpression impression =
                                impressionMap.get(ad.getId());

                        // View limit check
                        if (impression != null &&
                                ad.getViewFrequency() != null &&
                                impression.getViewFrequency() != null &&
                                impression.getViewFrequency() >= ad.getViewFrequency()) {

                            System.out.println(
                                    "Filtered: view limit reached | current="
                                            + impression.getViewFrequency()
                                            + " limit=" + ad.getViewFrequency()
                            );
                            return null;
                        }

                        return AdImpressionResponse.builder()
                                .adId(ad.getId())
                                .deviceId(deviceId)
                                .viewFrequency(
                                        impression != null
                                                ? impression.getViewFrequency()
                                                : 0
                                )
                                .lastViewedAt(
                                        impression != null
                                                ? impression.getLastViewedAt()
                                                : null
                                )
                                .ad(mapAdToResponse(ad))
                                .build();
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }



//    private AdsResponse mapAdToResponse(AdsModel ad) {
//
//        List<String> imagePaths = ad.getBannerImages() == null
//                ? List.of()
//                : ad.getBannerImages()
//                .stream()
//                .map(UploadedFile.RelativePath::getPath) // or toString()
//                .toList();
//
//        return AdsResponse.builder()
//                .id(ad.getId())
//                .bannerImages(imagePaths)
//                .link(ad.getLink())
//                .expiryDate(ad.getExpiryDate())
//                .slotFrequency(ad.getSlotFrequency())
//                .viewFrequency(ad.getViewFrequency())
//                .createdAt(ad.getCreatedAt())
//                .updatedAt(ad.getUpdatedAt())
//                .build();
//    }



    @Override
    public AdImpression incrementViewCount(String adId, String deviceId) {

        AdImpression impression = repository
                .findByAdIdAndDeviceId(adId, deviceId)
                .orElseGet(() -> {
                    AdImpression newImpression = new AdImpression();
                    newImpression.setAdId(adId);
                    newImpression.setDeviceId(deviceId);
                    newImpression.setViewFrequency(0); // starts at 0
                    return newImpression;
                });

        // increment view count
        Integer current = impression.getViewFrequency();
        impression.setViewFrequency(current == null ? 1 : current + 1);

        impression.setLastViewedAt(Instant.now());

        return repository.save(impression);
    }


    private AdsResponse mapAdToResponse(AdsModel ad) {
        return AdsResponse.builder()
                .id(ad.getId())
                .bannerImages(
                        ad.getBannerImages() == null
                                ? List.of()
                                : ad.getBannerImages()
                                .stream()
                                .map(UploadedFile.RelativePath::getPath)
                                .toList()
                )
                .link(ad.getLink())
                .expiryDate(ad.getExpiryDate())
                .slotFrequency(ad.getSlotFrequency())
                .viewFrequency(ad.getViewFrequency())
                .createdAt(ad.getCreatedAt())
                .updatedAt(ad.getUpdatedAt())
                .build();
    }





}
