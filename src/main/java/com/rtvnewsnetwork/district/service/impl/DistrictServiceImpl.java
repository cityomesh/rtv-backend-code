package com.rtvnewsnetwork.district.service.impl;

import com.rtvnewsnetwork.district.model.DistrictModel;
import com.rtvnewsnetwork.district.repository.DistrictRepository;
import com.rtvnewsnetwork.district.service.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DistrictServiceImpl implements DistrictService {


    @Autowired
    private DistrictRepository districtRepository;

    @Override
    public List<DistrictModel> addDistricts(List<DistrictModel> districts) {
        // Save all districts in one go
        return districtRepository.saveAll(districts);
    }

    @Override
    public List<DistrictModel> getAllDistricts() {
        return districtRepository.findAll(Sort.by("name"));
    }

    @Override
    public List<String> getRemainingDistricts(List<String> selectedDistricts) {
        List<DistrictModel> allDistrictModels = districtRepository.findAll();

        // Convert Model → List of names
        List<String> allDistrictNames = allDistrictModels.stream()
                .map(DistrictModel::getName)
                .filter(Objects::nonNull)
                .toList();

        // Remove user-selected districts
        return allDistrictNames.stream()
                .filter(name -> !selectedDistricts.contains(name))
                .toList();
    }


}
