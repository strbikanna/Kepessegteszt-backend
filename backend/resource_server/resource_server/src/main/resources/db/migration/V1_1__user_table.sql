create table user
(
    id         int primary key    not null auto_increment,
    first_name varchar(50),
    last_name  varchar(50),
    username   varchar(50) unique not null
);

create table role
(
    _name varchar(30) primary key not null
);

create table role_to_user
(
    user_id int         not null,
    role_id varchar(30) not null,
    constraint fk_role_to_user foreign key (user_id) references user (id),
    constraint fk_user_to_role foreign key (role_id) references role (_name)
);

create table profile_snapshot
(
    id         bigint primary key auto_increment,
    user_id    int              not null,
    _timestamp timestamp unique not null,
    constraint fk_snapshot_to_user foreign key (user_id) references user (id)
);

create table ability
(
    code        varchar(5) primary key not null,
    name        varchar(30),
    description varchar(1000)
);

create table profile_item
(
    id            bigint primary key auto_increment,
    ability_id    varchar(5) not null,
    user_id       int,
    ability_value int,
    constraint fk_profile_profile foreign key (user_id) references user (id),
    constraint fk_ability_profile foreign key (ability_id) references ability (code)
);

create table profile_snapshot_item
(
    id            bigint primary key auto_increment,
    ability_id    varchar(5) not null,
    snapshot_id   bigint,
    ability_value int,
    constraint fk_profile_snapshot foreign key (snapshot_id) references profile_snapshot (id),
    constraint fk_ability_snapshot foreign key (ability_id) references ability (code)
);
