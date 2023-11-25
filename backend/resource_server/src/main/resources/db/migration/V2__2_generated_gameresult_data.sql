INSERT INTO ABILITY
VALUES ('Ga', 'Auditory Processing', 'Ability to hear phonemes distinctly.', 'FLOATING');

create procedure GenerateUserData(userCount int)
begin
    declare i int;
    set i = 0;
    while i < userCount
        do
            set @username = concat('user', i);
            insert into user(first_name, last_name, username) value
                (concat('first_name', i), concat('last_name', i), @username);
            set i = i + 1;
        end while;
end;

create procedure GenerateUserProfileData()
begin
    declare finished integer default 0;
    declare userid integer;
    declare user_data_cursor cursor for
        select id from user
        left join role_to_user on user.id = role_to_user.user_id
        where role_id is null;

    declare continue handler for not found set finished = 1;

    open user_data_cursor;

    user_loop: LOOP
        fetch user_data_cursor into userid;
        if finished = 1 then
            leave user_loop;
        end if;

        insert into float_profile_item(user_id, ability_value, ability_id)
        select userid,
               rand() * 1.5,
               code
        from (select distinct code
              from ability
              where ability_type = 'FLOATING') as distinct_codes;

    end loop user_loop;
    close user_data_cursor;
end;

create procedure GenerateResultForCalculationData()
begin
    declare finished integer default 0;
    declare userid integer;
    declare gameid integer;
    declare ability_v float;
    declare points float;
    declare extra_points float;
    declare level integer;
    declare user_data_cursor cursor for
        select id from user
        left join role_to_user on user.id = role_to_user.user_id
        where role_id is null;
    declare continue handler for not found set finished = 1;

    insert into game(_name, _description, thumbnail_path, _active, url, config_description)
    values ('Cosmic sequence', 'Destroy the asteroids in the correct order.',
            'http://localhost:8090/game_images/cosmic.jpg', true, null,
            '{
              "gameName": "cosmic-sequence",
              "maxRound": "Defines the maximum number of rounds in the game.",
              "minAsteroidCount": "Minimum number of asteroids appearing in a round.",
              "maxAsteroidCount": "Maximum number of asteroids appearing in a round.",
              "maxHealthPoints": "Maximum health points for a player.",
              "numbersVisibilityDuration": "Duration of the numbers appearing on the asteroids.",
              "maxNumber": "Highest number appearing on the asteroids.",
              "winFieldName": "gameWon",
              "pointsFieldName": "round",
              "maxPointsFieldName": "maxRound",
              "extraPointsFieldName": "healthPoints",
              "maxExtraPointsFieldName": "maxHealthPoints"
            }');
    set gameid = last_insert_id();
    insert into game_abilities(game_id, ability_code) values (gameid, 'Ga');

    open user_data_cursor;

    user_loop: LOOP
        fetch user_data_cursor into userid;
        if finished = 1 then
            leave user_loop;
        end if;


        select ability_value into ability_v
                             from float_profile_item
                             where user_id = userid
                               and ability_id = 'Ga';
        if ability_v is null then set ability_v = 0;
        end if;
        set points = ability_v * 10;
        set extra_points = rand() * 2.5 + ability_v * 5;
        set level = rand() * 8;

        insert into result_for_calculation(game_id, user_id, _timestamp, config, result)
        values (gameid, userid, now(),
                json_object('maxRound', 15, 'minAsteroidCount', 3, 'maxAsteroidCount', 5, 'maxHealthPoints', 10,
                            'numbersVisibilityDuration', 3, 'maxNumber', 10),
                json_object('level', level, 'round', points, 'healthPoints', extra_points));

    end loop user_loop;
    close user_data_cursor;
end;
