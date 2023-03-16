package com.openvelog.openvelogbe.db;

import com.openvelog.openvelogbe.dummy.MemberDummyGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ActiveProfiles("test")
public class DummyDataInsertionTest {
    private MemberDummyGenerator memberDummyGenerator;

    @Autowired
    DummyDataInsertionTest(MemberDummyGenerator memberDummyGenerator) {
        this.memberDummyGenerator = memberDummyGenerator;
    }

    @Test
    public void insertDummyMembers() {
        assertTrue(memberDummyGenerator.insertDummiesIntoDatabase(10).size() > 20);
    }
}
