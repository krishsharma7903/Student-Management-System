package com.sms.dao;

import com.sms.model.User;

import java.util.List;

/**
 * Data Access Object interface for User Authentication accounts.
 * 
 * // [OOP] Interface implementation: UserDAO
 */
public interface UserDAO {
    boolean create(User user);
    boolean update(User user);
    boolean delete(int id);
    User findById(int id);
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findAll();
    int count();
}
