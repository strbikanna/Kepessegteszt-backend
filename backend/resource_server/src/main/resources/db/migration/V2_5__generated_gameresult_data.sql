DELIMITER //
create procedure GenerateUserData( in userCount int)
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
end; //
DELIMITER ;

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

create procedure GenerateResultForCalculationData(in gameid integer, in ability_code varchar(5))
begin
    declare finished integer default 0;
    declare userid integer;
    declare ability_v float;
    declare points float;
    declare extra_points float;
    declare level integer;
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


        select ability_value into ability_v
        from float_profile_item
        where user_id = userid
          and ability_id = ability_code;
        if ability_v is null then set ability_v = 0;
        end if;
        set points = ability_v * 10;
        set extra_points = rand() * 2.5 + ability_v * 5;
        set level = rand() * 10;

        insert into result_for_calculation(game_id, user_id, _timestamp, config, result)
        values (gameid, userid, now(),
                json_object('maxRound', 15, 'minAsteroidCount', 3, 'maxAsteroidCount', 5, 'maxHealthPoints', 10,
                            'numbersVisibilityDuration', 3, 'maxNumber', 10, 'maxLevel', 10),
                json_object('level', level, 'round', points, 'healthPoints', extra_points));

    end loop user_loop;
    close user_data_cursor;
end;
