package com.example.backend.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

public class Encrypt {
    private static final byte[] secret = "hfoKvXeFhifWGOL1".getBytes();

    public static String EncryptByAES(String data) {
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), secret).getEncoded();
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        // 加密
        return aes.encryptHex(data);
    }

    public static String DecryptByAES(String data) {
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), secret).getEncoded();
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        // 解密
        return aes.decryptStr(data);
    }


}
