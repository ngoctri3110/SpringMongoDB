package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.request.CreateProductRequest;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapper interface cho Product entity
 * Chuyển đổi giữa Product entity, ProductResponse DTO và CreateProductRequest DTO
 * 
 * Sử dụng MapStruct với Spring Component Model để tự động inject
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * Chuyển đổi Product entity sang ProductResponse DTO
     * Ánh xạ tất cả các trường từ Product sang ProductResponse
     * 
     * @param product Product entity cần chuyển đổi
     * @return ProductResponse DTO đã được ánh xạ
     */
    ProductResponse toResponse(Product product);

    /**
     * Chuyển đổi CreateProductRequest DTO sang Product entity
     * Tạo mới Product từ request DTO
     * 
     * @param request CreateProductRequest DTO chứa dữ liệu sản phẩm mới
     * @return Product entity đã được tạo từ request
     */
    Product toEntity(CreateProductRequest request);

    /**
     * Cập nhật Product entity từ CreateProductRequest DTO (partial update)
     * Chỉ cập nhật các trường được cung cấp trong request
     * 
     * @param request CreateProductRequest DTO chứa dữ liệu cần cập nhật
     * @param product Product entity cần cập nhật (target)
     */
    void updateEntity(CreateProductRequest request, @MappingTarget Product product);

    /**
     * Chuyển đổi danh sách Product entities sang danh sách ProductResponse DTOs
     * 
     * @param products Danh sách Product entities
     * @return Danh sách ProductResponse DTOs đã được ánh xạ
     */
    List<ProductResponse> toResponseList(List<Product> products);
}
