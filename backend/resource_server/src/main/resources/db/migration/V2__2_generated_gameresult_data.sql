INSERT INTO ABILITY
VALUES('Ga', 'Auditory Processing', 'Ability to hear phonemes distinctly.', 'FLOATING');

INSERT INTO GAME(id, _name, _description, thumbnail_path, _active, url, config_description)
VALUES (1, 'MUSIC-GAME', 'Best for your auditive memory.',
'http://localhost:8090/game_images/music_game.jpg', true, null, '{"level": 1, "gameName": "musical", "gameTitle": "Play with the songs!"}');

-- generate 300 users
DECLARE @i int = 0
WHILE @i < 300
BEGIN
    SET @i = @i + 1
    INSERT INTO USER(id, first_name, last_name, username)
    VALUES(@i, 'Test', 'User', CONCAT('user_', @i));
END

-- generate 50 game results with weak performance
DECLARE @j bigint = 0
DECLARE @user_id int = 0
WHILE @j < 50
BEGIN
    SET @j = @j + 1
    SET @user_id = @user_id + 1
    INSERT INTO GAMEPLAY(id, _timestamp, result, config, user_id)
    VALUES(@j, user_id, '2023-11-11 15:10:10', '{"score": ');
END