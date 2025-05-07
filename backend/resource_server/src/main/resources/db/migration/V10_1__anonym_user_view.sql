create view anonym_user_view as
select
    u.id,
    u.birth_date,
    u.address_city,
    u.gender
from
    user u;