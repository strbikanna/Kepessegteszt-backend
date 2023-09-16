-- users
INSERT INTO USERS(id, username, email, password, first_name, last_name)
VALUES (1, 'student_user', 'imaStudent@gmail.com', 'strongHashed11', 'Student', 'Simon');

INSERT INTO USERS(id, username, email, password, first_name, last_name)
VALUES (2, 'smart_student', 'smarty11@gmail.com', 'hashedValue##', 'Smart', 'Martha');

INSERT INTO USERS(id, username, email, password, first_name, last_name)
VALUES (3, 'teacher_user', 'imaTeacher@education.hu', 'easyPie', 'Teacher', 'Teresa');

-- roles
INSERT INTO ROLES(id, role_name)
VALUES(1, 'teacher');

INSERT INTO ROLES(id, role_name)
VALUES(2, 'scientist');

INSERT INTO ROLES(id, role_name)
VALUES(3, 'student');

INSERT INTO ROLES(id, role_name)
VALUES(4, 'admin');

-- user roles
INSERT INTO USER_ROLES(user_id, role_id)
VALUES(1, 3);

INSERT INTO USER_ROLES(user_id, role_id)
VALUES(2, 3);

INSERT INTO USER_ROLES(user_id, role_id)
VALUES(3, 1);

-- contacts
INSERT INTO CONTACTS(user_id, contact_id)
VALUES (1,3);
