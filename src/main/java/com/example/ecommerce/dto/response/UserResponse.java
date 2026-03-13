package com.example.ecommerce.dto.response;

import com.example.ecommerce.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private String id;
    private String email;
    private String username;
    private String fullName;
    private String phoneNumber;
    private String avatar;
    private List<Address> addresses;
    private List<String> roles;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
