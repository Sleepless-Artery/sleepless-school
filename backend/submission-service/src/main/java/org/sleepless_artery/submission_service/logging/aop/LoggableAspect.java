package org.sleepless_artery.submission_service.logging.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Aspect
@Component
public class LoggableAspect {

    @Around("@annotation(org.sleepless_artery.submission_service.logging.annotations.Loggable)")
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


    private Map<String, Object> extractArgs(ProceedingJoinPoint pjp) {
        var names = ((CodeSignature) pjp.getSignature()).getParameterNames();
        var values = pjp.getArgs();

        Map<String, Object> map = new LinkedHashMap<>();

        for (var i = 0; i < names.length; i++) {
            map.put(names[i], safeToLog(values[i]));
        }
        return map;
    }


    private Object safeToLog(Object obj) {
        if (obj == null) return null;

        if (obj instanceof MultipartFile f) {
            return Map.of("filename", Objects.requireNonNull(f.getOriginalFilename()), "size", f.getSize());
        }

        return obj.toString();
    }
}
