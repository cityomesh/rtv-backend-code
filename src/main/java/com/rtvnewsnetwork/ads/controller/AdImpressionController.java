package com.rtvnewsnetwork.ads.controller;

import com.rtvnewsnetwork.ads.dto.AdImpressionRequest;
import com.rtvnewsnetwork.ads.dto.AdImpressionResponse;
import com.rtvnewsnetwork.ads.model.AdImpression;
import com.rtvnewsnetwork.ads.service.AdImpressionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adImpressions")
@RequiredArgsConstructor
public class AdImpressionController {

    private final AdImpressionService service;


//    @PostMapping
//    public ResponseEntity<AdImpression> addImpression(
//            @RequestBody AdImpressionRequest request
//    ) {
//        return ResponseEntity.ok(service.addImpression(request));
//    }
//
//    @PatchMapping("/{id}")
//    public ResponseEntity<AdImpression> updateViewFrequency(
//            @PathVariable String id,
//            @RequestBody AdImpressionRequest request
//    ) {
//        return ResponseEntity.ok(service.updateViewFrequency(id, request));
//    }


    @GetMapping
    public ResponseEntity<List<AdImpressionResponse>> getByDeviceId(
            @RequestParam String deviceId
    ) {
        return ResponseEntity.ok(service.getByDeviceIdWithAds(deviceId));
    }

    @PostMapping("/increment")
    public ResponseEntity<AdImpression> incrementViewFrequency(
            @RequestParam String adId,
            @RequestParam String deviceId
    ) {
        return ResponseEntity.ok(
                service.incrementViewCount(adId, deviceId)
        );
    }
}
