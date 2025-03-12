alter table game drop column  config_description;
alter table game drop column  url;

alter table config_description_item rename column param_order to max_ability_effect;
alter table config_description_item modify max_ability_effect float;