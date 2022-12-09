package com.jd.accounting.services;

import com.jd.accounting.model.security.User;

import java.util.Set;

public interface UserService {
    public User getCurrentUser();
    public User findByUsername(String username);

    public Set<User> findAll();
    public User create(User user);
    public User updatePassword(User user, String newPassword);
}
