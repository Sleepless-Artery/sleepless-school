create table `user`
(
    id              BIGSERIAL PRIMARY KEY ,
    email_address   VARCHAR(255) NOT NULL UNIQUE,
    username        VARCHAR(50) NOT NULL,
    information     VARCHAR(1000)
);