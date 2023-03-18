package com.openvelog.openvelogbe.db;

import com.openvelog.openvelogbe.dummy.BlogDummyGenerator;
import com.openvelog.openvelogbe.dummy.BoardDummyGenerator;
import com.openvelog.openvelogbe.dummy.MemberDummyGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ActiveProfiles("test")
public class DummyDataInsertionTest {

    private MemberDummyGenerator memberDummyGenerator;

    private BlogDummyGenerator blogDummyGenerator;

    private BoardDummyGenerator boardDummyGenerator;

    @Autowired
    DummyDataInsertionTest(
            MemberDummyGenerator memberDummyGenerator,
            BlogDummyGenerator blogDummyGenerator,
            BoardDummyGenerator boardDummyGenerator
    ) {
        this.memberDummyGenerator = memberDummyGenerator;
        this.blogDummyGenerator = blogDummyGenerator;
        this.boardDummyGenerator = boardDummyGenerator;
    }

    @Test
    void insertDummyMembers() {
        assertTrue(memberDummyGenerator.insertDummiesIntoDatabase(100));
    }

    @Test
    void insertDummyBlogs() {
        assertTrue(blogDummyGenerator.insertDummiesIntoDatabase(100));
    }

    @Test
    void insertDummyBoards() {
        assertTrue(boardDummyGenerator.customizedInsertDummiesIntoDatabase());
    }
}
