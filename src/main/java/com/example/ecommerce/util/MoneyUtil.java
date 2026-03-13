package com.example.ecommerce.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Lớp tiện ích cho các phép toán tiền tệ.
 * 
 * Cung cấp các phương thức tĩnh để định dạng giá, tính toán thuế,
 * áp dụng giảm giá, làm tròn tiền, và xác thực giá.
 * 
 * Tất cả các phép tính sử dụng BigDecimal để đảm bảo độ chính xác
 * và sử dụng RoundingMode.HALF_UP cho làm tròn tiền tệ.
 * 
 * Ví dụ sử dụng:
 * <pre>
 *     // Định dạng giá
 *     String formatted = MoneyUtil.formatPrice(new BigDecimal("1234.567"));
 *     // Kết quả: "1.234,57 ₫"
 *     
 *     // Tính toán thuế (10%)
 *     BigDecimal tax = MoneyUtil.calculateTax(new BigDecimal("100"), 0.10);
 *     // Kết quả: 10.00
 *     
 *     // Áp dụng giảm giá (20%)
 *     BigDecimal final = MoneyUtil.calculateDiscount(new BigDecimal("100"), 20);
 *     // Kết quả: 80.00
 *     
 *     // Xác thực giá
 *     boolean valid = MoneyUtil.isValidPrice(new BigDecimal("99.99"));
 * </pre>
 * 
 * @author E-Commerce Team
 * @version 1.0
 */
public class MoneyUtil {
    
    // Số chữ số thập phân cho tiền tệ
    private static final int SCALE = 2;
    
    // Chế độ làm tròn cho tiền tệ
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    
    // Định dạng số theo Locale Việt Nam
    private static final NumberFormat VN_FORMAT = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    
    // Giá trị tối thiểu (0.01)
    private static final BigDecimal MIN_PRICE = new BigDecimal("0.01");
    
    private MoneyUtil() {
        throw new AssertionError("Không thể khởi tạo lớp tiện ích");
    }
    
    /**
     * Định dạng giá thành chuỗi với đơn vị tiền tệ.
     * 
     * @param price Giá cần định dạng
     * @return Giá được định dạng theo Locale Việt Nam (ví dụ: "1.234,57 ₫")
     * @throws IllegalArgumentException nếu price là null
     */
    public static String formatPrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Giá không thể null");
        }
        
        // Chuyển đổi sang scale 2
        price = price.setScale(SCALE, ROUNDING_MODE);
        
        return VN_FORMAT.format(price);
    }
    
    /**
     * Định dạng giá thành chuỗi số mà không có đơn vị tiền tệ.
     * 
     * @param price Giá cần định dạng
     * @return Chuỗi số được định dạng (ví dụ: "1.234,57")
     * @throws IllegalArgumentException nếu price là null
     */
    public static String formatPriceValue(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Giá không thể null");
        }
        
        price = price.setScale(SCALE, ROUNDING_MODE);
        
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        nf.setMinimumFractionDigits(SCALE);
        nf.setMaximumFractionDigits(SCALE);
        
        return nf.format(price);
    }
    
    /**
     * Tính toán thuế dựa trên số tiền và tỷ lệ thuế.
     * 
     * @param amount Số tiền cơ sở để tính thuế
     * @param taxRate Tỷ lệ thuế (ví dụ: 0.10 cho 10%)
     * @return Số tiền thuế được tính toán
     * @throws IllegalArgumentException nếu amount là null hoặc taxRate không hợp lệ
     */
    public static BigDecimal calculateTax(BigDecimal amount, double taxRate) {
        if (amount == null) {
            throw new IllegalArgumentException("Số tiền không thể null");
        }
        
        if (taxRate < 0 || taxRate > 1) {
            throw new IllegalArgumentException("Tỷ lệ thuế phải nằm trong khoảng 0.0 đến 1.0: " + taxRate);
        }
        
        BigDecimal tax = amount.multiply(new BigDecimal(taxRate));
        return tax.setScale(SCALE, ROUNDING_MODE);
    }
    
    /**
     * Tính toán giá sau thuế (bao gồm cả giá gốc).
     * 
     * @param amount Giá gốc
     * @param taxRate Tỷ lệ thuế (ví dụ: 0.10 cho 10%)
     * @return Giá sau thuế (giá gốc + thuế)
     * @throws IllegalArgumentException nếu tham số không hợp lệ
     */
    public static BigDecimal getPriceWithTax(BigDecimal amount, double taxRate) {
        if (amount == null) {
            throw new IllegalArgumentException("Số tiền không thể null");
        }
        
        BigDecimal tax = calculateTax(amount, taxRate);
        return amount.add(tax).setScale(SCALE, ROUNDING_MODE);
    }
    
    /**
     * Áp dụng giảm giá cho một số tiền.
     * 
     * @param amount Số tiền gốc
     * @param discountPercent Phần trăm giảm giá (ví dụ: 20 cho 20%)
     * @return Số tiền sau khi giảm giá
     * @throws IllegalArgumentException nếu tham số không hợp lệ
     */
    public static BigDecimal calculateDiscount(BigDecimal amount, double discountPercent) {
        if (amount == null) {
            throw new IllegalArgumentException("Số tiền không thể null");
        }
        
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Phần trăm giảm giá phải nằm trong khoảng 0 đến 100: " + discountPercent);
        }
        
        BigDecimal discountRate = new BigDecimal(1 - (discountPercent / 100.0));
        BigDecimal discountedAmount = amount.multiply(discountRate);
        
        return discountedAmount.setScale(SCALE, ROUNDING_MODE);
    }
    
    /**
     * Tính toán số tiền giảm giá thực tế.
     * 
     * @param amount Số tiền gốc
     * @param discountPercent Phần trăm giảm giá (ví dụ: 20 cho 20%)
     * @return Số tiền được giảm
     * @throws IllegalArgumentException nếu tham số không hợp lệ
     */
    public static BigDecimal getDiscountAmount(BigDecimal amount, double discountPercent) {
        if (amount == null) {
            throw new IllegalArgumentException("Số tiền không thể null");
        }
        
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Phần trăm giảm giá phải nằm trong khoảng 0 đến 100: " + discountPercent);
        }
        
        BigDecimal discountAmount = amount.multiply(new BigDecimal(discountPercent / 100.0));
        return discountAmount.setScale(SCALE, ROUNDING_MODE);
    }
    
    /**
     * Làm tròn giá tiền theo quy tắc HALF_UP (làm tròn lên khi >= 0.5).
     * 
     * @param value Giá trị cần làm tròn
     * @return Giá trị đã làm tròn thành 2 chữ số thập phân
     * @throws IllegalArgumentException nếu value là null
     */
    public static BigDecimal roundHalfUp(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("Giá trị không thể null");
        }
        
        return value.setScale(SCALE, ROUNDING_MODE);
    }
    
    /**
     * Xác thực xem giá có hợp lệ hay không (phải > 0).
     * 
     * @param price Giá cần xác thực
     * @return true nếu giá > 0, false nếu không
     */
    public static boolean isValidPrice(BigDecimal price) {
        if (price == null) {
            return false;
        }
        
        return price.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Xác thực xem giá có hợp lệ và không vượt quá giá trị tối đa hay không.
     * 
     * @param price Giá cần xác thực
     * @param maxPrice Giá tối đa được phép
     * @return true nếu giá nằm trong khoảng (0, maxPrice], false nếu không
     */
    public static boolean isValidPrice(BigDecimal price, BigDecimal maxPrice) {
        if (price == null || maxPrice == null) {
            return false;
        }
        
        return price.compareTo(BigDecimal.ZERO) > 0 && price.compareTo(maxPrice) <= 0;
    }
    
    /**
     * So sánh hai giá tiền.
     * 
     * @param price1 Giá thứ nhất
     * @param price2 Giá thứ hai
     * @return Âm nếu price1 < price2, 0 nếu bằng nhau, dương nếu price1 > price2
     * @throws IllegalArgumentException nếu tham số là null
     */
    public static int comparePrices(BigDecimal price1, BigDecimal price2) {
        if (price1 == null || price2 == null) {
            throw new IllegalArgumentException("Giá không thể null");
        }
        
        return price1.compareTo(price2);
    }
    
    /**
     * Tính toán tổng cộng từ danh sách các giá.
     * 
     * @param prices Danh sách các giá
     * @return Tổng cộng
     * @throws IllegalArgumentException nếu prices là null
     */
    public static BigDecimal calculateTotal(BigDecimal... prices) {
        if (prices == null || prices.length == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal price : prices) {
            if (price != null) {
                total = total.add(price);
            }
        }
        
        return total.setScale(SCALE, ROUNDING_MODE);
    }
    
    /**
     * Áp dụng nhiều giảm giá tuần tự.
     * 
     * @param amount Số tiền gốc
     * @param discountPercents Danh sách phần trăm giảm giá
     * @return Số tiền sau khi áp dụng tất cả giảm giá
     * @throws IllegalArgumentException nếu tham số không hợp lệ
     */
    public static BigDecimal applyMultipleDiscounts(BigDecimal amount, double... discountPercents) {
        if (amount == null) {
            throw new IllegalArgumentException("Số tiền không thể null");
        }
        
        if (discountPercents == null || discountPercents.length == 0) {
            return amount.setScale(SCALE, ROUNDING_MODE);
        }
        
        BigDecimal result = amount;
        for (double discountPercent : discountPercents) {
            result = calculateDiscount(result, discountPercent);
        }
        
        return result;
    }
    
    /**
     * Chuyển đổi giá từ một số nguyên (xu) sang BigDecimal (đồng).
     * 
     * Ví dụ: 12345 xu = 123.45 đồng
     * 
     * @param cents Giá tính bằng xu
     * @return Giá tính bằng đồng
     */
    public static BigDecimal fromCents(long cents) {
        return new BigDecimal(cents).divide(new BigDecimal("100"), SCALE, ROUNDING_MODE);
    }
    
    /**
     * Chuyển đổi giá từ BigDecimal (đồng) sang số nguyên (xu).
     * 
     * Ví dụ: 123.45 đồng = 12345 xu
     * 
     * @param price Giá tính bằng đồng
     * @return Giá tính bằng xu (số nguyên)
     * @throws IllegalArgumentException nếu price là null
     */
    public static long toCents(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Giá không thể null");
        }
        
        return price.multiply(new BigDecimal("100")).setScale(0, ROUNDING_MODE).longValue();
    }
    
    /**
     * Lấy tỷ lệ tăng giá từ giá gốc đến giá mới.
     * 
     * @param originalPrice Giá gốc
     * @param newPrice Giá mới
     * @return Tỷ lệ phần trăm tăng giá (âm nếu là giảm giá)
     * @throws IllegalArgumentException nếu tham số không hợp lệ
     */
    public static double getPriceChangePercent(BigDecimal originalPrice, BigDecimal newPrice) {
        if (originalPrice == null || newPrice == null) {
            throw new IllegalArgumentException("Giá không thể null");
        }
        
        if (originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Giá gốc không thể là 0");
        }
        
        BigDecimal change = newPrice.subtract(originalPrice);
        BigDecimal percent = change.divide(originalPrice, 4, ROUNDING_MODE).multiply(new BigDecimal("100"));
        
        return percent.doubleValue();
    }
}
