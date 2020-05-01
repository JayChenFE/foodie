package com.github.jaychenfe.controller;

import com.github.jaychenfe.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author jaychenfe
 */
@ApiIgnore
@RestController()
@RequestMapping("redis")
public class RedisController {

    private final RedisOperator redisOperator;

    @Autowired
    public RedisController(RedisOperator redisOperator) {
        this.redisOperator = redisOperator;
    }

    @GetMapping("/set")
    public Object set(String key, String value) {

        redisOperator.set(key, value);

        return "set ok";
    }

    @GetMapping("/get")
    public String get(String key) {
        return redisOperator.get(key);
    }

    @GetMapping("/delete")
    public Object delete(String key) {
        redisOperator.del(key);
        return "del ok";
    }
}
