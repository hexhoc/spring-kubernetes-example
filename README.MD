# SPRING BOOT MICROSERVICES ON KUBERNETES

## REQUIREMENTS

1. java 17
2. Docker
3. Kubernetes
4. Maven 4.0.0

## INSTALL KUBERNETES

 1. Docker desktop. Go to the setting - Kubernetes tab, and enable this options
 2. Open terminal and check nodes using command - kubectl get nodes

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