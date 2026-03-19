package org.sleepless_artery.assignment_service.logging.annotation;

import org.sleepless_artery.assignment_service.logging.event.LogEvent;

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
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessEvent {
    LogEvent value();
}

