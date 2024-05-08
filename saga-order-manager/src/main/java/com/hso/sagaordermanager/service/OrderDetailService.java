package com.hso.sagaordermanager.service;

import com.hso.sagaordermanager.model.OrderDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;


import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class OrderDetailService {

    public Map<Integer, OrderDto> orderDetails;

    @PostConstruct
    private void init(){
        orderDetails = Stream.of(
                new OrderDto(101,1,1,10,100.00,"Sample")
        ).collect(Collectors.toMap(
                OrderDto::getOrderId ,
                orderDto -> orderDto
        ));
    }

    public void addOrder(Exchange exchange){
        OrderDto orderDto = exchange.getMessage().getBody(OrderDto.class);
        orderDetails.put(orderDto.getOrderId(),orderDto);
    }

    public void addOrder4Kb(Exchange exchange){
        OrderDto orderDto = exchange.getMessage().getBody(OrderDto.class);
        orderDto.setDetails("a".repeat(4*1024)); //4kb
        orderDetails.put(orderDto.getOrderId(),orderDto);
    }

    public void addOrder3Kb(Exchange exchange){
        OrderDto orderDto = exchange.getMessage().getBody(OrderDto.class);
        orderDto.setDetails("a".repeat(3*1024)); //3kb
        orderDetails.put(orderDto.getOrderId(),orderDto);
    }

    public void removeOrder(Exchange exchange){
        OrderDto orderDto = exchange.getMessage().getBody(OrderDto.class);
        if (orderDto == null) return;
        orderDetails.remove(orderDto.getOrderId());
    }


}
