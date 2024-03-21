-- games
INSERT INTO game(id, _name, _description, thumbnail_path, _active, url, config_description)
VALUES (1, 'Lufipukkasztás', 'Pukkaszd ki a lufikat! Vigyázz a bombákkal!',
        'http://localhost:8090/game_images/balloon-pop.png', true, null,
        '{
          "gameName": "balloon-pop",
          "gameTitle": "Lufipukkasztás",
          "maxExtraPointsName": "maxHealthPoints",
          "extraPointsName": "healthPoints",
          "pointsName": "correct",
          "maxPointsName": "spawnedBalloons",
          "maxLevel": 8
        }');

INSERT INTO game(id, _name, _description, thumbnail_path, _active, url, config_description)
VALUES (2, 'Titkos rádió', 'Eltalálod, melyik gombot kell megnyomni a titkos rádión?',
        'http://localhost:8090/game_images/number_game.jpg', true, null,
        '{
          "gameName": "number-repeating",
          "timeBetweenNumbers": "Time interval between the appearance of numbers.",
          "maxRound": "Maximum number of rounds.",
          "minNumberCount": "Minimum count of numbers in each round.",
          "maxNumberCount": "Maximum count of numbers that can appear in a round.",
          "winFieldName": "gameWon",
          "maxLevel": 10
        }');

INSERT INTO game(id, _name, _description, thumbnail_path, _active, url, config_description)
VALUES (3, 'Űrirányítás', 'Találd el a megfelelő gombokat, hogy az űrhajód túlélje az űrhajszát.',
        'http://localhost:8090/game_images/cosmic-control.jpg', true, null, '{
    "gameName": "cosmic-control",
    "gameTitle": "Űrirányítás",
    "maxRound": "Defines the maximum number of rounds in the game.",
    "mistakes": "Number of wrong hits during the game.",
    "correct": "Number of correct hits during the game.",
    "round": "Last round reached in the game.",
    "maxExtraPointsName": "score",
    "extraPointsName": "correct",
    "pointsName": "round",
    "maxPointsName": "maxRound",
    "maxLevel": 12
  }');

INSERT INTO game(id, _name, _description, thumbnail_path, _active, url, config_description)
VALUES (4, 'Meteorháború', 'Csak a megfelelő sorrendben kilőtt meteorok számítanak, sikerülni fog?',
        'http://localhost:8090/game_images/cosmic-sequence.jpg', true, null,
        '{
          "gameName": "cosmic-sequence",
          "gameTitle": "Meteorháború",
          "maxRound": "Defines the maximum number of rounds in the game.",
          "minAsteroidCount": "Minimum number of asteroids appearing in a round.",
          "maxAsteroidCount": "Maximum number of asteroids appearing in a round.",
          "maxHealthPoints": "Maximum health points for a player.",
          "numbersVisibilityDuration": "Duration of the numbers appearing on the asteroids.",
          "maxNumber": "Highest number appearing on the asteroids.",
          "totalTime": "Total time of the player finding the correct order in milliseconds.",
          "pointsFieldName": "round",
          "maxPointsFieldName": "maxRound",
          "extraPointsFieldName": "healthPoints",
          "maxExtraPointsFieldName": "maxHealthPoints",
          "maxLevel": 8
        }');

INSERT INTO game(id, _name, _description, thumbnail_path, _active, url, config_description)
VALUES (5, 'Űrkutató képzés', 'Jól kell bánni a számokkal, gyakorolj, hogy űrkutató lehess!',
        'http://localhost:8090/game_images/number-total.jpg', true, null, '{
    "gameName": "number-total",
    "gameTitle": "Űrkutató képzés",
    "timeBetweenNumbers": "Time interval between the appearance of numbers.",
    "maxRound": "Maximum number of rounds.",
    "minNumberCount": "Minimum count of numbers in each round.",
    "maxNumberCount": "Maximum count of numbers that can appear in a round.",
    "minNumber": "The lowest number that can be displayed in the game.",
    "maxNumber": "The highest number that can be displayed in the game.",
    "operations": "Set of mathematical operations used, e.g., [''+'', ''-''].",
    "totalTime": "Total time of the player inputing numbers in milliseconds.",
    "winFieldName": "gameWon",
    "maxLevel": 10
  }');

