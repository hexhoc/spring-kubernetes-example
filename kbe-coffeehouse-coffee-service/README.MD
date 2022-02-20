# kbe-coffeehouse-coffee-service

openApi http://localhost:8080/v3/api-docs/

## 1. Description

## 2. Technical specification
### Configure liquibase 

- Add in pom.xml: 
``` 
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
```
- Add in application.properties:
```
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:/db/changelog/changelog-master.sql
```
- Add changelog-master.sql
```
--liquibase formatted sql
--changeset hexhoc:1
create table test1 
(
    id   int primary key,
    name varchar(255)
);
--rollback drop table test1;
```