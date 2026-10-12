// package com.rtvnewsnetwork.config.firebase;


// import com.amazonaws.services.s3.AmazonS3;
// import com.amazonaws.services.s3.model.S3Object;
// import com.google.auth.oauth2.GoogleCredentials;
// import com.google.firebase.FirebaseApp;
// import com.google.firebase.FirebaseOptions;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class FirebaseConfig {

//     private final AmazonS3 amazonS3;
//     private final String bucketName;
//     private final String sdkFileName;

//     public FirebaseConfig(AmazonS3 amazonS3,
//                           @Value("${rtv.secrets.bucketname}") String bucketName,
//                           @Value("${rtv.secrets.firebase_json}") String sdkFileName) {
//         this.amazonS3 = amazonS3;
//         this.bucketName = bucketName;
//         this.sdkFileName = sdkFileName;
//     }

//     @Bean
//     public FirebaseApp firebaseApp() throws Exception {
//         S3Object obj = amazonS3.getObject(bucketName, sdkFileName);

//         FirebaseOptions options = FirebaseOptions.builder()
//                 .setCredentials(GoogleCredentials.fromStream(obj.getObjectContent()))
//                 .build();

//         if (FirebaseApp.getApps().isEmpty()) {
//             return FirebaseApp.initializeApp(options);
//         } else {
//             return FirebaseApp.getInstance();
//         }
//     }
// }

package com.rtvnewsnetwork.config.firebase;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    private final AmazonS3 amazonS3;
    private final String bucketName;
    private final String sdkFileName;

    public FirebaseConfig(AmazonS3 amazonS3,
                          @Value("${rtv.secrets.bucketname:}") String bucketName,
                          @Value("${rtv.secrets.firebase_json:}") String sdkFileName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
        this.sdkFileName = sdkFileName;
    }

    @Bean
    public FirebaseApp firebaseApp() {
        try {
            if (bucketName == null || bucketName.isEmpty() ||
                sdkFileName == null || sdkFileName.isEmpty()) {
                logger.warn("Firebase S3 config missing — FCM notifications disabled");
                return null;
            }

            if (amazonS3 == null) {
                logger.warn("AmazonS3 bean not available — Firebase disabled");
                return null;
            }

            S3Object obj = amazonS3.getObject(bucketName, sdkFileName);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(obj.getObjectContent()))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                logger.info("Firebase initialized from S3");
                return FirebaseApp.initializeApp(options);
            } else {
                return FirebaseApp.getInstance();
            }
        } catch (Exception e) {
            logger.error("Firebase initialization failed: {}", e.getMessage());
            return null;
        }
    }
}
