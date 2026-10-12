package com.rtvnewsnetwork.ads.service;


import com.rtvnewsnetwork.ads.dto.AdImpressionRequest;
import com.rtvnewsnetwork.ads.dto.AdImpressionResponse;
import com.rtvnewsnetwork.ads.model.AdImpression;

import java.util.List;

public interface AdImpressionService {

    public List<AdImpressionResponse> getByDeviceIdWithAds(String deviceId);
    AdImpression incrementViewCount(String adId, String deviceId);


}
