# SPRING BOOT MICROSERVICES ON KUBERNETES

In this example we're looking at four microservices that worked in orchestration. We can view coffee, add coffee, delete
coffee, get coffee inventory, brew new coffee, and we can order coffee

Work order: 

1. ```order-service``` is generating orders (scheduled job to order coffee)
2. This is decrease inventory in ```inventory-service``` if ```inventory-service``` is fail, then we use ```inventory-failover``` 
3. If inventory is low, then coffee-service is brew new coffee and adding it to inventory



## REQUIREMENTS

1. java 17
2. Docker
3. Kubernetes
4. Maven 4.0.0

## BUILD

### Build using docker-compose (for local usage)
1. Open ```./docker``` folder and run command ```sh createDockerImages.sh```
2. Try to use ```GET http://localhost:9090/api/v1/coffee```

### Build using kubernetes (for prod)
1. Install kubectl and minikube, and start it ```minikube start```
2. Check node ```kubectl get nodes``` and context ```kubectl config get-contexts``` should be "minikube"
3. Run ```minikube dashboard``` and dashboard will open automatically in browser

### Configure elastic, kibana, filebeat
1. Open kibana ```http://localhost:5601```
2. Add dashboard for stream with name "filebeat". Kibana discover this stream automatically

### Launch createDockerImages script
```
cd docker
./createDockerImages
```
After that, all project automatically compile and create jar and docker image.


## SERVICES
| Services           | port | debug port |
|--------------------|------|------------|
| coffee-service     | 8080 | 9080       |
| inventory-service  | 8082 | 9082       |
| inventory-failover | 8083 | 9083       |
| order-service      | 8081 | 9081       |
| gateway            | 9090 | 9092       |

## INTRODUCTION

COFFEE HOUSE SPRING BOOT MICROSERVICES
- Three microservices to user to mimic the operation of a coffee house
  - **Coffee inventory service** - holds information about inventory levels.
	- Rest API used to get inventory information
	- JMS messaging used to increase inventory from brewing, decrease inventory froom order allocation
  **Coffee Inventory Failover Service** - Service to provide a positive response if inventory service is not avaible.
  - **Coffee service** - holds information about Coffees brewed, Rest API for Coffee CRUD opearations.
	- JMS messaging to brew Coffee when inventory is low
	- Scheduled job to brew Coffee
  - **Coffee order service** - Holds information about veer orders
	- RestAPI for Coffee order Operations
	- JMS Messaging to allocate Coffee orders
	- Tasting room service - sheduled job to order Coffees, which decreases inventory, which triggers brewing.
    Important to understand Coffee Order Service will generate orders, request allocations from inventory (reducing inventory) which triggers Coffee service to brew Coffee (adding to inventory)
- Data is pesisted to postgreSQL via JPA/Hibernate
- JMS is used for messaging between services

## INTERACTION OF SERVICES BETWEEN EACH OTHER

### ActiveMQ queue

**brewing-request** - Coffee-service using this queue to communicate for itself. If we have inventory on hand more than min needed, then we send message to brew that coffee. 

**new-inventory** - coffee-service send, inventory-service listen.  After coffee-service brew new coffee it send message to inventory-service to increase amount of this coffee.

**validate-order** - order-service send, coffee-service listen. order-service create new order and send it to coffee-service to validate it. If all coffee in order founded by upc, then order is validate.

**validate-order-result** - coffee-service send, order-service listen. If the order verification is successful, the coffee-service sends the result back to the order-service.

**allocate-order** - order-service send, inventory-service listen. Performs the shipment of coffee to the customer by order

**allocate-order-result** - inventory-service send, order-service listen. Get result about successfuly allocation.

**allocation-failure** - inventory-service send, order-service listen. Get result about failure allocation.

**deallocate-order** - order-service send, but have not any listener. Needed to fix this


****


### Coffee-service

Every 5 seconds, a scheduled task in **coffee-service** is run that checks the current inventory on hand for all
coffee (request **inventory-service**), if the inventory on hand <= min inventory, then we send a request to actimeMq to
brew more coffee. Coffee-service send message to brew coffee in activeMq and then listen that message(**
BrewCoffeeListener** class), and after get that message from queue start brewing (**BrewingServiceImpl** class)

### Order-service

Every 500 ms running method **createTastingRoomOrder** in **TastingRoomServiceScheduled** class. This method get random
coffee from **coffee-service**, create new order, create new state machine by current order and start new state machine
event (**VALIDATE_ORDER**).

State machine has total 5 actions, explore 2 of them:

1. **ValidateCoffeeOrder**. Use JmsTemplate to send message to **VALIDATE_ORDER_QUEUE** and **
   CoffeeOrderValidationListener** class in **coffee-service** listen this queue.

   If validate is passed, then we send message to **VALIDATE_ORDER_RESULT_QUEUE** and **
   CoffeeOrderValidationResultListener** class in **order-service** listen this queue.

2. **AllocateCoffeeOrder**. Use JmsTemplate to send message to **ALLOCATE_ORDER_QUEUE** and **AllocationListener** class
   in **inventory-service** listen this queue.

   If order is allocated normally, then we send message to **ALLOCATE_ORDER_RESULT_QUEUE** and **
   CoffeeOrderAllocationResultListener** class in **order-service** listen this queue.


## CHANGES FOR KUBERNETES

1. **Eurika** - is not used. Kubernetes is used for service discovery
2. **Spring cloud config** - Not used. Kubernetes used to manage environment properties.
3. **Single PostgreSQL instance** - One instance used. Just for simplicity, a production deployment should habe independent PostgreSQL database instances.
4. **Single github repo** - Again for simplicity. Microservices typically would have independent soource code repository


# Kubernetes VS Spring cloud

## REQUIREMENTS FOR MICROSERVICES:
1. **Discovery**. If service more than one, they should discover each other.
2. **Fault tolerance**. Resistance to crashes of one service. Fault tolerance - this is when one service crashes, resilience is when an atomic bomb falls on a data center
3. **Resilience**. Resistance to the fall of the entire data center
4. **Configuration**. Applications become ephemeral. They appear quickly and disappear quickly, and you need to configure them on the fly
5. **API Managment**. When there are a lot of applications, they need to be managed. More precisely, applications expose the API to the outside and they need to be managed.

## JAVA STACK:
1. Discovery - Eureka
2. Fault tolerance - Spring Cloud Circuit breaker
3. Resilience - Spring Cloud Load balancing
4. Configuration - Spring cloud Config server
5. API managment - Spring Cloud  gateway

## KUBERNETES STACK:
1. Discovery - K8S
2. Fault tolerance - Services + liveness / readiness
3. Resilience - istio (Service Mesh Pattern)
4. Configuration - ConfigMap + Secrets
5. API managment - istio (Service Mesh Pattern)