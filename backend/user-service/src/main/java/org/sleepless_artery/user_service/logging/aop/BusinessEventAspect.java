package org.sleepless_artery.user_service.logging.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.sleepless_artery.user_service.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.user_service.logging.annotation.BusinessEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Aspect for logging business events.
 * <p>
 * Logs method execution start, success, and failure for methods annotated with {@link BusinessEvent}.
 * Extracts key fields from method arguments: id, username, emailAddress (masked for security).
 * Differentiates logging levels for external service errors vs other exceptions.
 */
@Slf4j
@Aspect
@Component
public class BusinessEventAspect {

    @Around("@annotation(event)")
    public Object around(ProceedingJoinPoint joinPoint, BusinessEvent event) throws Throwable {
        var methodName = joinPoint.getSignature().toShortString();
        var logEvent = event.value();

        Map<String, Object> context = extractBusinessContext(joinPoint);
        log.info("{} method={} {}", logEvent.name() + "_STARTED", methodName, context);

        try {
            var result = joinPoint.proceed();
            log.info("{} method={} {}", logEvent.name() + "_SUCCESS", methodName, context);
            return result;
        } catch (Exception e) {
            if (e instanceof ExternalServiceUnavailableException) {
                log.warn("{} method={} {} error={}", logEvent.name() + "_FAILED", methodName, context, e.getMessage(), e);
            } else {
                log.error("{} method={} {} error={}", logEvent.name() + "_FAILED", methodName, context, e.getMessage(), e);
            }
            throw e;
        }
    }


    /**
     * Extracts key business context from method arguments.
     * Fields: id, username, emailAddress (masked).
     */
    private Map<String, Object> extractBusinessContext(ProceedingJoinPoint joinPoint) {
        Map<String, Object> context = new LinkedHashMap<>();

        for (var arg : joinPoint.getArgs()) {
            if (arg == null) continue;

            extractField(context, arg, "id");
            extractField(context, arg, "username");
            extractField(context, arg, "emailAddress", true);
        }

        return context;
    }


    /**
     * Extracts a field by name from an object via getter or reflection.
     *
     * @param context    map to populate
     * @param arg        object to inspect
     * @param fieldName  field name
     */
    private void extractField(Map<String, Object> context, Object arg, String fieldName) {
        extractField(context, arg, fieldName, false);
    }


    /**
     * Extracts a field by name from an object via getter or reflection, optionally masking the value.
     *
     * @param context    map to populate
     * @param arg        object to inspect
     * @param fieldName  field name
     * @param mask       whether to mask the value (for sensitive data)
     */
    private void extractField(Map<String, Object> context, Object arg, String fieldName, boolean mask) {
        try {
            Object value;

            var getterName = "get" + StringUtils.capitalize(fieldName);
            try {
                Method getter = arg.getClass().getMethod(getterName);
                value = getter.invoke(arg);
            } catch (NoSuchMethodException ignored) {
                var field = arg.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                value = field.get(arg);
            }

            if (value != null) {
                if (mask && value instanceof String email) {
                    value = maskEmail(email);
                }
                context.put(fieldName, value);
            }
        } catch (Exception ignored) {}
    }

    /**
     * Masks email address for logging.
     * <p>
     * Examples:
     * - a@b.com → a****@b.com
     * - abc@example.com → a**c@example.com
     *
     * @param email email to mask
     * @return masked email
     */
    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) return "****" + email.substring(at);
        return email.charAt(0) + "****" + email.charAt(at - 1) + email.substring(at);
    }
}