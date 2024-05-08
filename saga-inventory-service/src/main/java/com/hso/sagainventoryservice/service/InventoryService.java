package com.hso.sagainventoryservice.service;

import com.hso.sagainventoryservice.model.Item;

import com.hso.sagainventoryservice.model.OrderDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class InventoryService {

    @Getter
    private  Map<Integer, Item> inventory;

    @PostConstruct
    private void init(){
        inventory = Stream.of(
                        new Item(1,"xyz",20000),
                        new Item(2,"laptop", 30000),
                        new Item(3, "TV", 10000))
                .collect(Collectors.toMap(
                        Item::getId,
                        item -> item
                ));
    }


    public void updateInventory(Exchange exchange) {
        OrderDto orderDto = exchange.getMessage().getBody(OrderDto.class);
        Item item = inventory.get(orderDto.getItemId());
        if(orderDto.getQuantity() < item.getQuantity()){
            item.setQuantity(item.getQuantity() - orderDto.getQuantity());
            log.info("Item updated : {}",item);
            inventory.put(orderDto.getItemId(), item);
        }else
            throw new RuntimeException("Insufficient items in inventory");
    }

    public void restoreInventory(Exchange exchange){
        OrderDto orderDto = exchange.getMessage().getBody(OrderDto.class);
        if(orderDto == null) return;
        Item item = inventory.get(orderDto.getItemId());
        long currentQuantity = item.getQuantity();
        long orderQuantity = orderDto.getQuantity();
        if(orderQuantity >= currentQuantity) return;
        item.setQuantity(currentQuantity + orderQuantity);
        inventory.put(orderDto.getItemId(), item);
    }

}
