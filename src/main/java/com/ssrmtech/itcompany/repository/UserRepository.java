package com.ssrmtech.itcompany.repository;

import com.ssrmtech.itcompany.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    List<User> findByStatus(String status);
    List<User> findByRole(String role);
}