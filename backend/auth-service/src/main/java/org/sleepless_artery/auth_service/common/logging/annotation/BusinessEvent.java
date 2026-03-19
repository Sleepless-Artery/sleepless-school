package org.sleepless_artery.auth_service.common.logging.annotation;

import org.sleepless_artery.auth_service.common.logging.event.LogEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BusinessEvent {
    LogEvent value();
}