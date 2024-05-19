package com.example.account.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Redisson: Redis를 쉽게 사용하도록 도와주는 Client 라이브러리

@Configuration
public class RedisRepositoryConfig { // Redisson 설정 클래스
    @Value("${spring.data.redis.host}") // 127.0.0.1
    private String redisHost; // 호스트 주소

    @Value("${spring.data.redis.port}") // 6379
    private int redisPort; // 포트 번호

    @Bean // 아래 메소드가 반환하는 객체를 Spring 컨테이너가 관리하도록 Bean으로 정의
    public RedissonClient redissonClient() {
        Config config = new Config(); // config 객체 생성
        config.useSingleServer().setAddress("redis://" + redisHost + ":" + redisPort); // Redis 서버 주소 설정

        return Redisson.create(config); // RedissonClient 객체 반환
    }
}
