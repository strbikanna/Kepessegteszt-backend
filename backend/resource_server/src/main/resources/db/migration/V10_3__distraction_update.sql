alter table special_game_settings
    add column min_interval       int      default null,
    add column max_interval       int      default null,
    add column creation_timestamp datetime default current_timestamp not null,
    add column valid_minutes      int                                not null;