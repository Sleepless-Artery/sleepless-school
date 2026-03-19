CREATE TABLE role
(
  id        BIGSERIAL PRIMARY KEY,
  role_name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO role (role_name) VALUES ('USER'), ('ADMIN');

CREATE TABLE credential
(
  id            BIGSERIAL PRIMARY KEY,
  email_address VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE credential_role
(
   id               BIGSERIAL PRIMARY KEY,
   credential_id    BIGINT NOT NULL,
   role_id          BIGINT NOT NULL,
   FOREIGN KEY (credential_id) REFERENCES credential(id),
   FOREIGN KEY (role_id) REFERENCES role(id)
);