package com.hso.sagaordermanager.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private int orderId;
    private int customerId;
    private int itemId;
    private int quantity;
    private double amount;
    private String details;
}
