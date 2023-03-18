package com.openvelog.openvelogbe.dummy;

import com.openvelog.openvelogbe.common.GlobalExceptionHandler;
import com.openvelog.openvelogbe.common.entity.Member;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

public abstract class DummyGenerator<E, R extends JpaRepository> {
    private static final Logger log = LoggerFactory.getLogger(DummyGenerator.class);

    protected Random random = new Random();

    protected final R repository;

    DummyGenerator(R repository) {
        this.repository = repository;
    }

    public abstract E generateDummyEntityOfThis();

    public boolean insertDummyIntoDatabase() {
        E generatedDummyEntity = generateDummyEntityOfThis();
        boolean isSucceeded = true;

        try {
            repository.save(generatedDummyEntity);
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            log.error("Insertion fail.");
            isSucceeded = false;
        } catch (RuntimeException runtimeException) {
            runtimeException.printStackTrace();
            isSucceeded = false;
        }

        return isSucceeded;
    }

    public boolean insertDummiesIntoDatabase(long dummyCount) {
        long dummyInserted = 0;
        long duplicationCount = 0;

        while (dummyInserted < dummyCount) {
            boolean isInsertionSucceeded = insertDummyIntoDatabase();

            if (isInsertionSucceeded) {
                dummyInserted += 1;
                duplicationCount = 0;
            }
            else {
                // 테스트 시, 과부하를 막기위한 코드
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                duplicationCount += 1;
            }

            if (duplicationCount >= 10) {
                throw new IllegalArgumentException("Too many insertion fails at once. Some setting might be wrong");
            }
        }

        return true;
    }

    public boolean customizedInsertDummiesIntoDatabase() {
        return true;
    }
}
