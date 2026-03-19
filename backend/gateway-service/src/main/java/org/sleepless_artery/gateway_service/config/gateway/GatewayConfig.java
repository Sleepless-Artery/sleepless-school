package org.sleepless_artery.gateway_service.config.gateway;

import org.sleepless_artery.gateway_service.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;


/**
 * API Gateway routing configuration.
 *
 * <p>Defines HTTP routes and request forwarding rules for all platform services.
 * Routes are mapped to corresponding microservices using service discovery
 * with load-balanced URIs ({@code lb://}).</p>
 */
@Configuration
public class GatewayConfig {

    private static final String AUTH_SERVICE = "lb://auth-service";
    private static final String USER_SERVICE = "lb://user-service";
    private static final String COURSE_SERVICE = "lb://course-service";
    private static final String LESSON_SERVICE = "lb://lesson-service";
    private static final String ENROLLMENT_SERVICE = "lb://enrollment-service";
    private static final String ASSIGNMENT_SERVICE = "lb://assignment-service";
    private static final String SUBMISSION_SERVICE = "lb://submission-service";

    private GatewayFilterSpec secured(GatewayFilterSpec f, JwtAuthenticationFilter jwt) {
        return f.filter(jwt.apply(new JwtAuthenticationFilter.Config()))
                .stripPrefix(1);
    }

    /**
     * Defines API Gateway routing rules.
     *
     * <p>Routes incoming HTTP requests to corresponding microservices.</p>
     *
     * @param builder route locator builder
     * @param jwtFilter JWT authentication filter
     * @return configured {@link RouteLocator}
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtAuthenticationFilter jwtFilter) {
        return builder.routes()

                .route("auth-service-protected", r -> r
                        .path("/api/auth/logout",
                                "/api/auth/change-email-address",
                                "/api/auth/confirm-email-address")
                        .filters(f -> secured(f, jwtFilter))
                        .uri(AUTH_SERVICE))

                .route("auth-service-public", r -> r
                        .path("/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/confirm-registration",
                                "/api/auth/forgot-password",
                                "/api/auth/validate-reset-code",
                                "/api/auth/reset-password")
                        .filters(f -> f.stripPrefix(1))
                        .uri(AUTH_SERVICE))


                .route("user-edit", r -> r
                        .path("/api/profile/edit/{id}")
                        .and().method(HttpMethod.PUT)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/profile/edit/(?<id>[^/]+)", "/users/${id}"))
                        .uri(USER_SERVICE))

                .route("user-delete", r -> r
                        .path("/api/profile/delete/{id}")
                        .and().method(HttpMethod.DELETE)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/profile/delete/(?<id>[^/]+)", "/users/${id}"))
                        .uri(USER_SERVICE))

                .route("user-general", r -> r
                        .path("/api/profile/**")
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/profile(?<segment>/.*)?", "/users${segment}"))
                        .uri(USER_SERVICE))

                .route("course-create", r -> r
                        .path("/api/courses/create")
                        .and().method(HttpMethod.POST)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/courses/create(?<segment>/.*)?", "/courses${segment}"))
                        .uri(COURSE_SERVICE))

                .route("course-edit", r -> r
                        .path("/api/courses/edit/{id}")
                        .and().method(HttpMethod.PUT)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/courses/edit/(?<id>[^/]+)", "/courses/${id}"))
                        .uri(COURSE_SERVICE))

                .route("course-delete", r -> r
                        .path("/api/courses/delete/{id}")
                        .and().method(HttpMethod.DELETE)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/courses/delete/(?<id>[^/]+)", "/courses/${id}"))
                        .uri(COURSE_SERVICE))

                .route("course-search", r -> r
                        .path("/api/courses/search")
                        .and().method(HttpMethod.GET)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/courses/search(?<segment>/.*)?", "/courses${segment}"))
                        .uri(COURSE_SERVICE))

                .route("course-get", r -> r
                        .path("/api/courses/{id}")
                        .and().method(HttpMethod.GET)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/courses/(?<id>[^/]+)", "/courses/${id}"))
                        .uri(COURSE_SERVICE))

                .route("course-general", r -> r
                        .path("/api/courses/**")
                        .filters(f -> secured(f, jwtFilter))
                        .uri(COURSE_SERVICE))


                .route("lesson-create", r -> r
                        .path("/api/lessons/create")
                        .and().method(HttpMethod.POST)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/lessons/create(?<segment>/.*)?", "/lessons${segment}"))
                        .uri(LESSON_SERVICE))

                .route("lesson-edit", r -> r
                        .path("/api/lessons/edit/{id}")
                        .and().method(HttpMethod.PUT)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/lessons/edit/(?<id>[^/]+)", "/lessons/${id}"))
                        .uri(LESSON_SERVICE))

                .route("lesson-delete", r -> r
                        .path("/api/lessons/delete/{id}")
                        .and().method(HttpMethod.DELETE)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/lessons/delete/(?<id>[^/]+)", "/lessons/${id}"))
                        .uri(LESSON_SERVICE))

                .route("lesson-general", r -> r
                        .path("/api/lessons/**")
                        .filters(f -> secured(f, jwtFilter))
                        .uri(LESSON_SERVICE))


                .route("enrollment-leave", r -> r
                        .path("/api/leave")
                        .and().method(HttpMethod.DELETE)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/leave(?<segment>/.*)?", "/enrollments${segment}"))
                        .uri(ENROLLMENT_SERVICE))

                .route("enrollment-enroll", r -> r
                        .path("/api/enroll/**")
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/enroll(?<segment>/.*)?", "/enrollments${segment}"))
                        .uri(ENROLLMENT_SERVICE))


                .route("assignment-file-create", r -> r
                        .path("/api/assignments/file/create")
                        .and().method(HttpMethod.POST)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/assignments/file/create(?<segment>/.*)?", "/assignments/file${segment}"))
                        .uri(ASSIGNMENT_SERVICE))

                .route("assignment-file-edit", r -> r
                        .path("/api/assignments/file/edit/{id}")
                        .and().method(HttpMethod.PUT)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/assignments/file/edit/(?<id>[^/]+)", "/assignments/file/${id}"))
                        .uri(ASSIGNMENT_SERVICE))

                .route("assignment-file-update-file", r -> r
                        .path("/api/assignments/file/update/{id}")
                        .and().method(HttpMethod.PUT)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/assignments/file/update/(?<id>[^/]+)", "/assignments/file/${id}/file"))
                        .uri(ASSIGNMENT_SERVICE))

                .route("assignment-file-delete", r -> r
                        .path("/api/assignments/file/delete/{id}")
                        .and().method(HttpMethod.DELETE)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/assignments/file/delete/(?<id>[^/]+)", "/assignments/file/${id}"))
                        .uri(ASSIGNMENT_SERVICE))

                .route("assignment-file-download", r -> r
                        .path("/api/assignments/file/{id}/download")
                        .and().method(HttpMethod.GET)
                        .filters(f -> secured(f, jwtFilter))
                        .uri(ASSIGNMENT_SERVICE))

                .route("assignment-test-create", r -> r
                        .path("/api/assignments/test/create")
                        .and().method(HttpMethod.POST)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/assignments/test/create(?<segment>/.*)?", "/assignments/test${segment}"))
                        .uri(ASSIGNMENT_SERVICE))

                .route("assignment-test-edit", r -> r
                        .path("/api/assignments/test/edit/{id}")
                        .and().method(HttpMethod.PUT)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/assignments/test/edit/(?<id>[^/]+)", "/assignments/test/${id}"))
                        .uri(ASSIGNMENT_SERVICE))

                .route("assignment-test-delete", r -> r
                        .path("/api/assignments/test/delete/{id}")
                        .and().method(HttpMethod.DELETE)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/assignments/test/delete/(?<id>[^/]+)", "/assignments/test/${id}"))
                        .uri(ASSIGNMENT_SERVICE))

                .route("assignment-general", r -> r
                        .path("/api/assignments/**")
                        .filters(f -> secured(f, jwtFilter))
                        .uri(ASSIGNMENT_SERVICE))


                .route("submission-query", r -> r
                        .path("/api/submissions/query/**")
                        .and().method(HttpMethod.GET)
                        .filters(f -> secured(f, jwtFilter))
                        .uri(SUBMISSION_SERVICE))

                .route("submission-review", r -> r
                        .path("/api/submissions/review/{id}")
                        .filters(f -> secured(f, jwtFilter))
                        .uri(SUBMISSION_SERVICE))

                .route("submission-complete-test", r -> r
                        .path("/api/submissions/complete-test")
                        .and().method(HttpMethod.POST)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/submissions/complete-test(?<segment>/.*)?", "/submissions/solutions${segment}"))
                        .uri(SUBMISSION_SERVICE))

                .route("submission-file-download", r -> r
                        .path("/api/submissions/file/{id}/download")
                        .and().method(HttpMethod.GET)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/submissions/file/(?<id>[^/]+)/download",
                                        "/submissions/solutions/file/${id}/download"))
                        .uri(SUBMISSION_SERVICE))

                .route("submission-file-upload", r -> r
                        .path("/api/submissions/file/upload")
                        .and().method(HttpMethod.POST)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/submissions/file/upload(?<segment>/.*)?",
                                        "/submissions/solutions/file${segment}"))
                        .uri(SUBMISSION_SERVICE))

                .route("submission-file-update", r -> r
                        .path("/api/submissions/file/update/{id}")
                        .and().method(HttpMethod.PUT)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/submissions/file/update/(?<id>[^/]+)", "/submissions/solutions/file/${id}"))
                        .uri(SUBMISSION_SERVICE))

                .route("submission-delete", r -> r
                        .path("/api/submissions/delete/{id}")
                        .and().method(HttpMethod.DELETE)
                        .filters(f -> secured(f, jwtFilter)
                                .rewritePath("/submissions/delete/(?<id>[^/]+)", "/submissions/solutions/${id}"))
                        .uri(SUBMISSION_SERVICE))

                .route("submission-general", r -> r
                        .path("/api/submissions/**")
                        .filters(f -> secured(f, jwtFilter))
                        .uri(SUBMISSION_SERVICE))

                .build();
    }
}