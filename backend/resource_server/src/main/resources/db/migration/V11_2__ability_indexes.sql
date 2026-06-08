alter table ability add column model_index int default null;

update ability set ability.model_index = 1
where code like 'Gc';
update ability set ability.model_index = 2
where code like 'Gf';
update ability set ability.model_index = 3
where code like 'Gq';
update ability set ability.model_index = 4
where code like 'Grw';
update ability set ability.model_index = 5
where code like 'Gsm';
update ability set ability.model_index = 6
where code like 'Glr';
update ability set ability.model_index = 7
where code like 'Gv';
update ability set ability.model_index = 8
where code like 'Ga';
update ability set ability.model_index = 9
where code like 'Gs';
update ability set ability.model_index = 10
where code like 'Gt';
