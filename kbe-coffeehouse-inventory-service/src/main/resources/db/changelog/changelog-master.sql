--liquibase formatted sql
--changeset hexhoc:1
create table if not exists coffee_inventory
(
    id                 varchar(36) not null primary key,
    created_date       timestamp,
    last_modified_date timestamp,
    quantity_on_hand   integer,
    upc                varchar(255) unique,
    version            bigint
);
--rollback drop table coffee_inventory;