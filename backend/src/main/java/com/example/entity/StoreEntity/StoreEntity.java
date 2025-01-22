package com.example.entity.StoreEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("store")
public class StoreEntity {

    @PrimaryKey
    @Column("product_code") // 정확히 매핑
    private String productCode;

    @Column("product_name")
    private String productName;

    @Column("category")
    private String category;

    @Column("sub_category")
    private String subCategory;

    @Column("description")
    private String description;

    @Column("image_url")
    private String imageUrl;

    @Column("sale_price")
    private double salePrice;

    @Column("stock")
    private int stock;

    @Column("manufacturer_id")
    private String manufacturerId;
}

