package com.hso.saga.sagapaymentservice.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerCredit {

    private Integer customerId;
    private String customerName;
    private Double creditBalance;



}
