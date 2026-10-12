package com.rtvnewsnetwork.otp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OtpServiceVia2factor {
    @Value("${rtv.2factor.baseurl}")
    private String otpBaseUrl;
    @Value("${rtv.2factor.apikey}")
    private String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();


    public void sendOtpVia2factor(String phoneNumber, String otp) {
        try {
            // Build request URL
            String url = otpBaseUrl+"API/V1/"+apiKey+"/SMS/"+phoneNumber+"/"+otp;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // Check response
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("OTP sent successfully: " + response.getBody());
            } else {
                throw new RuntimeException("Failed to send OTP: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while sending OTP: " + e.getMessage(), e);
        }
    }
}
