CREATE TABLE users(
                      id         int PRIMARY KEY AUTO_INCREMENT,
                      username   varchar(51) UNIQUE NOT NULL,
                      email      varchar(50)        NOT NULL,
                      password   varchar(350)        NOT NULL,
                      first_name varchar(50)        NOT NULL,
                      last_name  varchar(50)        NOT NULL,
                      enabled    boolean DEFAULT TRUE
);
CREATE TABLE roles
(
    role_name varchar(20) UNIQUE NOT NULL PRIMARY KEY
);

CREATE TABLE user_roles(
    user_id int,
    role_id VARCHAR(20),
    constraint fk_user FOREIGN KEY(user_id) REFERENCES users(id),
    constraint fk_role FOREIGN KEY(role_id) REFERENCES roles(role_name)
);

CREATE TABLE contacts(
    user_id int,
    contact_id int,
    constraint fk_user_contact FOREIGN KEY(user_id) REFERENCES users(id),
    constraint fk_user_contact_2 FOREIGN KEY(contact_id) REFERENCES users(id)
);