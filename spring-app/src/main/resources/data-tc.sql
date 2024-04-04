-- Insert into user_custom
INSERT INTO user_custom (first_name, last_name, email, password, role) VALUES
                                                                           ('John', 'Doe', 'john.doe@example.com', 'password', 'ROLE_USER'),
                                                                           ('Jane', 'Doe', 'jane.doe@example.com', 'password', 'ROLE_ADMIN');

-- Insert into jwt_token
INSERT INTO jwt_token (token, revoked, user_id) VALUES
                                                    ('token123', FALSE, 1),
                                                    ('token456', FALSE, 2);

-- Insert into post
INSERT INTO post (approved, body, title, user_id, tags) VALUES
                                                            (TRUE, 'This is a post body', 'Post Title 1', 1, ARRAY['tag1','tag2']),
                                                            (FALSE, 'Another post body', 'Post Title 2', 2, ARRAY['tag3','tag4']);

-- Insert into comment
INSERT INTO comment (body, title, post_id, user_id) VALUES
                                                        ('This is a comment body', 'Comment Title 1', 1, 1),
                                                        ('Another comment body', 'Comment Title 2', 2, 2);

-- Insert into exercise
INSERT INTO exercise (muscle_groups, approved, body, title, user_id) VALUES
                                                                         (ARRAY['arms','legs'], TRUE, 'Exercise body 1', 'Exercise Title 1', 1),
                                                                         (ARRAY['chest','back'], FALSE, 'Exercise body 2', 'Exercise Title 2', 2);

-- Insert into training
INSERT INTO training (approved, body, title, user_id, price, exercises) VALUES
                                                                            (TRUE, 'Training body 1', 'Training Title 1', 1, 19.99, ARRAY[1]),
                                                                            (FALSE, 'Training body 2', 'Training Title 2', 2, 29.99, ARRAY[2]);

-- Insert into order_custom
INSERT INTO order_custom (shipping_address, payed, trainings, user_id) VALUES
                                                                           ('123 Fake St.', TRUE, ARRAY[1], 1),
                                                                           ('456 Real Ave.', FALSE, ARRAY[2], 2);
