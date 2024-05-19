package com.example.account.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

@Configuration // 어플리케이션 시작시 LocalRedisConfig를 설정 파일로 인식하도록 지정
public class LocalRedisConfig { // 임베디드 Redis 설정 클래스
    @Value("${spring.data.redis.port}") // YML 설정 파일에 정의된 값을 주입
    private int redisPort; // 포트 번호

    private RedisServer redisServer; // 서버를 나타내는 객체

    @PostConstruct
    public void startRedis() {
        redisServer = new RedisServer(redisPort);
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
