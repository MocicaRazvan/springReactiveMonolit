DROP TABLE IF EXISTS order_custom;
DROP TABLE IF EXISTS training;
DROP TABLE IF EXISTS exercise;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS jwt_token;
DROP TABLE IF EXISTS user_custom;

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
    user_likes    BIGINT[],
    user_dislikes BIGINT[],
    user_id       BIGINT,
    tags          TEXT[],
    created_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    images        TEXT[],
    FOREIGN KEY (user_id) REFERENCES user_custom (id)
);



CREATE TABLE IF NOT EXISTS comment
(
    id            SERIAL PRIMARY KEY,
    body          TEXT NOT NULL,
    title         TEXT NOT NULL,
    user_likes    BIGINT[],
    user_dislikes BIGINT[],
    post_id       BIGINT,
    user_id       BIGINT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    images        TEXT[],
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
    user_likes    BIGINT[],
    user_dislikes BIGINT[],
    user_id       BIGINT,
    created_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    images        TEXT[],
    videos        TEXT[],
    FOREIGN KEY (user_id) REFERENCES user_custom (id)
);

CREATE TABLE IF NOT EXISTS training
(
    id            SERIAL PRIMARY KEY,
    approved      BOOLEAN NOT NULL DEFAULT FALSE,
    body          TEXT    NOT NULL,
    title         TEXT    NOT NULL,
    user_likes    BIGINT[],
    user_dislikes BIGINT[],
    user_id       BIGINT,
    price         DECIMAL,
    exercises     BIGINT[],
    created_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    images        TEXT[],
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
-- Insert into user_custom
INSERT INTO user_custom (first_name, last_name, email, password, role, created_at, updated_at)
VALUES ('John', 'Doe', 'john.doe@example.com', 'password', 'ROLE_USER', '2021-01-01', '2021-01-01'),
       ('Jane', 'Doe', 'jane.doe@example.com', 'password', 'ROLE_ADMIN', '2021-01-01', '2021-01-01'),
       ('Jim', 'Doe', 'jim.doe@example.com', 'password', 'ROLE_TRAINER', '2021-01-01', '2021-01-01');


-- Insert into jwt_token
INSERT INTO jwt_token (token, revoked, user_id)
VALUES ('token123', FALSE, 1),
       ('token456', FALSE, 2);

-- Insert into post
INSERT INTO post (approved, body, title, user_id, tags, created_at, updated_at)
VALUES (TRUE, 'This is a post body', 'Post Title 1', 1, ARRAY ['tag1','tag2'], '2021-01-01', '2021-01-01'),
       (FALSE, 'Another post body', 'Post Title 2', 2, ARRAY ['tag3','tag4'], '2021-01-01', '2021-01-01');

-- Insert into comment
INSERT INTO comment (body, title, post_id, user_id, created_at, updated_at)
VALUES ('This is a comment body', 'Comment Title 1', 1, 1, '2021-01-01', '2021-01-01'),
       ('Another comment body', 'Comment Title 2', 2, 2, '2021-01-01', '2021-01-01');

-- Insert into exercise
INSERT INTO exercise (muscle_groups, approved, body, title, user_id, created_at, updated_at)
VALUES (ARRAY ['arms','legs'], TRUE, 'Exercise body 1', 'Exercise Title 1', 2, '2021-01-01', '2021-01-01'),
       (ARRAY ['arms','legs'], FALSE, 'Exercise body 2', 'Exercise Title 1', 2, '2021-01-01', '2021-01-01'),
       (ARRAY ['chest','back'], TRUE, 'Exercise body 3', 'Exercise Title 2', 3, '2021-01-01', '2021-01-01'),
       (ARRAY ['chest','legs'], TRUE, 'Exercise body 4', 'Exercise Title 3', 3, '2021-01-01', '2021-01-01');

-- Insert into training
INSERT INTO training (approved, body, title, user_id, price, exercises, created_at, updated_at)
VALUES (TRUE, 'Training body 1', 'Training Title 1', 2, 19.99, ARRAY [1], '2021-01-01', '2021-01-01'),
       (TRUE, 'Training body 2', 'Training Title 2', 3, 29.99, ARRAY [2,3], '2021-01-01', '2021-01-01'),
       (FALSE, 'Training body 3', 'Training Title 3', 3, 39.99, ARRAY [2,3], '2021-01-01', '2021-01-01');

-- Insert into order_custom
INSERT INTO order_custom (shipping_address, payed, trainings, user_id)
VALUES ('123 Fake St.', TRUE, ARRAY [1,2], 1),
       ('456 Real Ave.', FALSE, ARRAY [1], 2);
