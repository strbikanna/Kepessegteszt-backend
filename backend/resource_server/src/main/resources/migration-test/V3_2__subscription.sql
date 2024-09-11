CREATE TABLE SUBSCRIPTION(
    _name varchar(100) primary key,
    role_name varchar(30) not null,
    fee decimal not null,
    constraint fk_subscription_role_name foreign key(role_name) references ROLE(_name)
);