CREATE TABLE ORGANIZATION(
    id int primary key auto_increment,
    _name varchar(100) not null,
    address_house_number varchar(10) not null,
    address_street varchar(100) not null,
    address_city varchar(100) not null,
    address_zip varchar(10) not null
);
CREATE TABLE USER_ORGANIZATION(
    user_id int not null,
    organization_id int not null,
    constraint pk_user_organization primary key (user_id, organization_id),
    constraint fk_user_organization_to_user foreign key (user_id) references USER(id),
    constraint fk_user_organization_to_organization foreign key (organization_id) references ORGANIZATION(id)
);