package com.zzh.ping.push;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author zzh
 * @date 2024/09/10
 */
@Configuration
public class WebClientConfig {
    @Value("${pong.url}") // 获取pong服务的URL
    private String pongUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.create(pongUrl);
    }
}
