package com.hso.saga.sagapaymentservice.controller;


import com.hso.saga.sagapaymentservice.model.CustomerCredit;
import com.hso.saga.sagapaymentservice.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/credit")
public class CreditController {

    private final PaymentService paymentService;

    public CreditController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/getAll")
    public List<CustomerCredit> getAllItems(){
        return paymentService.getCustomerCreditAmount().values().stream().toList();
    }

}
