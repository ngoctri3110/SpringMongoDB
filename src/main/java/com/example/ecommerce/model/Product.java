package com.example.ecommerce.model;

import com.example.ecommerce.model.base.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Product document - Sản phẩm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "products")
public class Product extends BaseDocument {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String slug;

    private String description;

    private String longDescription;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private String discountPercentage;

    /**
     * Reference to Category (_id)
     * Có thể sử dụng @DBRef hoặc string reference
     */
    private String categoryId;

    /**
     * Product specifications/attributes - dynamic fields
     */
    private Map<String, Object> attributes = new HashMap<>();

    /**
     * Tags cho search
     */
    private List<String> tags = new ArrayList<>();

    /**
     * Image URLs
     */
    private List<String> images = new ArrayList<>();

    /**
     * Stock/inventory
     */
    private int stock = 0;

    /**
     * Rating aggregates
     */
    private double avgRating = 0.0;
    private int reviewCount = 0;

    /**
     * Status
     */
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, DISCONTINUED

    /**
     * SEO
     */
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;

    private boolean featured = false;

    public Product(String name, String slug, BigDecimal price, String categoryId) {
        this.name = name;
        this.slug = slug;
        this.price = price;
        this.categoryId = categoryId;
        this.status = "ACTIVE";
    }
}
