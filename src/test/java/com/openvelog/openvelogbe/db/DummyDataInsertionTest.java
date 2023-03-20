package com.openvelog.openvelogbe.db;

import com.openvelog.openvelogbe.dummy.BoardDummyGenerator;
import com.openvelog.openvelogbe.dummy.MemberDummyGenerator;
import com.openvelog.openvelogbe.dummy.WishDummyGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ActiveProfiles("test")
public class DummyDataInsertionTest {

    private MemberDummyGenerator memberDummyGenerator;

    private BoardDummyGenerator boardDummyGenerator;

    private WishDummyGenerator wishDummyGenerator;

    @Autowired
    DummyDataInsertionTest(
            MemberDummyGenerator memberDummyGenerator,
            BoardDummyGenerator boardDummyGenerator,
            WishDummyGenerator wishDummyGenerator
    ) {
        this.memberDummyGenerator = memberDummyGenerator;
        this.boardDummyGenerator = boardDummyGenerator;
        this.wishDummyGenerator = wishDummyGenerator;
    }

    @Test
    void insertDummyMembers() {
        assertTrue(memberDummyGenerator.insertDummiesIntoDatabase(100));
    }

    @Test
    void insertDummyBoards() {
        assertTrue(boardDummyGenerator.insertDummiesIntoDatabase(10000));
    }

    @Test
    void insertDummyWishes() throws InterruptedException {
        assertTrue(wishDummyGenerator.insertDummiesIntoDatabase(9800));
    }

}
