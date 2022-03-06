# Coffee house Inventory Service

**openApi** http://localhost:8082/v3/api-docs/

**swagger** http://localhost:8082/swagger-ui/index.html

## Description
Service for working with coffee objects. We can create new coffee, get information about an existing coffee or get list of coffee.

This service is used to control the remaining product (coffee).

**coffee-service** requests the current service every 5 seconds, whether there are leftovers in the warehouse, if there
are not enough of them, then it starts brewing coffee.

**order-service** every 500 ms creates an order that reduces the remaining stock. Accordingly, before creating a new
order, he creates a request to find out the number of stock balances