package org.sleepless_artery.auth_service.common.logging.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.sleepless_artery.auth_service.common.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.auth_service.common.logging.annotation.BusinessEvent;
import org.sleepless_artery.auth_service.credential.model.Credential;
import org.sleepless_artery.auth_service.role.model.Role;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Aspect for logging business events.
 * <p>
 * Logs method execution start, success, and failure for methods annotated with {@link BusinessEvent}.
 * Extracts key fields from method arguments: id, emailAddress, roleName.
 * Differentiates logging levels for external service errors vs other exceptions.
 */
@Slf4j
@Aspect
@Component
public class BusinessEventAspect {

    /**
     * Around advice that logs the start, success, and failure of methods annotated with {@link BusinessEvent}.
     *
     * @param joinPoint the join point representing the method invocation
     * @param event     the {@link BusinessEvent} annotation instance
     * @return the result of the intercepted method
     * @throws Throwable if the intercepted method throws an exception
     */
    @Around("@annotation(event)")
    public Object around(ProceedingJoinPoint joinPoint, BusinessEvent event) throws Throwable {
        var method = joinPoint.getSignature().toShortString();
        var logEvent = event.value();

        Map<String, Object> ctx = extractBusinessContext(joinPoint);
        log.info("{} method={} {}", logEvent.name() + "_STARTED", method, ctx);

        try {
            var result = joinPoint.proceed();
            log.info("{} method={} {}", logEvent.name() + "_SUCCESS", method, ctx);
            return result;
        } catch (Exception e) {
            if (e instanceof ExternalServiceUnavailableException) {
                log.warn("{} method={} {} error={}", logEvent.name() + "_FAILED", method, ctx, e.getMessage());
            } else {
                log.error("{} method={} {} error={}", logEvent.name() + "_FAILED", method, ctx, e.toString());
            }
            throw e;
        }
    }


    /**
     * Extracts key business context from method arguments.
     * Fields: id, emailAddress, roleName.
     *
     * @param joinPoint the join point representing the method invocation
     * @return a map of context fields and their values
     */
    private Map<String, Object> extractBusinessContext(JoinPoint joinPoint) {
        Map<String, Object> ctx = new LinkedHashMap<>();

        for (var arg : joinPoint.getArgs()) {
            if (arg == null) continue;

            if (arg instanceof Credential credential) {
                ctx.put("id", credential.getId());
                ctx.put("emailAddress", maskEmail(credential.getEmailAddress()));
            } else if (arg instanceof Role role) {
                ctx.put("id", role.getId());
                ctx.put("roleName", role.getRoleName());
            } else {
                extractIfPresent(ctx, arg, "id");
                extractIfPresent(ctx, arg, "emailAddress");
                extractIfPresent(ctx, arg, "roleName");
            }
        }

        return ctx;
    }


    /**
     * Extracts a field by name from an object via getter or reflection.
     *
     * @param context    map to populate
     * @param arg        object to inspect
     * @param fieldName  field name
     */
    private void extractIfPresent(Map<String, Object> context, Object arg, String fieldName) {
        try {
            var f = arg.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            var value = f.get(arg);

            if (value == null) return;

            if ("emailAddress".equals(fieldName) && value instanceof String email) {
                context.put(fieldName, maskEmail(email));
            } else {
                context.put(fieldName, value);
            }
        } catch (Exception ignored) {}
    }


    /**
     * Masks an email address for secure logging.
     * <p>
     * Example: {@code john.doe@example.com} -> {@code j*****e@example.com}
     *
     * @param email the email to mask
     * @return the masked email or {@code *****} if the input is invalid
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "*****";

        var parts = email.split("@");
        var local = parts[0];
        var domain = parts[1];

        if (local.length() <= 2) {
            local = "*".repeat(local.length());
        } else {
            local = local.charAt(0) + "*".repeat(local.length() - 2) + local.charAt(local.length() - 1);
        }

        return local + "@" + domain;
    }
}
