package com.muradtek.orderservice.config;

import com.muradtek.matching.engine.MatchingEngineService;
import com.muradtek.matching.engine.MatchingEngineServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public MatchingEngineService matchingEngineService() {
        return new MatchingEngineServiceImpl();
    }
}
