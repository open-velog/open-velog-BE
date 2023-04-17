/*
package com.openvelog.openvelogbe.openSearch.service;

import com.openvelog.openvelogbe.OpenVelogBeApplication;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import com.openvelog.openvelogbe.common.repository.MongoSearchLogRepository;
import com.openvelog.openvelogbe.config.TestAppMysqlDatabaseConfig;
import com.openvelog.openvelogbe.openSearch.dto.BoardDocumentDto;
import com.openvelog.openvelogbe.openSearch.dto.BoardDocumentResponseAndCountDto;
import com.openvelog.openvelogbe.openSearch.dto.BoardTestDocumentDto;
import com.openvelog.openvelogbe.openSearch.dto.BoardTestDocumentResponseAndCountDto;
import com.openvelog.openvelogbe.openSearch.entity.BoardTestDocument;
import org.junit.jupiter.api.Test;
import org.opensearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
@SpringBootTest(classes = {TestAppMysqlDatabaseConfig.class, OpenVelogBeApplication.class})
//@Transactional(transactionManager = "testAppMysqlTransactionManager")
@ActiveProfiles("test")
@MockBean(MongoSearchLogRepository.class) // Add this line to mock the bean
public class OpenSearchDifferenceTest {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private OpenSearchAddIndexAndSearch openSearchAddIndexAndSearch;


    public Set<Long> searchBoardsFromMySql(String keyword) {
        List<Board> boards = boardRepository.findByTitleContainingOrContentContaining(keyword, keyword);
        Set<Long> boardIds = new HashSet<>();
        for (Board board : boards) {
            boardIds.add(board.getId());
        }
        return boardIds;
    }

    public Set<Long> searchBoardsFromOpenSearch(String keyword )throws IOException {
        int page = 1;
        int size = 100; // 한 번에 가져올 데이터 크기를 지정합니다.
        BoardTestDocumentResponseAndCountDto results = openSearchAddIndexAndSearch.search(keyword, page, size);
        Set<Long> boardIds = new HashSet<>();
        while (!results.getContent().isEmpty()) {
            for (BoardTestDocumentDto boardDocumentDto : results.getContent()) {
                boardIds.add(boardDocumentDto.getId());
            }
            page++;
            results = openSearchAddIndexAndSearch.search(keyword, page, size);
        }
        return boardIds;
    }

    @Test
    public void compareSearchResults()throws IOException {
        String keyword = "보자기";
        Set<Long> mySqlResults = searchBoardsFromMySql(keyword);
        Set<Long> openSearchResults = searchBoardsFromOpenSearch(keyword);

        boolean isEqual = mySqlResults.equals(openSearchResults);
        if (isEqual) {
            System.out.println("두 검색 결과가 동일합니다.");
        } else {
            System.out.println("두 검색 결과가 다릅니다.");

            Set<Long> difference = new HashSet<>(mySqlResults);
            difference.removeAll(openSearchResults);
            System.out.println("MySQL 결과에만 있는 게시물 ID: " + difference);

            for (Long id : difference) {
                Optional<Board> board = boardRepository.findById(id);
                board.ifPresent(b -> System.out.println("MySQL 게시물: " + b));
            }

            difference = new HashSet<>(openSearchResults);
            difference.removeAll(mySqlResults);
            System.out.println("OpenSearch 결과에만 있는 게시물 ID: " + difference);

            for (Long id : difference) {
                Optional<Board> board = boardRepository.findById(id);
                board.ifPresent(b -> System.out.println("OpenSearch 게시물: " + b));
            }
        }

    }
}
*/
