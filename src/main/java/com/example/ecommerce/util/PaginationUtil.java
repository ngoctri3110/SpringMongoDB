package com.example.ecommerce.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Lớp tiện ích cho phân trang.
 * 
 * Cung cấp các phương thức tĩnh để hỗ trợ phân trang, xác thực tham số trang,
 * và tạo đối tượng Pageable với sắp xếp.
 * 
 * Ví dụ sử dụng:
 * <pre>
 *     // Xác thực tham số trang
 *     PaginationUtil.validatePageParams(0, 20);
 *     
 *     // Tạo Pageable mặc định
 *     Pageable pageable = PaginationUtil.createPageable(0, 10, "createdAt", Sort.Direction.DESC);
 *     
 *     // Lấy kích thước trang mặc định
 *     int defaultSize = PaginationUtil.getDefaultPageSize();
 * </pre>
 * 
 * @author E-Commerce Team
 * @version 1.0
 */
public class PaginationUtil {
    
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;
    
    private PaginationUtil() {
        throw new AssertionError("Không thể khởi tạo lớp tiện ích");
    }
    
    /**
     * Xác thực các tham số phân trang.
     * 
     * @param page Số trang (bắt đầu từ 0)
     * @param size Kích thước trang
     * @throws IllegalArgumentException nếu page < 0 hoặc size < 1
     */
    public static void validatePageParams(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Số trang không thể âm: " + page);
        }
        if (size < 1) {
            throw new IllegalArgumentException("Kích thước trang phải >= 1: " + size);
        }
    }
    
    /**
     * Tạo đối tượng Pageable với sắp xếp theo một trường.
     * 
     * @param page Số trang (bắt đầu từ 0)
     * @param size Kích thước trang
     * @param sortField Tên trường để sắp xếp
     * @param direction Hướng sắp xếp (ASC hoặc DESC)
     * @return Đối tượng Pageable được cấu hình
     * @throws IllegalArgumentException nếu tham số không hợp lệ
     */
    public static Pageable createPageable(int page, int size, String sortField, Sort.Direction direction) {
        validatePageParams(page, size);
        if (sortField == null || sortField.isBlank()) {
            throw new IllegalArgumentException("Trường sắp xếp không thể trống");
        }
        if (direction == null) {
            throw new IllegalArgumentException("Hướng sắp xếp không thể null");
        }
        
        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }
    
    /**
     * Tạo đối tượng Pageable với sắp xếp theo nhiều trường.
     * 
     * @param page Số trang (bắt đầu từ 0)
     * @param size Kích thước trang
     * @param sort Đối tượng Sort chứa các trường sắp xếp
     * @return Đối tượng Pageable được cấu hình
     * @throws IllegalArgumentException nếu tham số không hợp lệ
     */
    public static Pageable createPageable(int page, int size, Sort sort) {
        validatePageParams(page, size);
        if (sort == null) {
            throw new IllegalArgumentException("Đối tượng Sort không thể null");
        }
        
        return PageRequest.of(page, size, sort);
    }
    
    /**
     * Tạo đối tượng Pageable mà không có sắp xếp.
     * 
     * @param page Số trang (bắt đầu từ 0)
     * @param size Kích thước trang
     * @return Đối tượng Pageable được cấu hình
     * @throws IllegalArgumentException nếu tham số không hợp lệ
     */
    public static Pageable createPageable(int page, int size) {
        validatePageParams(page, size);
        return PageRequest.of(page, size);
    }
    
    /**
     * Chuẩn hóa kích thước trang để không vượt quá giá trị tối đa.
     * 
     * @param size Kích thước trang yêu cầu
     * @return Kích thước trang đã chuẩn hóa (tối thiểu 1, tối đa MAX_PAGE_SIZE)
     */
    public static int normalizePageSize(int size) {
        if (size < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }
    
    /**
     * Chuẩn hóa số trang.
     * 
     * @param page Số trang yêu cầu
     * @return Số trang đã chuẩn hóa (tối thiểu 0)
     */
    public static int normalizePage(int page) {
        return Math.max(0, page);
    }
    
    /**
     * Lấy kích thước trang mặc định.
     * 
     * @return Kích thước trang mặc định
     */
    public static int getDefaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }
    
    /**
     * Lấy kích thước trang tối đa.
     * 
     * @return Kích thước trang tối đa được phép
     */
    public static int getMaxPageSize() {
        return MAX_PAGE_SIZE;
    }
    
    /**
     * Tính toán chỉ số bắt đầu của bản ghi trong cơ sở dữ liệu.
     * 
     * @param page Số trang (bắt đầu từ 0)
     * @param size Kích thước trang
     * @return Chỉ số bắt đầu
     */
    public static int getOffset(int page, int size) {
        validatePageParams(page, size);
        return page * size;
    }
}
