package com.hso.sagainventoryservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item {
    public int id;
    private String itemName;
    private long quantity;

    public int getId(){
        return this.id;
    }
}
