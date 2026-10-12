package com.rtvnewsnetwork.auth;

import com.google.api.pathtemplate.ValidationException;
import com.rtvnewsnetwork.config.jwt.JwtTokenUtils;
import com.rtvnewsnetwork.user.model.User;
import com.rtvnewsnetwork.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;
    public AuthServiceImpl(UserService userService, JwtTokenUtils jwtTokenUtils, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtTokenUtils = jwtTokenUtils;
        this.passwordEncoder = passwordEncoder;
    }

//    @Override
//    public ResponseEntity<User> login(LoginRequest loginRequest) {
//        User userDetails = userService.findByPhoneNumber(loginRequest.getPhoneNumber());
//        if (userDetails == null) {
//            throw new UsernameNotFoundException("User not found with username: " + loginRequest.getPhoneNumber());
//        }
//
//        if (passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
//            String jwtToken = jwtTokenUtils.generateToken(userDetails);
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("jwt-token", jwtToken);
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(userDetails);
//        } else {
//            throw new BadCredentialsException("Invalid Credentials");
//        }
//
//    }



    @Override
    public ResponseEntity<User> login(LoginRequest loginRequest) {
        User userDetails = userService.findByPhoneNumber(loginRequest.getPhoneNumber());

        // User not found -> 401
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }

        // Password mismatch -> 401
        if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }


        boolean isAdmin = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Generate JWT token if all good -> 200
        String jwtToken = jwtTokenUtils.generateToken(userDetails);
        HttpHeaders headers = new HttpHeaders();
        headers.set("jwt-token", jwtToken);

        return ResponseEntity.ok()
                .headers(headers)
                .body(userDetails);
    }
    @Override
    public TokenResponse verifyToken(TokenDto tokenDto) {
        try {
            String token = tokenDto.getToken();
            if (token == null || !token.startsWith("Bearer ")) {
                return new TokenResponse(false);
            }

            // Remove "Bearer " prefix
            token = token.substring(7);

            // Parse JWT
            Jws<Claims> claims = jwtTokenUtils.parse(token);
            String userId = claims.getBody().getSubject();

            // Load user from DB
            User user = userService.getUserById(userId);
            if (user == null) {
                return new TokenResponse(false);
            }

            // ✅ Check if user has ADMIN authority
            boolean isAdmin = user.getAuthorities().stream()
                    .anyMatch(auth -> "ADMIN".equals(auth.getAuthority()));

            return new TokenResponse(isAdmin);

        } catch (Exception e) {
            return new TokenResponse(false);
        }
    }

}
