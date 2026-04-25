package com.example.usermanage.service;

import com.example.usermanage.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class FileStorageService {

    @Value("${data.file.path:users.json}")
    private String dataFilePath = "users.json";

    private ObjectMapper objectMapper;
    private AtomicLong nextId;
    private List<User> users;

    @PostConstruct
    public void init() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        users = new ArrayList<>();
        nextId = new AtomicLong(1);
        loadUsers();
    }

    private void loadUsers() {
        try {
            ClassPathResource resource = new ClassPathResource(dataFilePath);
            if (resource.exists()) {
                users = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<User>>() {});
                if (!users.isEmpty()) {
                    long maxId = users.stream()
                            .mapToLong(User::getId)
                            .max()
                            .orElse(0);
                    nextId.set(maxId + 1);
                }
            }
        } catch (IOException e) {
            users = new ArrayList<>();
        }
    }

    private void saveUsers() {
        try {
            ClassPathResource resource = new ClassPathResource(dataFilePath);
            File file;
            if (resource.exists()) {
                file = resource.getFile();
            } else {
                String path = "src/main/resources/" + dataFilePath;
                file = new File(path);
                file.getParentFile().mkdirs();
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, users);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save users data", e);
        }
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public Optional<User> findById(Long id) {
        return users.stream()
                .filter(user -> id.equals(user.getId()))
                .findFirst();
    }

    public Optional<User> findByCode(String code) {
        return users.stream()
                .filter(user -> code.equals(user.getCode()))
                .findFirst();
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(nextId.getAndIncrement());
            users.add(user);
        } else {
            int index = -1;
            for (int i = 0; i < users.size(); i++) {
                if (user.getId().equals(users.get(i).getId())) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                users.set(index, user);
            } else {
                users.add(user);
            }
        }
        saveUsers();
        return user;
    }

    public boolean deleteById(Long id) {
        boolean removed = users.removeIf(user -> id.equals(user.getId()));
        if (removed) {
            saveUsers();
        }
        return removed;
    }

    public boolean existsByCode(String code) {
        return users.stream()
                .anyMatch(user -> code.equals(user.getCode()));
    }

    public String getDataFilePath() {
        return dataFilePath;
    }

    public void setDataFilePath(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }
}
