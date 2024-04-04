CREATE TABLE IF NOT EXISTS user_custom
(
    id         SERIAL PRIMARY KEY,
    first_name VARCHAR(255)        NOT NULL,
    last_name  VARCHAR(255)        NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL UNIQUE,
    password   VARCHAR(255)        NOT NULL,
    role       varchar(50)         NOT NULL DEFAULT 'ROLE_USER',
    image      TEXT,
    created_at TIMESTAMP                    DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP                    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS jwt_token
(
    id         SERIAL PRIMARY KEY,
    token      VARCHAR(255) NOT NULL UNIQUE,
    revoked    BOOLEAN      NOT NULL,
    user_id    BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_custom (id)
);

CREATE TABLE IF NOT EXISTS post
(
    id            SERIAL PRIMARY KEY,
    approved      BOOLEAN NOT NULL DEFAULT FALSE,
    body          TEXT    NOT NULL,
    title         TEXT    NOT NULL,
    user_likes    BIGINT[]         DEFAULT '{}',
    user_dislikes BIGINT[]         DEFAULT '{}',
    user_id       BIGINT,
    tags          TEXT[],
    created_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    images        TEXT[]           default '{}',
    FOREIGN KEY (user_id) REFERENCES user_custom (id)
);



CREATE TABLE IF NOT EXISTS comment
(
    id            SERIAL PRIMARY KEY,
    body          TEXT NOT NULL,
    title         TEXT NOT NULL,
    user_likes    BIGINT[]  DEFAULT '{}',
    user_dislikes BIGINT[]  DEFAULT '{}',
    post_id       BIGINT,
    user_id       BIGINT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    images        TEXT[]    default '{}',
    FOREIGN KEY (post_id) REFERENCES post (id),
    FOREIGN KEY (user_id) REFERENCES user_custom (id)
);

CREATE TABLE IF NOT EXISTS exercise
(
    id            SERIAL PRIMARY KEY,
    muscle_groups TEXT[]  NOT NULL,
    approved      BOOLEAN NOT NULL DEFAULT FALSE,
    body          TEXT    NOT NULL,
    title         TEXT    NOT NULL,
    user_likes    BIGINT[]         DEFAULT '{}',
    user_dislikes BIGINT[]         DEFAULT '{}',
    user_id       BIGINT,
    created_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    images        TEXT[]           default '{}',
    videos        TEXT[]           default '{}',
    FOREIGN KEY (user_id) REFERENCES user_custom (id)
);

CREATE TABLE IF NOT EXISTS training
(
    id            SERIAL PRIMARY KEY,
    approved      BOOLEAN NOT NULL DEFAULT FALSE,
    body          TEXT    NOT NULL,
    title         TEXT    NOT NULL,
    user_likes    BIGINT[]         DEFAULT '{}',
    user_dislikes BIGINT[]         DEFAULT '{}',
    user_id       BIGINT,
    price         DECIMAL,
    exercises     BIGINT[],
    created_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    images        TEXT[]           default '{}',
    FOREIGN KEY (user_id) REFERENCES user_custom (id)

);

CREATE TABLE IF NOT EXISTS order_custom
(
    id               SERIAL PRIMARY KEY,
    shipping_address TEXT    NOT NULL,
    payed            BOOLEAN NOT NULL DEFAULT FALSE,
    trainings        BIGINT[],
    user_id          BIGINT,
    created_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_custom (id)
);