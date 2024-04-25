-- delete existing results
DELETE FROM gameplay;

-- delete existing recommendations
DELETE FROM recommended_game;

-- delete game abilities
DELETE FROM game_abilities WHERE game_id < 6;

-- delete results for calculation
DELETE FROM result_for_calculation WHERE game_id < 6;

-- delete games
DELETE FROM game WHERE id < 6;

-- insert games
INSERT INTO game(id, _name, _description, thumbnail_path, _active, url, config_description)
VALUES (5,'Űrninja', 'Vágd szét a gyümiket a gravitációs térben, mielőtt leesnének!',
        'https://coglica.aut.bme.hu/api/game_images/translator-robot.jpg', true, null, '{
    "winFieldName": "passed",
    "maxLevel": 10
  }');

-- recommended games
INSERT INTO recommended_game(_timestamp, config, _level, recommendee_id, game_id)
VALUES (now(), '{
  "timelimit": 10000,
  "number_of_questions": 3,
  "choice_number": 5,
  "word_length": 6,
  "number_of_letters_to_change": 1
}', 5, 1, 6);

INSERT INTO recommended_game(_timestamp, config, _level, recommendee_id, game_id)
VALUES (now(), '{
  "timelimit": 10000,
  "number_of_questions": 5,
  "word_length": 3,
  "change_letter_number": 1,
  "probably_changed": 50
}', 5, 1, 7);

INSERT INTO recommended_game(_timestamp, config, _level, recommendee_id, game_id)
VALUES (now(), '{
  "gravity": 70,
  "maxLife": 5,
  "maxFruitCount": 3,
  "bombSpawnRate": 5,
  "fruitSpeed": 100,
  "gameMode": 1,
  "scoreToWin": 10
}', 5, 1, 5);

-- config items of fruitninja
insert into config_description_item (game_id, param_name, param_order, initial_value, easiest_value, hardest_value, increment, description)
values (5,'gravity', 1, 70, 30, 70, 5, 'Gravtity that pulls the fruits down');

insert into config_description_item (game_id, param_name, param_order, initial_value, easiest_value, hardest_value, increment, description)
values (5,'maxLife', 2, 5, 3, 5, -1, 'Number of lives');

insert into config_description_item (game_id, param_name, param_order, initial_value, easiest_value, hardest_value, increment, description)
values (5,'maxFruitCount', 3, 3, 1, 5, 1, 'Number of fruits on the screen');

insert into config_description_item (game_id, param_name, param_order, initial_value, easiest_value, hardest_value, increment, description)
values (5,'bombSpawnRate', 4, 10, 5, 25, 5, 'Rate of bomb spawn');

insert into config_description_item (game_id, param_name, param_order, initial_value, easiest_value, hardest_value, increment, description)
values (5,'fruitSpeed', 5, 100, 70, 100, 5, 'Speed of the fruits');

insert into config_description_item (game_id, param_name, param_order, initial_value, easiest_value, hardest_value, increment, description)
values (5,'gameMode', 6, 1, 1, 5, 1, 'Game mode');

insert into config_description_item (game_id, param_name, param_order, initial_value, easiest_value, hardest_value, increment, description)
values (5,'scoreToWin', 7, 10, 5, 30, 1, 'Score to win');
