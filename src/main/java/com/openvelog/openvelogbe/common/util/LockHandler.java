package com.openvelog.openvelogbe.common.util;

import com.openvelog.openvelogbe.common.dto.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class LockHandler {
    private final RedissonClient redissonClient;

    static String REDISSON_KEY_PREFIX = "RLOCK:";

    public <T> T runOnLock(String key, Long waitTime, Long leaseTime, Supplier<T> execute) {
        T result = null;

        RLock lock = redissonClient.getLock(REDISSON_KEY_PREFIX + key);
        try {
            boolean acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
            if (acquired == true) {
                result = execute.get();
            } else {
                log.error("lock key acquiring failed on {}", key);
                throw new RuntimeException(ErrorMessage.REDIS_KEY_ACQUIRED_FAILED.getMessage());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(ErrorMessage.REDIS_TRY_LOCK_FAILED.getMessage());
        } finally {
            lock.unlock();
        }

        return result;
    }

}
