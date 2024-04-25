package com.hso.saga.sagapaymentservice.service;


import com.hso.saga.sagapaymentservice.model.CustomerCredit;
import com.hso.saga.sagapaymentservice.model.OrderDto;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class PaymentService {

    @Getter
    private Map<Integer, CustomerCredit> customerCreditAmount;

    @PostConstruct
    private void init() {
        customerCreditAmount = Stream.of(
                        new CustomerCredit(1, "jim", 2000.00),
                        new CustomerCredit(2, "pam", 3000.00),
                        new CustomerCredit(3, "mike", 5000.00))
                .collect(Collectors.toMap(
                        CustomerCredit::getCustomerId,
                        customerCredit -> customerCredit
                ));
    }

    public void updatePayment(Exchange exchange) {
        OrderDto orderDto = exchange.getMessage().getBody(OrderDto.class);
        CustomerCredit customerIdForOrder = customerCreditAmount.get(orderDto.getCustomerId());
        if (orderDto.getAmount() < customerIdForOrder.getCreditBalance()) {
            customerIdForOrder.setCreditBalance(customerIdForOrder.getCreditBalance() - orderDto.getAmount());
            log.info("Payment updated : {}", customerIdForOrder);
            customerCreditAmount.put(orderDto.getCustomerId(), customerIdForOrder);
        } else
            throw new RuntimeException("Insufficient balance in credit");

    }

    public void revertPayment(Exchange exchange) {
        OrderDto orderDto = exchange.getMessage().getBody(OrderDto.class);
       if(orderDto == null ) return;
        CustomerCredit customerIdForOrder = customerCreditAmount.get(orderDto.getCustomerId());
        double currentBalance =  customerIdForOrder.getCreditBalance();
        double customerAmountForOrder =orderDto.getAmount();
        customerIdForOrder.setCreditBalance(currentBalance);
        customerCreditAmount.put(orderDto.getCustomerId(), customerIdForOrder);
    }

}