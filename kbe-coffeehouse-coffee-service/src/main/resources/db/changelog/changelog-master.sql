--liquibase formatted sql
--changeset hexhoc:1
create table if not exists coffee
(
    id                 varchar(36) not null
        primary key,
    coffee_name        varchar(255),
    coffee_style       varchar(255),
    created_date       timestamp,
    last_modified_date timestamp,
    min_on_hand        integer,
    price              numeric(19, 2),
    quantity_on_hand   integer,
    quantity_to_brew   integer,
    upc                varchar(255) unique,
    version            bigint
);
--rollback drop table coffee;