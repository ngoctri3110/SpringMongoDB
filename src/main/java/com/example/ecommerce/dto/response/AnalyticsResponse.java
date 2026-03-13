package com.example.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for general analytics response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsResponse {

    private RevenueAnalyticsResponse revenue;
    
    private List<TopProductAnalyticsResponse> topProducts;
    
    private UserStatsAnalyticsResponse userStats;
    
    private String period;
    
    private String generatedAt;
}
