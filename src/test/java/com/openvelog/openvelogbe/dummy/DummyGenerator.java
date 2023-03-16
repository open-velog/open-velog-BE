package com.openvelog.openvelogbe.dummy;

import com.openvelog.openvelogbe.common.GlobalExceptionHandler;
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
            log.error("Unique property got duplicated.");
            isSucceeded = false;
        } catch (RuntimeException runtimeException) {
            runtimeException.printStackTrace();
            isSucceeded = false;
        }

        return isSucceeded;
    }

    public List<E> insertDummiesIntoDatabase(long dummyCount) {
        long dummyInserted = 0;

        while (dummyInserted < dummyCount) {
            dummyInserted = insertDummyIntoDatabase() ? dummyInserted + 1 : 0;
            if (dummyInserted >= 10) {
                throw new IllegalArgumentException("Too much duplication at one. Some setting might be wrong");
            }
        }

        return  repository.findAll();
    }
}
