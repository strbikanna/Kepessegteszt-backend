CREATE TABLE gameplay(
                         id            bigint           PRIMARY KEY AUTO_INCREMENT,
                         _timestamp    datetime         NOT NULL,
                         result        json             NOT NULL,
                         config        json             NOT NULL,
                         user_id       int,
                         recommended_game_id       int,
                         constraint fk_user FOREIGN KEY(user_id) REFERENCES user(id),
                         constraint fk_game FOREIGN KEY(recommended_game_id) REFERENCES game(id)
);
