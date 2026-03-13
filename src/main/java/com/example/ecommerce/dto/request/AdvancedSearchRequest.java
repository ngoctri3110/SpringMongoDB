package com.example.ecommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for advanced search functionality
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvancedSearchRequest {

    private String query;
    
    private String category;
    
    private BigDecimal minPrice;
    
    private BigDecimal maxPrice;
    
    private Integer minRating;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private Integer pageNumber;
    
    private Integer pageSize;
    
    private String sortBy;
    
    private String sortDirection;
}
