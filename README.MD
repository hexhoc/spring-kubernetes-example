# SPRING BOOT MICROSERVICES ON KUBERNETES

## INTRODUCTION

COFFEE HOUSE SPRING BOOT MICROSERVICES
- Three microservices to user to mimic the operation of a coffee house
  - **Coffee inventory service** - holds information about inventory levels.
	- Rest API used to get inventory information
	- JMS messaging used to increase inventory from brewing, decrease inventory froom order allocation
  **Coffee Inventory Failover Service** - Service to provide a positive response if inventory service is not avaible.
  - **Coffee service** - holds information about Coffee brewed, Rest API for Coffee CRUD opearations.
	- JMS messaging to brew Coffee when inventory is low
	- Scheduled job to brew Coffee
  - **Coffee order service** - Holds information about veer orders
	- RestAPI for Coffee order Operations
	- JMS Messaging to allocate Coffee orders
	- Tasting room service - sheduled job to order Coffee, which decreases inventory, which triggers brewing.
    Important to understand Coffee Order Service will generate orders, request allocations from inventory (reducing inventory) which triggers Coffee service to brew Coffee (adding to inventory)
- Data is pesisted to postgreSQL via JPA/Hibernate
- JMS is used for messaging between services

In this example we're looking at four microservices that worked in orchestration. We can view coffee, add coffee, delete
coffee, get coffee inventory, brew new coffee, and we can order coffee

Work order: 

1. ```order-service``` is generating orders (scheduled job to order coffee)
2. This is decrease inventory in ```inventory-service``` if ```inventory-service``` is fail, then we use ```inventory-failover``` 
3. If inventory is low, then coffee-service is brew new coffee and adding it to inventory

## INTERACTION OF SERVICES BETWEEN EACH OTHER

### ActiveMQ queue

1. **brewing-request** - **coffee-service** send message every 5 second  itself listen message.

    If we have inventory on hand more than min needed, then we send message to brew that coffee. 

2. **new-inventory** - **coffee-service** send, **inventory-service** listen. 

    After **coffee-service** brew new coffee it sends message to **inventory-service** to increase amount of this coffee.

3. **validate-order** - **order-service** send, **coffee-service** listen. 

    **order-service** create new order and send it to **coffee-service** to validate it. If all coffee in order founded by upc, then order is validate.

4. **validate-order-result** - **coffee-service** send, **order-service** listen. 

    If the order verification is successful, the **coffee-service** sends the result back to the **order-service**.

5. **allocate-order** - **order-service** send, **inventory-service** listen. 

    Performs the shipment of coffee to the customer by order

6. **allocate-order-result** - **inventory-service** send, **order-service** listen. 

    Get result about successfully allocation.

7. **allocation-failure** - **inventory-service** send, **order-service** listen. 

    Get result about failure allocation.

8. **deallocate-order** - **order-service** send, but have not any listener. 

    Needed to fix this


## SCHEDULED JOBS

### Check for low inventory (coffee-service, every 5 seconds)
1. **coffee-service** check the current inventory on hand for all coffee (**brewingService.checkForLowInventory()**). 
2. **coffee-service** Send rest api request to **inventory-service**, and get quantity on hand.
3. **coffee-service** if the inventory on hand <= min inventory, **coffee-service** send message to brew more coffee in queue **brewing-request**
4. **coffee-service** listen queue (**BrewCoffeeListener**. After get that message **coffee-service** begin brew coffee, increase quantity.
5. **coffee-service** send new quantity in queue **new-inventory**
6. **inventory-service** listen queue **new-inventory**. Get new inventory object (coffee id with quantity) and save it in db (table **coffee_inventory**)

### 2. Create tasting room order (order-service, every 500 ms)
1. **order-service** create new order with random coffee. **tastingRoomService.createTastingRoomOrder()**. Set order status = NEW
2. **order-service** send event to state machine. **CoffeeOrderManagerImpl.sendCoffeeOrderEvent()**
3. **order-service** state machine start event action in **ValidateCoffeeOrder.execute()** and send order to queue **validate-order**
4. **coffee-service** **CoffeeOrderValidationListener.listen()** listen queue **validate-order** and send message to queue **validate-order-result**
5. **order-service** listen queue **validate-order-result** (**CoffeeOrderValidationResultListener.listen()**) change order status from PENDING_VALIDATION to VALIDATED. 
6. **order-service** state machine start event action ALLOCATE_ORDER (**AllocateCoffeeOrder.execute()**) and send message (order) to queue **allocate-order**
7. **inventory-service** listen queue **allocate-order** in **AllocationListener.listen()** and check whether there are enough quantity to allocate an order or not and send result to queue **allocate-order-result**
8. **order-service** state machine set status ALLOCATED and save order to DB.


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
4. Apply that commands:
    ```
    kubectl apply -f postgres-deployment.yml
    kubectl apply -f postgres-service.yml
    kubectl apply -f jms-deployment.yml
    kubectl apply -f jms-service.yml
    kubectl apply -f inventory-deployment.yml
    kubectl apply -f inventory-service.yml
    kubectl apply -f inventory-failover-deployment.yml
    kubectl apply -f inventory-failover-service.yml
    kubectl apply -f coffee-service-deployment.yml
    kubectl apply -f coffee-service.yml
    kubectl apply -f order-service-deployment.yml
    kubectl apply -f order-service.yml
    ```

### Configure elastic, kibana, filebeat
1. Open kibana ```http://localhost:5601```
2. Add dashboard for stream with name "filebeat". Kibana discover this stream automatically

Filebeat collect console logs from every services and shipping it to elastic. Kibana check elastic and get all logs



## SERVICES
| Services           | port | debug port |
|--------------------|------|------------|
| coffee-service     | 8080 | 9080       |
| inventory-service  | 8082 | 9082       |
| inventory-failover | 8083 | 9083       |
| order-service      | 8081 | 9081       |
| gateway            | 9090 | 9092       |


## CHANGES FOR KUBERNETES

1. **Eureka** - is not used. Kubernetes is used for service discovery
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

## HOW WE PREPARE THIS PROJECT FOR KUBERNETES

### Create deployments for every services:
1. Postgres 

    Create and apply deployment 
    
    ```kubectl create deployment postgres --image=postgres --dry-run=client -o=yaml > postgres-deployment.yml```
    
    Open file postgres-deployment.yml and add env block.

    ```kubectl apply -f postgres-deployment.yml```

    Create and apply service 
    
    ```kubectl create service clusterip postgres --tcp=5432:5432 --dry-run=client -o=yaml > postgres-service.yml```

    ```kubectl apply -f postgres-service.yml```
2. JMS

    ```kubectl create deployment jms --image=vromero/activemq-artemis --dry-run=client -o=yaml > jms-deployment.yml```

    ```kubectl apply -f jms-deployment.yml```

    ```kubectl create service clusterip jms --tcp=8161:8161 --tcp=61616:61616 --dry-run=client -o=yaml > jms-service.yml```

    ```kubectl apply -f jms-service.yml```

3. Inventory-service

    ```kubectl create deployment inventory-service --image=hexhoc/kbe-coffeehouse-inventory-service --dry-run=client -o=yaml > inventory-deployment.yml```

    ```kubectl apply -f inventory-deployment.yml```

    ```kubectl create service clusterip inventory-service --tcp=8082:8082 --dry-run=client -o=yaml > inventory-service.yml```

    ```kubectl apply -f inventory-service.yml```

4. Inventory-failover-service

    ```kubectl create deployment inventory-failover --image=hexhoc/kbe-coffeehouse-inventory-failover --dry-run=client -o=yaml > inventory-failover-deployment.yml```

    ```kubectl apply -f inventory-failover-deployment.yml```

    ```kubectl create service clusterip inventory-failover --tcp=8083:8083 --dry-run=client -o=yaml > inventory-failover-service.yml```

    ```kubectl apply -f inventory-failover-service.yml```

5. coffee-service

    ```kubectl create deployment coffee-service --image=hexhoc/kbe-coffeehouse-coffee-service --dry-run=client -o=yaml > coffee-service-deployment.yml```

    ```kubectl apply -f coffee-service-deployment.yml```

    ```kubectl create service clusterip coffee-service --tcp=8080:8080 --tcp=9080:9080 --dry-run=client -o=yaml > coffee-service.yml```

    ```kubectl apply -f coffee-service.yml```

6. order-service

    ```kubectl create deployment order-service --image=hexhoc/kbe-coffeehouse-order-service --dry-run=client -o=yaml > order-service-deployment.yml```

    ```kubectl apply -f order-service-deployment.yml```

    ```kubectl create service clusterip order-service --tcp=8081:8081 --tcp=9081:9081 --dry-run=client -o=yaml > order-service.yml```

    ```kubectl apply -f order-service.yml```

10. Check all deployment and services

    ```kubectl get all```

11. Add readiness and liveness check. 

    Each service depends on the other and must wait for all dependent services before starting. For that we are use kubernetes readiness and liveness block

    add to each service properties file this:
    ```
    management.endpoint.health.probes.enabled=true
    management.health.readinessstate.enabled=true
    management.health.livenessstate.enabled=true
    ```

    add this block in each deployment:
    ```
        readinessProbe:
          httpGet:
            port: 8080
            path: /actuator/health/readiness
        livenessProbe:
          httpGet:
            port: 8080
            path: /actuator/health/liveness
    ```

12. Add gracefull shutdown for every service

    add to each service properties file this:
    ```
    server.shutdown=graceful
    ```

    add this block in each deployment:
    ```
        lifecycle:
          preStop:
            exec:
              command: ["sh", "-c", "sleep 10"]
    ```