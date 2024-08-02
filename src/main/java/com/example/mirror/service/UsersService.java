// src/main/java/com/example/mirror/service/UsersService.java
package com.example.mirror.service;

import com.example.mirror.entity.Users;

public interface UsersService {
    boolean register(Users user);
    Users login(String username, String password);
    boolean isUsernameTaken(String username);
    boolean isEmailTaken(String email);
}
