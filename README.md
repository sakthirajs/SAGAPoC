## README 
## To bring up all the services run the below command
docker compose up -d

## To execute and recreate the error, login to rabbit mq (localhost:15676) and click on the "Queues and Streams" tab.
## Inside the order.service, publish the below payload. This will make the order manager service to pass 4kb data to inventory service which will result in error "InventoryStatus : Rollback body Error reported: org.apache.camel.RuntimeCamelException: Cannot join LRA - cannot process this message."
  {
        "orderId": 101,
        "customerId": 1,
        "itemId": 1,
        "quantity": 11,
        "amount": 100.0,
        "details": "Sample"
    }



