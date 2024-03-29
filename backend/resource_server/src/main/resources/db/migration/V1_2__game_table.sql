CREATE TABLE game
(
    id                 int              PRIMARY KEY AUTO_INCREMENT,
    version            int              NOT NULL DEFAULT 1,
    _name              varchar(100)     NOT NULL,
    _description       varchar(3000),
    thumbnail_path     varchar(400)     NOT NULL,
    _active            boolean          DEFAULT TRUE,
    url                varchar(1000)    ,
    config_description json             NOT NULL
);

CREATE TABLE game_abilities
(
    game_id         int         NOT NULL,
    ability_code    varchar(5)  NOT NULL,
    constraint fk_game_abilities_game FOREIGN KEY(game_id) REFERENCES game(id),
    constraint fk_game_abilities_ability FOREIGN KEY(ability_code) REFERENCES ability(code)
)

