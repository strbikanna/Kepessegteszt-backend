alter table special_game_settings
    add column min_interval       int      default null;
alter table special_game_settings
    add column max_interval       int      default null;
alter table special_game_settings
    add column creation_timestamp datetime default current_timestamp not null;
alter table special_game_settings
    add column valid_minutes      int                                not null;