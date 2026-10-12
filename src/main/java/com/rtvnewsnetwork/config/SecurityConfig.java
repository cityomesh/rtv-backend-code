// package com.rtvnewsnetwork.config;

// import com.rtvnewsnetwork.config.jwt.JwtAuthenticationFilter;
// import com.rtvnewsnetwork.config.jwt.JwtTokenUtils;
// import com.rtvnewsnetwork.user.service.UserServiceImpl;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// import java.util.Arrays;
// import java.util.List;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     private final JwtAuthenticationFilter jwtAuthenticationFilter;

//     // Constructor injection ensures Spring provides the managed bean
//     public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
//         this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//     }

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//                 .csrf(csrf -> csrf.disable())
//                 .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS is configured separately
//                 .authorizeHttpRequests(auth -> auth
//                         .requestMatchers(
//                                 "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
//                                 "/auth-docs/**", "/actuator/**")
//                         .permitAll()
//                         .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
//                         .requestMatchers(HttpMethod.HEAD, "/api/**").permitAll()
//                         .requestMatchers(HttpMethod.GET, "/actuator/**", "/auth-docs").permitAll()
//                         .requestMatchers(HttpMethod.POST, "/api/auth/sendOtp").permitAll()
//                         .requestMatchers(HttpMethod.POST, "/api/auth/verifyOtp").permitAll()
//                         .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
//                         .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
//                         .requestMatchers(HttpMethod.POST, "/api/auth/verify-token").permitAll()
//                         .requestMatchers(HttpMethod.POST, "/api/fcmtoken").permitAll()
//                         .requestMatchers(HttpMethod.POST, "/api/news").permitAll()
//                         .requestMatchers(HttpMethod.POST, "/api/news/all").permitAll()
//                         .requestMatchers(HttpMethod.GET, "/api/reels/user").permitAll()
//                         .requestMatchers(HttpMethod.GET, "/api/adImpressions").permitAll()
//                         .requestMatchers(HttpMethod.POST, "/api/adImpressions/increment").permitAll()
//                         .requestMatchers(HttpMethod.GET, "/api/reels/user/all").hasAuthority("USER")
//                         .requestMatchers(HttpMethod.GET, "/api/user").hasAuthority("USER")
//                         .requestMatchers(HttpMethod.GET, "/api/reels/user").hasAuthority("USER")
//                         .requestMatchers(HttpMethod.GET).permitAll()
//                         .anyRequest().authenticated())
//                 .addFilterBefore(
//                         jwtAuthenticationFilter,
//                         UsernamePasswordAuthenticationFilter.class);

//         return http.build();
//     }

//     @Bean
//     public CorsConfigurationSource corsConfigurationSource() {
//         CorsConfiguration config = new CorsConfiguration();
//         config.setAllowedOrigins(List.of("http://localhost:4000", "https://mobile.dev.rtvlive.com",
//                 "http://localhost:3000", "https://rtv-live-1a8c2.web.app", "https://dashboard.rtvlive.com",
//                 "https://api.rtv.zendot.in", "https://dashboard.rtv.zendot.in", "http://api.rtv.zendot.in",
//                 "http://dashboard.rtvlive.com", "http://192.168.1.53:3000", "https://mobile.rtvlive.com",
//                 "http://mobile.rtvlive.com", "http://dev.dashboard.rtvlive.com", "https://dev.dashboard.rtvlive.com",
//                 "https://mobile.rtvlive.com", "http://mobile.rtvlive.com", "https:/api.dev.rawtv.com",
//                 "https://api.rawtv.com", "https://dev.dashboard.rawtv.com", "https://dashboard.rawtv.com"));
//         config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//         config.setAllowedHeaders(List.of("*"));
//         config.setAllowedHeaders(List.of("*"));
//         config.setExposedHeaders(List.of("jwt-token"));
//         config.setAllowCredentials(true);
//         config.setAllowedOriginPatterns(List.of("http://192.168.1.*:3000"));

//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", config);
//         return source;
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }
// }


package com.rtvnewsnetwork.config;

import com.rtvnewsnetwork.config.jwt.JwtAuthenticationFilter;
import com.rtvnewsnetwork.config.jwt.JwtTokenUtils;
import com.rtvnewsnetwork.user.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                                "/auth-docs/**", "/actuator/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.HEAD, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/**", "/auth-docs").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/sendOtp").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/verifyOtp").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/verify-token").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/fcmtoken").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/news").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/news/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reels/user").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/adImpressions").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/adImpressions/increment").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reels/user/all").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET, "/api/user").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET, "/api/reels/user").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                // Local development
                "http://localhost:4000",
                "http://localhost:3000",
                "http://localhost:5173",
                
                // Your RTV Server (NEW)
                "http://103.189.178.115",
                "http://103.189.178.115:4000",
                "http://103.189.178.115:3000",
                "http://103.189.178.115:80",
                
                // Existing production URLs
                "https://mobile.dev.rtvlive.com",
                "https://rtv-live-1a8c2.web.app",
                "https://dashboard.rtvlive.com",
                "https://api.rtv.zendot.in",
                "https://dashboard.rtv.zendot.in",
                "http://api.rtv.zendot.in",
                "http://dashboard.rtvlive.com",
                "https://mobile.rtvlive.com",
                "http://mobile.rtvlive.com",
                "http://dev.dashboard.rtvlive.com",
                "https://dev.dashboard.rtvlive.com",
                "https://api.dev.rawtv.com",
                "https://api.rawtv.com",
                "https://dev.dashboard.rawtv.com",
                "https://dashboard.rawtv.com"
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("jwt-token"));
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of(
                "http://192.168.1.*:3000",
                "http://103.189.178.*:*"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
