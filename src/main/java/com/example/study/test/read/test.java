package com.example.study.test.read;

import java.util.Arrays;

public class test {
    public static void main(String[] args) {

        String str = "\tinvestor.huihuigj.com    \"GET /share/_nuxt/pages/assistant/com/detail.pages/assistant/com/product-list.pages/assistant/index.02ee9324.js HTTP/1.1\"    200";
        byte[] ss = str.getBytes();

        int a = 0;
        int b = 0;
        for(int y=0; y< ss.length; y++) {
            System.out.println(ss[y]);
            System.out.println(ss[y] == 47);
            if(ss[y] == '/' && a == 0){
                a = y;
                System.out.println(a);
                byte[] urlByte = Arrays.copyOfRange(ss, a, ss.length);
                String url = new String(urlByte);
                System.out.println(url);
            } else if(ss[y]=='/' && b == 0){
                b = y;
                byte[] urlByte = Arrays.copyOfRange(ss, a, b - 4);
                String url = new String(urlByte);
                System.out.println(url);
            }

        }

//        System.out.println(bytes[0]);
//        System.out.println();
//
//
//        System.out.println(bytes);
//        System.out.println(bytes1);
    }
}
