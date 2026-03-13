package com.example.ecommerce.util;

import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

/**
 * Lớp tiện ích để quản lý mã lỗi và thông điệp lỗi.
 * 
 * Định nghĩa các mã lỗi hệ thống và cung cấp các phương thức để lấy
 * thông điệp lỗi tương ứng và trạng thái HTTP.
 * 
 * Ví dụ sử dụng:
 * <pre>
 *     // Lấy thông điệp lỗi
 *     String message = ErrorCodeUtil.getErrorMessage(ErrorCodeUtil.NOT_FOUND);
 *     
 *     // Lấy trạng thái HTTP
 *     HttpStatus status = ErrorCodeUtil.getHttpStatus(ErrorCodeUtil.VALIDATION_FAILED);
 *     
 *     // Sử dụng mã lỗi trong Exception
 *     throw new BusinessException(ErrorCodeUtil.INSUFFICIENT_STOCK, 
 *         ErrorCodeUtil.getErrorMessage(ErrorCodeUtil.INSUFFICIENT_STOCK));
 * </pre>
 * 
 * @author E-Commerce Team
 * @version 1.0
 */
public class ErrorCodeUtil {
    
    // Định nghĩa các mã lỗi
    public static final String NOT_FOUND = "ERR_001";
    public static final String DUPLICATE_RESOURCE = "ERR_002";
    public static final String INSUFFICIENT_STOCK = "ERR_003";
    public static final String INVALID_ORDER = "ERR_004";
    public static final String PAYMENT_FAILED = "ERR_005";
    public static final String VALIDATION_FAILED = "ERR_006";
    public static final String INVALID_EMAIL = "ERR_007";
    public static final String INVALID_SLUG = "ERR_008";
    public static final String UNAUTHORIZED = "ERR_009";
    public static final String FORBIDDEN = "ERR_010";
    public static final String INTERNAL_ERROR = "ERR_999";
    
    // Map ánh xạ mã lỗi với thông điệp lỗi
    private static final Map<String, String> ERROR_MESSAGES = new HashMap<>();
    
    // Map ánh xạ mã lỗi với trạng thái HTTP
    private static final Map<String, HttpStatus> HTTP_STATUSES = new HashMap<>();
    
    static {
        // Khởi tạo thông điệp lỗi
        ERROR_MESSAGES.put(NOT_FOUND, "Tài nguyên không được tìm thấy");
        ERROR_MESSAGES.put(DUPLICATE_RESOURCE, "Tài nguyên đã tồn tại");
        ERROR_MESSAGES.put(INSUFFICIENT_STOCK, "Không đủ hàng trong kho");
        ERROR_MESSAGES.put(INVALID_ORDER, "Đơn hàng không hợp lệ");
        ERROR_MESSAGES.put(PAYMENT_FAILED, "Thanh toán thất bại");
        ERROR_MESSAGES.put(VALIDATION_FAILED, "Xác thực dữ liệu thất bại");
        ERROR_MESSAGES.put(INVALID_EMAIL, "Địa chỉ email không hợp lệ");
        ERROR_MESSAGES.put(INVALID_SLUG, "Slug không hợp lệ");
        ERROR_MESSAGES.put(UNAUTHORIZED, "Không được phép truy cập (chưa xác thực)");
        ERROR_MESSAGES.put(FORBIDDEN, "Không có quyền truy cập");
        ERROR_MESSAGES.put(INTERNAL_ERROR, "Lỗi máy chủ nội bộ");
        
        // Khởi tạo trạng thái HTTP
        HTTP_STATUSES.put(NOT_FOUND, HttpStatus.NOT_FOUND);
        HTTP_STATUSES.put(DUPLICATE_RESOURCE, HttpStatus.CONFLICT);
        HTTP_STATUSES.put(INSUFFICIENT_STOCK, HttpStatus.BAD_REQUEST);
        HTTP_STATUSES.put(INVALID_ORDER, HttpStatus.BAD_REQUEST);
        HTTP_STATUSES.put(PAYMENT_FAILED, HttpStatus.PAYMENT_REQUIRED);
        HTTP_STATUSES.put(VALIDATION_FAILED, HttpStatus.BAD_REQUEST);
        HTTP_STATUSES.put(INVALID_EMAIL, HttpStatus.BAD_REQUEST);
        HTTP_STATUSES.put(INVALID_SLUG, HttpStatus.BAD_REQUEST);
        HTTP_STATUSES.put(UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        HTTP_STATUSES.put(FORBIDDEN, HttpStatus.FORBIDDEN);
        HTTP_STATUSES.put(INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private ErrorCodeUtil() {
        throw new AssertionError("Không thể khởi tạo lớp tiện ích");
    }
    
    /**
     * Lấy thông điệp lỗi tương ứng với mã lỗi.
     * 
     * @param errorCode Mã lỗi
     * @return Thông điệp lỗi, hoặc "Lỗi không xác định" nếu mã lỗi không tồn tại
     */
    public static String getErrorMessage(String errorCode) {
        if (errorCode == null || errorCode.isBlank()) {
            return "Lỗi không xác định";
        }
        
        return ERROR_MESSAGES.getOrDefault(errorCode, "Lỗi không xác định: " + errorCode);
    }
    
    /**
     * Lấy trạng thái HTTP tương ứng với mã lỗi.
     * 
     * @param errorCode Mã lỗi
     * @return Trạng thái HTTP, hoặc INTERNAL_SERVER_ERROR nếu mã lỗi không tồn tại
     */
    public static HttpStatus getHttpStatus(String errorCode) {
        if (errorCode == null || errorCode.isBlank()) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        return HTTP_STATUSES.getOrDefault(errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Kiểm tra xem mã lỗi có tồn tại hay không.
     * 
     * @param errorCode Mã lỗi cần kiểm tra
     * @return true nếu mã lỗi tồn tại, false nếu không
     */
    public static boolean isValidErrorCode(String errorCode) {
        if (errorCode == null || errorCode.isBlank()) {
            return false;
        }
        
        return ERROR_MESSAGES.containsKey(errorCode);
    }
    
    /**
     * Đăng ký một mã lỗi tùy chỉnh với thông điệp và trạng thái HTTP.
     * 
     * Phương thức này cho phép thêm các mã lỗi tùy chỉnh vào hệ thống
     * (chỉ khuyến khích sử dụng trong các trường hợp đặc biệt).
     * 
     * @param errorCode Mã lỗi tùy chỉnh (ví dụ: "ERR_100")
     * @param message Thông điệp lỗi
     * @param status Trạng thái HTTP
     * @throws IllegalArgumentException nếu mã lỗi đã tồn tại
     */
    public static void registerCustomErrorCode(String errorCode, String message, HttpStatus status) {
        if (errorCode == null || errorCode.isBlank()) {
            throw new IllegalArgumentException("Mã lỗi không thể null hoặc trống");
        }
        
        if (ERROR_MESSAGES.containsKey(errorCode)) {
            throw new IllegalArgumentException("Mã lỗi đã tồn tại: " + errorCode);
        }
        
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Thông điệp lỗi không thể null hoặc trống");
        }
        
        if (status == null) {
            throw new IllegalArgumentException("Trạng thái HTTP không thể null");
        }
        
        ERROR_MESSAGES.put(errorCode, message);
        HTTP_STATUSES.put(errorCode, status);
    }
    
    /**
     * Lấy tất cả các mã lỗi được đăng ký.
     * 
     * @return Bản sao của bản đồ chứa tất cả mã lỗi
     */
    public static Map<String, String> getAllErrorMessages() {
        return new HashMap<>(ERROR_MESSAGES);
    }
    
    /**
     * Lấy danh sách tất cả các mã lỗi.
     * 
     * @return Tập hợp chứa tất cả mã lỗi
     */
    public static java.util.Set<String> getAllErrorCodes() {
        return new java.util.HashSet<>(ERROR_MESSAGES.keySet());
    }
}
