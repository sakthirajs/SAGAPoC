package com.hso.sagainventoryservice.controller;

import com.hso.sagainventoryservice.model.Item;
import com.hso.sagainventoryservice.service.InventoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/getAll")
    public List<Item> getAllItems(){
        return inventoryService.getInventory().values().stream().toList();
    }
}
