package com.example.backend.service;

import com.example.backend.entity.User;

public interface UserService {
    void save(User user);

    User getUserByPhone(String phone);

    User selectUserByUserId(Integer id);

    void modifyUserInfo(Integer UserId, String newUserName, String newUserPhone, String newUserPassword) throws Exception;
}
