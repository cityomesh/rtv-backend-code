package com.rtvnewsnetwork.ads.service;

import com.rtvnewsnetwork.ads.dto.AdsRequest;
import com.rtvnewsnetwork.ads.dto.AdsResponse;
import com.rtvnewsnetwork.ads.model.AdsModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdsService {

    AdsModel createAds(AdsRequest request);

    AdsModel updateAds(String id, AdsRequest request);

    public Page<AdsModel> getAllAds(int page, int size);
    AdsModel getAdById(String id);
    void deleteAdById(String id);


}
