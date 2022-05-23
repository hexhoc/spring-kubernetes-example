# Coffeehouse Coffee Service

**openApi** http://localhost:8080/v3/api-docs/

**swagger** http://localhost:8080/swagger-ui/index.html

## Description
Service for working with coffee objects. We can create new coffee, get information about an existing coffee or get list of coffee.

### Request inventory on hand
If we request information about coffee, with **showInventoryOnHand** parameter, then **coffee-service** make request to **inventory-service** to get current inventory.

Is three way to get data from **inventory-service**:
1. Using feign client failover(**InventoryFailoverService**). Feign client get info from **inventory-failover**. For using this client you should use property - spring.profile.active = "local-discovery" or spring.profile.active = "digitalocean"
2. Using feign client (**CoffeeInventoryServiceFeign**)
3. Using rest template (**CoffeeInventoryServiceRestTemplateImpl**). This client used by default.

### Brew coffee
Every 5 seconds, a scheduled task is run that checks the current inventory on hand for all coffee (request **inventory-service**), if the inventory on hand <= min inventory, then we send a request to actimeMq to brew more coffee. 
Coffee-service send message to brew coffee in activeMq and then listen that message(**BrewCoffeeListener** class), and after get that message from queue start brewing (**BrewingServiceImpl** class)

### Order validation
Class **CoffeeOrderValidationListener** used for listen (@KafkaListener) any order event from **order-service** (validate-order queue) and after compute result send it back to **order-service** (validate-order-result queue)


