package org.sleepless_artery.assignment_service.logging.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.sleepless_artery.assignment_service.logging.annotation.Loggable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Aspect that intercepts methods annotated with {@link Loggable}
 * and logs their execution details.
 */
@Slf4j
@Aspect
@Component
public class LoggableAspect {

    /**
     * Around advice that logs the start, success, and failure of methods annotated with {@link Loggable}.
     *
     * @param joinPoint the join point representing the method invocation
     * @return the result of the method execution
     * @throws Throwable if the intercepted method throws an exception
     */
    @Around("@annotation(org.sleepless_artery.assignment_service.logging.annotation.Loggable)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        var start = System.currentTimeMillis();

        var method = joinPoint.getSignature().toShortString();
        Map<String, Object> args = extractArgs(joinPoint);

        log.info("method_start event=method_invocation method={} args={}", method, args);

        try {
            var result = joinPoint.proceed();
            var duration = System.currentTimeMillis() - start;

            log.info("method_success method={} durationMs={} result={}", method, duration, safeToLog(result));

            return result;
        } catch (Exception e) {
            var duration = System.currentTimeMillis() - start;

            log.error("method_error method={} durationMs={} error={} args={}", method, duration, e, args);

            throw e;
        }
    }


    /**
     * Extracts method parameter names and values into a map for logging.
     *
     * @param proceedingJoinPoint the join point representing the method invocation
     * @return map of parameter names to safe-to-log values
     */
    private Map<String, Object> extractArgs(ProceedingJoinPoint proceedingJoinPoint) {
        var names = ((CodeSignature) proceedingJoinPoint.getSignature()).getParameterNames();
        var values = proceedingJoinPoint.getArgs();

        Map<String, Object> map = new LinkedHashMap<>();

        for (var i = 0; i < names.length; i++) {
            map.put(names[i], safeToLog(values[i]));
        }
        return map;
    }


    /**
     * Converts objects into a form safe for logging.
     *
     * @param obj the object to log
     * @return object safe for logging
     */
    private Object safeToLog(Object obj) {
        if (obj == null) return null;

        if (obj instanceof MultipartFile f) {
            return Map.of("filename", Objects.requireNonNull(f.getOriginalFilename()), "size", f.getSize());
        }

        return obj.toString();
    }
}