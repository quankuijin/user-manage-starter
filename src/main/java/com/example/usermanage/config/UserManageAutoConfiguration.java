package com.example.usermanage.config;

import com.example.usermanage.controller.AuthController;
import com.example.usermanage.controller.UserController;
import com.example.usermanage.service.AuthService;
import com.example.usermanage.service.FileStorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AdminProperties.class)
@ConditionalOnProperty(prefix = "user.manage", name = "enabled", havingValue = "true", matchIfMissing = true)
public class UserManageAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FileStorageService fileStorageService() {
        return new FileStorageService();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthService authService(AdminProperties adminProperties) {
        AuthService authService = new AuthService();
        authService.setAdminProperties(adminProperties);
        return authService;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthController authController(AuthService authService) {
        AuthController controller = new AuthController();
        controller.setAuthService(authService);
        return controller;
    }

    @Bean
    @ConditionalOnMissingBean
    public UserController userController(FileStorageService fileStorageService, AuthService authService) {
        UserController controller = new UserController();
        controller.setFileStorageService(fileStorageService);
        controller.setAuthService(authService);
        return controller;
    }
}
