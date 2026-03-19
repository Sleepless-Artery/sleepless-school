CREATE TABLE enrollment
(
    id          BIGSERIAL PRIMARY KEY,
    student_id  BIGINT NOT NULL,
    course_id   BIGINT NOT NULL
);