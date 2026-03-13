package com.example.ecommerce.repository;

import com.example.ecommerce.IntegrationTest;
import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for UserRepository
 * Tests CRUD operations and derived queries for User documents
 *
 * @author E-Commerce Platform
 * @version 1.0
 */
@DisplayName("UserRepository Tests")
class UserRepositoryTest extends IntegrationTest {

    /**
     * Test: Create a new user and retrieve it
     * Chức năng: Tạo người dùng mới và lấy lại
     */
    @Test
    @DisplayName("Tạo người dùng mới và lấy lại thành công")
    void testCreateUser() {
        // Arrange
        User user = new User("john@example.com", "johndoe", "John Doe");
        user.setPassword("password123");
        user.setPhoneNumber("0123456789");

        // Act
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Assert
        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u)
                        .extracting(User::getEmail, User::getUsername, User::getFullName)
                        .containsExactly("john@example.com", "johndoe", "John Doe")
                );
    }

    /**
     * Test: Find user by email (derived query method)
     * Chức năng: Tìm người dùng bằng email
     */
    @Test
    @DisplayName("Tìm người dùng bằng email thành công")
    void testFindByEmail() {
        // Arrange
        String email = "alice@example.com";
        createTestUser(email, "alice", "Alice");

        // Act
        Optional<User> foundUser = userRepository.findByEmail(email);

        // Assert
        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u)
                        .extracting(User::getEmail, User::getUsername)
                        .containsExactly(email, "alice")
                );
    }

    /**
     * Test: Find user by username (derived query method)
     * Chức năng: Tìm người dùng bằng username
     */
    @Test
    @DisplayName("Tìm người dùng bằng username thành công")
    void testFindByUsername() {
        // Arrange
        String username = "bobsmith";
        createTestUser("bob@example.com", username, "Bob Smith");

        // Act
        Optional<User> foundUser = userRepository.findByUsername(username);

        // Assert
        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u)
                        .extracting(User::getUsername, User::getEmail)
                        .containsExactly(username, "bob@example.com")
                );
    }

    /**
     * Test: Email uniqueness constraint
     * Chức năng: Kiểm tra tính duy nhất của email
     */
    @Test
    @DisplayName("Email phải là duy nhất")
    void testEmailUniqueness() {
        // Arrange
        String uniqueEmail = "unique@example.com";
        createTestUser(uniqueEmail, "user1", "User One");

        // Act
        boolean exists = userRepository.existsByEmail(uniqueEmail);
        boolean notExists = userRepository.existsByEmail("notexist@example.com");

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    /**
     * Test: Username uniqueness constraint
     * Chức năng: Kiểm tra tính duy nhất của username
     */
    @Test
    @DisplayName("Username phải là duy nhất")
    void testUsernameUniqueness() {
        // Arrange
        String uniqueUsername = "uniqueuser";
        createTestUser("user1@example.com", uniqueUsername, "User One");

        // Act
        boolean exists = userRepository.existsByUsername(uniqueUsername);
        boolean notExists = userRepository.existsByUsername("nonexistentuser");

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    /**
     * Test: Update user information
     * Chức năng: Cập nhật thông tin người dùng
     */
    @Test
    @DisplayName("Cập nhật thông tin người dùng thành công")
    void testUpdateUser() {
        // Arrange
        User user = createTestUser("original@example.com", "originaluser", "Original Name");

        // Act
        user.setFullName("Updated Name");
        user.setPhoneNumber("9876543210");
        user.setAvatar("https://example.com/avatar.jpg");
        User updatedUser = userRepository.save(user);

        // Assert
        User retrieved = userRepository.findById(updatedUser.getId()).orElseThrow();
        assertThat(retrieved)
                .extracting(User::getFullName, User::getPhoneNumber, User::getAvatar)
                .containsExactly("Updated Name", "9876543210", "https://example.com/avatar.jpg");
    }

    /**
     * Test: Delete a user
     * Chức năng: Xóa người dùng
     */
    @Test
    @DisplayName("Xóa người dùng thành công")
    void testDeleteUser() {
        // Arrange
        User user = createTestUser("delete@example.com", "deleteuser", "Delete Me");
        String userId = user.getId();

        // Act
        userRepository.delete(user);
        Optional<User> foundUser = userRepository.findById(userId);

        // Assert
        assertThat(foundUser).isEmpty();
        assertThat(userRepository.count()).isZero();
    }

    /**
     * Test: Find active users with pagination
     * Chức năng: Tìm người dùng hoạt động với phân trang
     */
    @Test
    @DisplayName("Tìm người dùng hoạt động với phân trang thành công")
    void testFindActiveUsers() {
        // Arrange
        User activeUser1 = createTestUser("active1@example.com", "active1", "Active One");
        activeUser1.setActive(true);
        userRepository.save(activeUser1);
        
        User activeUser2 = createTestUser("active2@example.com", "active2", "Active Two");
        activeUser2.setActive(true);
        userRepository.save(activeUser2);
        
        User inactiveUser = createTestUser("inactive@example.com", "inactive", "Inactive");
        inactiveUser.setActive(false);
        userRepository.save(inactiveUser);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> activeUsers = userRepository.findByActiveTrue(pageable);

        // Assert
        assertThat(activeUsers)
                .extracting(User::getUsername)
                .containsExactlyInAnyOrder("active1", "active2");
        assertThat(activeUsers.getTotalElements()).isEqualTo(2);
    }

    /**
     * Test: Find users by role
     * Chức năng: Tìm người dùng theo vai trò
     */
    @Test
    @DisplayName("Tìm người dùng theo vai trò thành công")
    void testFindByRole() {
        // Arrange
        User adminUser = createTestUser("admin@example.com", "admin", "Admin User");
        adminUser.getRoles().add("ADMIN");
        userRepository.save(adminUser);
        
        User customerUser = createTestUser("customer@example.com", "customer", "Customer User");
        customerUser.getRoles().add("CUSTOMER");
        userRepository.save(customerUser);
        
        User superAdminUser = createTestUser("superadmin@example.com", "superadmin", "Super Admin");
        superAdminUser.getRoles().add("ADMIN");
        superAdminUser.getRoles().add("SUPER_ADMIN");
        userRepository.save(superAdminUser);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> adminUsers = userRepository.findByRolesContaining("ADMIN", pageable);

        // Assert
        assertThat(adminUsers.getTotalElements()).isEqualTo(2);
        assertThat(adminUsers)
                .extracting(User::getUsername)
                .containsExactlyInAnyOrder("admin", "superadmin");
    }

    /**
     * Test: User with embedded addresses
     * Chức năng: Người dùng với địa chỉ nhúng
     */
    @Test
    @DisplayName("Tạo người dùng với địa chỉ nhúng thành công")
    void testUserWithAddresses() {
        // Arrange
        User user = createTestUser("address@example.com", "addressuser", "Address User");
        
        Address homeAddress = new Address();
        homeAddress.setStreet("123 Home Street");
        homeAddress.setCity("Home City");
        homeAddress.setPostalCode("12345");
        homeAddress.setCountry("Home Country");
        user.getAddresses().add(homeAddress);
        
        Address officeAddress = new Address();
        officeAddress.setStreet("456 Office Avenue");
        officeAddress.setCity("Office City");
        officeAddress.setPostalCode("67890");
        officeAddress.setCountry("Office Country");
        user.getAddresses().add(officeAddress);

        // Act
        User savedUser = userRepository.save(user);
        User retrievedUser = userRepository.findById(savedUser.getId()).orElseThrow();

        // Assert
        assertThat(retrievedUser.getAddresses())
                .hasSize(2)
                .extracting(Address::getCity)
                .containsExactlyInAnyOrder("Home City", "Office City");
    }

    /**
     * Test: User creation with default roles
     * Chức năng: Tạo người dùng với vai trò mặc định
     */
    @Test
    @DisplayName("Người dùng mới có vai trò USER mặc định")
    void testDefaultRoles() {
        // Act
        User user = new User("user@example.com", "newuser", "New User");
        User savedUser = userRepository.save(user);

        // Assert
        assertThat(savedUser.getRoles())
                .contains("USER");
    }

    /**
     * Test: Pagination of users
     * Chức năng: Phân trang người dùng
     */
    @Test
    @DisplayName("Phân trang người dùng thành công")
    void testUserPagination() {
        // Arrange
        for (int i = 1; i <= 15; i++) {
            createTestUser("user" + i + "@example.com", "user" + i, "User " + i);
        }

        // Act
        Pageable page1 = PageRequest.of(0, 5);
        Page<User> firstPage = userRepository.findAll(page1);
        
        Pageable page2 = PageRequest.of(1, 5);
        Page<User> secondPage = userRepository.findAll(page2);

        // Assert
        assertThat(firstPage.getTotalElements()).isEqualTo(15);
        assertThat(firstPage.getTotalPages()).isEqualTo(3);
        assertThat(firstPage.getContent()).hasSize(5);
        assertThat(firstPage.isFirst()).isTrue();
        
        assertThat(secondPage.getContent()).hasSize(5);
        assertThat(secondPage.isLast()).isFalse();
    }

    /**
     * Test: User deactivation
     * Chức năng: Vô hiệu hóa người dùng
     */
    @Test
    @DisplayName("Vô hiệu hóa người dùng thành công")
    void testUserDeactivation() {
        // Arrange
        User user = createTestUser("deactivate@example.com", "deactiveuser", "Deactivate User");
        assertThat(user.isActive()).isTrue();

        // Act
        user.setActive(false);
        userRepository.save(user);
        User deactivatedUser = userRepository.findById(user.getId()).orElseThrow();

        // Assert
        assertThat(deactivatedUser.isActive()).isFalse();
    }

    /**
     * Test: Find user with non-existent email
     * Chức năng: Tìm người dùng không tồn tại
     */
    @Test
    @DisplayName("Tìm người dùng không tồn tại trả về rỗng")
    void testFindNonExistentUser() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
        Optional<User> foundByUsername = userRepository.findByUsername("nonexistentuser");

        // Assert
        assertThat(foundUser).isEmpty();
        assertThat(foundByUsername).isEmpty();
    }

    /**
     * Test: Multiple users with same role
     * Chức năng: Nhiều người dùng có cùng vai trò
     */
    @Test
    @DisplayName("Nhiều người dùng có cùng vai trò")
    void testMultipleUsersWithSameRole() {
        // Arrange
        for (int i = 1; i <= 5; i++) {
            User user = createTestUser("vendor" + i + "@example.com", "vendor" + i, "Vendor " + i);
            user.getRoles().add("VENDOR");
            userRepository.save(user);
        }

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> vendors = userRepository.findByRolesContaining("VENDOR", pageable);

        // Assert
        assertThat(vendors.getTotalElements()).isEqualTo(5);
        assertThat(vendors.getContent())
                .extracting(User::getUsername)
                .allMatch(username -> username.startsWith("vendor"));
    }
}
