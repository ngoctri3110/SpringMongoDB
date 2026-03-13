package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.request.CreateOrderRequest;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.model.Order;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper interface cho Order entity
 * Chuyển đổi giữa Order entity, OrderResponse DTO và CreateOrderRequest DTO
 * 
 * Sử dụng MapStruct với Spring Component Model để tự động inject
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    /**
     * Chuyển đổi Order entity sang OrderResponse DTO
     * Ánh xạ tất cả các trường từ Order sang OrderResponse
     * Bao gồm các embedded documents như items, shippingAddress, paymentInfo
     * 
     * @param order Order entity cần chuyển đổi
     * @return OrderResponse DTO đã được ánh xạ
     */
    OrderResponse toResponse(Order order);

    /**
     * Chuyển đổi CreateOrderRequest DTO sang Order entity
     * Tạo mới Order từ request DTO
     * 
     * @param request CreateOrderRequest DTO chứa dữ liệu đơn hàng mới
     * @return Order entity đã được tạo từ request
     */
    Order toEntity(CreateOrderRequest request);

    /**
     * Chuyển đổi danh sách Order entities sang danh sách OrderResponse DTOs
     * 
     * @param orders Danh sách Order entities
     * @return Danh sách OrderResponse DTOs đã được ánh xạ
     */
    List<OrderResponse> toResponseList(List<Order> orders);
}
