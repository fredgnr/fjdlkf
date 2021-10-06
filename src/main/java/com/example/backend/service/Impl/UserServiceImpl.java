package com.example.backend.service.Impl;

import com.example.backend.dao.UserDao;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;

    @Transactional
    public void save(User user) {
        userDao.save(user);
    }

    public User getUserByPhone(String phone) {
        return userDao.findUserByPhone(phone);
    }

    public User selectUserByUserId(Integer id) {
        return userDao.findUserById(id);
    }

    @Transactional
    public void modifyUserInfo(Integer UserId, String newUserName, String newUserPhone, String newUserPassword) throws Exception {
        User user = selectUserByUserId(UserId);
        if (user == null) {
            user = new User(UserId, newUserName, newUserPhone, newUserPassword);
        } else {
            user.setUsername(newUserName);
            user.setPhone(newUserPhone);
            user.setPassword(newUserPassword);
        }
        save(user);
    }
}
