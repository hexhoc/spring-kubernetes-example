# kbe-coffeehouse-coffee-service

openApi http://localhost:8080/v3/api-docs/

## 1. Description

## 2. Technical specification

### Create database in postresql
- Create postgresql instance
- Create database - postrges

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

### Configure artemis

- Get docker image 
```
docker pull vromero/activemq-artemis
```
- Run docker image
```
docker run --name activemq-artemis -it --rm -p 8161:8161 -p 61616:61616 vromero/activemq-artemis
```
- Set credentials in application.properties
```
# defaults for this docker image - vromero/activemq-artemis
spring.artemis.user=artemis
spring.artemis.password=simetraehcapa
```

