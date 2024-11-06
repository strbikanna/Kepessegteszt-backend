alter table ability modify column name varchar(50) not null;

insert into ability(code,name, ability_type, description) values
('CVD', 'Color vision deficiency', 'ENUMERATED', 'Disability to distinguish colors'),
('APD', 'Auditory processing disorder', 'ENUMERATED', 'Difficulty in processing auditory information'),
('DYL', 'Dyslexia', 'ENUMERATED', 'Difficulty in reading'),
('DYG', 'Dysgraphia', 'ENUMERATED', 'Difficulty in writing'),
('ADHD', 'Attention deficit hyperactivity disorder', 'ENUMERATED', 'Difficulty in focusing and controlling impulses'),
('DYC', 'Dyscalculia', 'ENUMERATED', 'Difficulty in understanding numbers'),
('AUT', 'Autism', 'ENUMERATED', 'Difficulty in social interaction'),
('VI', 'Visual impairment', 'ENUMERATED', 'Difficulty in seeing'),
('PI', 'Physical impairment', 'ENUMERATED', 'Difficulty in moving');

insert into enum_profile_item(user_id, ability_id, ability_value)
select u.id, a.code, 'UNKNOWN' from user u, ability a where a.ability_type = 'ENUMERATED';
