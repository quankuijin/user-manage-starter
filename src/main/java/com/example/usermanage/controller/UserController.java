package com.example.usermanage.controller;

import com.example.usermanage.dto.Result;
import com.example.usermanage.entity.User;
import com.example.usermanage.service.AuthService;
import com.example.usermanage.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private FileStorageService fileStorageService;
    private AuthService authService;

    private boolean isAuthenticated(String token) {
        return authService.validateToken(token);
    }

    @GetMapping
    public ResponseEntity<Result<List<User>>> getAllUsers(
            @RequestHeader(value = "Authorization", required = false) String token) {
        if (!isAuthenticated(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error(401, "未登录，请先登录"));
        }
        List<User> users = fileStorageService.findAll();
        return ResponseEntity.ok(Result.success(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result<User>> getUserById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        if (!isAuthenticated(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error(401, "未登录，请先登录"));
        }
        Optional<User> user = fileStorageService.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(Result.success(user.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Result.error(404, "用户不存在"));
    }

    @PostMapping
    public ResponseEntity<Result<User>> createUser(
            @Valid @RequestBody User user,
            @RequestHeader(value = "Authorization", required = false) String token) {
        if (!isAuthenticated(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error(401, "未登录，请先登录"));
        }
        if (fileStorageService.existsByCode(user.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, "用户编码已存在"));
        }
        User savedUser = fileStorageService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success(savedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result<User>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User user,
            @RequestHeader(value = "Authorization", required = false) String token) {
        if (!isAuthenticated(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error(401, "未登录，请先登录"));
        }
        Optional<User> existingUser = fileStorageService.findById(id);
        if (!existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.error(404, "用户不存在"));
        }
        
        Optional<User> userWithSameCode = fileStorageService.findByCode(user.getCode());
        if (userWithSameCode.isPresent() && !userWithSameCode.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, "用户编码已存在"));
        }
        
        user.setId(id);
        User updatedUser = fileStorageService.save(user);
        return ResponseEntity.ok(Result.success(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteUser(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        if (!isAuthenticated(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error(401, "未登录，请先登录"));
        }
        if (!fileStorageService.findById(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.error(404, "用户不存在"));
        }
        fileStorageService.deleteById(id);
        return ResponseEntity.ok(Result.success());
    }

    public FileStorageService getFileStorageService() {
        return fileStorageService;
    }

    @Autowired
    public void setFileStorageService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public AuthService getAuthService() {
        return authService;
    }

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
}
