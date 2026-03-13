package com.example.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for top product analytics response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopProductAnalyticsResponse {

    private String productId;
    
    private String productName;
    
    private Long totalSold;
    
    private BigDecimal totalRevenue;
    
    private Double averageRating;
    
    private Long totalReviews;
    
    private Integer rank;
}
