package com.example.musify.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Bucket4jConfig {
    @Bean
    public Bucket bucket(){
        long tokens = 3;
        Refill refill = Refill.greedy(tokens, Duration.ofSeconds(30));
        Bandwidth limit = Bandwidth.classic(tokens,refill);

        return Bucket.builder().addLimit(limit).build();
    }
}
