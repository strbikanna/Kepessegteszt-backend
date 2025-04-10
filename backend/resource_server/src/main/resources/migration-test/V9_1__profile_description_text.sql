create table profile_description(
    id int primary key auto_increment,
    user_id int not null,
    timestamp datetime default current_timestamp,
    generated_text text,
    constraint fk_profile_description_user foreign key (user_id) references user(id)
);