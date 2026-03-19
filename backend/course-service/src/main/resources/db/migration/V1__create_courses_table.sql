CREATE TABLE course
(
    id                  BIGSERIAL PRIMARY KEY,
    title               VARCHAR(255) NOT NULL,
    author_id           BIGINT NOT NULL,
    creation_date       DATE NOT NULL,
    last_update_date    DATE NOT NULL,
    description         VARCHAR(2000)
);