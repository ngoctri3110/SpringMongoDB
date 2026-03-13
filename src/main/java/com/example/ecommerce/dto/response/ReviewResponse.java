package com.example.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO cho Review response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private String id;
    private String userId;
    private String productId;
    private Integer rating;
    private String title;
    private String comment;
    private List<String> helpful;
    private Boolean verified;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
