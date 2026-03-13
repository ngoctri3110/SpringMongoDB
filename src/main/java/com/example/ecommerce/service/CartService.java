package com.example.ecommerce.service;

import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing shopping carts.
 * Handles cart retrieval, adding/removing items, updating quantities, and calculating totals.
 * Validates product existence and stock availability.
 *
 * @author E-Commerce Platform
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    /**
     * Retrieves the shopping cart for a user.
     * Creates a new empty cart if one doesn't exist.
     *
     * @param userId the user ID
     * @return the Cart object for the user
     */
    @Transactional
    public Cart getCartByUserId(String userId) {
        log.debug("Fetching cart for user: {}", userId);

        Optional<Cart> existingCart = cartRepository.findByUserId(userId);
        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        // Create new empty cart if doesn't exist
        Cart newCart = Cart.builder()
                .userId(userId)
                .items(new ArrayList<>())
                .totalQuantity(0)
                .totalPrice(BigDecimal.ZERO)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();

        Cart savedCart = cartRepository.save(newCart);
        log.debug("New cart created for user: {}", userId);

        return savedCart;
    }

    /**
     * Adds an item to the user's cart.
     * If item already exists, updates the quantity instead.
     * Validates product exists and has sufficient stock.
     *
     * @param userId the user ID
     * @param productId the product ID to add
     * @param quantity the quantity to add
     * @return the updated Cart object
     * @throws ResourceNotFoundException if product doesn't exist
     * @throws InsufficientStockException if stock is insufficient
     */
    @Transactional
    public Cart addToCart(String userId, String productId, Integer quantity) {
        log.info("Adding item to cart - userId: {}, productId: {}, quantity: {}", userId, productId, quantity);

        // Validate quantity
        if (quantity == null || quantity <= 0) {
            log.warn("Invalid quantity: {}", quantity);
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        // Verify product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product", "id", productId));

        // Validate product has sufficient stock
        if (product.getStock() < quantity) {
            log.warn("Insufficient stock for product: {} - requested: {}, available: {}",
                    productId, quantity, product.getStock());
            throw new InsufficientStockException(
                    "Insufficient stock for product: " + product.getName());
        }

        // Get or create cart
        Cart cart = getCartByUserId(userId);

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update existing item quantity
            CartItem item = existingItem.get();
            Integer newQuantity = item.getQuantity() + quantity;

            if (product.getStock() < newQuantity) {
                log.warn("Insufficient stock when updating quantity - requested: {}, available: {}",
                        newQuantity, product.getStock());
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getName());
            }

            item.setQuantity(newQuantity);
            item.setTotalPrice(product.getPrice().multiply(new BigDecimal(newQuantity)));
            log.debug("Updated quantity for product {} in cart to: {}", productId, newQuantity);
        } else {
            // Add new item to cart
            CartItem newItem = new CartItem(
                    productId,
                    product.getName(),
                    product.getPrice(),
                    quantity
            );
            cart.getItems().add(newItem);
            log.debug("Added new item to cart: {} with quantity: {}", productId, quantity);
        }

        // Recalculate totals
        cart.recalculateTotals();
        Cart updatedCart = cartRepository.save(cart);

        log.info("Item added to cart successfully for user: {}", userId);
        return updatedCart;
    }

    /**
     * Removes an item from the user's cart.
     *
     * @param userId the user ID
     * @param productId the product ID to remove
     * @return the updated Cart object
     * @throws ResourceNotFoundException if cart doesn't exist
     */
    @Transactional
    public Cart removeFromCart(String userId, String productId) {
        log.info("Removing item from cart - userId: {}, productId: {}", userId, productId);

        Cart cart = getCartByUserId(userId);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            log.warn("Cart is empty for user: {}", userId);
            return cart;
        }

        // Remove item from cart
        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        if (removed) {
            log.debug("Item removed from cart: {}", productId);
            cart.recalculateTotals();
        } else {
            log.warn("Product not found in cart: {}", productId);
        }

        Cart updatedCart = cartRepository.save(cart);
        log.info("Item removed from cart successfully for user: {}", userId);

        return updatedCart;
    }

    /**
     * Clears all items from the user's cart.
     *
     * @param userId the user ID
     * @return the cleared Cart object
     */
    @Transactional
    public Cart clearCart(String userId) {
        log.info("Clearing cart for user: {}", userId);

        Cart cart = getCartByUserId(userId);
        cart.setItems(new ArrayList<>());
        cart.setTotalQuantity(0);
        cart.setTotalPrice(BigDecimal.ZERO);

        Cart clearedCart = cartRepository.save(cart);
        log.info("Cart cleared successfully for user: {}", userId);

        return clearedCart;
    }

    /**
     * Updates the quantity of an item in the cart.
     * Can increase or decrease the quantity.
     *
     * @param userId the user ID
     * @param productId the product ID
     * @param newQuantity the new quantity (must be > 0, or item will be removed)
     * @return the updated Cart object
     * @throws ResourceNotFoundException if cart or item doesn't exist
     * @throws InsufficientStockException if stock is insufficient for new quantity
     */
    @Transactional
    public Cart updateItemQuantity(String userId, String productId, Integer newQuantity) {
        log.info("Updating item quantity - userId: {}, productId: {}, newQuantity: {}", 
                userId, productId, newQuantity);

        // Validate quantity
        if (newQuantity == null || newQuantity < 0) {
            log.warn("Invalid quantity: {}", newQuantity);
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        Cart cart = getCartByUserId(userId);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            log.warn("Cart is empty for user: {}", userId);
            throw new ResourceNotFoundException("Cart item not found for product: " + productId);
        }

        // Find the item
        Optional<CartItem> itemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (itemOpt.isEmpty()) {
            log.warn("Product not found in cart: {}", productId);
            throw new ResourceNotFoundException("Cart item not found for product: " + productId);
        }

        CartItem item = itemOpt.get();

        // If quantity is 0, remove the item
        if (newQuantity == 0) {
            cart.getItems().remove(item);
            log.debug("Item removed from cart due to quantity 0: {}", productId);
        } else {
            // Validate stock availability
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product", "id", productId));

            if (product.getStock() < newQuantity) {
                log.warn("Insufficient stock for product: {} - requested: {}, available: {}",
                        productId, newQuantity, product.getStock());
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getName());
            }

            // Update quantity and total price
            item.setQuantity(newQuantity);
            item.setTotalPrice(item.getPrice().multiply(new BigDecimal(newQuantity)));
            log.debug("Item quantity updated to: {}", newQuantity);
        }

        // Recalculate totals
        cart.recalculateTotals();
        Cart updatedCart = cartRepository.save(cart);

        log.info("Item quantity updated successfully for user: {}", userId);
        return updatedCart;
    }

    /**
     * Retrieves the total number of items in the user's cart.
     *
     * @param userId the user ID
     * @return total quantity of items in cart
     */
    @Transactional(readOnly = true)
    public Integer getCartItemCount(String userId) {
        Optional<Cart> cart = cartRepository.findByUserId(userId);
        return cart.map(Cart::getTotalQuantity).orElse(0);
    }

    /**
     * Retrieves the total price of all items in the user's cart.
     *
     * @param userId the user ID
     * @return total price as BigDecimal
     */
    @Transactional(readOnly = true)
    public BigDecimal getCartTotalPrice(String userId) {
        Optional<Cart> cart = cartRepository.findByUserId(userId);
        return cart.map(Cart::getTotalPrice).orElse(BigDecimal.ZERO);
    }

    /**
     * Validates that all items in the cart have sufficient stock.
     *
     * @param userId the user ID
     * @return true if all items have sufficient stock, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean validateCartInventory(String userId) {
        log.debug("Validating cart inventory for user: {}", userId);

        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        if (cartOpt.isEmpty()) {
            return true; // Empty cart is valid
        }

        Cart cart = cartOpt.get();
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return true; // Empty cart is valid
        }

        // Check each item
        for (CartItem item : cart.getItems()) {
            Optional<Product> productOpt = productRepository.findById(item.getProductId());
            if (productOpt.isEmpty() || productOpt.get().getStock() < item.getQuantity()) {
                log.warn("Inventory validation failed for product: {}", item.getProductId());
                return false;
            }
        }

        return true;
    }

    /**
     * Synchronizes cart with current product prices.
     * Updates cart item prices to match current product prices.
     *
     * @param userId the user ID
     * @return the synchronized Cart object
     */
    @Transactional
    public Cart synchronizeCartPrices(String userId) {
        log.info("Synchronizing cart prices for user: {}", userId);

        Cart cart = getCartByUserId(userId);

        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            for (CartItem item : cart.getItems()) {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Product", "id", item.getProductId()));

                // Update price if changed
                if (!item.getPrice().equals(product.getPrice())) {
                    BigDecimal oldPrice = item.getPrice();
                    item.setPrice(product.getPrice());
                    item.setTotalPrice(product.getPrice().multiply(new BigDecimal(item.getQuantity())));
                    log.debug("Cart item price updated - productId: {}, oldPrice: {}, newPrice: {}",
                            item.getProductId(), oldPrice, product.getPrice());
                }
            }

            // Recalculate totals
            cart.recalculateTotals();
        }

        Cart updatedCart = cartRepository.save(cart);
        log.info("Cart prices synchronized for user: {}", userId);

        return updatedCart;
    }
}
