package com.rtvnewsnetwork.ads.service.impl;

import com.rtvnewsnetwork.ads.dto.AdsRequest;
import com.rtvnewsnetwork.ads.dto.AdsResponse;
import com.rtvnewsnetwork.ads.model.AdsModel;
import com.rtvnewsnetwork.ads.repository.AdsRepository;
import com.rtvnewsnetwork.ads.service.AdsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdsServiceImpl implements AdsService {

    private final AdsRepository adsRepository;

    @Override
    public AdsModel createAds(AdsRequest request) {
        AdsModel banner = new AdsModel();
        mapRequestToModel(request, banner);
        return adsRepository.save(banner);
    }

    @Override
    public AdsModel updateAds(String id, AdsRequest request) {

        AdsModel existingBanner = adsRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Banner not found with id: " + id)
                );

        if (request.getBannerImages() != null) {
            existingBanner.setBannerImages(request.getBannerImages());
        }

        if (request.getLink() != null) {
            existingBanner.setLink(request.getLink());
        }

        if (request.getExpiryDate() != null) {
            existingBanner.setExpiryDate(request.getExpiryDate());
        }

        if (request.getSlotFrequency() != null) {
            existingBanner.setSlotFrequency(request.getSlotFrequency());
        }

        if (request.getViewFrequency() != null) {
            existingBanner.setViewFrequency(request.getViewFrequency());
        }

        return adsRepository.save(existingBanner);
    }




    @Override
    public Page<AdsModel> getAllAds(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return adsRepository.findAll(pageable);
    }


    private AdsResponse mapToResponse(AdsModel ad) {

        List<String> images = ad.getBannerImages() == null
                ? List.of()
                : ad.getBannerImages()
                .stream()
                .map(RelativePath::getPath) // or toString()
                .toList();

        return AdsResponse.builder()
                .id(ad.getId())
                .bannerImages(images)
                .link(ad.getLink())
                .expiryDate(ad.getExpiryDate())
                .slotFrequency(ad.getSlotFrequency())
                .viewFrequency(ad.getViewFrequency())
                .createdAt(ad.getCreatedAt())
                .updatedAt(ad.getUpdatedAt())
                .build();
    }



    private void mapRequestToModel(AdsRequest request, AdsModel banner) {
        banner.setBannerImages(request.getBannerImages());
        banner.setLink(request.getLink());
        banner.setExpiryDate(request.getExpiryDate());
        banner.setSlotFrequency(request.getSlotFrequency());
        banner.setViewFrequency(request.getViewFrequency());
    }


//    private void mergeBannerImages(
//            AdsModel banner,
//            List<RelativePath> incomingImages
//    ) {
//        if (incomingImages == null || incomingImages.isEmpty()) {
//            return;
//        }
//
//        List<RelativePath> existing = banner.getBannerImages();
//        if (existing == null) {
//            existing = new ArrayList<>();
//        }
//
//        // Detect intent: does request reference any existing image?
//        boolean containsExisting = incomingImages.stream()
//                .anyMatch(existing::contains);
//
//        if (containsExisting) {
//            // FINAL STATE MODE → allow removals
//            existing.removeIf(img -> !incomingImages.contains(img));
//        }
//
//        // ADD MODE (always add new ones)
//        for (RelativePath img : incomingImages) {
//            if (!existing.contains(img)) {
//                existing.add(img);
//            }
//        }
//
//        banner.setBannerImages(existing);
//    }

    @Override
    public AdsModel getAdById(String id) {
        return adsRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Ad not found with id: " + id
                        )
                );
    }

    @Override
    public void deleteAdById(String id) {

        if (!adsRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Ad not found with id: " + id
            );
        }

        adsRepository.deleteById(id);
    }






}
