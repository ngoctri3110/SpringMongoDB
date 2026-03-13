package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho cập nhật Review
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReviewRequest {

    @Min(value = 1, message = "Đánh giá phải từ 1-5 sao")
    @Max(value = 5, message = "Đánh giá phải từ 1-5 sao")
    private Integer rating;

    private String title;

    private String comment;

    private String status;
}
