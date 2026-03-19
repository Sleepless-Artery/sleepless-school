package org.sleepless_artery.submission_service.repository.test;

import org.sleepless_artery.submission_service.model.test.TestSubmission;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TestSubmissionRepository extends JpaRepository<TestSubmission, Long> {

}
