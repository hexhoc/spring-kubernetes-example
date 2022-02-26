# Coffeehouse Coffee Service

**openApi** http://localhost:8080/v3/api-docs/

**swagger** http://localhost:8080/swagger-ui/index.html

## Description
Service for working with coffee objects. We can create new coffee, get information about an existing coffee or get list of coffee.

If we request information about coffee, with **showInventoryOnHand** parameter, then **coffee-service** make request to **inventory-service** to get current inventory.

Every 5 seconds, a scheduled task is run that checks the current inventory on hand for all coffee (request ** inventory-service **), if the inventory on hand <= min inventory, then we send a request to actimeMq to brew more coffee. 
Coffee-service send message to brew coffee in activeMq and then listen that message(**BrewCoffeeListener** class), and after get that message from queue start brewing (**BrewingServiceImpl** class)

