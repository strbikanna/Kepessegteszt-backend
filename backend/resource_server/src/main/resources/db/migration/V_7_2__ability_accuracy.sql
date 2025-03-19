alter table float_profile_item change column ability_value ability_value decimal(7, 6);

alter table float_profile_snapshot_item change column ability_value ability_value decimal(7, 6);

alter table float_profile_item add column ability_accuracy decimal(7, 6) default 0;

alter table enum_profile_item add column ability_accuracy decimal(7, 6) default 0;