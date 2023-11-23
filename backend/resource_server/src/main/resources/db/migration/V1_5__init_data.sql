-- games
-- comment out for tests
INSERT INTO GAME(id, _name, _description, thumbnail_path, _active, url, config_description)
VALUES (1, 'balloon-pop', 'This is a very epic game. Try not to die from the bombs.',
        'http://localhost:8090/game_images/balloon_game.jpg', true, null,
        '{
          "gameName": "balloon-pop",
          "maxExtraPointsName": "maxHealthPoints",
          "extraPointsName": "healthPoints",
          "pointsName": "score",
          "maxPointsName": "maxBalloons"
        }');

INSERT INTO GAME(id, _name, _description, thumbnail_path, _active, url, config_description)
VALUES (2, 'Radio buttons', 'Push the correct buttons of the secret radio.',
        'http://localhost:8090/game_images/number_game.jpg', true, null,
        '{
          "timeBetweenNumbers": "Time interval between the appearance of numbers.",
          "maxRound": "Maximum number of rounds.",
          "minNumberCount": "Minimum count of numbers in each round.",
          "maxNumberCount": "Maximum count of numbers that can appear in a round.",
          "winFieldName": "gameWon"
        }');

INSERT INTO GAME(id, _name, _description, thumbnail_path, _active, url, config_description)
VALUES (3, 'Cosmic control', 'Push the correct buttons of the secret radio.',
        'http://localhost:8090/game_images/2.png', true, null, '{
    "level": "1",
    "gameName": "cosmic-control",
    "gameTitle": "Control the cosmos"
  }');

INSERT INTO GAME(id, _name, _description, thumbnail_path, _active, url, config_description)
VALUES (4, 'Cosmic sequence', 'Push the correct buttons of the secret radio.',
        'http://localhost:8090/game_images/cosmic.jpg', true, null,
        '{
          "gameName": "cosmic-sequence",
          "maxRound": "Defines the maximum number of rounds in the game.",
          "minAsteroidCount": "Minimum number of asteroids appearing in a round.",
          "maxAsteroidCount": "Maximum number of asteroids appearing in a round.",
          "maxHealthPoints": "Maximum health points for a player.",
          "numbersVisibalityDuration": "Duration of the numbers appearing on the asteroids.",
          "maxNumber": "Highest number appearing on the asteroids.",
          "winFieldName": "gameWon",
          "pointsFieldName": "round",
          "maxPointsFieldName": "maxRound",
          "extraPointsFieldName": "healthPoints",
          "maxExtraPointsFieldName": "maxHealthPoints"
        }');

INSERT INTO GAME(id, _name, _description, thumbnail_path, _active, url, config_description)
VALUES (5, 'Total number', 'Push the correct buttons of the secret radio.',
        'http://localhost:8090/game_images/number_game.jpg', true, null, '{
    "timeBetweenNumbers": "Time interval between the appearance of numbers.",
    "maxRound": "Maximum number of rounds.",
    "minNumberCount": "Minimum count of numbers in each round.",
    "maxNumberCount": "Maximum count of numbers that can appear in a round.",
    "minNumber": "The lowest number that can be displayed in the game.",
    "maxNumber": "The highest number that can be displayed in the game.",
    "operations": "Set of mathematical operations used, e.g., [''+'', ''-''].",
    "winFieldName": "gameWon"
  }');

