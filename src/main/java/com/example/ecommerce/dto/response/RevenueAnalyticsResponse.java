package com.example.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for revenue analytics response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueAnalyticsResponse {

    private BigDecimal totalRevenue;
    
    private BigDecimal averageOrderValue;
    
    private Long totalOrders;
    
    private BigDecimal totalRefunds;
    
    private String period;
    
    private String currency;
}
