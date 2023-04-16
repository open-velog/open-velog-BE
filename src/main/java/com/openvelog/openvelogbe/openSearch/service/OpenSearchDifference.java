package com.openvelog.openvelogbe.openSearch.service;

import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import com.openvelog.openvelogbe.openSearch.dto.BoardDocumentDto;
import com.openvelog.openvelogbe.openSearch.dto.BoardDocumentResponseAndCountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OpenSearchDifference {
    private final BoardRepository boardRepository;
    private final OpenSearchService openSearchService;


    public Set<Long> searchBoardsFromMySql(String keyword) {
        List<Board> boards = boardRepository.searchTitleOrContentOrBlogTitle(keyword,0,300,300);
        Set<Long> boardIds = new HashSet<>();
        for (Board board : boards) {
            boardIds.add(board.getId());
        }
        return boardIds;
    }

    public Set<Long> searchBoardsFromOpenSearch(String keyword ) {
        int page = 1;
        int size = 100; // 한 번에 가져올 데이터 크기를 지정합니다.
        BoardDocumentResponseAndCountDto results = openSearchService.search(keyword, page, size, null);
        Set<Long> boardIds = new HashSet<>();
        while (!results.getContent().isEmpty()) {
            for (BoardDocumentDto boardDocumentDto : results.getContent()) {
                boardIds.add(boardDocumentDto.getId());
            }
            page++;
            results = openSearchService.search(keyword, page, size,null);
        }
        return boardIds;
    }


    public void compareSearchResults(String keyword) {
        Set<Long> mySqlResults = searchBoardsFromMySql(keyword);
        Set<Long> openSearchResults = searchBoardsFromOpenSearch(keyword);

        System.out.println("MySQL에서 반환된 게시글 ID의 총 수: " + mySqlResults.size());
        System.out.println("OpenSearch에서 반환된 게시글 ID의 총 수: " + openSearchResults.size());

        boolean isEqual = mySqlResults.equals(openSearchResults);
        if (isEqual) {
            System.out.println("두 검색 결과가 동일합니다.");
        } else {
            System.out.println("두 검색 결과가 다릅니다.");

            Set<Long> difference = new HashSet<>(mySqlResults);
            difference.removeAll(openSearchResults);
            System.out.println("MySQL 결과에만 있는 게시물 ID: " + difference);

            difference = new HashSet<>(openSearchResults);
            difference.removeAll(mySqlResults);
            System.out.println("OpenSearch 결과에만 있는 게시물 ID: " + difference);

        }

    }

}
