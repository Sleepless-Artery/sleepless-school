package org.sleepless_artery.auth_service.config.security;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.credential.service.query.CredentialQueryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;


/**
 * Main Spring Security configuration for HTTP requests.
 * Configures CSRF, authentication providers, and authorization rules.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CredentialQueryService queryService;
    private final PasswordEncoder passwordEncoder;

    @Value("${management.endpoints.web.base-path:/internal/actuator}")
    private String actuatorEndpointsPath;

    @Value("${springdoc.swagger-ui.path:/swagger-ui.html}")
    private String swaggerUiPath;

    @Value("${basic-request-path}")
    private String basicRequestPath;

    private static final String ADMIN_ROLE = "ADMIN";


    /**
     * Configures the security filter chain.
     * Disables CSRF, sets stateless session policy, and defines endpoint access rules.
     *
     * @param http the HttpSecurity to modify
     * @return the built SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(daoAuthenticationProvider())
                .authorizeHttpRequests(authorizeRequest -> authorizeRequest
                        .requestMatchers("/grpc/**").permitAll()
                        .requestMatchers("/" + basicRequestPath + "/**").permitAll()
                        .requestMatchers(swaggerUiPath, "/v3/api-docs/**").permitAll()
                        .requestMatchers(actuatorEndpointsPath + "/health").permitAll()
                        .requestMatchers(actuatorEndpointsPath + "/info").permitAll()
                        .requestMatchers(actuatorEndpointsPath +"/**").hasRole(ADMIN_ROLE)
                        .anyRequest().denyAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                );

        return http.build();
    }


    /**
     * Configures the DAO Authentication Provider with custom UserDetailsService.
     *
     * @return DaoAuthenticationProvider bean
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        var daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(queryService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }


    /**
     * Exposes the AuthenticationManager bean.
     *
     * @param authenticationConfiguration the Spring Security configuration
     * @return AuthenticationManager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}