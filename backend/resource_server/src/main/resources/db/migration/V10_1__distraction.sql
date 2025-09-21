create table special_game_settings(
    fk_user_id int not null,
    distraction_type varchar(50) not null,
    constraint pk_special_game_settings primary key (fk_user_id, distraction_type),
    constraint fk_special_game_settings_user foreign key (fk_user_id) references users(id) on delete cascade
);