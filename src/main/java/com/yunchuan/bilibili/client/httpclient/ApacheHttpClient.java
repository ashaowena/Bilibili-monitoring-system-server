package com.yunchuan.bilibili.client.httpclient;


import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class ApacheHttpClient {

    @Bean
    public CloseableHttpClient HttpClient() {
        CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .build();
        return httpClient;
    }




}
