// // package com.rtvnewsnetwork.config.aws;


// // import com.amazonaws.auth.AWSStaticCredentialsProvider;
// // import com.amazonaws.auth.BasicAWSCredentials;
// // import com.amazonaws.regions.Regions;
// // import com.amazonaws.services.s3.AmazonS3;
// // import com.amazonaws.services.s3.AmazonS3ClientBuilder;
// // import org.springframework.beans.factory.annotation.Value;
// // import org.springframework.context.annotation.Bean;
// // import org.springframework.context.annotation.Configuration;

// // @Configuration
// // public class AwsS3Config {

// //     private final String accessKeyId;
// //     private final String secretAccessKey;
// //     private final String region;

// //     public AwsS3Config(
// //             @Value("${aws.access_key_id}") String accessKeyId,
// //             @Value("${aws.secret_access_key}") String secretAccessKey,
// //             @Value("${aws.s3.region}") String region) {
// //         this.accessKeyId = accessKeyId;
// //         this.secretAccessKey = secretAccessKey;
// //         this.region = region;
// //     }

// //     @Bean
// //     public AmazonS3 amazonS3() {
// //         BasicAWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);

// //         return AmazonS3ClientBuilder.standard()
// //                 .withRegion(Regions.fromName(region))
// //                 .withCredentials(new AWSStaticCredentialsProvider(credentials))
// //                 .build();
// //     }
// // }

// package com.rtvnewsnetwork.config.aws;

// import com.amazonaws.auth.AWSStaticCredentialsProvider;
// import com.amazonaws.auth.BasicAWSCredentials;
// import com.amazonaws.regions.Regions;
// import com.amazonaws.services.s3.AmazonS3;
// import com.amazonaws.services.s3.AmazonS3ClientBuilder;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class AwsS3Config {

//     private static final Logger logger = LoggerFactory.getLogger(AwsS3Config.class);

//     private final String accessKeyId;
//     private final String secretAccessKey;
//     private final String region;

//     public AwsS3Config(
//             @Value("${aws.access_key_id:}") String accessKeyId,
//             @Value("${aws.secret_access_key:}") String secretAccessKey,
//             @Value("${aws.s3.region:ap-south-1}") String region) {
//         this.accessKeyId = accessKeyId;
//         this.secretAccessKey = secretAccessKey;
//         this.region = region;
//     }

//     @Bean
//     public AmazonS3 amazonS3() {
//         try {
//             if (accessKeyId == null || accessKeyId.isEmpty() ||
//                 secretAccessKey == null || secretAccessKey.isEmpty()) {
//                 logger.warn("AWS credentials missing — S3 features disabled");
//                 return null;
//             }

//             BasicAWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);

//             return AmazonS3ClientBuilder.standard()
//                     .withRegion(Regions.fromName(region))
//                     .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                     .build();
//         } catch (Exception e) {
//             logger.error("AWS S3 init failed: {}", e.getMessage());
//             return null;
//         }
//     }
// }


package com.rtvnewsnetwork.config.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3Config {

    private static final Logger logger = LoggerFactory.getLogger(AwsS3Config.class);

    private final String accessKeyId;
    private final String secretAccessKey;
    private final String region;

    public AwsS3Config(
            @Value("${aws.access_key_id:}") String accessKeyId,
            @Value("${aws.secret_access_key:}") String secretAccessKey,
            @Value("${aws.s3.region:ap-south-1}") String region) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.region = region;
    }

    @Bean
    public AmazonS3 amazonS3() {
        try {
            if (accessKeyId == null || accessKeyId.isEmpty() ||
                secretAccessKey == null || secretAccessKey.isEmpty()) {
                logger.warn("AWS credentials missing — S3 features disabled");
                return null;
            }

            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);

            return AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.fromName(region))
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();
        } catch (Exception e) {
            logger.error("AWS S3 init failed: {}", e.getMessage());
            return null;
        }
    }
}