package com.openvelog.openvelogbe.csv;

import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.config.TestConfig;
import com.openvelog.openvelogbe.dummy.MemberDummyGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(TestConfig.class)
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
