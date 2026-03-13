package com.example.ecommerce.util;

import java.util.regex.Pattern;

/**
 * Lớp tiện ích để xác thực dữ liệu.
 * 
 * Cung cấp các phương thức tĩnh để xác thực email, slug, trạng thái đơn hàng,
 * phương thức thanh toán, xếp hạng, lượng hàng, và các chuỗi ký tự.
 * 
 * Ví dụ sử dụng:
 * <pre>
 *     // Xác thực email
 *     boolean validEmail = ValidationUtil.isValidEmail("user@example.com");
 *     
 *     // Xác thực slug
 *     boolean validSlug = ValidationUtil.isValidSlug("product-name-123");
 *     
 *     // Xác thực xếp hạng
 *     boolean validRating = ValidationUtil.isValidRating(4);
 *     
 *     // Kiểm tra lượng hàng
 *     boolean sufficient = ValidationUtil.isStockSufficient(5, 10);
 * </pre>
 * 
 * @author E-Commerce Team
 * @version 1.0
 */
public class ValidationUtil {
    
    // Regex pattern để xác thực email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Regex pattern để xác thực slug (chỉ chứa chữ, số, gạch ngang, dấu gạch dưới)
    private static final Pattern SLUG_PATTERN = Pattern.compile(
        "^[a-z0-9]+(?:[_-][a-z0-9]+)*$"
    );
    
    // Enum cho trạng thái đơn hàng
    private enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED, RETURNED
    }
    
    // Enum cho phương thức thanh toán
    private enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, WALLET, COD
    }
    
    private ValidationUtil() {
        throw new AssertionError("Không thể khởi tạo lớp tiện ích");
    }
    
    /**
     * Kiểm tra xem chuỗi có trống hay không (null hoặc rỗng).
     * 
     * @param str Chuỗi cần kiểm tra
     * @return true nếu chuỗi là null hoặc rỗng, false nếu không
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
    
    /**
     * Kiểm tra xem chuỗi có trống hoặc chỉ chứa khoảng trắng hay không.
     * 
     * @param str Chuỗi cần kiểm tra
     * @return true nếu chuỗi là null hoặc chỉ chứa khoảng trắng, false nếu không
     */
    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
    
    /**
     * Xác thực địa chỉ email.
     * 
     * @param email Địa chỉ email cần xác thực
     * @return true nếu email hợp lệ, false nếu không
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        
        // Kiểm tra độ dài
        if (email.length() > 254) {
            return false;
        }
        
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Xác thực slug (định danh thân thiện cho URL).
     * 
     * Slug chỉ có thể chứa:
     * - Chữ cái thường (a-z)
     * - Số (0-9)
     * - Gạch ngang (-) và dấu gạch dưới (_)
     * - Không được bắt đầu hoặc kết thúc bằng gạch ngang hoặc dấu gạch dưới
     * 
     * @param slug Slug cần xác thực
     * @return true nếu slug hợp lệ, false nếu không
     */
    public static boolean isValidSlug(String slug) {
        if (isEmpty(slug)) {
            return false;
        }
        
        // Kiểm tra độ dài (tối thiểu 1, tối đa 255)
        if (slug.length() > 255) {
            return false;
        }
        
        // Kiểm tra định dạng
        if (!SLUG_PATTERN.matcher(slug).matches()) {
            return false;
        }
        
        // Slug không được bắt đầu hoặc kết thúc bằng gạch ngang/dấu gạch dưới
        if (slug.startsWith("-") || slug.startsWith("_") || 
            slug.endsWith("-") || slug.endsWith("_")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Xác thực trạng thái đơn hàng.
     * 
     * Trạng thái hợp lệ: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED, RETURNED
     * 
     * @param status Trạng thái đơn hàng cần xác thực
     * @return true nếu trạng thái hợp lệ, false nếu không
     */
    public static boolean isValidOrderStatus(String status) {
        if (isEmpty(status)) {
            return false;
        }
        
        try {
            OrderStatus.valueOf(status.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Xác thực phương thức thanh toán.
     * 
     * Phương thức thanh toán hợp lệ: CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, WALLET, COD
     * 
     * @param method Phương thức thanh toán cần xác thực
     * @return true nếu phương thức hợp lệ, false nếu không
     */
    public static boolean isValidPaymentMethod(String method) {
        if (isEmpty(method)) {
            return false;
        }
        
        try {
            PaymentMethod.valueOf(method.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Xác thực xếp hạng sản phẩm.
     * 
     * Xếp hạng hợp lệ: 1, 2, 3, 4, 5 sao
     * 
     * @param rating Xếp hạng cần xác thực
     * @return true nếu xếp hạng nằm trong khoảng 1-5, false nếu không
     */
    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }
    
    /**
     * Kiểm tra xem lượng hàng có đủ để đáp ứng yêu cầu hay không.
     * 
     * @param requested Số lượng yêu cầu
     * @param available Số lượng có sẵn
     * @return true nếu số lượng có sẵn >= số lượng yêu cầu, false nếu không
     */
    public static boolean isStockSufficient(int requested, int available) {
        return requested > 0 && available >= requested;
    }
    
    /**
     * Xác thực xem số lượng có hợp lệ hay không.
     * 
     * @param quantity Số lượng cần xác thực
     * @return true nếu số lượng > 0, false nếu không
     */
    public static boolean isValidQuantity(int quantity) {
        return quantity > 0;
    }
    
    /**
     * Xác thực xem ID có hợp lệ hay không (không trống và không null).
     * 
     * @param id ID cần xác thực
     * @return true nếu ID hợp lệ, false nếu không
     */
    public static boolean isValidId(String id) {
        return !isEmpty(id) && id.trim().length() > 0;
    }
    
    /**
     * Xác thực độ dài chuỗi nằm trong phạm vi cho phép.
     * 
     * @param str Chuỗi cần xác thực
     * @param minLength Độ dài tối thiểu
     * @param maxLength Độ dài tối đa
     * @return true nếu độ dài chuỗi nằm trong phạm vi, false nếu không
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (isEmpty(str)) {
            return minLength == 0;
        }
        
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Xác thực xem chuỗi chỉ chứa chữ cái và số.
     * 
     * @param str Chuỗi cần xác thực
     * @return true nếu chuỗi chỉ chứa chữ cái và số, false nếu không
     */
    public static boolean isAlphanumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        
        return str.matches("^[a-zA-Z0-9]+$");
    }
}
