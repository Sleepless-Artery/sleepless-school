package org.sleepless_artery.enrollment_service.logging.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.sleepless_artery.enrollment_service.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.enrollment_service.logging.annotation.BusinessEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Aspect for logging business events.
 * <p>
 * Logs method execution start, success, and failure for methods annotated with {@link BusinessEvent}.
 * Extracts key fields from method arguments: id, studentId, courseId.
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
     * Fields: id, title, authorId.
     */
    private Map<String, Object> extractBusinessContext(ProceedingJoinPoint joinPoint) {
        Map<String, Object> context = new LinkedHashMap<>();

        for (var arg : joinPoint.getArgs()) {
            if (arg == null) continue;

            extractField(context, arg, "id");
            extractField(context, arg, "studentId");
            extractField(context, arg, "courseId");
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
                context.put(fieldName, value);
            }
        } catch (Exception ignored) {}
    }
}