package com.cqupt.art.author.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.util.Set;

public class RedisTest {
    @Autowired
    RedisTemplate redisTemplate;
    @Test
    public void test(){
        BoundSetOperations memberId = redisTemplate.boundSetOps("memberId");
        memberId.add(1);
        memberId.add(2);
        memberId.add(2);
        memberId.add(3);
        Set members = memberId.members();
        for (Object member : members) {
            System.out.println((String) member);
        }
    }
}
