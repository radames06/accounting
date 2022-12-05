package com.jd.accounting.services;

import com.jd.accounting.model.security.User;

public interface UserService {
    public User findByUsername(String username);
    //public User currentUser();
}
