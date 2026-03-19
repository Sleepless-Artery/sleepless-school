CREATE TABLE submission (
    id BIGSERIAL    PRIMARY KEY,
    assignment_id   BIGINT NOT NULL,
    student_id      BIGINT NOT NULL,
    submitted_at    TIMESTAMP,
    score           DOUBLE PRECISION,
    submission_type VARCHAR(31) NOT NULL
);


CREATE INDEX idx_submission_assignment_id ON submission(assignment_id);
CREATE INDEX idx_submission_student_id ON submission(student_id);


CREATE TABLE file_submission (
    submission_id       BIGINT PRIMARY KEY,
    status              VARCHAR(20),
    review_comment      VARCHAR(1000),
    file_key            VARCHAR(255) NOT NULL,
    display_filename    VARCHAR(255),
    CONSTRAINT fk_file_submission
        FOREIGN KEY (submission_id) REFERENCES submission(id) ON DELETE CASCADE
);

CREATE TABLE test_submission (
    submission_id BIGINT PRIMARY KEY,
    CONSTRAINT fk_test_submission
        FOREIGN KEY (submission_id) REFERENCES submission(id) ON DELETE CASCADE
);

CREATE TABLE test_submission_answers (
    submission_id           BIGINT NOT NULL,
    selected_option_index   INTEGER NOT NULL,
    CONSTRAINT fk_test_submission_answers
        FOREIGN KEY (submission_id) REFERENCES test_submission(submission_id) ON DELETE CASCADE
);


CREATE INDEX idx_test_submission_answers_submission_id ON test_submission_answers(submission_id);