CREATE TABLE user_group
(
    id                   int primary key auto_increment,
    _name                varchar(100) not null,
    address_house_number varchar(10),
    address_street       varchar(100),
    address_city         varchar(100),
    address_zip          varchar(10),
    organization_id      int,
    parent_group_id      int,
    group_type           varchar(10),
    constraint fk_group_organization foreign key (organization_id) references user_group (id),
    constraint fk_group_parent_group foreign key (parent_group_id) references user_group (id)
);
CREATE TABLE group_member
(
    user_id  int NOT NULL,
    group_id int NOT NULL,
    constraint pk_user_group primary key (user_id, group_id),
    constraint fk_group_member_user foreign key (user_id) references user (id),
    constraint fk_group_member_group foreign key (group_id) references user_group (id)
);

CREATE TABLE group_admin
(
    user_id  int NOT NULL,
    group_id int NOT NULL,
    constraint pk_admin_group primary key (user_id, group_id),
    constraint fk_group_admin_user foreign key (user_id) references user (id),
    constraint fk_group_admin_group foreign key (group_id) references user_group (id)
);
