package org.microservices.userservice.config;

import lombok.RequiredArgsConstructor;
import org.microservices.userservice.security.TokenRequiredFilter;
import org.microservices.userservice.security.oauth2.OAuth2AuthenticationSuccessHandler;
import org.microservices.userservice.security.oauth2.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class for the User Service.
 * Configures web security, authentication, and authorization settings.
 * Enables method-level security and stateless session management.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Service for loading user-specific data.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Service for handling OAuth2 authentication.
     */
    private final CustomOAuth2UserService customOAuth2UserService;

    /**
     * Encoder for password hashing and verification.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Handler for successful OAuth2 authentication.
     */
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    /**
     * Custom filter to enforce token on sensitive endpoints without redirect behavior.
     */
    private final TokenRequiredFilter tokenRequiredFilter;

    /**
     * Configures the security filter chain.
     * Sets up CSRF protection, request authorization rules, session management,
     * and OAuth2 login configuration.
     *
     * @param http HttpSecurity to be configured
     * @return The configured SecurityFilterChain
     * @throws Exception If an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                        })
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                );

        http.addFilterBefore(tokenRequiredFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Creates and configures an authentication provider.
     * Sets the user details service and password encoder.
     *
     * @return Configured DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * Creates an authentication manager.
     *
     * @param config Authentication configuration
     * @return AuthenticationManager instance
     * @throws Exception If an error occurs during creation
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}