package com.rtvnewsnetwork.district.service;

import com.rtvnewsnetwork.district.model.DistrictModel;

import java.util.List;

public interface DistrictService {
    List<DistrictModel> addDistricts(List<DistrictModel> districts);
    List<DistrictModel> getAllDistricts();
    List<String> getRemainingDistricts(List<String> selectedDistricts);
}
