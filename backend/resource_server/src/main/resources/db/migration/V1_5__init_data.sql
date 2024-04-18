-- games
INSERT INTO game(id, _name, _description, thumbnail_path, _active, url, config_description)
VALUES (6,'Űrfordítás', 'Segíts a robotnak a fordításban, találd meg a helytelen szót!',
        'http://localhost:8090/game_images/translator-robot.jpg', true, null, '{
    "gameName": "word-reader",
    "gameTitle": "Űrfordítás",
    "timelimit": "Time limit for each question in milliseconds.",
    "number_of_questions": "Number of questions in the game.",
    "choice_number": "Number of choices for each question.",
    "word_length": "Length of the words in the game.",
    "number_of_letters_to_change": "Number of letters to change in the words.",
    "winFieldName": "gameWon",
    "maxLevel": 10
  }');

INSERT INTO game(id, _name, _description, thumbnail_path, _active, url, config_description)
VALUES (7,'Űrlénykihallgatás', 'Segíts a robotnak a tolmácsolásban!',
        'http://localhost:8090/game_images/translator-robot.jpg', true, null, '{
    "gameName": "word-listening",
    "gameTitle": "Űrtolmácsolás",
    "timelimit": "Time limit for each question in milliseconds.",
    "number_of_questions": "Number of questions in the game.",
    "word_length": "Length of the words in the game.",
    "change_letter_number": "Number of letters to change in the words.",
    "probably_changed": "Probability of changing a letter in the word.",
    "winFieldName": "gameWon",
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
  "probably_changed": 0.5
}', 5, 1, 7);
