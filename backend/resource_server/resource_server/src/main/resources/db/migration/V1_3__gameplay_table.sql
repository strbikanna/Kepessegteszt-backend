CREATE TABLE GAMEPLAY(
                         id            bigint           PRIMARY KEY,
                         time_stamp    datetime         NOT NULL,
                         score         varchar(500)     NOT NULL,
                         user_id       int,
                         game_id       int,
                         constraint fk_user FOREIGN KEY(user_id) REFERENCES user(id),
                         constraint fk_game FOREIGN KEY(game_id) REFERENCES games(id)
);