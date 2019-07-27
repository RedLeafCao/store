package com.can.store.shopping.commons;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyMD5Units {
    private String md5Code;

    public static MyMD5Units getInstance(String input){
        MyMD5Units md5 = new MyMD5Units();
        md5.stringMd5(input);
        return md5;
    }

    private void stringMd5(String input){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 将输入的字符串转为字节数组
            byte[] inputToByteArray = input.getBytes();
            // 将字节数组放入md中
            md.update(inputToByteArray);
            // 获取字节数组的结果
            byte[] result = md.digest();

            md5Code = byteToString(result);
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    private String byteToString(byte[] byteInput){
        char[] hexDigest = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        if(null == byteInput){
            throw new NullPointerException("字节数组为空");
        }

        char result[] = new char[byteInput.length*2];

        int index = 0;
        for(int i = 0;i<byteInput.length;i++){
            result[index++] = hexDigest[byteInput[i]>>>4 & 0xF];
            result[index++] = hexDigest[byteInput[i] & 0xF];
        }
        return new String(result);
    }

    public String getMd5Code(){
        return this.md5Code;
    }
}
