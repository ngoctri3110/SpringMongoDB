package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.AddCartItemRequest;
import com.example.ecommerce.dto.response.CartResponse;
import com.example.ecommerce.mapper.CartMapper;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Cart Controller - REST endpoints cho Cart
 */
@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;

    /**
     * GET /api/v1/carts/{userId} - Lấy giỏ hàng của user
     * 
     * @param userId User ID
     * @return ResponseEntity<CartResponse> với status 200 OK
     */
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable String userId) {
        log.info("GET /api/v1/carts/{} - Fetching cart", userId);
        Cart cart = cartService.getCartByUserId(userId);
        CartResponse response = cartMapper.toResponse(cart);
        return ResponseEntity.ok(response);
    }

    /**
      * POST /api/v1/carts/{userId}/items - Thêm item vào giỏ hàng
      * 
      * @param userId User ID
      * @param request AddCartItemRequest
      * @return ResponseEntity<CartResponse> với status 201 Created
      */
    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> addItemToCart(
            @PathVariable String userId,
            @Valid @RequestBody AddCartItemRequest request) {
        log.info("POST /api/v1/carts/{}/items - Adding item to cart", userId);
        Cart cart = cartService.addToCart(userId, request.getProductId(), request.getQuantity());
        CartResponse response = cartMapper.toResponse(cart);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
      * PUT /api/v1/carts/{userId}/items/{productId} - Cập nhật item trong giỏ hàng
      * 
      * @param userId User ID
      * @param productId Product ID
      * @param request AddCartItemRequest (chứa quantity)
      * @return ResponseEntity<CartResponse> với status 200 OK
      */
    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable String userId,
            @PathVariable String productId,
            @Valid @RequestBody AddCartItemRequest request) {
        log.info("PUT /api/v1/carts/{}/items/{} - Updating cart item", userId, productId);
        Cart cart = cartService.updateItemQuantity(userId, productId, request.getQuantity());
        CartResponse response = cartMapper.toResponse(cart);
        return ResponseEntity.ok(response);
    }

    /**
      * DELETE /api/v1/carts/{userId}/items/{productId} - Xóa item khỏi giỏ hàng
      * 
      * @param userId User ID
      * @param productId Product ID
      * @return ResponseEntity<CartResponse> với status 200 OK
      */
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> removeCartItem(
            @PathVariable String userId,
            @PathVariable String productId) {
        log.info("DELETE /api/v1/carts/{}/items/{} - Removing item from cart", userId, productId);
        Cart cart = cartService.removeFromCart(userId, productId);
        CartResponse response = cartMapper.toResponse(cart);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/carts/{userId} - Xóa toàn bộ giỏ hàng
     * 
     * @param userId User ID
     * @return ResponseEntity với status 204 No Content
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable String userId) {
        log.info("DELETE /api/v1/carts/{} - Clearing cart", userId);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
