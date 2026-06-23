package org.sleepless_artery.lesson_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.EnableKafka;


@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = true)
@EnableKafka
@SpringBootApplication
public class LessonServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LessonServiceApplication.class, args);
    }

}
