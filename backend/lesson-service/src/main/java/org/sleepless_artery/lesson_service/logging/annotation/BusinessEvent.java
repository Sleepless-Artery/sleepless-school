package org.sleepless_artery.lesson_service.logging.annotation;

import org.sleepless_artery.lesson_service.logging.event.LogEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation used to mark business operations
 * that should be logged as application events.
 *
 * <p>The event type is defined by {@link LogEvent}.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BusinessEvent {
    LogEvent value();
}