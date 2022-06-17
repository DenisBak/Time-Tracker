package com.denis.controls;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class T {
    @Test
    public void test() {
        Map<String, String> s = new HashMap<>();
        s.put("a", "b");
        s.put("b", "a");
        s.remove("a");
        System.out.println(s);
        System.out.println(s.get("a"));
    }
}
