package com.ssrmtech.itcompany.service;

import com.ssrmtech.itcompany.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(String id);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(String id);
    User approveUser(String id);
    User rejectUser(String id);
    void updateLastLogin(String id);
    List<User> getUsersByRole(String role);
    User getUserByEmail(String email);
    User findUserByEmailInAllTables(String email);
}