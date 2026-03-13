package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.response.CartResponse;
import com.example.ecommerce.model.Cart;
import org.mapstruct.Mapper;

/**
 * Mapper interface cho Cart entity
 * Chuyển đổi giữa Cart entity và CartResponse DTO
 * 
 * Sử dụng MapStruct với Spring Component Model để tự động inject
 */
@Mapper(componentModel = "spring")
public interface CartMapper {

    /**
     * Chuyển đổi Cart entity sang CartResponse DTO
     * Ánh xạ tất cả các trường từ Cart sang CartResponse
     * Bao gồm danh sách các item trong giỏ hàng
     * 
     * @param cart Cart entity cần chuyển đổi
     * @return CartResponse DTO đã được ánh xạ
     */
    CartResponse toResponse(Cart cart);

    /**
     * Tạo Cart entity từ userId
     * Tạo Cart rỗng cho người dùng mới
     * 
     * @param userId ID của người dùng
     * @return Cart entity với userId được set
     */
    default Cart toEntity(String userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setTotalQuantity(0);
        cart.setTotalPrice(java.math.BigDecimal.ZERO);
        return cart;
    }
}
