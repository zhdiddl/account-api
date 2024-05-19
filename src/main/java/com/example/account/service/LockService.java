package com.example.account.service;

import com.example.account.exception.AccountException;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j // 로그를 편하게 기록
@Service // Spring의 서비스 클래스
@RequiredArgsConstructor // final 필드 초기화하는 생성자 자동 생성
public class LockService {
    private final RedissonClient redissonClient; // RedissonClient 객체 주입

    // 분산 락: 여러 인스턴스가 동시에 동일한 리소스에 접근하지 못하게 막아주는 기능
    public void lock(String accountNumber) {
        RLock lock = redissonClient.getLock(getLockKey(accountNumber));
        log.debug("Trying lock for accountNumber : {}", accountNumber);

        try { // 락을 시도, 1초 동안 대기하고, 락을 획득하면 15초 동안 유지
            boolean isLock = lock.tryLock(1, 15, TimeUnit.SECONDS);
            if (!isLock) {
                log.error("========= Lock acquisition failed ========");
                throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);
            }
        } catch (AccountException e) {
            throw e;
        } catch (Exception e) {
            log.error("Redis lock failed", e); // 예외 발생시 로그 기록
        }
    }

    public void unlock(String accountNumber) {
        log.debug("Unlock for accountNumber : {}", accountNumber);
        redissonClient.getLock(getLockKey(accountNumber)).unlock();
    }

    private static String getLockKey(String accountNumber) {
        return "ACLK" + accountNumber;
    }
}
