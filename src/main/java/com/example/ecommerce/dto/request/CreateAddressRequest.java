package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho Address
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAddressRequest {

    @NotBlank(message = "Địa chỉ không được để trống")
    private String street;

    @NotBlank(message = "Thành phố không được để trống")
    private String city;

    @NotBlank(message = "Tỉnh/Bang không được để trống")
    private String state;

    @NotBlank(message = "Mã bưu điện không được để trống")
    private String postalCode;

    @NotBlank(message = "Quốc gia không được để trống")
    private String country;

    private String type; // HOME, WORK, OTHER
}
