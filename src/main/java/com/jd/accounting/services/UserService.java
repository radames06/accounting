package com.jd.accounting.services;

import com.jd.accounting.model.security.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Set;

public interface UserService extends UserDetailsService {
    public User getCurrentUser();
    public User loadUserByUsername(String username);

    public Set<User> findAll();
    public User create(User user);
    public User updatePassword(User user, String newPassword);
    public User updateUser(User oldUser, User newUser);
}
