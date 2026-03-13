package com.example.ecommerce.model;

import com.example.ecommerce.model.base.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * User document - Đại diện cho user trong hệ thống
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "users")
public class User extends BaseDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private String username;

    private String password;

    private String fullName;

    private String phoneNumber;

    private String avatar;

    /**
     * Embedded document - Addresses lồng trong User
     * Không cần tạo collection riêng
     */
    private List<Address> addresses = new ArrayList<>();

    /**
     * Roles/Permissions
     */
    private List<String> roles = new ArrayList<>();

    private boolean active = true;

    private String provider; // google, facebook, local

    private String providerId;

    public User(String email, String username, String fullName) {
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.roles.add("USER");
    }
}
