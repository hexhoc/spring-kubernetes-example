# Coffeehouse Order Service

This service is used to work with orders. By default, a scheduled task is started every 500 ms, which
generates a tasting order.

In this service, the *state machine* mechanism is used, each state of the machine is tied to the status of the order and, depending on
the status change, the described actions are performed. List of actions in the package (**com.example.orderservice.sm.actions**)

At each status change event, we perform actions, some of these actions are related to the communication of the current
service with **coffee-service** and **inventory-service** via message queues (**JMS**)

An example of such actions is below:

1. **ValidateCoffeeOrder**. Use JmsTemplate to send message to **VALIDATE_ORDER_QUEUE** and **
   CoffeeOrderValidationListener** class in **coffee-service** listen this queue.

   If validate is passed, then we send message to **VALIDATE_ORDER_RESULT_QUEUE** and **
   CoffeeOrderValidationResultListener** class in **order-service** listen this queue.

2. **AllocateCoffeeOrder**. Use JmsTemplate to send message to **ALLOCATE_ORDER_QUEUE** and **AllocationListener** class
   in **inventory-service** listen this queue.

   If order is allocated normally, then we send message to **ALLOCATE_ORDER_RESULT_QUEUE** and **
   CoffeeOrderAllocationResultListener** class in **order-service** listen this queue.