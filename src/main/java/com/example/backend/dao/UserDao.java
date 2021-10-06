package com.example.backend.dao;

import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Integer> {
    User findUserByPhone(String phone);

    User findUserById(int userId);
}
