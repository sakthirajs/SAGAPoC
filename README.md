## README 
## To bring up all the services run the below command
docker compose up -d

[source code of all services is given just for your reference]

## To execute and recreate the error follow below steps, 
## 1. Login to rabbit mq (localhost:15676) with username: guest and password: guest
## 2. click on the "Queues and Streams" tab.
## 3. Inside the order.service queue , publish the below payload. 
{
        "orderId": 101,
        "customerId": 1,
        "itemId": 1,
        "quantity": 11,
        "amount": 100.0,
        "details": "Sample"
}

## This will make the order manager service to pass 4kb data to inventory service which will result in below error in inventory-service
"InventoryStatus : Rollback body Error reported: org.apache.camel.RuntimeCamelException: Cannot join LRA - cannot process this message."




