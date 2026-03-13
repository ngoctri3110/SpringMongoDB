package com.example.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private String id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private String categoryId;
    private List<String> tags;
    private List<String> images;
    private int stock;
    private double avgRating;
    private int reviewCount;
    private String status;
    private boolean featured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
