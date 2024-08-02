// src/main/java/com/example/mirror/controller/UsersController.java
package com.example.mirror.controller;

import com.example.mirror.entity.Users;
import com.example.mirror.service.UsersService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import java.time.Duration;
import java.util.Enumeration;
import java.util.Arrays;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private UsersService usersService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Users user) {
        boolean success = usersService.register(user);
        if (success) {
            return ResponseEntity.ok("注册成功");
        } else {
            return ResponseEntity.status(500).body("注册失败");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Users user, HttpSession session, HttpServletResponse response) {
        Users loggedInUser = usersService.login(user.getUsername(), user.getPassword());
        if (loggedInUser != null) {
            // 将用户信息存储到 session 中
            session.setAttribute("user", loggedInUser);

            // 手动设置新的 Cookie
            Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
            sessionCookie.setPath("/");
            sessionCookie.setHttpOnly(false);
            sessionCookie.setDomain("localhost"); // 设置为主机名即可
            sessionCookie.setMaxAge(24 * 60 * 60); // 设置Cookie过期时间，单位为秒
            response.addCookie(sessionCookie);

            // 打印刚刚设置的 Cookie 信息到控制台
            System.out.println("Cookie Name: " + sessionCookie.getName());
            System.out.println("Cookie Value: " + sessionCookie.getValue());
            System.out.println("Cookie Path: " + sessionCookie.getPath());
            System.out.println("Cookie HttpOnly: " + sessionCookie.isHttpOnly());

            return ResponseEntity.ok("登录成功");
        } else {
            logger.info("用户 {} 登录失败", user.getUsername());
            return ResponseEntity.status(401).body("登录失败，用户名或密码错误");
        }
    }

    @GetMapping("/session")
    public ResponseEntity<String> getSessionInfo(HttpSession session) {
        // 获取用户信息
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            logger.info("用户未登录");
            return ResponseEntity.status(401).body("用户未登录");
        } else {
            // 输出会话信息到控制台
            logger.info("会话 ID: {}", session.getId());
            logger.info("会话创建时间: {}", session.getCreationTime());
            logger.info("会话最后访问时间: {}", session.getLastAccessedTime());
            logger.info("会话最大不活动间隔时间: {}", session.getMaxInactiveInterval());

            // 输出会话中所有属性的名称
            Enumeration<String> attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();
                logger.info("会话属性: {} = {}", attributeName, session.getAttribute(attributeName));
            }
            logger.info("用户信息: {}", user);
            return ResponseEntity.ok("用户已登录\n会话信息已输出到控制台");
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user != null) {
            return ResponseEntity.ok("用户已登录");
        } else {
            return ResponseEntity.status(401).body("用户未登录");
        }
    }
}
