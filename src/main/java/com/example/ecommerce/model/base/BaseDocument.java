package com.example.ecommerce.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * Base document với audit fields (createdAt, updatedAt)
 * Tất cả entities nên extend class này
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDocument {

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
