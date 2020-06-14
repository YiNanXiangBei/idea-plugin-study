package com.example.plugindemo.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yinan
 * @date 2020/6/9
 */
public class Main {
    public static void main(String[] args) {
        String s = "1";
        System.out.println(s.getClass().equals(String.class));
        System.out.println(s.getClass().equals(Object.class));
    }
}

enum HttpMethod {
    /**
     * GET
     */
    GET,

    /**
     * POST
     */
    POST,

    /**
     * PUT
     */
    PUT,

    /**
     * DELETE
     */
    DELETE,

    /**
     * PATCH
     */
    PATCH,

    /**
     * HEAD
     */
    HEAD
}