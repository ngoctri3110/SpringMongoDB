package com.example.ecommerce.enums;

import java.io.Serializable;

/**
 * Enum định nghĩa các trạng thái của đánh giá/review sản phẩm.
 *
 * <p><b>Biểu đồ chuyển trạng thái:</b></p>
 * <pre>
 *   PENDING_APPROVAL → APPROVED (hiển thị công khai)
 *   PENDING_APPROVAL → REJECTED (từ chối)
 *   PENDING_APPROVAL → SPAM (đánh dấu là spam)
 *   APPROVED ↔ SPAM (có thể đánh dấu lại)
 *   REJECTED → (không thể thay đổi)
 * </pre>
 *
 * <p><b>Mô tả từng trạng thái:</b></p>
 * <ul>
 *   <li>PENDING_APPROVAL: Đánh giá chờ duyệt từ quản trị viên</li>
 *   <li>APPROVED: Đánh giá đã được duyệt, hiển thị công khai</li>
 *   <li>REJECTED: Đánh giá bị từ chối, không hiển thị</li>
 *   <li>SPAM: Đánh giá được đánh dấu là spam</li>
 * </ul>
 *
 * @author Ecommerce Team
 * @version 1.0
 */
public enum ReviewStatus implements Serializable {

    /**
     * Đánh giá chờ duyệt từ quản trị viên.
     */
    PENDING_APPROVAL("pending_approval", "Chờ duyệt"),

    /**
     * Đánh giá đã được duyệt, hiển thị công khai.
     */
    APPROVED("approved", "Đã duyệt"),

    /**
     * Đánh giá bị từ chối, không hiển thị.
     */
    REJECTED("rejected", "Bị từ chối"),

    /**
     * Đánh giá được đánh dấu là spam hoặc lạm dụng.
     */
    SPAM("spam", "Spam/Lạm dụng");

    private final String value;
    private final String description;

    /**
     * Constructor cho ReviewStatus enum.
     *
     * @param value       Giá trị của trạng thái đánh giá
     * @param description Mô tả chi tiết của trạng thái
     */
    ReviewStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * Lấy giá trị của trạng thái đánh giá.
     *
     * @return Giá trị dạng chuỗi
     */
    public String getValue() {
        return value;
    }

    /**
     * Lấy mô tả của trạng thái đánh giá.
     *
     * @return Mô tả chi tiết
     */
    public String getDescription() {
        return description;
    }

    /**
     * Chuyển đổi giá trị chuỗi thành ReviewStatus enum.
     * Tìm kiếm không phân biệt chữ hoa/thường.
     *
     * @param value Giá trị cần chuyển đổi
     * @return ReviewStatus tương ứng
     * @throws IllegalArgumentException Nếu giá trị không hợp lệ
     */
    public static ReviewStatus fromValue(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Giá trị không thể rỗng");
        }

        String normalizedValue = value.toLowerCase().trim();
        for (ReviewStatus status : ReviewStatus.values()) {
            if (status.getValue().equalsIgnoreCase(normalizedValue) ||
                status.name().equalsIgnoreCase(normalizedValue)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Giá trị trạng thái đánh giá không hợp lệ: " + value);
    }

    /**
     * Kiểm tra xem đánh giá có hiển thị công khai không.
     *
     * @return true nếu hiển thị, false nếu không
     */
    public boolean isVisible() {
        return this == APPROVED;
    }

    /**
     * Kiểm tra xem đánh giá có cần phê duyệt không.
     *
     * @return true nếu cần phê duyệt, false nếu không
     */
    public boolean isPending() {
        return this == PENDING_APPROVAL;
    }

    /**
     * Kiểm tra xem có thể chuyển từ trạng thái này sang trạng thái khác không.
     *
     * @param targetStatus Trạng thái đích
     * @return true nếu chuyển được, false nếu không
     */
    public boolean canTransitionTo(ReviewStatus targetStatus) {
        if (this == targetStatus) {
            return false;
        }

        return switch (this) {
            case PENDING_APPROVAL -> targetStatus == APPROVED || targetStatus == REJECTED || targetStatus == SPAM;
            case APPROVED -> targetStatus == SPAM;
            case SPAM -> targetStatus == APPROVED;
            case REJECTED -> false; // Không thể thay đổi từ REJECTED
        };
    }

    @Override
    public String toString() {
        return value;
    }
}
