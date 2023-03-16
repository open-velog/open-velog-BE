package com.openvelog.openvelogbe.dummy;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Random;

@Slf4j
public abstract class DummyGenerator<E, R extends JpaRepository> {
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
            dummyInserted += insertDummyIntoDatabase() ? 1 : 0;
        }

        return  repository.findAll();
    }
}
