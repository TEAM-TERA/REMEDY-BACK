package org.example.remedy.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "org.example.remedy.infrastructure")
public class FeignClientConfig {
	// FeignClient 빈 등록
}
