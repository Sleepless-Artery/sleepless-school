package org.sleepless_artery.submission_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;


@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = true)
@EnableKafka
@SpringBootApplication
public class SubmissionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubmissionServiceApplication.class, args);
	}

}
