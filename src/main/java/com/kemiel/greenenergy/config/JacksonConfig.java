package com.kemiel.greenenergy.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson 全域序列化設定，註冊 JavaTimeModule 支援 Java 8 時間型別。
 */
@Configuration
public class JacksonConfig {

    /**
     * 擴充 Jackson ObjectMapper，支援 LocalDateTime、YearMonth 序列化。
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer(){
        return builder -> builder.modules(new JavaTimeModule());
    }
}