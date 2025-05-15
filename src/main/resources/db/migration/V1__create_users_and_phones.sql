CREATE TABLE user_app
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL,
    password   VARCHAR(100) NOT NULL,
    active     BOOLEAN      NOT NULL,
    created    TIMESTAMP    NOT NULL,
    modified   TIMESTAMP    NOT NULL,
    last_login TIMESTAMP,
    token      VARCHAR(255),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE phone
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    number       VARCHAR(50) NOT NULL,
    city_code    VARCHAR(10),
    country_code VARCHAR(10),
    user_id      UUID        NOT NULL,
    CONSTRAINT fk_user_phone FOREIGN KEY (user_id) REFERENCES user_app (id) ON DELETE CASCADE,
    CONSTRAINT uq_phone_number UNIQUE (number)
);