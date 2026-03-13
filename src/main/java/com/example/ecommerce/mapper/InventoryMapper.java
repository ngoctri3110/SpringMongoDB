package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.request.CreateInventoryRequest;
import com.example.ecommerce.dto.response.InventoryResponse;
import com.example.ecommerce.model.Inventory;
import org.mapstruct.Mapper;

/**
 * Mapper interface cho Inventory entity
 * Chuyển đổi giữa Inventory entity, InventoryResponse DTO và CreateInventoryRequest DTO
 * 
 * Sử dụng MapStruct với Spring Component Model để tự động inject
 */
@Mapper(componentModel = "spring")
public interface InventoryMapper {

    /**
     * Chuyển đổi Inventory entity sang InventoryResponse DTO
     * Ánh xạ tất cả các trường từ Inventory sang InventoryResponse
     * 
     * @param inventory Inventory entity cần chuyển đổi
     * @return InventoryResponse DTO đã được ánh xạ
     */
    InventoryResponse toResponse(Inventory inventory);

    /**
     * Chuyển đổi CreateInventoryRequest DTO sang Inventory entity
     * Tạo mới Inventory từ request DTO
     * 
     * @param request CreateInventoryRequest DTO chứa dữ liệu inventory mới
     * @return Inventory entity đã được tạo từ request
     */
    Inventory toEntity(CreateInventoryRequest request);
}
