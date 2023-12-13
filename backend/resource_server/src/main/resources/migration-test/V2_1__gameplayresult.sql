DROP TABLE GAMEPLAY;
CREATE TABLE GAMEPLAY(
                         id            bigint           PRIMARY KEY AUTO_INCREMENT,
                         _timestamp    datetime         NOT NULL,
                         result        json             NOT NULL,
                         config        json             NOT NULL,
                         user_id       int,
                         recommended_game_id       bigint,
                         constraint fk_user FOREIGN KEY(user_id) REFERENCES user(id),
                         constraint fk_game FOREIGN KEY(recommended_game_id) REFERENCES recommended_game(id)
);