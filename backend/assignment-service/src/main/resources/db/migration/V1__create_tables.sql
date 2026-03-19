CREATE TABLE assignment (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(100) NOT NULL,
    lesson_id       BIGINT NOT NULL,
    description     VARCHAR(500),
    max_score       DOUBLE PRECISION NOT NULL,
    deadline        TIMESTAMP,
    assignment_type VARCHAR(31) NOT NULL
);


CREATE INDEX idx_assignment_lesson_id ON assignment(lesson_id);


CREATE TABLE file_assignment (
    assignment_id       BIGINT PRIMARY KEY,
    file_key            VARCHAR(255) NOT NULL UNIQUE,
    display_filename    VARCHAR(255) NOT NULL,
    CONSTRAINT fk_file_assignment
        FOREIGN KEY (assignment_id) REFERENCES assignment(id) ON DELETE CASCADE
);

CREATE TABLE test_assignment (
    assignment_id   BIGINT PRIMARY KEY,
    test_condition  TEXT,
    CONSTRAINT fk_test_assignment
        FOREIGN KEY (assignment_id) REFERENCES assignment(id) ON DELETE CASCADE
);

CREATE TABLE test_assignment_options (
    assignment_id   BIGINT NOT NULL,
    options         TEXT NOT NULL,
    CONSTRAINT fk_test_assignment_options
        FOREIGN KEY (assignment_id) REFERENCES test_assignment(assignment_id) ON DELETE CASCADE
);


CREATE INDEX idx_test_assignment_options_assignment_id ON test_assignment_options(assignment_id);


CREATE TABLE test_assignment_correct_options (
    assignment_id           BIGINT NOT NULL,
    correct_options_indices INTEGER NOT NULL,
    CONSTRAINT fk_test_assignment_correct_options
        FOREIGN KEY (assignment_id) REFERENCES test_assignment(assignment_id) ON DELETE CASCADE
);


CREATE INDEX idx_test_assignment_correct_options_assignment_id ON test_assignment_correct_options(assignment_id);