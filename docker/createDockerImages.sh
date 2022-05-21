
# before start this script you should do "sudo docker login"
docker login

cd "../"
# clean all project builds
sh mvnw clean -f pom.xml
# create new jar file for each project
sh mvnw package -Dmaven.test.skip -f pom.xml

cd "./kbe-coffeehouse-coffee-service" || exit
docker build -t hexhoc/kbe-coffeehouse-coffee-service .

cd "../kbe-coffeehouse-gateway" || exit
docker build -t hexhoc/kbe-coffeehouse-gateway .

cd "../kbe-coffeehouse-inventory-failover" || exit
docker build -t hexhoc/kbe-coffeehouse-inventory-failover .

cd "../kbe-coffeehouse-inventory-service" || exit
docker build -t hexhoc/kbe-coffeehouse-inventory-service .

cd "../kbe-coffeehouse-order-service" || exit
docker build -t hexhoc/kbe-coffeehouse-order-service .

cd "../docker" || exit

#docker push hexhoc/kbe-coffeehouse-coffee-service
#docker push hexhoc/kbe-coffeehouse-coffee-service
#docker push hexhoc/kbe-coffeehouse-inventory-failover
#docker push hexhoc/kbe-coffeehouse-inventory-service
#docker push hexhoc/kbe-coffeehouse-order-service

 docker-compose stop
 docker-compose rm -f
 docker-compose up --build -d