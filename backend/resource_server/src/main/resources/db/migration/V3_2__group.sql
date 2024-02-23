CREATE TABLE `group`
(
    id int PRIMARY KEY AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    organisation_id int,
    parent_group_id int,
    constraint fk_group_organization foreign key (organisation_id) references organization(id),
    constraint fk_group_parent_group foreign key (parent_group_id) references `group` (id)
);

CREATE TABLE USER_GROUP(
    user_id int NOT NULL,
    group_id int NOT NULL,
    constraint pk_user_group primary key (user_id, group_id),
    constraint fk_user_group_user foreign key (user_id) references USER(id),
    constraint fk_user_group_group foreign key (group_id) references `group`(id)
);