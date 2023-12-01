-- frontend client
insert into oauth2_registered_client(id, client_id, client_secret, client_name, client_authentication_methods,authorization_grant_types, redirect_uris, post_logout_redirect_uris, scopes, client_settings, token_settings)
values ('eeaa-56789-oojk-2401', 'frontend-client-002233',  'cognitive-app', 'xxx-frontend-hhh345', 'none', 'authorization_code', 'http://localhost:4200,http://localhost:4200/games,http://localhost:4200/silent-renew.html', 'http://localhost:4200', 'openid,game',
     '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
     '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",200.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300.000000000]}');

-- postman client
insert into oauth2_registered_client(id, client_id, client_secret, client_name, client_authentication_methods,authorization_grant_types, redirect_uris, scopes, client_settings, token_settings)
values ('eeaw-56h89-o45jk-24c01', 'postman-client-007', '123', 'xxx-postman-spy', 'client_secret_basic', 'authorization_code', 'https://oauth.pstmn.io/v1/callback', 'openid,game',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300.000000000]}');
    

-- users
INSERT INTO USERS(id, username, email, password, first_name, last_name)
VALUES (1, 'student_user', 'imaStudent@gmail.com', 'strongHashed11', 'Student', 'Simon');

INSERT INTO USERS(id, username, email, password, first_name, last_name)
VALUES (2, 'smart_student', 'smarty11@gmail.com', 'hashedValue##', 'Smart', 'Martha');

INSERT INTO USERS(id, username, email, password, first_name, last_name)
VALUES (3, 'teacher_user', 'imaTeacher@education.hu', 'easyPie', 'Teacher', 'Teresa');

INSERT INTO USERS(id, username, email, password, first_name, last_name)
VALUES (4, 'admin_human', 'immaadmin@vip.hu', 'topSecret', 'Admin', 'Person');

-- roles
INSERT INTO ROLES
VALUES ('TEACHER');

INSERT INTO ROLES
VALUES ('SCIENTIST');

INSERT INTO ROLES
VALUES ('STUDENT');

INSERT INTO ROLES
VALUES ( 'ADMIN');

INSERT INTO ROLES
VALUES ( 'PARENT');

INSERT INTO ROLES
VALUES ('PARENT_REQUEST');

INSERT INTO ROLES
VALUES ('TEACHER_REQUEST');

INSERT INTO ROLES
VALUES ('SCIENTIST_REQUEST');

-- user roles
INSERT INTO USER_ROLES(user_id, role_id)
VALUES (1, 'STUDENT');

INSERT INTO USER_ROLES(user_id, role_id)
VALUES (2, 'STUDENT');

INSERT INTO USER_ROLES(user_id, role_id)
VALUES (3, 'TEACHER');

INSERT INTO USER_ROLES(user_id, role_id)
VALUES (4, 'ADMIN');

-- contacts
INSERT INTO CONTACTS(user_id, contact_id)
VALUES (1,3);
