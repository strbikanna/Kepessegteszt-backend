alter table gameplay add column passed boolean default false;
update gameplay set passed = true where JSON_EXTRACT(result, '$.passed') = true;