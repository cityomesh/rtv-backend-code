package com.rtvnewsnetwork.otp.service;


import com.rtvnewsnetwork.common.exception.ExternalServiceException;
import com.rtvnewsnetwork.otp.model.OtpException;
import com.rtvnewsnetwork.otp.model.OtpModel;
import com.rtvnewsnetwork.otp.model.OtpResponse;
import com.rtvnewsnetwork.otp.repository.OtpRepository;
import com.rtvnewsnetwork.user.model.User;
import com.rtvnewsnetwork.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {

    private final UserService userService;
    private final RestTemplate restTemplate;
    private final OtpRepository otpRepository;
    private final OtpServiceVia2factor otpServiceVia2factor;
    private final String fixedOtp;
    private final String fixedMobileNumber;
    public OtpServiceImpl(
            UserService userService,
            RestTemplate restTemplate,
            OtpRepository otpRepository,
            OtpServiceVia2factor otpServiceVia2factor,
            @Value("${rtv.otp.fixed}") String fixedOtp,
            @Value("${rtv.mobile.number}") String fixedMobileNumber
    ) {
        this.userService = userService;
        this.restTemplate = restTemplate;
        this.otpRepository = otpRepository;
        this.otpServiceVia2factor = otpServiceVia2factor;
        this.fixedOtp = fixedOtp;
        this.fixedMobileNumber = fixedMobileNumber;
    }
//    @Override
//    public OtpResponse sendOtp(String phoneNumber, String apphash) throws OtpException {
//        String otpUrl = otpBaseUrl + "+91" + phoneNumber+"/AUTOGEN2/OTP1"; // Consider appending apphash if API requires it
//
//        try {
//            ResponseEntity<OtpResponse> responseEntity =
//                    restTemplate.getForEntity(otpUrl, OtpResponse.class);
//
//            OtpResponse response = responseEntity.getBody();
//
//            if (responseEntity.getStatusCode() == HttpStatus.OK && response != null) {
//                if ("Success".equalsIgnoreCase(response.getStatus())) {
//                    return response;
//                } else {
//                    throw new OtpException(response.getDetails() != null
//                            ? response.getDetails()
//                            : "OTP sending failed");
//                }
//            } else {
//                throw new OtpException("Invalid response from OTP service");
//            }
//
//        } catch (Exception e) {
//            throw new ExternalServiceException("Failed to send OTP: " + e.getMessage(), e);
//        }
//    }
//


    @Override
    public void sendOtp(String phoneNumber, String apphash) throws OtpException {
        Instant now = Instant.now();

        OtpModel existingOtp = otpRepository.findTopByPhoneNumberOrderByCreatedAtDesc(phoneNumber);

        if (existingOtp != null && Duration.between(existingOtp.getLastSentAt(), now).getSeconds() < 60) {
            throw new OtpException("OTP can not be sent within 1 minute");
        }

        // Generate new OTP
        String otp ;
        if(fixedMobileNumber.equals(phoneNumber)){
            otp = fixedOtp;
        }else{
            otp = generateOtp();
        }
        // Create new OtpModel
        OtpModel otpModel = new OtpModel();
        otpModel.setPhoneNumber(phoneNumber);
        otpModel.setOtp(otp);
        otpModel.setCreatedAt(now);
        otpModel.setExpiresAt(now.plusSeconds(600));
        otpModel.setLastSentAt(now);

        // Save to repository
        otpRepository.save(otpModel);

        // Send SMS
        otpServiceVia2factor.sendOtpVia2factor(phoneNumber, otp);
    }

    @Override
    public User verifyOtp(String phoneNumber, String otp) throws OtpException {
        if(fixedMobileNumber.equals(phoneNumber) && fixedOtp.equals(otp)){
            return userService.findByUsernameElseCreate(fixedMobileNumber);
        }
        OtpModel existingOtp = otpRepository.findTopByPhoneNumberOrderByCreatedAtDesc(phoneNumber);

        if (existingOtp == null) {
            throw new OtpException("OTP not found");
        }

        Instant now = Instant.now();

        // Check OTP conditions
        if (existingOtp.isUsed()) {
            throw new OtpException("Verify with valid OTP");
        }

        if (now.isAfter(existingOtp.getExpiresAt())) {
            throw new OtpException("OTP has expired");
        }

        if (existingOtp.getFailedAttempts() >= 3) {
            throw new OtpException("Maximum OTP attempts exceeded");
        }

        if (existingOtp.getOtp().equals(otp)) {
            // Mark OTP as used
            existingOtp.setUsed(true);
            otpRepository.save(existingOtp);

            // Return user (find or create)
            return userService.findByUsernameElseCreate(phoneNumber);
        } else {
            // Increment failed attempts
            existingOtp.setFailedAttempts(existingOtp.getFailedAttempts() + 1);
            otpRepository.save(existingOtp);

            if (existingOtp.getFailedAttempts() >= 3) {
                throw new OtpException("You have exceeded the maximum attempts");
            } else {
                throw new OtpException("Incorrect OTP");
            }
        }
    }

//    @Override
//    public User verifyOtp(String phoneNumber, String otp) throws OtpException {
//        String otpUrl=otpBaseUrl+"VERIFY3/91"+phoneNumber+"/"+otp;
//        try {
//            ResponseEntity<OtpResponse> responseEntity =
//                    restTemplate.getForEntity(otpUrl, OtpResponse.class);
//
//            OtpResponse response = responseEntity.getBody();
//
//            if (responseEntity.getStatusCode() == HttpStatus.OK && response != null) {
//                if ("Success".equalsIgnoreCase(response.getStatus())) {
//                    return userService.findByUsernameElseCreate("+91"+phoneNumber);
//                } else {
//                    throw new OtpException(response.getDetails() != null
//                            ? response.getDetails()
//                            : "OTP sending failed");
//                }
//            } else {
//                throw new OtpException("Invalid response from OTP service");
//            }
//
//        } catch (Exception e) {
//            throw new ExternalServiceException("Failed to send OTP: " + e.getMessage(), e);
//        }
//    }

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // ensures 6 digits
        return String.valueOf(otp);
    }


}