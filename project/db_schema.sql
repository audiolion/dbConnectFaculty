begin;

drop type if exists "status_t" cascade;
create type "status_t" as enum (
     'student'
    ,'faculty'
    ,'other'
);

drop table if exists "paper" cascade;
create table "papers" (
     'id'         bigserial   primary key
    ,'title'      varchar(45) not null
    ,'abstract'   text        not null
    ,'citation'   varchar(45) not null
    ,'keyword'    varchar(45) not null
);
  
drop table if exists "authorship" cascade;
create table "authorship" (

);

drop table if exists "researcher" cascade;
create table "faculty" (
     'id'            bigserial   primary key
    ,'first_name'    varchar(45)  not null
    ,'last_name'     varchar(45)  not null
    ,'password_hash' bytea        default null
    ,'password_salt' bytea        default null
    ,'email'         varchar(45)  not null
    ,'status'        status_t     not null
);

drop table if exists "interest" cascade;
create table "interest" (
     'id'       bigserial   primary key
    ,'field'    varchar(45) not null
    ,'interest' varchar(45) not null
);

drop table if exists "researcher_interest" cascade;
create table "researcher_interest" (
   
);

alter table "researcher_interest" add foreign key ("interest_id") references "interest" ("id");
alter table "researcher_interest" add foreign key ("researcher_id") references "researcher" ("id");

alter table "authorship" add foreign key ("researcher_id") references "researcher" ("id");
alter table "authorship" add foreign key ("paper_id") references "paper" ("id");

commit;
