CREATE TABLE GAMES (
                      id                    int             PRIMARY KEY,
                      game_name             varchar(100)    NOT NULL,
                      game_description      varchar(1000),
                      icon                  varchar(1000)   NOT NULL,
                      game_active           boolean         DEFAULT TRUE,
                      url                   varchar(1000)   NOT NULL,
                      json_descriptor       varchar(1000)   NOT NULL
);

