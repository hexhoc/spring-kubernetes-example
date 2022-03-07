cd "../"
@REM clean all project builds
call mvnw.cmd clean -f pom.xml
@REM create new jar file for each project
call mvnw.cmd package -Dmaven.test.skip -f pom.xml

cd "./kbe-coffeehouse-coffee-service"
call docker build -t example/kbe-coffeehouse-coffee-service .

cd "../kbe-coffeehouse-gateway"
call docker build -t example/kbe-coffeehouse-gateway .

cd "../kbe-coffeehouse-inventory-failover"
call docker build -t example/kbe-coffeehouse-inventory-failover .

cd "../kbe-coffeehouse-inventory-service"
call docker build -t example/kbe-coffeehouse-inventory-service .

cd "../kbe-coffeehouse-order-service"
call docker build -t example/kbe-coffeehouse-order-service .

cd "../docker"

call docker-compose stop
call docker-compose rm -f
call docker-compose up --build --no-start