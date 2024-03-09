
alter table user add column birth_date date;
alter table user add column subscription varchar(100);
alter table user add constraint fk_user_subscription foreign key (subscription) references subscription(_name);
alter table user add column address_house_number varchar(10);
alter table user add column address_street varchar(100);
alter table user add column address_city varchar(100);
alter table user add column address_zip varchar(10);