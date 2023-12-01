CREATE TABLE USERS(
                      id         int PRIMARY KEY AUTO_INCREMENT,
                      username   varchar(51) UNIQUE NOT NULL,
                      email      varchar(50)        NOT NULL,
                      password   varchar(50)        NOT NULL,
                      first_name varchar(50)        NOT NULL,
                      last_name  varchar(50)        NOT NULL,
                      enabled    boolean DEFAULT TRUE
);
CREATE TABLE ROLES
(
    role_name varchar(20) UNIQUE NOT NULL PRIMARY KEY
);

CREATE TABLE USER_ROLES(
    user_id int,
    role_id VARCHAR(20),
    constraint fk_user FOREIGN KEY(user_id) REFERENCES users(id),
    constraint fk_role FOREIGN KEY(role_id) REFERENCES roles(role_name)
);

CREATE TABLE CONTACTS(
    user_id int,
    contact_id int,
    constraint fk_user_contact FOREIGN KEY(user_id) REFERENCES users(id),
    constraint fk_user_contact_2 FOREIGN KEY(contact_id) REFERENCES users(id)
);