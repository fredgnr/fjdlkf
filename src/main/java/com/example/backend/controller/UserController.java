package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import com.example.backend.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("test")
public class UserController {

    @Autowired
    UserService userService;

    private String code = "";

    @RequestMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> params) {
        Map<String, Object> ret = new HashMap<>();
        String phone = params.get("phone").toString();
        String password = params.get("password").toString();
        User user = userService.getUserByPhone(phone);
        if (user == null) {
            ret.put("status", false);
            ret.put("exec", "该号码未注册!");
            return ret;
        }
        if (!user.getPassword().equals(password)) {
            ret.put("status", false);
            ret.put("exec", "密码错误!");
            return ret;
        }
        ret.put("status", true);
        ret.put("exec", "登录成功!");
        ret.put("user_id", user.getId());
        return ret;
    }


    @PostMapping("/register")
    public Map<String, Object> register(HttpServletRequest request,
                                        @RequestBody Map<String, Object> params) {
        Map<String, Object> ret = new HashMap<>();
        String phone = params.get("phone").toString();
        String checkCode = params.get("checkCode").toString();
        String password = params.get("password").toString();


        if (code.equals("")) {
            ret.put("status", false);
            ret.put("exec", "请先获取验证码!");
            return ret;
        }
        if (!checkCode.equals(code)) {
            ret.put("status", false);
            ret.put("exec", "验证码错误!");
            return ret;
        }
        User user = new User();
        user.setPhone(phone);
        user.setPassword(password);
        userService.save(user);
        code = "";
        ret.put("status", true);
        ret.put("exec", "注册成功");
        return ret;
    }

    @PostMapping("/sendCode/{function}")
    public Map<String, Object> sendCode(@PathVariable(value = "function") String function,
                                        HttpServletRequest request,
                                        @RequestBody Map<String, Object> params) {
        Map<String, Object> ret = new HashMap<>();
        try {
            String phone = params.get("phone").toString();
            if (function.equals("findPass")) {
                if (userService.getUserByPhone(phone) == null) {
                    ret.put("status", false);
                    ret.put("exec", "该号码未注册!");
                    return ret;
                }
            }
            if (function.equals("register")) {
                if (userService.getUserByPhone(phone) != null) {
                    ret.put("status", false);
                    ret.put("exec", "该号码已注册!");
                    return ret;
                }
            }
            //code = SendMessageUtil.code();
            code = "6324";
            String desc = "您的验证码是:" + code + "，打死也不要告诉别人哦！";
            //int flag = SendMessageUtil.send(phone, desc);
            int flag = 1;
            if (flag != 1) {
                ret.put("status", false);
                ret.put("exec", "发送失败");
                return ret;
            }
            ret.put("status", true);
            ret.put("exec", "发送成功");
        } catch (Exception e) {
            ret.put("test", e.toString());
        } finally {
            return ret;
        }
    }

    //
    @RequestMapping("/findPass")
    @SuppressWarnings("all")
    public Map<String, Object> findPassWord(HttpServletRequest request,
                                            @RequestBody Map<String, Object> params) {
        Map<String, Object> ret = new HashMap<>();
        String phone = params.get("phone").toString();
        String checkCode = params.get("checkCode").toString();
        String password = params.get("password").toString();
        if (code.equals("")) {
            ret.put("status", false);
            ret.put("exec", "请先获取验证码!");
            return ret;
        }
        if (!checkCode.equals(code)) {
            ret.put("status", false);
            ret.put("exec", "验证码错误!");
            return ret;
        }
        User user = userService.getUserByPhone(phone);
        user.setPassword(password);
        userService.save(user);
        code = "";
        ret.put("status", true);
        ret.put("exec", "修改密码成功");
        return ret;
    }

    @PostMapping("/personalcenter1")
    public Map<String, Object> showInfo(@RequestBody Map<String, Object> returnMap) {
        Integer UserId = Integer.parseInt(returnMap.get("UserId").toString());
        User selectedUser = userService.selectUserByUserId(UserId);
        String username = selectedUser.getUsername();
        String phone = selectedUser.getPhone();
        String password = selectedUser.getPassword();
        String path = selectedUser.getImage();
        Map<String, Object> map = new HashMap<>();
        map.put("UserId", UserId);
        map.put("UserName", username);
        map.put("UserPhone", phone);
        map.put("UserPassword", password);
        map.put("path", path);
        return map;
    }

    @PostMapping("/personalcenter2")
    public Map<String, Object> modifyInfo(@RequestBody Map<String, Object> returnMap) {
        try {
            Integer UserId = Integer.parseInt(returnMap.get("UserId").toString());
            String newUserName = returnMap.get("newUserName").toString();
            String newUserPhone = returnMap.get("newUserPhone").toString();
            String newUserPassword = returnMap.get("newUserPassword").toString();
            userService.modifyUserInfo(UserId, newUserName, newUserPhone, newUserPassword);
            Map<String, Object> map = new HashMap<>();
            map.put("newUserName", newUserName);
            map.put("newUserPhone", newUserPhone);
            map.put("newUserPassword", newUserPassword);
            map.put("isSuccessed", true);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            User selectedUser = userService.selectUserByUserId(Integer.parseInt(returnMap.get("UserId").toString()));
            String username = selectedUser.getUsername();
            String phone = selectedUser.getPhone();
            String password = selectedUser.getPassword();
            Map<String, Object> map = new HashMap<>();
            map.put("UserName", username);
            map.put("UserPhone", phone);
            map.put("UserPassword", password);
            map.put("isSuccessed", false);
            return map;
        }
    }

    @PostMapping("/personalcenter/changepic")
    public Map<String, Object> uploadImage(@RequestParam("id") int id,
                                           @RequestParam("file") MultipartFile image) {
        Map<String, Object> ret = new HashMap<>();
        User user = userService.selectUserByUserId(id);
        String fileName = image.getOriginalFilename();
        String filePath = "/home/backend/images";
        try {
            FileUtil.uploadFile(image.getBytes(), filePath, fileName);
            user.setImage(filePath + "/" + fileName);
            userService.save(user);
        } catch (Exception e) {
            ret.put("status", false);
            ret.put("exec", e.toString());
            ret.put("path", "储存失败!");
        }
        ret.put("status", true);
        ret.put("exec", "上传相片成功");
        ret.put("path", filePath + "/" + fileName);
        return ret;
    }
}
