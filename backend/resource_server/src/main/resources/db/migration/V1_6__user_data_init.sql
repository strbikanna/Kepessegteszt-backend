-- example users
insert into user(id,first_name, last_name, username)
values(1,'Theresa', 'Teacher', 'teacher_user');

insert into user(id,first_name, last_name, username)
values(2,'Simon', 'Student', 'student_user');

insert into user(id,first_name, last_name, username)
values(3,'Thom', 'ScienceLover', 'scientist_user');

-- roles
insert into role
values('ADMIN');
insert into role
values('STUDENT');
insert into role
values('SCIENTIST');
insert into role
values('TEACHER');
insert into role
values('PARENT');
insert into role
values('GAME');

-- users with roles
insert into role_to_user(user_id, role_id) values(1,'TEACHER');
insert into role_to_user(user_id, role_id) values(2,'STUDENT');
insert into role_to_user(user_id, role_id) values(3,'SCIENTIST');

-- example abilities
insert into ability
values('Gf', 'Fluid reasoning', 'Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.', 'FLOATING');

insert into ability
values('Gc', 'Crystallized intelligence', 'Képesség a korábban megszerzett tudás felhasználására.', 'FLOATING');

insert into ability
values('Gv', 'Visual processing', 'Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.', 'FLOATING');

insert into ability
values('Glr', 'Long-term memory', 'Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.', 'FLOATING');

insert into ability
values('Ga', 'Auditory processing', 'Képesség hangok érzékelésére, a hallott információk feldolgozására.', 'FLOATING');

insert into ability
values('Gsm', 'Short-trem memory', 'Képesség a rövidtávú emlékezet  használatára.', 'FLOATING');

-- student with abilities
insert into float_profile_item(user_id, ability_id, ability_value) values(2,'Gf', 1.0);
insert into float_profile_item(user_id, ability_id, ability_value) values(2,'Gc', 0.997);
insert into float_profile_item(user_id, ability_id, ability_value) values(2,'Gv', 1.002);
insert into float_profile_item(user_id, ability_id, ability_value) values(2,'Glr', 1.0);
insert into float_profile_item(user_id, ability_id, ability_value) values(2,'Ga', 1.151);
insert into float_profile_item(user_id, ability_id, ability_value) values(2,'Gsm', 1.0);

-- snapshot abilities
insert into float_profile_snapshot_item(user_id, ability_id, ability_value, _timestamp)
values(2,'Gf', 0.78, '2023-11-01 10:00:00');
insert into float_profile_snapshot_item(user_id, ability_id, ability_value, _timestamp)
values(2,'Gf', 0.82, '2023-11-21 14:55:00');
insert into float_profile_snapshot_item(user_id, ability_id, ability_value, _timestamp)
values(2,'Gf', 0.91, '2023-12-05 16:15:00');


insert into float_profile_snapshot_item(user_id, ability_id, ability_value, _timestamp)
values(2,'Gc', 0.7, '2023-11-01 10:00:00');
insert into float_profile_snapshot_item(user_id, ability_id, ability_value, _timestamp)
values(2,'Gc', 0.76, '2023-11-21 14:55:00');
insert into float_profile_snapshot_item(user_id, ability_id, ability_value, _timestamp)
values(2,'Gc', 0.9, '2023-12-05 16:15:00');

insert into float_profile_snapshot_item(user_id, ability_id, ability_value, _timestamp)
values(2,'Gv', 0.8, '2023-11-01 10:00:00');
insert into float_profile_snapshot_item(user_id, ability_id, ability_value, _timestamp)
values(2,'Gv', 0.8, '2023-11-21 14:55:00');
insert into float_profile_snapshot_item(user_id, ability_id, ability_value, _timestamp)
values(2,'Gv', 0.99, '2023-12-05 16:15:00');

insert into float_profile_snapshot_item(user_id, ability_id, ability_value, _timestamp)
values(2,'Glr', 0.9, '2023-11-01 10:00:00');
insert into float_profile_snapshot_item(user_id, ability_id, ability_value, _timestamp)
values(2,'Glr', 0.97, '2023-11-21 14:55:00');
insert into float_profile_snapshot_item(user_id, ability_id, ability_value, _timestamp)
values(2,'Glr', 0.95, '2023-12-05 16:15:00');

insert into float_profile_snapshot_item(user_id, ability_id, ability_value, _timestamp)
values(2,'Ga', 1.05, '2023-12-05 16:15:00');

insert into float_profile_snapshot_item(user_id, ability_id, ability_value, _timestamp)
values(2,'Gsm', 0.96, '2023-12-05 16:15:00');

insert into game_abilities(game_id, ability_code) values(4,'Gv');
