package com.example.backend.utils;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.util.Random;

public class SendMessageUtil {
    private static final String SMS_Url = "http://sms.webchinese.cn/web_api/";
    private static final String Uid = "dobby1";//SMS用户id
    private static final String Key = "d41d8cd98f00b204e980";//接口密钥

    //desc 短信内容
    public static Integer send(String sendPhoneNum, String desc) throws IOException {

        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(SMS_Url);
        post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");// 在头文件中设置转码

        //设置参数
        NameValuePair[] data = {
                new NameValuePair("Uid", Uid),//登录用户名
                new NameValuePair("Key", Key),//秘钥
                new NameValuePair("smsMob", sendPhoneNum),
                new NameValuePair("smsText", desc)
        };

        post.setRequestBody(data);

//        try {
//            client.executeMethod(post);
//        } catch (Exception e) {  e.printStackTrace();  }
        client.executeMethod(post);

        Header[] headers = post.getResponseHeaders();
        int statusCode = post.getStatusCode();
        System.out.println("statusCode:" + statusCode);
        for (Header h : headers) {
            System.out.println(h.toString());
        }

        String result = "";
//        try {
//            result = new String(post.getResponseBodyAsString().getBytes("gbk"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        result = new String(post.getResponseBodyAsString().getBytes("gbk"));
        post.releaseConnection();

        return Integer.parseInt(result);
    }

    public static String getMessage(Integer code) {
        String message;
        if (code > 0) {
            message = "SMS-f发送成功！短信发送数量：" + code + "条";
        } else if (code == -1) {
            message = "SMS-没有该用户账户";
        } else if (code == -2) {
            message = "SMS-接口密钥不正确";
        } else if (code == -21) {
            message = "SMS-MD5接口密钥加密不正确";
        } else if (code == -3) {
            message = "SMS-短信数量不足";
        } else if (code == -11) {
            message = "SMS-该用户被禁用";
        } else if (code == -14) {
            message = "SMS-短信内容出现非法字符";
        } else if (code == -4) {
            message = "SMS-手机号格式不正确";
        } else if (code == -41) {
            message = "SMS-手机号码为空";
        } else if (code == -42) {
            message = "SMS-短信内容为空";
        } else if (code == -51) {
            message = "SMS-短信签名格式不正确接口签名格式为：【签名内容】";
        } else if (code == -6) {
            message = "SMS-IP限制";
        } else {
            message = "其他错误";
        }
        return message;
    }

    public static String code() {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            char ch = str.charAt(new Random().nextInt(str.length()));
            sb.append(ch);
        }
        return sb.toString();
    }

}