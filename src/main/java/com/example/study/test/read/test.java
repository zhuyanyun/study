package com.example.study.test.read;

import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class test {
    public static void main(String[] args) {

        Map<String, Integer> oneMap = new HashMap<>();
        System.out.println(CollectionUtils.isEmpty(oneMap));
    }
}
