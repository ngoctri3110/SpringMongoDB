package com.example.ecommerce.repository;

import com.example.ecommerce.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository - CRUD operations cho User
 * 
 * MongoRepository provides:
 * - save(), saveAll()
 * - findById(), findAll()
 * - update(), delete()
 * - Derived query methods (findBy...)
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Tìm user bằng email
     * Generated query: db.users.findOne({ email: ? })
     */
    Optional<User> findByEmail(String email);

    /**
     * Tìm user bằng username
     */
    Optional<User> findByUsername(String username);

    /**
     * Kiểm tra email tồn tại
     */
    boolean existsByEmail(String email);

    /**
     * Kiểm tra username tồn tại
     */
    boolean existsByUsername(String username);

    /**
     * Lấy danh sách active users
     */
    Page<User> findByActiveTrue(Pageable pageable);

    /**
     * Lấy danh sách users theo role
     */
    Page<User> findByRolesContaining(String role, Pageable pageable);
}
