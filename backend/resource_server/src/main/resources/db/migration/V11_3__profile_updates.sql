create table profile_update_item(
    ability_id varchar(5) not null,
    recommended_game_id bigint not null,
    valid_on_success boolean not null,
    primary key (ability_id, recommended_game_id, valid_on_success),
    foreign key (ability_id) references ability(code) on delete cascade,
    foreign key (recommended_game_id) references recommended_game(id) on delete cascade,
    updated_value float not null,
    created_at datetime
);