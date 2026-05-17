package com.meditrack.patientservice.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .cacheDefaults(cacheConfiguration(Duration.ofMinutes(10)))
                .withCacheConfiguration("patient-service:patients", cacheConfiguration(Duration.ofMinutes(30)))
                .withCacheConfiguration("patient-service:patient-summaries", cacheConfiguration(Duration.ofMinutes(10)))
                .withCacheConfiguration("patient-service:patient-diseases", cacheConfiguration(Duration.ofMinutes(10)))
                .withCacheConfiguration("patient-service:hospitals", cacheConfiguration(Duration.ofHours(1)))
                .withCacheConfiguration("patient-service:hospital-login-configs", cacheConfiguration(Duration.ofHours(1)))
                .withCacheConfiguration("patient-service:medical-professionals", cacheConfiguration(Duration.ofMinutes(30)))
                .withCacheConfiguration("patient-service:encounters", cacheConfiguration(Duration.ofMinutes(10)));
    }

    private RedisCacheConfiguration cacheConfiguration(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        redisSerializer()));
    }

    private GenericJackson2JsonRedisSerializer redisSerializer() {
        ObjectMapper objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}
