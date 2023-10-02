-- example users
insert into user(id,first_name, last_name, username)
values(1,'Theresa', 'Teacher', 'teacher_user');

insert into user(id,first_name, last_name, username)
values(2,'Simon', 'Student', 'student_user');

insert into user(id,first_name, last_name, username)
values(3,'Thom', 'ScienceLover', 'scientist_user');

-- roles
insert into role
values(1, 'ADMIN');
insert into role
values(2, 'STUDENT');
insert into role
values(3, 'SCIENTIST');
insert into role
values(4, 'TEACHER');
insert into role
values(5, 'PARENT');

-- users with roles
insert into role_to_user(user_id, role_id) values(1,4);
insert into role_to_user(user_id, role_id) values(2,2);
insert into role_to_user(user_id, role_id) values(3,3);

-- example abilities
insert into ability(code, name, description)
values('Gf', 'Fluid reasoning', 'The use of deliberate and controlled mental operations to solve
novel problems that cannot be performed automatically.');

insert into ability values('Gsm', 'Short-trem memory', 'The ability to apprehend and maintain awareness of a limited
number of elements of information in the immediate situation.');
