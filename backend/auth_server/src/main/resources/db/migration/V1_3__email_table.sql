CREATE TABLE email
(
    id               int PRIMARY KEY AUTO_INCREMENT,
    verification_key varchar(51) UNIQUE NOT NULL,
    user_id          int,
    valid            datetime,
    constraint fk_user_email FOREIGN KEY (user_id) REFERENCES users (id)
);