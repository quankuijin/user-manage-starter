package com.example.usermanage.service;

import com.example.usermanage.config.AdminProperties;
import com.example.usermanage.dto.LoginRequest;
import com.example.usermanage.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private AdminProperties adminProperties;
    private String currentToken;

    public LoginResponse login(LoginRequest request) {
        if (adminProperties.getUsername().equals(request.getUsername()) &&
            adminProperties.getPassword().equals(request.getPassword())) {
            currentToken = UUID.randomUUID().toString();
            return new LoginResponse(true, "登录成功", currentToken, request.getUsername());
        }
        return new LoginResponse(false, "用户名或密码错误");
    }

    public boolean validateToken(String token) {
        return token != null && token.equals(currentToken);
    }

    public void logout() {
        currentToken = null;
    }

    public AdminProperties getAdminProperties() {
        return adminProperties;
    }

    @Autowired
    public void setAdminProperties(AdminProperties adminProperties) {
        this.adminProperties = adminProperties;
    }
}
