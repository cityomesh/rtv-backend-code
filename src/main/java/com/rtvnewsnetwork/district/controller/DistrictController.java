package com.rtvnewsnetwork.district.controller;


import com.rtvnewsnetwork.district.model.DistrictModel;
import com.rtvnewsnetwork.district.service.DistrictService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/district")
@RequiredArgsConstructor
public class DistrictController {

    @Autowired
    private DistrictService districtService;

    @Operation(
            summary = "Add districts",
            description = "Creates multiple district records in one request.",
            tags = { "District Management" }
    )
    @PostMapping
    public List<DistrictModel> addDistricts(@RequestBody List<DistrictModel> districts) {
        return districtService.addDistricts(districts);
    }

    @Operation(
            summary = "Get all districts",
            description = "Fetches all available districts.",
            tags = { "District Management" }
    )
    @GetMapping
    public List<DistrictModel> getAllDistricts() {
        return districtService.getAllDistricts();
    }
}
