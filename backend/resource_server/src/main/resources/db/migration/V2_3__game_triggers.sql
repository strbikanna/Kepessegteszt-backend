create trigger game_version_upgrade before update on game
    for each row
begin
    if strcmp(OLD._name, New._name) != 0 ||
       json_contains(OLD.config_description, NEW.config_description) != 1 || json_contains(NEW.config_description, OLD.config_description) != 1
        || strcmp(OLD.url, NEW.url) != 0
    then
        set NEW.version = OLD.version + 1;
    end if;
end