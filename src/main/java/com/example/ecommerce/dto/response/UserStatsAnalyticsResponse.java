package com.example.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user statistics analytics response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatsAnalyticsResponse {

    private Long totalUsers;
    
    private Long activeUsers;
    
    private Long newUsers;
    
    private Long totalOrders;
    
    private Double averageOrdersPerUser;
    
    private Double conversionRate;
    
    private String period;
}
