package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.request.ProcessPaymentRequest;
import com.example.ecommerce.dto.response.PaymentTransactionResponse;
import com.example.ecommerce.model.PaymentTransaction;
import org.mapstruct.Mapper;

/**
 * Mapper interface cho PaymentTransaction entity
 * Chuyển đổi giữa PaymentTransaction entity, PaymentTransactionResponse DTO và ProcessPaymentRequest DTO
 * 
 * Sử dụng MapStruct với Spring Component Model để tự động inject
 */
@Mapper(componentModel = "spring")
public interface PaymentTransactionMapper {

    /**
     * Chuyển đổi PaymentTransaction entity sang PaymentTransactionResponse DTO
     * Ánh xạ tất cả các trường từ PaymentTransaction sang PaymentTransactionResponse
     * 
     * @param transaction PaymentTransaction entity cần chuyển đổi
     * @return PaymentTransactionResponse DTO đã được ánh xạ
     */
    PaymentTransactionResponse toResponse(PaymentTransaction transaction);

    /**
     * Chuyển đổi ProcessPaymentRequest DTO sang PaymentTransaction entity
     * Tạo mới PaymentTransaction từ request DTO
     * 
     * @param request ProcessPaymentRequest DTO chứa dữ liệu giao dịch thanh toán mới
     * @return PaymentTransaction entity đã được tạo từ request
     */
    PaymentTransaction toEntity(ProcessPaymentRequest request);
}
