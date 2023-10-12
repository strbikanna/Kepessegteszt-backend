CREATE TABLE GAMES (
                      id                    int             PRIMARY KEY,
                      name             varchar(100)    NOT NULL,
                      description      varchar(1000),
                      icon                  varchar(1000)   NOT NULL,
                      active           boolean         DEFAULT TRUE,
                      url                   varchar(1000)   NOT NULL,
                      json_descriptor       varchar(1000)   NOT NULL
);

