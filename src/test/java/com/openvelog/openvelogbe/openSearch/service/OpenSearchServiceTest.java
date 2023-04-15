package com.openvelog.openvelogbe.openSearch.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openvelog.openvelogbe.OpenVelogBeApplication;
import com.openvelog.openvelogbe.board.dto.BoardRequestDto;
import com.openvelog.openvelogbe.board.dto.BoardResponseDto;
import com.openvelog.openvelogbe.board.service.BoardService;
import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.common.repository.BlogRepository;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import com.openvelog.openvelogbe.common.repository.MemberRepository;
import com.openvelog.openvelogbe.common.security.UserDetailsImpl;
import com.openvelog.openvelogbe.common.security.UserDetailsServiceImpl;
import com.openvelog.openvelogbe.config.TestAppMysqlDatabaseConfig;
import com.openvelog.openvelogbe.member.dto.MemberResponseDto;
import com.openvelog.openvelogbe.member.dto.SignupRequestDto;
import com.openvelog.openvelogbe.member.service.MemberService;
import com.openvelog.openvelogbe.openSearch.dto.BoardDocumentDto;
import com.openvelog.openvelogbe.openSearch.dto.BoardDocumentResponseAndCountDto;
import com.openvelog.openvelogbe.openSearch.dto.BoardTestDocumentDto;
import com.openvelog.openvelogbe.openSearch.dto.BoardTestDocumentResponseAndCountDto;
import org.opensearch.client.Response;
import org.opensearch.client.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensearch.client.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {TestAppMysqlDatabaseConfig.class, OpenVelogBeApplication.class})
@Transactional(transactionManager = "testAppMysqlTransactionManager")
@ActiveProfiles("test")
public class OpenSearchServiceTest {
    @Autowired
    private RestClient restClient;
    @Autowired
    private OpenSearchAddIndexAndSearch openSearchAddIndexAndSearch;
    @Autowired
    private MemberService memberService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private BoardRepository boardRepository;


    @BeforeEach
    public void setUp() throws IOException {
        //board_test 인덱스를 분석기 설정과 함께 만듭니다.
        openSearchAddIndexAndSearch.createIndexWithAnalyzer();

        // 테스트용 Board 데이터를 생성하고 저장합니다.
        List<Board> testBoards = createTestBoards();
        openSearchAddIndexAndSearch.indexTestBoards(testBoards);

        //OpenSearch의 인덱싱은 비동기적으로 처리되기에 인덱스를 강제로 새로 고침하고 문서 갯수 확인
        Request refreshRequest = new Request("POST", "/board_test/_refresh");
        restClient.performRequest(refreshRequest);

        // 인덱스가 잘 등록되었는지 확인하기 위해 인덱스의 문서 개수를 확인합니다.
        Request request = new Request("GET", "/board_test/_count");
        Response countResponse = restClient.performRequest(request);
        assertEquals(200, countResponse.getStatusLine().getStatusCode());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode countNode = objectMapper.readTree(countResponse.getEntity().getContent());
        int documentCount = countNode.get("count").asInt();

        // 여기서, boardRepository를 사용하여 실제 데이터베이스에 있는 게시판의 문서 개수를 가져와서 비교합니다.
        int expectedDocumentCount = (int)boardRepository.count();
        assertEquals(expectedDocumentCount, documentCount);
    }


    @AfterEach
    public void tearDown() throws IOException {
        // 테스트용 인덱스를 삭제합니다.
        deleteTestIndex();
    }

    @Test
    public void testSearch() throws IOException {
        // OpenSearchService를 사용하여 검색어를 기반으로 게시판 데이터를 검색합니다.
        String keyword = "test";
        Integer page = 1;
        Integer size = 10;

        BoardTestDocumentResponseAndCountDto response = openSearchAddIndexAndSearch.search(keyword, page, size);
        List<BoardTestDocumentDto> results = response.getContent();

        assertNotNull(results);

        // 검색 결과가 있을 경우 hits 객체에서 각 hit의 _source를 사용하여 결과를 검증합니다.
        if (!results.isEmpty()) {
            assertEquals(response.getNumberOfElements(), results.size());

            // 결과의 제목 또는 내용에 검색어가 포함되어 있는지 확인합니다.
            for (BoardTestDocumentDto boardTestDocumentDto : results) {
                boolean titleContainsKeyword = boardTestDocumentDto.getTitle().toLowerCase().contains(keyword.toLowerCase());
                boolean contentContainsKeyword = boardTestDocumentDto.getContent().toLowerCase().contains(keyword.toLowerCase());

                assertTrue(titleContainsKeyword || contentContainsKeyword);
            }
        } else {
            // 검색 결과가 없을 경우, totalHits와 numberOfElements가 0인지 확인합니다.
            assertEquals(0, response.getTotalElements());
            assertEquals(0, response.getNumberOfElements());
        }
    }

    private List<Board> createTestBoards() {
        List<Board> testBoards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            //회원 가입
            SignupRequestDto signupRequestDto = new SignupRequestDto("testUserId"+i, "testUserName"+i, "testPassword"+i, "test@test"+i+".com", Gender.M, LocalDate.of(1990+i, 1, 1));
            MemberResponseDto memberResponse = memberService.signup(signupRequestDto);
            Optional<Member> member = memberRepository.findByUserId("testUserId"+i);
            Optional<Blog> blog = blogRepository.findByMemberId(member.get().getId());
            assertEquals(memberResponse.getBlogId(), blog.get().getId());

            // 게시글 작성 및 확인
            BoardRequestDto.BoardAdd dto = new BoardRequestDto.BoardAdd(blog.get().getId(),"testTitle"+i,"testContent"+i);
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername("testUserId"+i);
            BoardResponseDto boardResponse = boardService.createBoard(dto,userDetails);
            Optional<Board> board = boardRepository.findByTitle("testTitle"+i);
            // 값을 확인하고 값이 있을 경우 리스트에 추가
            if (board.isPresent()) {
                Board realboard = board.get();
                testBoards.add(realboard);
            }
            assertEquals("testTitle"+i, boardResponse.getTitle());
            assertEquals("testContent"+i, boardResponse.getContent());


        }
        return testBoards;
    }

    private void deleteTestIndex() throws IOException {
        Response response = restClient.performRequest(new Request("DELETE", "/board_test"));
        assertEquals(200, response.getStatusLine().getStatusCode());
    }
}
