package com.hso.saga.sagapaymentservice.routebuilder;

import com.hso.saga.sagapaymentservice.model.OrderDto;
import com.hso.saga.sagapaymentservice.service.PaymentService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaCompletionMode;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.saga.InMemorySagaService;
import org.springframework.stereotype.Component;

@Component
public class PaymentRouteBuilder extends RouteBuilder {

    private final PaymentService paymentService;

    public PaymentRouteBuilder(PaymentService inventoryService) {
        this.paymentService = inventoryService;
    }

    @Override
    public void configure() throws Exception {

//        getContext().addService(new InMemorySagaService());

        onException(Exception.class)
                .handled(true)
                .transform().simple("Error reported: ${exception.message} - cannot process this message.")
                .to("direct:rollbackPayment");

        from("spring-rabbitmq:saga.poc?queues=payment.service&routingKey=payment.key")
                .log("message body from OrderManager ${body}")
                .unmarshal().json(JsonLibrary.Jackson, OrderDto.class) // to Convert String to Java Object
                .saga()
                .propagation(SagaPropagation.REQUIRES_NEW)
                .completionMode(SagaCompletionMode.AUTO)
                .option("body", body())
                .compensation("direct:rollbackPayment")
                .to("direct:updatePayment") // completion endpoint
                .end();


        from("direct:rollbackPayment")
                .bean(paymentService, "revertPayment")
                .log("rollback payment done")
                .setHeader("PaymentStatus", simple("Rollback"))
                .log("PaymentStatus : ${exchange.getIn().getHeader(\"PaymentStatus\").toString()} body ${body}")
                .marshal(dataFormat().json().end())
                .to("spring-rabbitmq:saga.poc?queues=payment.response&routingKey=payment.response.key");


        from("direct:updatePayment")
                .bean(paymentService, "updatePayment")
                .log("update Payment done")
                .setHeader("PaymentStatus", simple("Success"))
                .log("PaymentStatus : ${exchange.getIn().getHeader(\"PaymentStatus\").toString()}")
                .log("with body ${body}")
                .marshal(dataFormat().json().end())
                .to("spring-rabbitmq:saga.poc?queues=payment.response&routingKey=payment.response.key");

    }

}
