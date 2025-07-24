package com.ssrmtech.itcompany.repository;

import com.ssrmtech.itcompany.model.ParentAdmin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentAdminRepository extends MongoRepository<ParentAdmin, String> {
    Optional<ParentAdmin> findByEmail(String email);
}