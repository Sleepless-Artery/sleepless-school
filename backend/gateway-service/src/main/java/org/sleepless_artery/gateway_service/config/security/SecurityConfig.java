package org.sleepless_artery.gateway_service.config.security;

import org.sleepless_artery.gateway_service.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * Security configuration for the API Gateway.
 *
 * <p>Configures WebFlux security settings including request authorization,
 * exception handling, and CORS policy. Authentication is handled by
 * {@link JwtAuthenticationFilter}, therefore all exchanges are permitted
 * at the Spring Security level.</p>
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Configures the reactive security filter chain.
     *
     * @param http security configuration builder
     * @return configured security filter chain
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                .authenticationEntryPoint((exchange, e) ->
                        Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")))
                .accessDeniedHandler((exchange, e) ->
                        Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden"))));

        return http.build();
    }


    /**
     * Configures CORS policy for the gateway.
     *
     * @return configured CORS web filter
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        var corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of(
                "http://localhost",
                "http://localhost:80",
                "http://192.168.49.2",
                "http://sleepless-school.com"
        ));
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}