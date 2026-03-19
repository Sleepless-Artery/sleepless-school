package org.sleepless_artery.auth_service.common.logging.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.sleepless_artery.auth_service.common.logging.annotation.Loggable;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;


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
    @Around("@annotation(org.sleepless_artery.auth_service.common.logging.annotation.Loggable)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        var start = System.currentTimeMillis();

        var method = joinPoint.getSignature().toShortString();
        Map<String, Object> args = extractArgs(joinPoint);

        log.info("method_start event=method_invocation method={} args={}", method, args);

        try {
            var result = joinPoint.proceed();
            var duration = System.currentTimeMillis() - start;

            log.info("method_success method={} durationMs={} result={}", method, duration, result);

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
            map.put(names[i], values[i].toString());
        }
        return map;
    }
}