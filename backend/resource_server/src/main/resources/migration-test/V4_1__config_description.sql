create table config_description_item
(
    id            bigint primary key auto_increment,
    param_name    varchar(100) not null,
    param_order   int          not null,
    easiest_value int          not null,
    hardest_value int          not null,
    initial_value int          not null,
    increment     int          not null,
    description   varchar(255) not null,
    game_id       int,
    constraint fk_config_item_game foreign key (game_id) references game (id)
);

-- delete config_description json from game later
-- alter table game drop column config_description;
