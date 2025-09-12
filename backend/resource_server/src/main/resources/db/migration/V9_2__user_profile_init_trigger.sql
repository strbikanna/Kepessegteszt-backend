-- on user insert -> create profile items with default values for each ability
-- if ability type is floating -> default value is 0.9
create trigger init_user_profile
    after insert
    on user
    for each row
begin
    insert into float_profile_item(ability_id, user_id, ability_value)
    select code, new.id, 0.9
    from ability
    where ability_type = 'FLOATING';
end;

