begin;

drop type if exists "status_t" cascade;
create type "status_t" as enum (
     'undergraduate'
    ,'faculty'
    ,'graduate'
    ,'other'
);

drop table if exists "field" cascade;
create table "field" (
     "id"         bigserial   primary key
    ,"name"       varchar(45) not null
);

drop table if exists "researcher_field" cascade;
create table "researcher_field" (
     "field_id"       bigint  not null
    ,"researcher_id"  bigint  not null
    ,constraint "no_duplicate_researcher_fields" unique ("field_id","researcher_id")      
);

drop table if exists "paper" cascade;
create table "paper" (
     "id"         bigserial   primary key
    ,"title"      varchar(45) not null
    ,"abstract"   text        not null
    ,"citation"   varchar(45) not null
);
  
drop table if exists "authorship" cascade;
create table "authorship" (
     "researcher_id" bigint  not null 
    ,"paper_id"      bigint  not null
    ,constraint "no_duplicate_authors" unique ("researcher_id","paper_id")
);

drop table if exists "paper_keywords" cascade;
create table "keywords" (
     "id"         bigserial primary key
    ,"keyword"    varchar   not null
);

drop table if exists "researcher" cascade;
create table "researcher" (
     "id"            bigserial   primary key
    ,"first_name"    varchar(45)  not null
    ,"last_name"     varchar(45)  not null
    ,"password_hash" varchar(40)  default null
    ,"password_salt" varchar(40)  default null
    ,"email"         varchar(45)  not null
    ,"status"        status_t     not null
);

drop table if exists "interest" cascade;
create table "interest" ( 
     "id"       bigserial   primary key
    ,"field_id" bigint      not null
    ,"interest" varchar(45) not null
);

drop table if exists "researcher_interest" cascade;
create table "researcher_interest" (
     "researcher_id"   bigint  not null
    ,"interest_id" bigint  not null 
    ,constraint "no_duplicate_research_interests" unique ("researcher_id","interest_id")
);

alter table "researcher_interest" add foreign key ("researcher_id") references "researcher" ("id");
alter table "researcher_interest" add foreign key ("interest_id") references "interest" ("id");

alter table "authorship" add foreign key ("researcher_id") references "researcher" ("id");
alter table "authorship" add foreign key ("paper_id") references "paper" ("id");

alter table "researcher_field" add foreign key ("field_id") references "field" ("id");
alter table "researcher_field" add foreign key ("researcher_id") references "researcher" ("id");

alter table "interest" add foreign key ("field_id") references "field" ("id");

create index "interest_id_index" on "interest" ("id");

create index "researcher_id_index" on "researcher" ("id");

create index "paper_id_index" on "paper" ("id");

create index "field_id_index" on "field" ("id");

commit;
