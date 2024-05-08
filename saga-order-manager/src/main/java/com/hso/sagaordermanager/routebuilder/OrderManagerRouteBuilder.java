package com.hso.sagaordermanager.routebuilder;

import com.hso.sagaordermanager.model.OrderDto;
import com.hso.sagaordermanager.service.OrderDetailService;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaCompletionMode;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.saga.InMemorySagaService;
import org.springframework.stereotype.Component;

@Component
public class OrderManagerRouteBuilder extends RouteBuilder {

    private OrderDto orderDto = null;

    private final OrderDetailService orderDetailService;

    public OrderManagerRouteBuilder(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @Override
    public void configure() throws Exception {

//        getContext().addService(new InMemorySagaService());

        onException(Exception.class)
                .handled(true)
                .to("direct:cancelOrder");


        from("spring-rabbitmq:saga.poc?queues=order.service&routingKey=order.key")
                .log("message body received at OrderManager ${body}")
                .unmarshal().json(JsonLibrary.Jackson, OrderDto.class)
                .process(this::enrich)
                .saga()
                .propagation(SagaPropagation.REQUIRES_NEW)
                .completionMode(SagaCompletionMode.AUTO)
                .option("body", body())
                .compensation("direct:cancelOrder")
                .to("direct:newOrder")
                .end();

        //Initiate new order
        from("direct:newOrder")
                .log("creating a new Order")
                .setHeader("InventoryStatus", simple("Rollback"))
                .setHeader("PaymentStatus", simple("Rollback"))
//                .bean(orderDetailService, "addOrder")
//                .bean(orderDetailService, "addOrder4Kb")
                .bean(orderDetailService, "addOrder4Kb")
                .to("direct:updateInventory");

        //call inventory service to update Inventory
        from("direct:updateInventory")
                .marshal(dataFormat().json().end())
                .log("Calling Inventory Service To update Inventory ${body}")
                .setHeader("InventoryStatus",simple("pending"))
                .to("spring-rabbitmq:saga.poc?queues=inventory.service&routingKey=inventory.key");

        //upon getting success response from the inventory service, we will call the PaymentService else we will cancel order
        from("spring-rabbitmq:saga.poc?queues=inventory.response&routingKey=inventory.response.key")
                .log("InventoryStatus Received from  InventoryService ${exchange.getIn().getHeader(\"InventoryStatus\").toString()} " +
                        " body:  ${body}")
                .choice()
                .when(header("InventoryStatus").isEqualTo("Success"))
                .to("direct:updatePayment")
                .otherwise()
                .to("direct:cancelOrder")
                .endChoice();

        //Call payment service to update the Payment Information
        from("direct:updatePayment")
                .log("Calling Payment Service To update payment ${body}")
                .to("spring-rabbitmq:saga.poc?queues=payment.service&routingKey=payment.key");

        //upon getting success response from the payment service, we will call the PaymentService else we will complete the order
        //upon getting Rollback response, we will call the inventory service to roll back the changes with status - Rollback
        from("spring-rabbitmq:saga.poc?queues=payment.response&routingKey=payment.response.key")
                .log("PaymentStatus Received from  PaymentService ${exchange.getIn().getHeader(\"PaymentStatus\").toString()} " +
                        " body:  ${body}")
                .choice()
                .when(header("PaymentStatus").isEqualTo("Success"))
                .log("Order Placed Successfully")
                .otherwise()
                .process(exchange -> {
                    // Set a new body value after compensation
                    exchange.getIn().setBody(orderDto);
                })
                .marshal(dataFormat().json().end())
                .setHeader("InventoryStatus", simple("Rollback"))
                .log("Calling Inventory service to rollback Inventory with Body : ${body}")
                .to("spring-rabbitmq:saga.poc?queues=inventory.service.&routingKey=inventory.key")
                .endChoice();

        //upon getting either Rollback status from Payment or Inventory , we will cancel the order.
        from("direct:cancelOrder")
                .log("Cancelling Order ")
                .log("PaymentStatus : ${exchange.getIn().getHeader(\"PaymentStatus\").toString()}")
                .log("InventoryStatus : ${exchange.getIn().getHeader(\"InventoryStatus\").toString()}")
                .process(exchange -> {
                    exchange.getIn().setBody(orderDto);
                })
                .bean(orderDetailService, "removeOrder")
                .log("Order Cancelled");
    }

    private void enrich(Exchange exchange) {
        orderDto = exchange.getMessage().getBody(OrderDto.class);
    }
}
