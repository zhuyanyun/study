package com.example.study;

public class Aastest {

    public static void main(String[] args) {
        String s = "/api/mcapi///api/v1/activity/latest?";
        String s2 = "/api/mcapi///api/v1/activity/latest?aslflasflasf";
        if(s.endsWith("?")){
            s = s.substring(0,s.length()-1);
            System.out.println(s);
        }
        String[] split = s2.split("\\?");
        System.out.println(split[0]);

    }
}
