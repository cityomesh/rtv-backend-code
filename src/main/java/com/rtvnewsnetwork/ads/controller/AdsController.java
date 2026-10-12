package com.rtvnewsnetwork.ads.controller;

import com.rtvnewsnetwork.ads.dto.AdsRequest;
import com.rtvnewsnetwork.ads.dto.AdsResponse;
import com.rtvnewsnetwork.ads.model.AdsModel;
import com.rtvnewsnetwork.ads.service.AdsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
public class AdsController {

    private final AdsService adService;

    @Operation(summary = "Create a new ad")
    @PostMapping
    public AdsModel createBanner(@Valid @RequestBody AdsRequest request) {
        return adService.createAds(request);
    }

    @Operation(summary = "Update an existing ad")
    @PutMapping("/{id}")
    public AdsModel updateBanner(
            @PathVariable String id,
            @Valid @RequestBody AdsRequest request
    ) {
        return adService.updateAds(id, request);
    }

    @Operation(summary = "Get all ads")
    @GetMapping
    public ResponseEntity<Page<AdsModel>> getAllBanners(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adService.getAllAds(page, size));
    }


    @Operation(summary = "Get ad by id")
    @GetMapping("/{id}")
    public ResponseEntity<AdsModel> getAdById(@PathVariable String id) {
        return ResponseEntity.ok(adService.getAdById(id));
    }

    @Operation(summary = "Delete ad by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdById(@PathVariable String id) {
        adService.deleteAdById(id);
        return ResponseEntity.noContent().build();
    }


}
