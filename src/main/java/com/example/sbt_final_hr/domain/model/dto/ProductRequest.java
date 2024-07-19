package com.example.sbt_final_hr.domain.model.dto;

import com.example.sbt_final_hr.domain.model.entity.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
    //long으로 써야함 Long으로 쓰면 안됨.
    private long id;
    private String name;
    private int price;

    public Product toEntity() {
        Product product = new Product();
        //UPDATE때문에 PK SETTER 적어줘야함.
        product.setId(this.id);
        product.setName(this.name);
        product.setPrice(this.price);
        return product;
    }





}
