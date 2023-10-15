CREATE TABLE GAMEPLAY(
                         id            bigint           PRIMARY KEY AUTO_INCREMENT,
                         _timestamp    datetime         NOT NULL,
                         result         json     NOT NULL,
                         user_id       int,
                         game_id       int,
                         constraint fk_user FOREIGN KEY(user_id) REFERENCES user(id),
                         constraint fk_game FOREIGN KEY(game_id) REFERENCES game(id)
);