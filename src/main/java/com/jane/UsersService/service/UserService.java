package com.jane.UsersService.service;

import com.jane.UsersService.ui.model.User;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User createUser(User user);
    User getUser(String userId);
    List<User> getUsers(int page, int limit);
}
