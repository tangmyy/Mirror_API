// src/main/java/com/example/mirror/service/impl/UsersServiceImpl.java
package com.example.mirror.service.Impl;

import com.example.mirror.entity.Users;
import com.example.mirror.mapper.UsersMapper;
import com.example.mirror.service.UsersService;
import com.example.mirror.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Service
public class UsersServiceImpl implements UsersService {

    private static final Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public boolean register(Users user) {
        if (isUsernameTaken(user.getUsername())) {
            logger.info("用户名 {} 已被占用", user.getUsername());
            throw new IllegalArgumentException("用户名已被占用");
        }
        if (isEmailTaken(user.getEmail())) {
            logger.info("邮箱 {} 已被占用", user.getEmail());
            throw new IllegalArgumentException("邮箱已被占用");
        }
        user.setCreated(LocalDateTime.now());
        user.setPassword(PasswordUtil.encodePassword(user.getPassword()));
        int result = usersMapper.insert(user);
        if (result > 0) {
            logger.info("用户 {} 注册成功", user.getUsername());
        } else {
            logger.info("用户 {} 注册失败", user.getUsername());
        }
        return result > 0;
    }

    @Override
    public Users login(String username, String password) {
        Users user = usersMapper.findByUsername(username);
        if (user != null && PasswordUtil.matches(password, user.getPassword())) {
            logger.info("用户 {} 登录成功", username);
            return user;
        }
        logger.info("用户 {} 登录失败", username);
        return null;
    }

    @Override
    public boolean isUsernameTaken(String username) {
        boolean isTaken = usersMapper.findByUsername(username) != null;
        logger.info("用户名 {} 检查结果: {}", username, isTaken ? "已被占用" : "可用");
        return isTaken;
    }

    @Override
    public boolean isEmailTaken(String email) {
        boolean isTaken = usersMapper.findByEmail(email) != null;
        logger.info("邮箱 {} 检查结果: {}", email, isTaken ? "已被占用" : "可用");
        return isTaken;
    }
}
