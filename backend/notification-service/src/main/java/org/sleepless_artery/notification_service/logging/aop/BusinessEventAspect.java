package org.sleepless_artery.notification_service.logging.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.sleepless_artery.notification_service.logging.annotation.BusinessEvent;
import org.springframework.stereotype.Component;


/**
 * Aspect for logging business events.
 * <p>
 * Logs method execution start, success, and failure for methods annotated with {@link BusinessEvent}.
 */
@Slf4j
@Aspect
@Component
public class BusinessEventAspect {

    @Around("@annotation(event)")
    public Object around(ProceedingJoinPoint joinPoint, BusinessEvent event) throws Throwable {
        var methodName = joinPoint.getSignature().toShortString();
        var logEvent = event.value();

        log.info("{} method={} {}", logEvent.name() + "_STARTED", methodName);

        var result = joinPoint.proceed();
        log.info("{} method={} {}", logEvent.name() + "_SUCCESS", methodName);
        return result;
    }
}