package org.sleepless_artery.assignment_service.logging.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.sleepless_artery.assignment_service.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.assignment_service.logging.annotation.BusinessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Aspect for logging business events.
 * <p>
 * Logs method execution start, success, and failure for methods annotated with {@link BusinessEvent}.
 * Extracts key fields from method arguments: id, lessonId, fileKey, displayFilename.
 * Differentiates logging levels for external service errors vs other exceptions.
 */
@Slf4j
@Aspect
@Component
public class BusinessEventAspect {

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
     * Fields: id, lessonId, fileKey, displayFilename.
     */
    private Map<String, Object> extractBusinessContext(JoinPoint joinPoint) {
        Map<String, Object> ctx = new LinkedHashMap<>();

        for (var arg : joinPoint.getArgs()) {
            if (arg == null) continue;

            extractIfPresent(ctx, arg, "id");
            extractIfPresent(ctx, arg, "lessonId");
            extractIfPresent(ctx, arg, "fileKey");
            extractIfPresent(ctx, arg, "displayFilename");

            if (arg instanceof MultipartFile f) {
                ctx.put("uploadedFilename", f.getOriginalFilename());
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
            var v = f.get(arg);
            if (v != null) context.put(fieldName, v);
        } catch (Exception ignored) {}
    }
}
