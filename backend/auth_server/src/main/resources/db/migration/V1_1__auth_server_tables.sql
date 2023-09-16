CREATE TABLE USERS(
    id int PRIMARY KEY,
    username varchar(50) UNIQUE NOT NULL,
    email varchar(50) NOT NULL,
    password varchar(50) NOT NULL,
    first_name varchar(50) NOT NULL,
    last_name varchar(50) NOT NULL
);
CREATE TABLE ROLES(
    id int PRIMARY KEY,
    role_name varchar(20) UNIQUE NOT NULL
);

CREATE TABLE USER_ROLES(
    user_id int,
    role_id int,
    constraint fk_user FOREIGN KEY(user_id) REFERENCES users(id),
    constraint fk_role FOREIGN KEY(role_id) REFERENCES roles(id)
);

CREATE TABLE CONTACTS(
    user_id int,
    contact_id int,
    constraint fk_user_contact FOREIGN KEY(user_id) REFERENCES users(id),
    constraint fk_user_contact_2 FOREIGN KEY(contact_id) REFERENCES users(id)
);