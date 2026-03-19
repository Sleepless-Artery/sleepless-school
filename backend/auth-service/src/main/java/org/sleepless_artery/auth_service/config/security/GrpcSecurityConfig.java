package org.sleepless_artery.auth_service.config.security;

import net.devh.boot.grpc.server.security.authentication.BasicGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Security configuration for gRPC server interactions.
 */
@Configuration
public class GrpcSecurityConfig {

    /**
     * Provides an authentication reader for basic gRPC requests.
     *
     * @return GrpcAuthenticationReader bean
     */
    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        return new BasicGrpcAuthenticationReader();
    }
}