package com.hso.sagaordermanager.controller;


import com.hso.sagaordermanager.model.OrderDto;
import com.hso.sagaordermanager.service.OrderDetailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orderdetails")
public class OrderDetailControllers {

    private final OrderDetailService orderDetailService;

    public OrderDetailControllers(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @GetMapping("/getAll")
    public List<OrderDto> getAllOrderDetails(){
       return new ArrayList<>(orderDetailService.orderDetails.values());
    }

}
