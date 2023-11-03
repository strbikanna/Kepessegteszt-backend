CREATE TABLE GAME
(
    id                 int              PRIMARY KEY AUTO_INCREMENT,
    _name              varchar(100)     NOT NULL,
    _description       varchar(3000),
    thumbnail_path     varchar(400)     NOT NULL,
    _active            boolean          DEFAULT TRUE,
    url                varchar(1000)    ,
    config_description json             NOT NULL,
    affected_abilities json
);

