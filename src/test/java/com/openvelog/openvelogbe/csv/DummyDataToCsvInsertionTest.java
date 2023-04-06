package com.openvelog.openvelogbe.csv;

import com.openvelog.openvelogbe.common.entity.Member;

import com.openvelog.openvelogbe.dummy.MemberDummyGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class DummyDataToCsvInsertionTest {

    private MemberDummyGenerator memberDummyGenerator;

    @Autowired
    DummyDataToCsvInsertionTest(MemberDummyGenerator memberDummyGenerator) {
        this.memberDummyGenerator = memberDummyGenerator;
    }

    @Test
    void generateMembersSaveToDBAndSaveToCSV() {
        int count = 300;
        String filePath = "members.csv";
        List<Member> savedMembers = memberDummyGenerator.createDummyMembersSaveToDBAndSaveToCSV(count, filePath);
        assertEquals(count, savedMembers.size());

        File csvFile = new File(filePath);
        assertTrue(csvFile.exists());
    }
}
