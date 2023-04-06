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

import java.util.ArrayList;
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

    abstract public boolean insertDummiesIntoDatabase(int dummyCount) throws InterruptedException;
    //public abstract List<E> createDummyMembersSaveToDBAndSaveToCSV(int count, String filePath);

}
