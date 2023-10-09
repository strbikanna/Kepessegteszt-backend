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

create table ability
(
    code        varchar(5) primary key not null,
    name        varchar(30),
    description varchar(1000),
    ability_type varchar(30)
);

create table float_profile_item
(
    id            bigint primary key auto_increment,
    ability_id    varchar(5) not null,
    user_id       int,
    ability_value float,
    constraint fk_float_profile_user foreign key (user_id) references user (id),
    constraint fk_ability_float_profile foreign key (ability_id) references ability (code)
);

create table enum_profile_item
(
    id            bigint primary key auto_increment,
    ability_id    varchar(5) not null,
    user_id       int,
    ability_value varchar(30),
    constraint fk_enum_profile_user foreign key (user_id) references user (id),
    constraint fk_ability_enum_profile foreign key (ability_id) references ability (code)
);

create table float_profile_snapshot_item
(
    id            bigint primary key auto_increment,
    ability_id    varchar(5) not null,
    user_id    int              not null,
    _timestamp datetime unique not null,
    ability_value float,
    constraint fk_float_snapshot_to_user foreign key (user_id) references user (id),
    constraint fk_ability_float_snapshot foreign key (ability_id) references ability (code)
);

create table enum_profile_snapshot_item
(
    id            bigint primary key auto_increment,
    ability_id    varchar(5) not null,
    user_id    int              not null,
    _timestamp datetime unique not null,
    ability_value varchar(30),
    constraint fk_enum_snapshot_to_user foreign key (user_id) references user (id),
    constraint fk_ability_enum_snapshot foreign key (ability_id) references ability (code)
);
