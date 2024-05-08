package com.hso.sagainventoryservice.routebuilder;

import com.hso.sagainventoryservice.model.OrderDto;
import com.hso.sagainventoryservice.service.InventoryService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaCompletionMode;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.saga.InMemorySagaService;
import org.springframework.stereotype.Component;

@Component
public class InventoryRouteBuilder extends RouteBuilder {

    private final InventoryService inventoryService;

    public InventoryRouteBuilder(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public void configure() throws Exception {

//        getContext().addService(new InMemorySagaService());

        onException(Exception.class)
                .handled(true)
                .transform().simple("Error reported: ${exception.message} - cannot process this message.")
                .to("direct:rollbackInventory");

        from("spring-rabbitmq:saga.poc?queues=inventory.service&routingKey=inventory.key")
                .log("message body from OrderManager ${body}")
                .log("InventoryStatus Received from  OrderManager ${exchange.getIn().getHeader(\"InventoryStatus\").toString()}")
                .unmarshal().json(JsonLibrary.Jackson, OrderDto.class) // to Convert String to Java Object
                .saga()
                .propagation(SagaPropagation.REQUIRES_NEW)
                .completionMode(SagaCompletionMode.AUTO)
                .option("body", body())
                .compensation("direct:rollbackInventory")
                .choice()
                .when(header("InventoryStatus").isEqualTo("Rollback"))
                .to("direct:rollbackInventory")
                .otherwise()
                .to("direct:updateInventory")
                .endChoice()
                .end();


        from("direct:rollbackInventory")
                .bean(inventoryService, "restoreInventory")
                .log("rollback inventory done")
                .setHeader("InventoryStatus", simple("Rollback"))
                .log("InventoryStatus : ${exchange.getIn().getHeader(\"InventoryStatus\").toString()} body ${body}")
                .process(exchange -> {
                    exchange.getIn().setBody("Inventory Status Restored.");
                })
                .to("spring-rabbitmq:saga.poc?queues=inventory.response&routingKey=inventory.response.key");

        from("direct:updateInventory")
                .bean(inventoryService, "updateInventory")
                .log("update inventory done")
                .setHeader("InventoryStatus", simple("Success"))
                .log("InventoryStatus : ${exchange.getIn().getHeader(\"InventoryStatus\").toString()} body ${body}")
                .marshal(dataFormat().json().end())
                .to("spring-rabbitmq:saga.poc?queues=inventory.response&routingKey=inventory.response.key")
                .end();



    }

}
