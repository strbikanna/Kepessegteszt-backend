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
-- create trigger trigger_result after insert on gameplay
-- for each row
--     begin
--         set @game_id = (select game.id from game
--                                   inner join recommended_game rg on game.id = rg.game_id
--                                  inner join gameplay gp on rg.id = gp.recommended_game_id
--                                  where gp.id = new.id
--                        );
--        insert into result_for_calculation (_timestamp, result, config, user_id, game_id)
--        values (new._timestamp, new.result, new.config, new.user_id, @game_id );
--    end;