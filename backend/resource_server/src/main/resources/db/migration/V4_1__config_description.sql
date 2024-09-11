-- config description item used for automating configuration of gameplays based on difficulty level
create table config_description_item (
    id bigint primary key auto_increment,
    param_name varchar(100) not null,
    param_order int not null,
    easiest_value int not null,
    hardest_value int not null,
    initial_value int not null,
    increment int not null,
    description varchar(255) not null,
    game_id int,
    constraint fk_config_item_game foreign key (game_id) references game (id)
);

-- delete config_description json from game later
-- alter table game drop column config_description;

-- insert config descriptions
insert into config_description_item (game_id, param_name, param_order, initial_value, easiest_value, hardest_value, increment, description)
values (6,'timelimit', 5, 10000, 30000, 3000, -1000, 'Time limit for each question in milliseconds.');

insert into config_description_item (game_id, param_name,param_order, initial_value, easiest_value, hardest_value, increment, description)
values (6,'number_of_questions', 2, 3, 1, 10, 1, 'Number of questions in the game.');

insert into config_description_item (game_id, param_name, param_order, initial_value, easiest_value, hardest_value, increment, description)
values (6,'choice_number', 4, 4, 2, 6, 1, 'Number of choices for each question.');

insert into config_description_item (game_id, param_name, param_order, initial_value, easiest_value, hardest_value, increment, description)
values (6,'word_length', 1, 2, 1, 5, 1, 'Length of the vowels in words in the game.');

insert into config_description_item (game_id, param_name, param_order, initial_value, easiest_value, hardest_value, increment, description)
values (6,'number_of_letters_to_change', 3, 2, 3, 1, -1, 'Number of letters to change in the word to make it a new word.');

insert into config_description_item (game_id, param_name, param_order, initial_value, easiest_value, hardest_value, increment, description)
values (7,'timelimit', 5, 10000, 30000, 3000, -1000, 'Time limit for each question in milliseconds.');

insert into config_description_item (game_id, param_name, param_order,initial_value, easiest_value, hardest_value, increment, description)
values (7,'number_of_questions', 3, 3, 1, 10, 1, 'Number of questions in the game.');

insert into config_description_item (game_id, param_name, param_order, initial_value, easiest_value, hardest_value, increment, description)
values (7,'probably_changed', 4, 40, 10, 80, 5, 'Probability of changing a letter in the word, in percentage.');

insert into config_description_item (game_id, param_name, param_order, initial_value, easiest_value, hardest_value, increment, description)
values (7,'word_length', 1, 2, 1, 5, 1, 'Length of the vowels in words in the game.');

insert into config_description_item (game_id, param_name, param_order, initial_value, easiest_value, hardest_value, increment, description)
values (7,'change_letter_number', 2, 1, 2, 1, -1, 'Number of letters to change in the word to make it a new word.');
