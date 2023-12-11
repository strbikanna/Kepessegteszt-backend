CREATE TABLE RESULT_FOR_CALCULATION
(
    id                bigint PRIMARY KEY AUTO_INCREMENT,
    _timestamp        datetime NOT NULL,
    result            json     NOT NULL,
    normalized_result float DEFAULT NULL,
    config            json     NOT NULL,
    user_id           int,
    game_id           int,
    constraint fk_user_calc_result FOREIGN KEY (user_id) REFERENCES user (id),
    constraint fk_game_calc_result FOREIGN KEY (game_id) REFERENCES game (id)
);
