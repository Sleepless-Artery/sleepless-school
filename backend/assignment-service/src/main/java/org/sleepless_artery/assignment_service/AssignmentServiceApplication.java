package org.sleepless_artery.assignment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.EnableKafka;


@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = true)
@EnableKafka
@SpringBootApplication
public class AssignmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssignmentServiceApplication.class, args);
	}

}
