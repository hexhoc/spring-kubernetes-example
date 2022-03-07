cd "../"
# clean all project builds
sh mvn clean -f pom.xml
# create new jar file for each project
sh mvn package -Dmaven.test.skip -f pom.xml

cd "./kbe-coffeehouse-coffee-service" || exit
sh docker build -t example/kbe-coffeehouse-coffee-service-1.0 .

cd "../kbe-coffeehouse-gateway" || exit
sh docker build -t example/kbe-coffeehouse-gateway-1.0 .

cd "../kbe-coffeehouse-inventory-failover" || exit
sh docker build -t example/kbe-coffeehouse-inventory-failover-1.0 .

cd "../kbe-coffeehouse-inventory-service" || exit
sh docker build -t example/kbe-coffeehouse-inventory-service-1.0 .

cd "../kbe-coffeehouse-order-service" || exit
sh docker build -t example/kbe-coffeehouse-order-service-1.0 .

cd "../docker" || exit

docker-compose stop
docker-compose rm -f
docker-compose up --build -d