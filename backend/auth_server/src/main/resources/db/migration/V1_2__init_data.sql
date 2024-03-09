-- frontend client, secret not used
insert into oauth2_registered_client(id, client_id, client_secret, client_name, client_authentication_methods,authorization_grant_types, redirect_uris, post_logout_redirect_uris, scopes, client_settings, token_settings)
values ('eeaa-56789-oojk-2401', 'frontend-client-002233',  'cognitive-app', 'xxx-frontend-hhh345', 'none', 'authorization_code', 'http://localhost:4200,http://localhost:4200/games,http://localhost:4200/silent-renew.html', 'http://localhost:4200', 'openid,game',
     '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
     '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",200.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300.000000000]}');

-- postman client, secret: 123
insert into oauth2_registered_client(id, client_id, client_secret, client_name, client_authentication_methods,authorization_grant_types, redirect_uris, scopes, client_settings, token_settings)
values ('eeaw-56h89-o45jk-24c01', 'postman-client-007', '$2a$10$/MfkXXcAQtLaYkOSvoaAFuCSaAGji1aciHeUBfKh5KYwSBsWEzpZC', 'xxx-postman-spy', 'client_secret_basic,none', 'authorization_code,refresh_token', 'https://oauth.pstmn.io/v1/callback', 'openid,game,client.create',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":true,"settings.client.require-authorization-consent":false}',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300.000000000]}');
    

-- users
INSERT INTO USERS(id, username, email, password, first_name, last_name) -- password is strongHashed11
VALUES (1, 'student_user', 'imaStudent@gmail.com', '$2a$10$9M/SoUsEs7Uw//XhT95yl.vuTPUpnkbyFr7FqHpjZUqmmtFqtmrmS', 'Student', 'Simon');

INSERT INTO USERS(id, username, email, password, first_name, last_name) -- őassword is hashedValue##
VALUES (2, 'smart_student', 'smarty11@gmail.com', '$2a$10$gSEsFgTQ0LjwriT9chQdIOt/vFzMyrOTicHx/zHOg9L57nPNpcLLm', 'Smart', 'Martha');

INSERT INTO USERS(id, username, email, password, first_name, last_name) -- password is easyPie
VALUES (3, 'teacher_user', 'imaTeacher@education.hu', '$2a$10$HriyN0j9B1iS0eJ40uqV2.a/dFNMlkZ26.NLbCeRBCecgRAjhp0Ru', 'Teacher', 'Teresa');

INSERT INTO USERS(id, username, email, password, first_name, last_name) -- password is topSecret
VALUES (4, 'admin_man', 'immaadmin@vip.hu', '$2a$10$FQw1GzjQwJc0IDU1ZA92yOjagIngXHVXzdk6l/nFvU7syKpJyafqG', 'Admin', 'Arnold');

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
