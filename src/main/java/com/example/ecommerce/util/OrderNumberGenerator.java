package com.example.ecommerce.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Lớp tiện ích để tạo số đơn hàng duy nhất.
 * 
 * Tạo số đơn hàng theo định dạng "ORD-YYYY-NNNNNN" trong đó:
 * - YYYY là năm hiện tại
 * - NNNNNN là số thứ tự 6 chữ số
 * 
 * Ví dụ: ORD-2025-000001, ORD-2025-000002, v.v.
 * 
 * Ví dụ sử dụng:
 * <pre>
 *     // Tạo số đơn hàng
 *     String orderNumber = OrderNumberGenerator.generateOrderNumber();
 *     // Kết quả: "ORD-2025-000001"
 *     
 *     // Kiểm tra định dạng
 *     boolean valid = OrderNumberGenerator.isValidFormat("ORD-2025-000001");
 *     
 *     // Phân tích số đơn hàng
 *     String year = OrderNumberGenerator.extractYear("ORD-2025-000001");
 *     long sequence = OrderNumberGenerator.extractSequence("ORD-2025-000001");
 * </pre>
 * 
 * Lưu ý: Không thread-safe theo mặc định, cần đồng bộ hóa nếu sử dụng trong
 * môi trường đa luồng.
 * 
 * @author E-Commerce Team
 * @version 1.0
 */
public class OrderNumberGenerator {
    
    private static final String ORDER_PREFIX = "ORD";
    private static final String SEPARATOR = "-";
    private static final int SEQUENCE_LENGTH = 6;
    private static final long MAX_SEQUENCE = 999999L; // 999,999
    
    // Bộ đếm tuần tự để tạo số đơn hàng duy nhất
    // Được đặt lại mỗi năm
    private static final AtomicLong sequenceCounter = new AtomicLong(0);
    
    // Lưu giữ năm hiện tại để phát hiện thay đổi năm
    private static volatile int lastYear = getCurrentYear();
    
    private OrderNumberGenerator() {
        throw new AssertionError("Không thể khởi tạo lớp tiện ích");
    }
    
    /**
     * Tạo số đơn hàng duy nhất với định dạng "ORD-YYYY-NNNNNN".
     * 
     * @return Số đơn hàng được tạo (ví dụ: "ORD-2025-000001")
     */
    public static String generateOrderNumber() {
        int currentYear = getCurrentYear();
        
        // Nếu năm thay đổi, đặt lại bộ đếm
        if (currentYear != lastYear) {
            synchronized (OrderNumberGenerator.class) {
                if (currentYear != lastYear) {
                    sequenceCounter.set(0);
                    lastYear = currentYear;
                }
            }
        }
        
        // Tăng bộ đếm và lấy giá trị
        long nextSequence = sequenceCounter.incrementAndGet();
        
        // Kiểm tra xem bộ đếm có vượt quá giới hạn hay không
        if (nextSequence > MAX_SEQUENCE) {
            throw new IllegalStateException(
                "Đạt giới hạn tối đa số đơn hàng cho năm " + currentYear + ": " + MAX_SEQUENCE
            );
        }
        
        // Định dạng số thứ tự thành chuỗi 6 chữ số
        String formattedSequence = String.format("%0" + SEQUENCE_LENGTH + "d", nextSequence);
        
        return ORDER_PREFIX + SEPARATOR + currentYear + SEPARATOR + formattedSequence;
    }
    
    /**
     * Tạo số đơn hàng với tiền tố tùy chỉnh.
     * 
     * @param customPrefix Tiền tố tùy chỉnh (ví dụ: "PRE" hoặc "TEST")
     * @return Số đơn hàng được tạo với tiền tố tùy chỉnh
     */
    public static String generateOrderNumberWithPrefix(String customPrefix) {
        if (customPrefix == null || customPrefix.isBlank()) {
            throw new IllegalArgumentException("Tiền tố không thể null hoặc trống");
        }
        
        int currentYear = getCurrentYear();
        long nextSequence = sequenceCounter.incrementAndGet();
        
        if (nextSequence > MAX_SEQUENCE) {
            throw new IllegalStateException("Đạt giới hạn tối đa số đơn hàng cho năm " + currentYear);
        }
        
        String formattedSequence = String.format("%0" + SEQUENCE_LENGTH + "d", nextSequence);
        
        return customPrefix + SEPARATOR + currentYear + SEPARATOR + formattedSequence;
    }
    
    /**
     * Lấy năm hiện tại.
     * 
     * @return Năm hiện tại (ví dụ: 2025)
     */
    private static int getCurrentYear() {
        return LocalDate.now().getYear();
    }
    
    /**
     * Kiểm tra xem số đơn hàng có định dạng hợp lệ hay không.
     * 
     * Định dạng hợp lệ: "ORD-YYYY-NNNNNN" trong đó:
     * - ORD là tiền tố cố định
     * - YYYY là năm (4 chữ số)
     * - NNNNNN là số thứ tự (6 chữ số)
     * 
     * @param orderNumber Số đơn hàng cần kiểm tra
     * @return true nếu định dạng hợp lệ, false nếu không
     */
    public static boolean isValidFormat(String orderNumber) {
        if (orderNumber == null || orderNumber.isBlank()) {
            return false;
        }
        
        String[] parts = orderNumber.split("-");
        
        // Kiểm tra số lượng phần
        if (parts.length != 3) {
            return false;
        }
        
        // Kiểm tra tiền tố
        if (!parts[0].equals(ORDER_PREFIX)) {
            return false;
        }
        
        // Kiểm tra năm (4 chữ số)
        if (parts[1].length() != 4 || !parts[1].matches("\\d{4}")) {
            return false;
        }
        
        // Kiểm tra số thứ tự (6 chữ số)
        if (parts[2].length() != SEQUENCE_LENGTH || !parts[2].matches("\\d{" + SEQUENCE_LENGTH + "}")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Kiểm tra xem số đơn hàng có hợp lệ hay không (kiểm tra định dạng và giá trị năm).
     * 
     * @param orderNumber Số đơn hàng cần kiểm tra
     * @return true nếu số đơn hàng hợp lệ, false nếu không
     */
    public static boolean isValid(String orderNumber) {
        if (!isValidFormat(orderNumber)) {
            return false;
        }
        
        try {
            String[] parts = orderNumber.split("-");
            int year = Integer.parseInt(parts[1]);
            
            // Kiểm tra năm có nằm trong phạm vi hợp lệ hay không (năm 1900 đến năm hiện tại + 10)
            int currentYear = getCurrentYear();
            return year >= 1900 && year <= currentYear + 10;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Trích xuất năm từ số đơn hàng.
     * 
     * @param orderNumber Số đơn hàng
     * @return Năm (ví dụ: 2025), hoặc -1 nếu định dạng không hợp lệ
     */
    public static int extractYear(String orderNumber) {
        if (!isValidFormat(orderNumber)) {
            return -1;
        }
        
        try {
            String[] parts = orderNumber.split("-");
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Trích xuất số thứ tự từ số đơn hàng.
     * 
     * @param orderNumber Số đơn hàng
     * @return Số thứ tự (ví dụ: 1), hoặc -1 nếu định dạng không hợp lệ
     */
    public static long extractSequence(String orderNumber) {
        if (!isValidFormat(orderNumber)) {
            return -1;
        }
        
        try {
            String[] parts = orderNumber.split("-");
            return Long.parseLong(parts[2]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Đặt lại bộ đếm (chỉ khuyến khích sử dụng trong các bài kiểm tra hoặc giai đoạn phát triển).
     * 
     * Cảnh báo: Phương thức này không an toàn trong môi trường sản xuất!
     */
    public static void resetCounter() {
        synchronized (OrderNumberGenerator.class) {
            sequenceCounter.set(0);
            lastYear = getCurrentYear();
        }
    }
    
    /**
     * Lấy giá trị bộ đếm hiện tại.
     * 
     * @return Giá trị bộ đếm hiện tại
     */
    public static long getCurrentSequence() {
        return sequenceCounter.get();
    }
    
    /**
     * Lấy định dạng mặc định của số đơn hàng.
     * 
     * @return Chuỗi mô tả định dạng (ví dụ: "ORD-YYYY-NNNNNN")
     */
    public static String getFormatPattern() {
        return ORDER_PREFIX + SEPARATOR + "YYYY" + SEPARATOR + String.format("%0" + SEQUENCE_LENGTH + "s", "N").replace(" ", "N");
    }
}
