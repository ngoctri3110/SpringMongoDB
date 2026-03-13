package com.example.ecommerce.model;

import com.example.ecommerce.model.base.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Category document - Danh mục sản phẩm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "categories")
public class Category extends BaseDocument {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String slug;

    private String description;

    private String image;

    /**
     * Parent category ID - cho hierarchical categories
     */
    private String parentId;

    /**
     * Level - 0 for top-level, 1 for child, etc.
     */
    private int level = 0;

    private int displayOrder = 0;

    private boolean active = true;

    public Category(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
}
