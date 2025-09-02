package org.microservices.formservice.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to enable caching in the application.
 * This enables the use of caching annotations like @Cacheable, @CacheEvict, and @CachePut.
 */
@Configuration
@EnableCaching
public class CacheConfig {
    // The @EnableCaching annotation triggers the Spring Framework to create proxies around
    // methods annotated with caching annotations like @Cacheable, @CacheEvict, and @CachePut.
    // By default, Spring uses a simple in-memory cache based on ConcurrentHashMap.
    // For production environments, consider configuring a more robust caching solution like Redis or Caffeine.
}