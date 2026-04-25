package com.example.usermanage.controller;

import com.example.usermanage.dto.LoginRequest;
import com.example.usermanage.dto.LoginResponse;
import com.example.usermanage.dto.Result;
import com.example.usermanage.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        if (response.isSuccess()) {
            return Result.success(response);
        }
        return Result.error(401, response.getMessage());
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        authService.logout();
        return Result.success();
    }

    @GetMapping("/check")
    public Result<Boolean> checkAuth(@RequestHeader(value = "Authorization", required = false) String token) {
        boolean valid = authService.validateToken(token);
        return Result.success(valid);
    }

    public AuthService getAuthService() {
        return authService;
    }

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
}
