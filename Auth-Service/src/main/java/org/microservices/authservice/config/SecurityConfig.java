package org.microservices.authservice.config;

import lombok.RequiredArgsConstructor;
import org.microservices.authservice.security.JwtTokenFilter;
import org.microservices.authservice.security.JwtTokenProvider;
import org.microservices.authservice.service.CustomerUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomerUserDetailService customerUserDetailService;

    /**
     * Creates and configures an instance of {@link AuthenticationManager} using a custom authentication provider.
     *
     * @return an instance of {@link AuthenticationManager} that manages authentication processes
     * @throws Exception if an error occurs while creating the {@link AuthenticationManager}
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return new org.springframework.security.authentication.ProviderManager(authenticationProvider());
    }

    /**
     * Configures and returns an instance of {@link DaoAuthenticationProvider}.
     * This provider is responsible for handling authentication using a custom
     * {@link CustomerUserDetailService} and a password encoder.
     *
     * @return an instance of {@link DaoAuthenticationProvider} configured with a
     *         user details service and password encoder
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customerUserDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configures the security filter chain for the application.
     *
     * This method disables CSRF protection, configures CORS with a custom source,
     * defines authorization rules for HTTP requests, sets stateless session management,
     * and adds a custom JWT filter into the security filter chain.
     *
     * @param http an instance of {@link HttpSecurity} used to configure the security filter chain
     * @return a configured {@link SecurityFilterChain} instance
     * @throws Exception if an error occurs during the configuration of the security filter chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Creates and returns an instance of {@link JwtTokenFilter}.
     *
     * The {@link JwtTokenFilter} is a Spring Security filter used to inspect
     * and validate JWT tokens in incoming HTTP requests. It ensures
     * authentication and authorization based on the token's validity and
     * associated user details.
     *
     * @return an instance of {@link JwtTokenFilter} configured with a
     *         {@link JwtTokenProvider} and {@link CustomerUserDetailService}
     */
    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(jwtTokenProvider, customerUserDetailService);
    }

    /**
     * Configures and returns an instance of {@link CorsConfigurationSource} to handle
     * Cross-Origin Resource Sharing (CORS) settings for the application.
     *
     * This method creates a {@link CorsConfiguration} instance with permissive settings
     * to allow all origins, headers, and methods, while also enabling credentials.
     * The configuration is applied to all endpoints.
     *
     * @return a fully configured {@link CorsConfigurationSource} instance with the specified CORS settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Configures and returns a {@link PasswordEncoder} bean for encoding passwords.
     *
     * This method initializes a {@link BCryptPasswordEncoder} instance with a
     * specified strength parameter, which determines the computational complexity
     * of password encoding and increases resistance to brute-force attacks.
     *
     * @return an instance of {@link BCryptPasswordEncoder} configured with a
     *         strength parameter of 12
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

}