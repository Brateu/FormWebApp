package org.microservices.formservice.config;

import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.microservices.formservice.exception.UserNotFoundException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for Feign clients to handle different content types.
 */
@Configuration
@Slf4j
public class FeignConfig {

    /**
     * Creates a custom decoder that can handle various content types including text/html.
     * This is needed because sometimes services might return error pages as HTML.
     *
     * @return A decoder that can handle various content types
     */
    @Bean
    public Decoder feignDecoder() {
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();

        List<MediaType> supportedMediaTypes = new ArrayList<>(jacksonConverter.getSupportedMediaTypes());

        supportedMediaTypes.add(MediaType.TEXT_HTML);
        jacksonConverter.setSupportedMediaTypes(supportedMediaTypes);

        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringConverter.setSupportedMediaTypes(List.of(MediaType.TEXT_HTML));
        HttpMessageConverter<Object> longHtmlConverter = new HttpMessageConverter<Object>() {
            @Override
            public boolean canRead(Class<?> clazz, MediaType mediaType) {
                return (clazz == Long.class || clazz == long.class) && 
                       mediaType != null && mediaType.includes(MediaType.TEXT_HTML);
            }

            @Override
            public boolean canWrite(Class<?> clazz, MediaType mediaType) {
                return false;
            }

            @Override
            public List<MediaType> getSupportedMediaTypes() {
                return List.of(MediaType.TEXT_HTML);
            }

            @Override
            public Object read(Class<?> clazz, org.springframework.http.HttpInputMessage inputMessage) 
                    throws java.io.IOException {
                try {
                    String content = new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    log.debug("Attempting to extract Long from HTML content: {}", content);

                    if (content.contains("<html") || content.contains("<!DOCTYPE html")) {
                        log.debug("Received HTML content: {}", content);

                        if (content.contains("User not found")) {
                            String email = "unknown";
                            int emailIndex = content.indexOf("email:");
                            if (emailIndex > 0) {
                                int endIndex = content.indexOf("</", emailIndex);
                                if (endIndex > emailIndex) {
                                    email = content.substring(emailIndex + 6, endIndex).trim();
                                }
                            }
                            throw new org.microservices.formservice.exception.UserNotFoundException(email);
                        }

                        throw new RuntimeException("Received HTML error page");
                    }
                    return Long.parseLong(content.trim());
                } catch (NumberFormatException e) {
                    log.error("Failed to parse Long from HTML content", e);
                    throw new RuntimeException("Failed to parse Long from HTML content", e);
                }
            }

            @Override
            public void write(Object o, MediaType contentType, org.springframework.http.HttpOutputMessage outputMessage) {
                throw new UnsupportedOperationException("Writing not supported");
            }
        };

        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(longHtmlConverter);
        converters.add(jacksonConverter);
        converters.add(stringConverter);

        ObjectFactory<HttpMessageConverters> objectFactory = 
            () -> new HttpMessageConverters(converters);

        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
    }

    /**
     * Creates a standard encoder for Feign clients.
     *
     * @return A standard Spring encoder
     */
    @Bean
    public Encoder feignEncoder() {
        return new SpringEncoder(
            () -> new HttpMessageConverters(new MappingJackson2HttpMessageConverter())
        );
    }

    /**
     * Creates a custom error decoder that can handle HTML error responses.
     * This is needed because sometimes services might return error pages as HTML.
     *
     * @return A custom error decoder
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    /**
     * Custom error decoder that handles HTML responses and other error cases.
     */
    public static class CustomErrorDecoder implements ErrorDecoder {
        private final ErrorDecoder defaultErrorDecoder = new Default();

        @Override
        public Exception decode(String methodKey, Response response) {
            String email = extractEmailFromMethodKey(methodKey);

            if (response.headers().containsKey("Content-Type") && 
                response.headers().get("Content-Type").toString().contains("text/html")) {

                log.error("Received HTML response for method: {}, status: {}", 
                          methodKey, response.status());
                if (response.status() == 404) {
                    return new UserNotFoundException(email);
                } else if (response.status() >= 400 && response.status() < 500) {
                    try {
                        String responseBody = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
                        log.error("Error response body: {}", responseBody);

                        if (responseBody.contains("User not found")) {
                            return new UserNotFoundException(email);
                        }

                        return FeignException.errorStatus(methodKey, response);
                    } catch (IOException e) {
                        log.error("Error reading response body", e);
                        return FeignException.errorStatus(methodKey, response);
                    }
                } else {
                    try {
                        String responseBody = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
                        log.error("Error response body: {}", responseBody);

                        if (responseBody.contains("User not found")) {
                            return new UserNotFoundException(email);
                        }

                        return new RuntimeException("Error calling " + methodKey + 
                                                  ": Received HTML response with status " + 
                                                  response.status() + ". Response body contains HTML content.");
                    } catch (IOException e) {
                        log.error("Error reading response body", e);
                        return new RuntimeException("Error calling " + methodKey + 
                                                  ": Received HTML response with status " + 
                                                  response.status());
                    }
                }
            } else if (response.status() == 404) {
                return new UserNotFoundException(email);
            }
            return defaultErrorDecoder.decode(methodKey, response);
        }

        /**
         * Extracts the email from the method key if possible.
         * 
         * @param methodKey the method key
         * @return the email or "unknown" if not found
         */
        private String extractEmailFromMethodKey(String methodKey) {
            String email = "unknown";
            if (methodKey.contains("getUserIdByEmail")) {
                String[] parts = methodKey.split("/");
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].equals("email") && i + 1 < parts.length) {
                        email = parts[i + 1];
                        break;
                    }
                }
            }
            return email;
        }
    }
}