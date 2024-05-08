package com.hso.sagainventoryservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDto {
    private int orderId;
    private int customerId;
    private int itemId;
    private int quantity;
    private double amount;
    private String details;
}
