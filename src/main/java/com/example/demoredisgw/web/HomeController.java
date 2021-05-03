package com.example.demoredisgw.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    
    @Autowired
    StringRedisTemplate redisTemplate ;

    @GetMapping("/keys")
    public ResponseEntity<?> keys() {

        return ResponseEntity.ok().body(redisTemplate.keys("*"));
    }
}
