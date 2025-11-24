package org.example.remedy.infrastructure.storage.s3;

import org.example.remedy.global.config.S3Config;
import org.example.remedy.global.properties.S3Properties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {
                S3Config.class,
                S3Properties.class,
                S3StorageService.class
        })
        @EnableAutoConfiguration(exclude = {
                HibernateJpaAutoConfiguration.class,
                DataSourceAutoConfiguration.class
        })
public class S3ConnectionTest {


    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Properties s3Properties;

    @Autowired
    private S3StorageService s3StorageService;

    @Test
    @DisplayName("S3 버킷에 정상적으로 연결되야 함")
    void connectBucket() {
        HeadBucketResponse headBucketResponse = s3Client.headBucket(
                builder -> builder.bucket(s3Properties.getBucket())
        );

        assertThat(headBucketResponse).isNotNull();
        System.out.println("S3 연결 성공! 버킷 접근 가능: " + s3Properties.getBucket());
    }

    @Test
    @DisplayName("파일 업로드 하고나서 접근 가능한 URL이 반환되야 함")
    void uploadFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "dummy data".getBytes()
        );

        String url = s3StorageService.uploadFile(file);
        System.out.println("업로드 성공! 접근 URL: " + url);
    }

}
